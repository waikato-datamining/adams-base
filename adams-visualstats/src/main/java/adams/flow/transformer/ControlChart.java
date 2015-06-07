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
 * ControlChart.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spc.IndividualsControlChart;
import adams.data.spc.Limits;
import adams.data.spc.MatrixControlChart;
import adams.data.spc.UChart;
import adams.data.statistics.StatUtils;
import adams.flow.container.ControlChartContainer;
import adams.flow.core.Unknown;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies a control chart algorithm to the data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[][]<br>
 * &nbsp;&nbsp;&nbsp;double[][]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Float[][]<br>
 * &nbsp;&nbsp;&nbsp;float[][]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer[][]<br>
 * &nbsp;&nbsp;&nbsp;int[][]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.ControlChartContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.ControlChartContainer: Algor, Chart, Data, Prepared, Limits
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
 * &nbsp;&nbsp;&nbsp;default: ControlChart
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the control chart containers as array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-chart &lt;adams.data.spc.ControlChart&gt; (property: chart)
 * &nbsp;&nbsp;&nbsp;The control chart algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spc.UChart
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6264 $
 */
public class ControlChart
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 4013915680601748582L;

  /** the control chart to use. */
  protected adams.data.spc.ControlChart m_Chart;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies a control chart algorithm to the data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "chart", "chart",
	    new UChart());
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return ControlChartContainer.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the control chart containers as array or one-by-one.";
  }

  /**
   * Sets the chart to use.
   *
   * @param value	the chart
   */
  public void setChart(adams.data.spc.ControlChart value) {
    m_Chart = value;
    reset();
  }

  /**
   * Returns the chart to use.
   *
   * @return		the chart
   */
  public adams.data.spc.ControlChart getChart() {
    return m_Chart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String chartTipText() {
    return "The control chart algorithm to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "chart", m_Chart);
  }
  
  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Double[][].class, double[][].class, java.lang.Float[][].class, float[][].class, java.lang.Integer[][].class, int[][].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    if (m_Chart == null)
      return new Class[]{Unknown.class};
    else if (m_Chart instanceof IndividualsControlChart)
      return new Class[]{Double[].class, double[].class, Float[].class, float[].class, Integer[].class, int[].class};
    else if (m_Chart instanceof MatrixControlChart)
      return new Class[]{Double[][].class, double[][].class, Float[][].class, float[][].class, Integer[][].class, int[][].class};
    else
      throw new IllegalStateException("Unhandled control chart type: " + m_Chart.getClass().getName());
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    List<Limits>		stats;
    Object			data;
    double[]			prepared;
    ControlChartContainer	cont;
    Number[]			numberArray;
    Number[][]			numberMatrix;

    result = null;

    m_Queue.clear();
    try {
      data = m_InputToken.getPayload();
      if (m_Chart instanceof IndividualsControlChart) {
	if (data instanceof Double[])
	  numberArray = (Double[]) data;
	else if (data instanceof double[])
	  numberArray = StatUtils.toNumberArray((double[]) data);
	else if (data instanceof Float[])
	  numberArray = (Float[]) data;
	else if (data instanceof float[])
	  numberArray = StatUtils.toNumberArray((float[]) data);
	else if (data instanceof Integer[])
	  numberArray = (Integer[]) data;
	else if (data instanceof int[])
	  numberArray = StatUtils.toNumberArray((int[]) data);
	else
	  throw new IllegalStateException("Unhandled data type: " + Utils.classToString(data.getClass()));
	stats    = ((IndividualsControlChart) m_Chart).calculate(numberArray);
	prepared = ((IndividualsControlChart) m_Chart).prepare(numberArray);
	cont     = new ControlChartContainer(m_Chart, null, data, prepared, stats.toArray(new Limits[stats.size()]));
	m_Queue.add(cont);
      }
      else if (m_Chart instanceof MatrixControlChart) {
	if (data instanceof Double[][])
	  numberMatrix = (Double[][]) data;
	else if (data instanceof double[][])
	  numberMatrix = StatUtils.toNumberMatrix((double[][]) data);
	else if (data instanceof Float[][])
	  numberMatrix = (Float[][]) data;
	else if (data instanceof float[][])
	  numberMatrix = StatUtils.toNumberMatrix((float[][]) data);
	else if (data instanceof Integer[][])
	  numberMatrix = (Integer[][]) data;
	else if (data instanceof int[][])
	  numberMatrix = StatUtils.toNumberMatrix((int[][]) data);
	else
	  throw new IllegalStateException("Unhandled data type: " + Utils.classToString(data.getClass()));
	stats    = ((MatrixControlChart) m_Chart).calculate(numberMatrix);
	prepared = ((MatrixControlChart) m_Chart).prepare(numberMatrix);
	cont     = new ControlChartContainer(m_Chart, null, data, prepared, stats.toArray(new Limits[stats.size()]));
	m_Queue.add(cont);
      }
      else {
        throw new IllegalStateException("Unhandled control chart type: " + m_Chart.getClass().getName());
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException(
	  "Failed to generate control chart data: "
	      + m_InputToken.getPayload(), e);
    }

    return result;
  }
}
