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
 * SpreadSheetPlotGenerator.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.Token;
import adams.flow.transformer.plotgenerator.AbstractPlotGenerator;
import adams.flow.transformer.plotgenerator.XYPlotGenerator;

/**
 <!-- globalinfo-start -->
 * Outputs plot containers generated from a spreadsheet.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, IsMarker
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetPlotGenerator
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
 * <pre>-generator &lt;adams.flow.transformer.plotgenerator.AbstractPlotGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The plot generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.plotgenerator.XYPlotGenerator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetPlotGenerator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 1147935218531182101L;

  /** the generator to use. */
  protected AbstractPlotGenerator m_Generator;

  /** the generated plot containers. */
  protected List<SequencePlotterContainer> m_Containers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs plot containers generated from a spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new XYPlotGenerator());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Containers = new ArrayList<SequencePlotterContainer>();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
  }

  /**
   * Sets the plot generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractPlotGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the current plot generator.
   *
   * @return		the generator
   */
  public AbstractPlotGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The plot generator to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;

    result = null;

    sheet = (SpreadSheet) m_InputToken.getPayload();
    try {
      m_Containers = m_Generator.generate(sheet);
    }
    catch (Exception e) {
      result = handleException(
	  "Failed to generate plot containers with " 
	      + OptionUtils.getCommandLine(m_Generator), e);
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
    return (m_Containers.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result        = new Token(m_Containers.get(0));
    m_InputToken  = null;
    m_Containers.remove(0);

    return result;
  }
}
