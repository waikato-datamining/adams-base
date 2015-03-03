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
 * MakePlotContainer.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.lang.reflect.Array;

import adams.core.QuickInfoHelper;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.source.Start;

/**
 <!-- globalinfo-start -->
 * Creates a named container for the SequencePlotter actor.<br/>
 * In case of Double arrays arriving at the input, they must have length 2 for X and Y, 4 for X&#47;Y&#47;Y-Error (low&#47;high) or 6 X&#47;Y&#47;X-Error (low-high)&#47;Y-Error (low-high).<br/>
 * Integer arrays can only have the length 2.<br/>
 * It is also possible, depending on the plotter sink setup, to use strings for x and&#47;or y.<br/>
 * In order to force an update of the plot, overriding the current plot-updater setup, you can create a special plot container of type UPDATE and send that to the plot (can be triggered with a null-token, e.g., from a adams.flow.source.Start source). Useful if you want to take a screenshot at a specific time, but avoid costly screen refreshs.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer[]<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Object[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SequencePlotterContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.SequencePlotterContainer: PlotName, X, Y, Content type, Error X, Error Y, MetaData
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MakePlotContainer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-plot-name &lt;java.lang.String&gt; (property: plotName)
 * &nbsp;&nbsp;&nbsp;The name for the plot.
 * &nbsp;&nbsp;&nbsp;default: Plot
 * </pre>
 * 
 * <pre>-type &lt;PLOT|MARKER|OVERLAY|UPDATE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of plot container to create.
 * &nbsp;&nbsp;&nbsp;default: PLOT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MakePlotContainer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2488434486963278287L;

  /** the name of the plot. */
  protected String m_PlotName;

  /** the type plot container to create. */
  protected ContentType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Creates a named container for the SequencePlotter actor.\n"
	+ "In case of Double arrays arriving at the input, they must have "
	+ "length 2 for X and Y, 4 for X/Y/Y-Error (low/high) or "
	+ "6 X/Y/X-Error (low-high)/Y-Error (low-high).\n"
	+ "Integer arrays can only have the length 2.\n"
	+ "It is also possible, depending on the plotter sink setup, to use "
	+ "strings for x and/or y.\n"
	+ "In order to force an update of the plot, overriding the current "
	+ "plot-updater setup, you can create a special plot container of "
	+ "type " + ContentType.UPDATE + " and send that to the plot (can be "
	+ "triggered with a null-token, e.g., from a " + Start.class.getName() + " "
	+ "source). Useful if you want to take a screenshot at a specific time, "
	+ "but avoid costly screen refreshs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "plot-name", "plotName",
	    "Plot");

    m_OptionManager.add(
	    "type", "type",
	    ContentType.PLOT);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = QuickInfoHelper.toString(this, "plotName", (m_PlotName.length() > 0 ? m_PlotName : "-unnamed-"), "name: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");

    return result;
  }

  /**
   * Sets the plot name.
   *
   * @param value	the name
   */
  public void setPlotName(String value) {
    m_PlotName = value;
    reset();
  }

  /**
   * Returns the current plot name.
   *
   * @return		the name
   */
  public String getPlotName() {
    return m_PlotName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotNameTipText() {
    return "The name for the plot.";
  }

  /**
   * Sets the type of container to create.
   *
   * @param value	the type
   */
  public void setType(ContentType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of container to create.
   *
   * @return		the type
   */
  public ContentType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of plot container to create.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Double.class, java.lang.Double[].class, java.lang.Integer.class, java.lang.Integer[].class, java.lang.String.class, java.lang.String[].class, java.lang.Object[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    if (m_Type == ContentType.UPDATE)
      return new Class[]{Unknown.class};
    else
      return new Class[]{Double.class, Double[].class, Integer.class, Integer[].class, String.class, String[].class, Object[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object			array;
    SequencePlotterContainer	cont;
    int				len;

    result = null;

    cont = null;

    if (m_Type == ContentType.UPDATE) {
      cont = new SequencePlotterContainer(m_PlotName, null, m_Type);
    }
    else {
      if (m_InputToken.getPayload().getClass().isArray()) {
	array = m_InputToken.getPayload();
	len   = Array.getLength(array);
	if (len == 2) {
	  cont = new SequencePlotterContainer(
	      m_PlotName,
	      (Comparable) Array.get(array, 0),
	      (Comparable) Array.get(array, 1),
	      m_Type);
	}
	else if (len == 4) {
	  cont = new SequencePlotterContainer(
	      m_PlotName,
	      (Comparable) Array.get(array, 0),
	      (Comparable) Array.get(array, 1),
	      null,
	      new Double[]{(Double) Array.get(array, 2), (Double) Array.get(array, 3)},
	      m_Type);
	}
	else if (len == 6) {
	  cont = new SequencePlotterContainer(
	      m_PlotName,
	      (Comparable) Array.get(array, 0),
	      (Comparable) Array.get(array, 1),
	      new Double[]{(Double) Array.get(array, 2), (Double) Array.get(array, 3)},
	      new Double[]{(Double) Array.get(array, 4), (Double) Array.get(array, 5)},
	      m_Type);
	}
	else {
	  result = "Array must have length 2, 4 or 6 (provided: " + Array.getLength(array) + ")!";
	}
      }
      else {
	cont = new SequencePlotterContainer(
	    m_PlotName,
	    (Comparable) m_InputToken.getPayload(),
	    m_Type);
      }
    }

    if (cont != null)
      m_OutputToken = new Token(cont);

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
}
