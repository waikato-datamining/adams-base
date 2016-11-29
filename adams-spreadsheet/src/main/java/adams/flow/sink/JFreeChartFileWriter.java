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

/**
 * JFreeChartFileWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.JAIImageWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.sink.jfreechartplot.chart.AbstractChartGenerator;
import adams.flow.sink.jfreechartplot.chart.XYLineChart;
import adams.flow.sink.jfreechartplot.dataset.AbstractDatasetGenerator;
import adams.flow.sink.jfreechartplot.dataset.DefaultXY;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Generates a JFreeChart plot and writes the bitmap to a file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: JFreeChartFileWriter
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to write the plot to
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-dataset &lt;adams.flow.sink.jfreechartplot.dataset.AbstractDatasetGenerator&gt; (property: dataset)
 * &nbsp;&nbsp;&nbsp;The dataset generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.jfreechartplot.dataset.DefaultXY
 * </pre>
 * 
 * <pre>-chart &lt;adams.flow.sink.jfreechartplot.chart.AbstractChartGenerator&gt; (property: chart)
 * &nbsp;&nbsp;&nbsp;The chart generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.jfreechartplot.chart.XYLineChart
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the plot.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the plot.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.AbstractImageWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The image writer to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.JAIImageWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JFreeChartFileWriter
  extends AbstractFileWriter {

  private static final long serialVersionUID = -2648121220428217287L;

  /** the dataset generator. */
  protected AbstractDatasetGenerator m_Dataset;

  /** the chart generator. */
  protected AbstractChartGenerator m_Chart;

  /** the width of the plot. */
  protected int m_Width;

  /** the height of the plot. */
  protected int m_Height;

  /** the image writer to use. */
  protected AbstractImageWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a JFreeChart plot and writes the bitmap to a file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dataset", "dataset",
      new DefaultXY());

    m_OptionManager.add(
      "chart", "chart",
      new XYLineChart());

    m_OptionManager.add(
      "width", "width",
      800, -1, null);

    m_OptionManager.add(
      "height", "height",
      600, -1, null);

    m_OptionManager.add(
      "writer", "writer",
      new JAIImageWriter());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The file to write the plot to";
  }

  /**
   * Sets the dataset generator.
   *
   * @param value	the generator
   */
  public void setDataset(AbstractDatasetGenerator value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the dataset generator.
   *
   * @return		the generator
   */
  public AbstractDatasetGenerator getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset generator to use.";
  }

  /**
   * Sets the chart generator.
   *
   * @param value	the generator
   */
  public void setChart(AbstractChartGenerator value) {
    m_Chart = value;
    reset();
  }

  /**
   * Returns the chart generator.
   *
   * @return		the generator
   */
  public AbstractChartGenerator getChart() {
    return m_Chart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String chartTipText() {
    return "The chart generator to use.";
  }

  /**
   * Sets the width of the plot.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the plot.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the plot.";
  }

  /**
   * Sets the height of the plot.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the plot.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the plot.";
  }

  /**
   * Sets the image writer.
   *
   * @param value	the writer
   */
  public void setWriter(AbstractImageWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the image writer.
   *
   * @return		the writer
   */
  public AbstractImageWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The image writer to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "dataset", m_Dataset, ", dataset: ");
    result += QuickInfoHelper.toString(this, "chart", m_Chart, ", chart: ");
    result += QuickInfoHelper.toString(this, "width", m_Width, ", width:");
    result += QuickInfoHelper.toString(this, "height", m_Height, ", height:");
    result += QuickInfoHelper.toString(this, "writer", m_Writer, ", writer: ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;
    Dataset 			dataset;
    JFreeChart			jfreechart;
    BufferedImage		image;
    BufferedImageContainer	cont;

    result = null;

    try {
      sheet      = (SpreadSheet) m_InputToken.getPayload();
      dataset    = m_Dataset.generate(sheet);
      jfreechart = m_Chart.generate(dataset);
      image      = jfreechart.createBufferedImage(m_Width, m_Height);
      cont       = new BufferedImageContainer();
      cont.setImage(image);
      m_Writer.write(m_OutputFile, cont);
    }
    catch (Exception e) {
      result = handleException("Failed to generate plot!", e);
    }

    return result;
  }
}
