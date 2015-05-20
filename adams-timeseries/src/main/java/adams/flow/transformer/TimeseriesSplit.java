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
 * TimeseriesSplit.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.Arrays;

import adams.core.QuickInfoHelper;
import adams.data.timeseries.Timeseries;
import adams.flow.transformer.timeseriessplit.AbstractTimeseriesSplitter;
import adams.flow.transformer.timeseriessplit.PassThrough;

/**
 <!-- globalinfo-start -->
 * Splits the incoming timeseries into sub-series using the specified splitter algorithm.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: TimeseriesSplit
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the sub-series are output as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-splitter &lt;adams.flow.transformer.timeseriessplit.AbstractTimeseriesSplitter&gt; (property: splitter)
 * &nbsp;&nbsp;&nbsp;The splitting algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.timeseriessplit.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7856 $
 */
public class TimeseriesSplit
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the splitter. */
  protected AbstractTimeseriesSplitter m_Splitter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the incoming timeseries into sub-series using the specified splitter algorithm.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "splitter", "splitter",
	    new PassThrough());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the sub-series are output as array rather than one-by-one.";
  }

  /**
   * Sets the splitting algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setSplitter(AbstractTimeseriesSplitter value) {
    m_Splitter = value;
    reset();
  }

  /**
   * Returns the splitting algorithm in use.
   *
   * @return		the algorithm
   */
  public AbstractTimeseriesSplitter getSplitter() {
    return m_Splitter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitterTipText() {
    return "The splitting algorithm to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "splitter", m_Splitter, "splitter: ");
    result += QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "as array", ", ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Timeseries.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return Timeseries.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Timeseries		series;
    Timeseries[]	sub;

    result = null;

    series = (Timeseries) m_InputToken.getPayload();
    m_Queue.clear();
    try {
      sub = m_Splitter.split(series);
      m_Queue.addAll(Arrays.asList(sub));
    }
    catch (Exception e) {
      result = handleException("Failed to split timeseries", e);
    }

    return result;
  }
}
