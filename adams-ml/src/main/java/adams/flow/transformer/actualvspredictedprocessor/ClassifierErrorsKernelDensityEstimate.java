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
 * ClassifierErrorsKernelDensityEstimate.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.actualvspredictedprocessor;

import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.ColorHelper;
import adams.gui.visualization.core.KernelDensityEstimation;
import adams.gui.visualization.core.KernelDensityEstimation.Mode;
import adams.gui.visualization.core.KernelDensityEstimation.RenderState;

import java.awt.Color;

/**
 * Generates a self-contained HTML file displaying a kernel density based scatter plot of actual vs predicted.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ClassifierErrorsKernelDensityEstimate
  extends AbstractActualVsPredictedHtmlProcessor {

  private static final long serialVersionUID = -1672249549785770694L;

  /**
   * The type of color palette to use.
   */
  public enum ColorPaletteType {
    PREDEFINED,
    BICOLOR,
  }

  /** the number of bins to generate on X and Y. */
  protected int m_NumBins;

  /** the bandwidth. */
  protected double m_Bandwidth;

  /** the type of color palette to use. */
  protected ColorPaletteType m_ColorPaletteType;

  /** the color palette. */
  protected String m_ColorPalettePredefined;

  /** the start color. */
  protected Color m_ColorStart;

  /** the end color. */
  protected Color m_ColorEnd;

  /** the opacity to use. */
  protected double m_Opacity;

  /** the circle size. */
  protected int m_CircleSize;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a self-contained HTML file displaying a kernel density based scatter plot of actual vs predicted.\n"
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
      "num-bins", "numBins",
      100, 1, null);

    m_OptionManager.add(
      "bandwidth", "bandwidth",
      1.0, 0.0, null);

    m_OptionManager.add(
      "color-palette-type", "colorPaletteType",
      ColorPaletteType.PREDEFINED);

    m_OptionManager.add(
      "color-palette-predefined", "colorPalettePredefined",
      "Blues");

    m_OptionManager.add(
      "color-start", "colorStart",
      Color.WHITE);

    m_OptionManager.add(
      "color-end", "colorEnd",
      Color.BLUE);

    m_OptionManager.add(
      "opacity", "opacity",
      0.8, 0.0, 1.0);

    m_OptionManager.add(
      "circle-size", "circleSize",
      10, 1, null);
  }

  /**
   * Sets the number of bins to generate on X and Y axis.
   *
   * @param value	the number of bins
   */
  public void setNumBins(int value) {
    if (getOptionManager().isValid("numBins", value)) {
      m_NumBins = value;
      reset();
    }
  }

  /**
   * Returns the number of bins to generate on X and Y axis.
   *
   * @return		the number of bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to generate on X and Y axis.";
  }

  /**
   * Sets the bandwidth for kernel density estimates.
   *
   * @param value	the bandwidth
   */
  public void setBandwidth(double value) {
    if (getOptionManager().isValid("bandwidth", value)) {
      m_Bandwidth = value;
      reset();
    }
  }

  /**
   * Returns the bandwidth for kernel density estimates.
   *
   * @return		the bandwidth
   */
  public double getBandwidth() {
    return m_Bandwidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bandwidthTipText() {
    return "The bandwidth for kernel density estimates.";
  }

  /**
   * Sets what color palette type to use.
   *
   * @param value	the type
   */
  public void setColorPaletteType(ColorPaletteType value) {
    m_ColorPaletteType = value;
    reset();
  }

  /**
   * Returns what color palette type to use.
   *
   * @return		the type
   */
  public ColorPaletteType getColorPaletteType() {
    return m_ColorPaletteType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorPaletteTypeTipText() {
    return "Determines what color palette type to use, e.g., a predefined one.";
  }

  /**
   * Sets the color palette to use.
   *
   * @param value	the palette
   */
  public void setColorPalettePredefined(String value) {
    m_ColorPalettePredefined = value;
    reset();
  }

  /**
   * Returns the color palette to use.
   *
   * @return		the palette
   */
  public String getColorPalettePredefined() {
    return m_ColorPalettePredefined;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorPalettePredefinedTipText() {
    return "The name of the predefined color palette to use; see https://plotly.com/python/builtin-colorscales/";
  }

  /**
   * Sets the starting color for the color palette ({@link ColorPaletteType#BICOLOR}).
   *
   * @param value	the color
   */
  public void setColorStart(Color value) {
    m_ColorStart = value;
    reset();
  }

  /**
   * Returns the starting color for the color palette ({@link ColorPaletteType#BICOLOR}).
   *
   * @return		the color
   */
  public Color getColorStart() {
    return m_ColorStart;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorStartTipText() {
    return "The starting color when using " + ColorPaletteType.BICOLOR + ".";
  }

  /**
   * Sets the end color for the color palette ({@link ColorPaletteType#BICOLOR}).
   *
   * @param value	the color
   */
  public void setColorEnd(Color value) {
    m_ColorEnd = value;
    reset();
  }

  /**
   * Returns the end color for the color palette ({@link ColorPaletteType#BICOLOR}).
   *
   * @return		the color
   */
  public Color getColorEnd() {
    return m_ColorEnd;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorEndTipText() {
    return "The end color when using " + ColorPaletteType.BICOLOR + ".";
  }

  /**
   * Sets the opacity to use.
   *
   * @param value	the opacity
   */
  public void setOpacity(double value) {
    if (getOptionManager().isValid("opacity", value)) {
      m_Opacity = value;
      reset();
    }
  }

  /**
   * Returns the opacity to use.
   *
   * @return		the opacity
   */
  public double getOpacity() {
    return m_Opacity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String opacityTipText() {
    return "The opacity to use for the color palette.";
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
   * Processes the actual vs predicted data and returns
   * the output generated.
   *
   * @param sheet the data to process
   * @return the output
   */
  @Override
  protected String doProcess(SpreadSheet sheet) {
    StringBuilder		result;
    String			xStr;
    String			yStr;
    String 			dataStr;
    KernelDensityEstimation 	kde;
    RenderState 		state;
    double[]			actual;
    double[]			predicted;
    double[]			density;
    String			densityStr;
    int				i;

    actual     = getActualNumeric(sheet);
    xStr       = Utils.arrayToString(actual);
    predicted  = getPredictedNumeric(sheet);
    yStr       = Utils.arrayToString(predicted);
    dataStr    = Utils.arrayToString(getAdditional(sheet));

    kde        = new KernelDensityEstimation();
    kde.setMode(Mode.KDE);
    kde.setNumBins(m_NumBins);
    kde.setBandwidth(m_Bandwidth);
    state     = kde.calculate(actual, predicted);
    density   = new double[actual.length];
    for (i = 0; i < actual.length; i++)
      density[i] = state.getDensity(actual[i], predicted[i]);
    densityStr = Utils.arrayToString(density);

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
    result.append("        var density = [").append(densityStr).append("];\n");
    result.append("\n");
    result.append("        var trace1 = {\n");
    result.append("            x: actual,\n");
    result.append("            y: predicted,\n");
    result.append("            text: data.map(id => id),\n");
    result.append("            mode: 'markers',\n");
    result.append("            name: '',\n");  // to suppress "trace 0"
    result.append("            type: 'scattergl',\n");
    result.append("            marker: {\n");
    result.append("                size: ").append(m_CircleSize).append(",\n");
    result.append("                color: density,\n");
    switch (m_ColorPaletteType) {
      case PREDEFINED:
	result.append("                colorscale: '").append(m_ColorPalettePredefined).append("',\n");
	break;
      case BICOLOR:
	result.append("                colorscale: [\n");
	result.append("                    [0, '").append(ColorHelper.toHex(m_ColorStart)).append("'],\n");
	result.append("                    [1, '").append(ColorHelper.toHex(m_ColorEnd)).append("'],\n");
	result.append("                ],\n");
	break;
      default:
	throw new IllegalStateException("Unhandled color palette type: " + m_ColorPaletteType);
    }
    result.append("                reversescale: true,\n");
    result.append("                showscale: true,\n");
    result.append("                colorbar: { title: '' },\n");
    result.append("                opacity: ").append(m_Opacity).append(",\n");
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
    result.append("            name: '',\n");
    result.append("            line: { color: 'red', dash: 'dash', width: 2 },\n");
    result.append("            showlegend: false,\n");
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
}
