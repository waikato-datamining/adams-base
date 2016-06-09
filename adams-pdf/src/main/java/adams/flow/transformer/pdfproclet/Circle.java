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
 * Circle.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.base.BaseString;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfContentByte;

import java.awt.Color;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Draws a circle.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-regexp-filename &lt;adams.core.base.BaseRegExp&gt; (property: regExpFilename)
 * &nbsp;&nbsp;&nbsp;The regular expression that the filename must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-extension &lt;adams.core.base.BaseString&gt; [-extension ...] (property: extensions)
 * &nbsp;&nbsp;&nbsp;The file extension(s) that the processor will be used for.
 * &nbsp;&nbsp;&nbsp;default: *
 * </pre>
 * 
 * <pre>-x &lt;float&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The absolute X position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-y &lt;float&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The absolute Y position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-radius &lt;float&gt; (property: radius)
 * &nbsp;&nbsp;&nbsp;The radius.
 * &nbsp;&nbsp;&nbsp;default: 10.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.001
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color of the rectangle.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-line-width &lt;float&gt; (property: lineWidth)
 * &nbsp;&nbsp;&nbsp;The line width.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.001
 * </pre>
 * 
 * <pre>-fill &lt;boolean&gt; (property: fill)
 * &nbsp;&nbsp;&nbsp;If enabled the shape gets filled with the specified color.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Circle
  extends AbstractPdfProclet
  implements PdfProcletWithVariableFileExtension {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /** the file extensions. */
  protected BaseString[] m_Extensions;

  /** the absolute X position. */
  protected float m_X;

  /** the absolute Y position. */
  protected float m_Y;

  /** the radius. */
  protected float m_Radius;

  /** the color. */
  protected Color m_Color;

  /** the line width. */
  protected float m_LineWidth;

  /** whether to fill the circle. */
  protected boolean m_Fill;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  public String globalInfo() {
    return "Draws a circle.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "extension", "extensions",
      new BaseString[]{new BaseString(MATCH_ALL_EXTENSION)});

    m_OptionManager.add(
      "x", "X",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "y", "Y",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "radius", "radius",
      10.0f, 0.001f, null);

    m_OptionManager.add(
      "color", "color",
      Color.BLACK);

    m_OptionManager.add(
      "line-width", "lineWidth",
      1.0f, 0.001f, null);

    m_OptionManager.add(
      "fill", "fill",
      false);
  }

  /**
   * Returns the extensions that the processor can process.
   *
   * @return		the extensions (no dot)
   */
  public BaseString[] getExtensions() {
    return m_Extensions;
  }

  /**
   * Sets the extensions that the processor can process.
   *
   * @param value	the extensions (no dot)
   */
  public void setExtensions(BaseString[] value) {
    m_Extensions = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extensionsTipText() {
    return "The file extension(s) that the processor will be used for.";
  }

  /**
   * Sets the absolute X position.
   *
   * @param value	the X position
   */
  public void setX(float value) {
    if (getOptionManager().isValid("X", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the absolute X position.
   *
   * @return		the X position
   */
  public float getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The absolute X position.";
  }

  /**
   * Sets the absolute Y position.
   *
   * @param value	the Y position
   */
  public void setY(float value) {
    if (getOptionManager().isValid("Y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the absolute Y position.
   *
   * @return		the Y position
   */
  public float getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The absolute Y position.";
  }

  /**
   * Sets the radius.
   *
   * @param value	the radius
   */
  public void setRadius(float value) {
    if (getOptionManager().isValid("radius", value)) {
      m_Radius = value;
      reset();
    }
  }

  /**
   * Returns the radius.
   *
   * @return		the radius
   */
  public float getRadius() {
    return m_Radius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String radiusTipText() {
    return "The radius.";
  }

  /**
   * Sets the color.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color of the rectangle.";
  }

  /**
   * Sets the line width.
   *
   * @param value	the line width
   */
  public void setLineWidth(float value) {
    if (getOptionManager().isValid("lineWidth", value)) {
      m_LineWidth = value;
      reset();
    }
  }

  /**
   * Returns the line width.
   *
   * @return		the line width
   */
  public float getLineWidth() {
    return m_LineWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lineWidthTipText() {
    return "The line width.";
  }

  /**
   * Sets whether to fill the shape with the specified color.
   *
   * @param value	true if to fill
   */
  public void setFill(boolean value) {
    m_Fill = value;
    reset();
  }

  /**
   * Returns whether to fill the shape with the specified color.
   *
   * @return		true if to fill
   */
  public boolean getFill() {
    return m_Fill;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillTipText() {
    return "If enabled the shape gets filled with the specified color.";
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, File file) throws Exception {
    PdfContentByte	cb;

    cb = generator.getWriter().getDirectContent();
    cb.saveState();
    cb.setColorStroke(new BaseColor(m_Color.getRGB()));
    cb.setColorFill(new BaseColor(m_Color.getRGB()));
    cb.setLineWidth(m_LineWidth);
    cb.circle(m_X, m_Y, m_Radius);
    if (m_Fill)
      cb.fillStroke();
    else
      cb.stroke();
    cb.restoreState();

    return true;
  }
}
