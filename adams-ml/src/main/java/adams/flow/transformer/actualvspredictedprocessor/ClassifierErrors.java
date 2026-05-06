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
 * ClassifierErrors.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.actualvspredictedprocessor;

import adams.core.Utils;
import adams.core.net.HtmlUtils;
import adams.data.spreadsheet.SpreadSheet;

import java.awt.Color;

/**
 * Generates a self-contained HTML file displaying a scatter plot of actual vs predicted.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ClassifierErrors
  extends AbstractActualVsPredictedHtmlProcessor {

  private static final long serialVersionUID = -4001003730048047162L;

  /** the circle fill color. */
  protected Color m_CircleFillColor;

  /** the circle size. */
  protected int m_CircleSize;

  /** the border color. */
  protected Color m_BorderColor;

  /** the thickness of the border. */
  protected double m_BorderThickness;

  /** whether to show the legend. */
  protected boolean m_ShowLegend;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a self-contained HTML file displaying a scatter plot of actual vs predicted.\n"
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
      "show-legend", "showLegend",
      true);
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
   * Sets whether to show the legend.
   *
   * @param value	true if to show
   */
  public void setShowLegend(boolean value) {
    m_ShowLegend = value;
    reset();
  }

  /**
   * Returns whether to show the legend.
   *
   * @return		true if to show
   */
  public boolean getShowLegend() {
    return m_ShowLegend;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showLegendTipText() {
    return "If enabled, the legend is displayed.";
  }

  /**
   * Processes the actual vs predicted data and returns
   * the output generated.
   *
   * @param sheet the data to process
   * @return the output
   */
  @Override
  protected String doProcess(SpreadSheet sheet) {
    StringBuilder	result;
    String		xStr;
    String		yStr;
    String 		dataStr;

    xStr    = Utils.arrayToString(getActualNumeric(sheet));
    yStr    = Utils.arrayToString(getPredictedNumeric(sheet));
    dataStr = Utils.arrayToString(getAdditional(sheet));

    result = new StringBuilder();
    result.append("<!DOCTYPE html>\n");
    result.append(generatedByAdams());
    result.append("<html>\n");
    result.append("<head>\n");
    result.append("    <title>").append(m_Title).append("</title>\n");
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
    result.append("            name: 'data',\n");
    result.append("            type: 'scattergl',\n");
    result.append("            showlegend: true,\n");
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
    result.append("            name: 'diagonal',\n");
    result.append("            line: { color: 'red', dash: 'dash', width: 2 },\n");
    result.append("            showlegend: true,\n");
    result.append("            type: 'scatter'\n");
    result.append("        };\n");
    result.append("\n");
    result.append("        var data = [trace1, trace2];\n");
    result.append("\n");
    result.append("        var layout = {\n");
    result.append("            title: '").append(m_Title).append("',\n");
    result.append("            xaxis: { title: 'Actual Values', zeroline: false },\n");
    result.append("            yaxis: { title: 'Predicted Values', zeroline: false },\n");
    result.append("            hovermode: 'closest',\n");
    result.append("            showlegend: ").append(m_ShowLegend).append("\n");
    result.append("        };\n");
    result.append("\n");
    result.append("        Plotly.newPlot('plot', data, layout);\n");
    result.append("    </script>\n");
    result.append("</body>\n");
    result.append("</html>\n");
    result.append("\n");

    return result.toString();
  }
}
