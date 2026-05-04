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
 * ClassifierErrorsHTML.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.core.Shortening;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.net.HtmlUtils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BrowserHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MultiPagePane;
import adams.gui.core.ParameterPanel;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import com.github.fracpete.javautils.enumerate.Enumerated;
import weka.classifiers.Evaluation;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Generates a self-contained HTML file displaying a scatter plot of actual vs predicted.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ClassifierErrorsHTML
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = 3326496263530951885L;

  /** the circle fill color. */
  protected Color m_CircleFillColor;

  /** the circle size. */
  protected int m_CircleSize;

  /** the border color. */
  protected Color m_BorderColor;

  /** the thickness of the border. */
  protected double m_BorderThickness;

  /** the output file to generate. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a self-contained HTML file displaying a scatter plot of actual vs predicted.\n"
      + "When not explicitly specifying a file name, one will get generated automatically using the "
      + "users temp directory. These files will get automatically deleted when application/JVM shut "
      + "down in a controlled manner.\n"
      + "Additional attributes can be associated with the data points and are displayed when hovering "
      + "over a data point in the plot. The format for the hover display is:\n"
      + "Actual: ...\n"
      + "Predicted: ...\n"
      + "Data: <InstIndex>: att1,att2,...";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "circle-fill-color", "circleFillColor",
      new Color(0, 0, 255, 127));

    m_OptionManager.add(
      "circle-size", "circleSize",
      10, 1, null);

    m_OptionManager.add(
      "border-color", "borderColor",
      new Color(0, 0, 255, 127));

    m_OptionManager.add(
      "border-thickness", "borderThickness",
      0.4, 0.0, null);

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile("."));
  }

  /**
   * Sets the fill color of the circle.
   *
   * @param value	the color
   */
  public void setCircleFillColor(Color value) {
    m_CircleFillColor = value;
    reset();
  }

  /**
   * Returns the fill color of the circle.
   *
   * @return		the color
   */
  public Color getCircleFillColor() {
    return m_CircleFillColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String circleFillColorTipText() {
    return "The fill color to use for circles.";
  }

  /**
   * Sets the size of the circles.
   *
   * @param value	the size
   */
  public void setCircleSize(int value) {
    if (getOptionManager().isValid("circleSize", value)) {
      m_CircleSize = value;
      reset();
    }
  }

  /**
   * Returns the size of the circles.
   *
   * @return		the size
   */
  public int getCircleSize() {
    return m_CircleSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String circleSizeTipText() {
    return "The size of the circles.";
  }

  /**
   * Sets the border color of the circle.
   *
   * @param value	the color
   */
  public void setBorderColor(Color value) {
    m_BorderColor = value;
    reset();
  }

  /**
   * Returns the border color of the circle.
   *
   * @return		the color
   */
  public Color getBorderColor() {
    return m_BorderColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String borderColorTipText() {
    return "The border color to use for circles.";
  }

  /**
   * Sets the thickness of the circle border.
   *
   * @param value	the thickness
   */
  public void setBorderThickness(double value) {
    if (getOptionManager().isValid("borderThickness", value)) {
      m_BorderThickness = value;
      reset();
    }
  }

  /**
   * Returns the thickness of the circle border.
   *
   * @return		the thickness
   */
  public double getBorderThickness() {
    return m_BorderThickness;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String borderThicknessTipText() {
    return "The thickness of the circle border.";
  }

  /**
   * Sets the output file to generate.
   *
   * @param value	the output, automatically generated if pointing to a dir
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file to generate.
   *
   * @return		the output, automatically generated if pointing to a dir
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The output file to generate, gets automatically determined if pointing to a directory.";
  }

  /**
   * The title to use for the tab.
   *
   * @return the title
   */
  @Override
  public String getTitle() {
    return "Errors (HTML)";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item the item to check
   * @return true if output can be generated
   */
  @Override
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation()
	     && (item.getEvaluation().predictions() != null)
	     && (item.getEvaluation().getHeader() != null)
	     && (item.getEvaluation().getHeader().classAttribute().isNumeric());
  }

  /**
   * Generates the HTML from the data.
   *
   * @param x		the actual values
   * @param y		the predictions
   * @param data	the associated data strings
   * @return		the HTML
   */
  protected String generateHtml(double[] x, double[] y, String[] data, String title) {
    StringBuilder	result;
    String		xStr;
    String		yStr;
    String 		dataStr;

    xStr    = Utils.arrayToString(x);
    yStr    = Utils.arrayToString(y);
    dataStr = Utils.arrayToString(data);

    result = new StringBuilder();
    result.append("<!DOCTYPE html>\n");
    result.append("<html>\n");
    result.append("<head>\n");
    result.append("    <title>").append(title).append("</title>\n");
    result.append("    <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>\n");
    result.append("    <style>\n");
    result.append("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
    result.append("        #plot { width: 100%; height: 750px; }\n");
    result.append("    </style>\n");
    result.append("</head>\n");
    result.append("<body>\n");
    result.append("    <div id=\"plot\"></div>\n");
    result.append("    <script>\n");
    result.append("        var actual = [").append(xStr).append("];\n");
    result.append("        var predicted = [").append(yStr).append("];\n");
    result.append("        var data = [").append(dataStr).append("];\n");
    result.append("\n");
    result.append("        var trace1 = {\n");
    result.append("            x: actual,\n");
    result.append("            y: predicted,\n");
    result.append("            text: data.map(id => id),\n");
    result.append("            mode: 'markers',\n");
    result.append("            name: '',\n");  // to suppress "trace 0"
    result.append("            type: 'scattergl',\n");
    result.append("            marker: {\n");
    result.append("                color: '").append(HtmlUtils.rgba(m_CircleFillColor)).append("',\n");
    result.append("                size: ").append(m_CircleSize).append(",\n");
    result.append("                line: {\n");
    result.append("                    color: '").append(HtmlUtils.rgba(m_BorderColor)).append("',\n");
    result.append("                    width: ").append(m_BorderThickness).append("\n");
    result.append("                }\n");
    result.append("            },\n");
    result.append("            hovertemplate: '<b>Actual:</b> %{x}<br>' +\n");
    result.append("                          '<b>Predicted:</b> %{y}<br>' +\n");
    result.append("                          '<b>Data:</b> %{text}'\n");
    result.append("        };\n");
    result.append("\n");
    result.append("        // Identity line (y=x)\n");
    result.append("        var minVal = Math.min(...actual, ...predicted);\n");
    result.append("        var maxVal = Math.max(...actual, ...predicted);\n");
    result.append("        var trace2 = {\n");
    result.append("            x: [minVal, maxVal],\n");
    result.append("            y: [minVal, maxVal],\n");
    result.append("            mode: 'lines',\n");
    result.append("            name: 'Ideal (y=x)',\n");
    result.append("            line: { color: 'red', dash: 'dash', width: 2 },\n");
    result.append("            type: 'scatter'\n");
    result.append("        };\n");
    result.append("\n");
    result.append("        var data = [trace1, trace2];\n");
    result.append("\n");
    result.append("        var layout = {\n");
    result.append("            title: '").append(title).append("',\n");
    result.append("            xaxis: { title: 'Actual Values', zeroline: false },\n");
    result.append("            yaxis: { title: 'Predicted Values', zeroline: false },\n");
    result.append("            hovermode: 'closest',\n");
    result.append("            showlegend: true\n");
    result.append("        };\n");
    result.append("\n");
    result.append("        Plotly.newPlot('plot', data, layout);\n");
    result.append("    </script>\n");
    result.append("</body>\n");
    result.append("</html>\n");
    result.append("\n");

    return result.toString();
  }


  /**
   * Generates a plot actor from the evaluation.
   *
   * @param index 		the 0-based fold index, -1 if not for fold
   * @param eval		the evaluation to use
   * @param originalIndices 	the original indices, can be null
   * @param additionalAttributes 	the additional attribute to use, can be null
   * @param errors 		for collecting errors
   * @return			the generated panel, null if failed to generate
   */
  protected ComponentContentPanel createOutput(int index, ResultItem item, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, MessageCollection errors) {
    SpreadSheet		sheet;
    Row			row;
    ParameterPanel	paramPanel;
    JPanel		panel;
    FileChooserPanel	fileChooser;
    BaseButton		buttonOpen;
    double[] 		actual;
    double[] 		predicted;
    String[] 		data;
    String		title;
    String		html;
    int			i;
    int			n;
    File 		outputFile;
    final String	url;
    StringBuilder 	cell;

    sheet = PredictionHelper.toSpreadSheet(this, errors, eval, originalIndices, additionalAttributes, false);
    if (sheet == null) {
      if (errors.isEmpty())
	errors.add("Failed to generate prediction!");
      return null;
    }

    actual    = SpreadSheetUtils.getNumericColumn(sheet, 0);
    predicted = SpreadSheetUtils.getNumericColumn(sheet, 1);
    if (sheet.getColumnCount() > 3) {
      data = new String[sheet.getRowCount()];
      for (i = 0; i < sheet.getRowCount(); i++) {
	row = sheet.getRow(i);
	cell = new StringBuilder("\"").append(row.getCell(2).getContent()).append(":");  // instance index
	for (n = 3; n < sheet.getColumnCount(); n++) {
	  if (n > 3)
	    cell.append(",");
	  cell.append(row.getCell(n).getContent().replace("\"", ""));
	}
	cell.append("\"");
	data[i] = cell.toString();
      }
    }
    else {
      data = new String[sheet.getRowCount()];
      for (i = 0; i < data.length; i++)
	data[i] = "" + (i+1);
    }
    title = Shortening.shortenEnd(item.getEvaluation().getHeader().relationName(), 40);
    if (index > -1)
      title += " (" + (index+1) + ")";
    html = generateHtml(actual, predicted, data, title);

    if (m_OutputFile.isDirectory()) {
      outputFile = TempUtils.createTempFile("adams-", ".html");
      outputFile.deleteOnExit();
    }
    else {
      outputFile = m_OutputFile;
    }
    if (index > -1)
      outputFile = FileUtils.replaceExtension(outputFile, "-" + (index+1) + ".html");

    if (!FileUtils.writeToFile(outputFile.getAbsolutePath(), html))
      return null;

    paramPanel = new ParameterPanel();

    fileChooser = new FileChooserPanel();
    fileChooser.setCurrent(outputFile);
    fileChooser.setEnabled(false);
    paramPanel.addParameter("Output file", fileChooser);

    url = outputFile.toURI().toString();
    buttonOpen = new BaseButton(ImageManager.getIcon("browser"));
    buttonOpen.addActionListener((ActionEvent e) -> BrowserHelper.openURL(url));
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.add(buttonOpen);
    paramPanel.addParameter("Open in browser", panel);

    return new ComponentContentPanel(paramPanel, true);
  }

  /**
   * Generates output from the item.
   *
   * @param item   the item to generate output for
   * @param errors for collecting error messages
   * @return the output component, null if failed to generate
   */
  @Override
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    MultiPagePane multiPage;

    if (item.hasFoldEvaluations()) {
      multiPage = newMultiPagePane(item);
      addPage(multiPage, "Full", createOutput(-1, item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors), 0);
      for (Enumerated<Evaluation> eval: enumerate(item.getFoldEvaluations()))
	addPage(multiPage, "Fold " + (eval.index + 1), createOutput(eval.index, item, item.getFoldEvaluation(eval.index), item.getFoldOriginalIndices(eval.index), item.getAdditionalAttributes(), errors), eval.index + 1);
      if (multiPage.getPageCount() > 0)
	multiPage.setSelectedIndex(0);
      return multiPage;
    }
    else {
      return createOutput(-1, item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors);
    }
  }
}
