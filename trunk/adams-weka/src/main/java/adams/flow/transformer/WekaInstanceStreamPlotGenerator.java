/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * WekaInstanceStreamPlotGenerator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instance;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates plot containers from a range of attributes of the weka.core.Instance objects being passed through.<br/>
 * The generator merely uses the internal data representation for generating the Y value of the plot container.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: InstanceStreamPlotGenerator
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-attributes &lt;java.lang.String&gt; (property: attributes)
 * &nbsp;&nbsp;&nbsp;The range of attributes to create plot containers for; A range is a comma-separated
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); the
 * &nbsp;&nbsp;&nbsp;following placeholders can be used as well: first, second, third, last_2,
 * &nbsp;&nbsp;&nbsp; last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceStreamPlotGenerator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 6449128249417195569L;

  /** the range of attributes to plot. */
  protected Range m_Attributes;

  /** the generated containers. */
  protected List<SequencePlotterContainer> m_Containers;

  /** the counter for the X value of the containers. */
  protected int m_Counter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates plot containers from a range of attributes of the "
      + "weka.core.Instance objects being passed through.\n"
      + "The generator merely uses the internal data representation for "
      + "generating the Y value of the plot container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "attributes", "attributes",
	    new Range(Range.ALL));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Attributes = new Range();
    m_Containers = new ArrayList<SequencePlotterContainer>();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Containers.clear();
    m_Counter = 0;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "attributes", m_Attributes);
  }

  /**
   * Sets the range of attributes to create plot containers for.
   *
   * @param value	the range
   */
  public void setAttributes(Range value) {
    m_Attributes = value;
    reset();
  }

  /**
   * Returns the range of attributes to create plot containers for.
   *
   * @return		the range
   */
  public Range getAttributes() {
    return m_Attributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributesTipText() {
    return "The range of attributes to create plot containers for; " + m_Attributes.getExample();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instance			inst;
    SequencePlotterContainer	cont;
    int[]			indices;
    int				i;

    result = null;

    inst = (Instance) m_InputToken.getPayload();

    m_Counter++;
    m_Containers.clear();
    m_Attributes.setMax(inst.numAttributes());
    indices = m_Attributes.getIntIndices();

    for (i = 0; i < indices.length; i++) {
      if (inst.attribute(indices[i]).isNominal())
	cont = new SequencePlotterContainer(
	    inst.dataset().attribute(indices[i]).name(),
	    new Double(m_Counter),
	    inst.stringValue(indices[i]));
      else
	cont = new SequencePlotterContainer(
	    inst.dataset().attribute(indices[i]).name(),
	    new Double(m_Counter),
	    inst.value(indices[i]));
      m_Containers.add(cont);
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.SequencePlotterContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SequencePlotterContainer.class};
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Containers != null) && (m_Containers.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result       = new Token(m_Containers.get(0));
    m_InputToken = null;
    m_Containers.remove(0);

    return result;
  }
}
