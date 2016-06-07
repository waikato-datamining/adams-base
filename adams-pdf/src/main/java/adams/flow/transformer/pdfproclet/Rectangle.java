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
 * Rectangle.java
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
 * Draws a rectangle.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-add-filename &lt;boolean&gt; (property: addFilename)
 * &nbsp;&nbsp;&nbsp;Whether to add the file name before the actual file content as separate 
 * &nbsp;&nbsp;&nbsp;paragraph.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-font-filename &lt;adams.core.io.PdfFont&gt; (property: fontFilename)
 * &nbsp;&nbsp;&nbsp;The font to use for printing the file name header.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Bold-12
 * </pre>
 * 
 * <pre>-color-filename &lt;java.awt.Color&gt; (property: colorFilename)
 * &nbsp;&nbsp;&nbsp;The color to use for printing the file name header.
 * &nbsp;&nbsp;&nbsp;default: #000000
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
 * <pre>-width &lt;float&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width.
 * &nbsp;&nbsp;&nbsp;default: 10.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.001
 * </pre>
 * 
 * <pre>-height &lt;float&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the rectangle.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rectangle
  extends AbstractPdfProclet
  implements PdfProcletWithVariableFileExtension, PdfProcletWithAbsolutePosition {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /** the file extensions. */
  protected BaseString[] m_Extensions;

  /** the absolute X position. */
  protected float m_X;

  /** the absolute Y position. */
  protected float m_Y;

  /** the width. */
  protected float m_Width;

  /** the height. */
  protected float m_Height;

  /** the color. */
  protected Color m_Color;

  /** the line width. */
  protected float m_LineWidth;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  public String globalInfo() {
    return "Draws a rectangle.";
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
      "width", "width",
      10.0f, 0.001f, null);

    m_OptionManager.add(
      "height", "height",
      10.0f, 0.001f, null);

    m_OptionManager.add(
      "color", "color",
      Color.BLACK);

    m_OptionManager.add(
      "line-width", "lineWidth",
      1.0f, 0.001f, null);
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
   * Sets the width.
   *
   * @param value	the width
   */
  public void setWidth(float value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width.
   *
   * @return		the width
   */
  public float getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width.";
  }

  /**
   * Sets the height.
   *
   * @param value	the height
   */
  public void setHeight(float value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height.
   *
   * @return		the height
   */
  public float getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the rectangle.";
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
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, File file) throws Exception {
    boolean		result;
    PdfContentByte	cb;

    result = addFilename(generator, file);
    if (result) {
      cb = generator.getWriter().getDirectContent();
      cb.saveState();
      cb.setColorStroke(new BaseColor(m_Color.getRGB()));
      cb.setLineWidth(m_LineWidth);
      cb.rectangle(m_X, m_Y, m_X + m_Width, m_Y + m_Height);
      cb.stroke();
      cb.restoreState();
    }

    return result;
  }
}
