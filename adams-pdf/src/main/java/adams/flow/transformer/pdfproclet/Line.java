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
 * Line.java
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
 * Draws a line.
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
 * <pre>-x1 &lt;float&gt; (property: X1)
 * &nbsp;&nbsp;&nbsp;The absolute X1 position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-y1 &lt;float&gt; (property: Y1)
 * &nbsp;&nbsp;&nbsp;The absolute Y1 position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-x2 &lt;float&gt; (property: X2)
 * &nbsp;&nbsp;&nbsp;The absolute X2 position.
 * &nbsp;&nbsp;&nbsp;default: 10.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.001
 * </pre>
 * 
 * <pre>-y2 &lt;float&gt; (property: Y2)
 * &nbsp;&nbsp;&nbsp;The absolute Y2 position.
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
public class Line
  extends AbstractPdfProclet
  implements PdfProcletWithVariableFileExtension {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /** the file extensions. */
  protected BaseString[] m_Extensions;

  /** the absolute X1 position. */
  protected float m_X1;

  /** the absolute Y1 position. */
  protected float m_Y1;

  /** the absolute X2 position. */
  protected float m_X2;

  /** the absolute Y2 position. */
  protected float m_Y2;

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
    return "Draws a line.";
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
      "x1", "X1",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "y1", "Y1",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "x2", "X2",
      10.0f, 0.001f, null);

    m_OptionManager.add(
      "y2", "Y2",
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
   * Sets the absolute X1 position.
   *
   * @param value	the X1 position
   */
  public void setX1(float value) {
    if (getOptionManager().isValid("X1", value)) {
      m_X1 = value;
      reset();
    }
  }

  /**
   * Returns the absolute X1 position.
   *
   * @return		the X1 position
   */
  public float getX1() {
    return m_X1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String X1TipText() {
    return "The absolute X1 position.";
  }

  /**
   * Sets the absolute Y1 position.
   *
   * @param value	the Y1 position
   */
  public void setY1(float value) {
    if (getOptionManager().isValid("Y1", value)) {
      m_Y1 = value;
      reset();
    }
  }

  /**
   * Returns the absolute Y1 position.
   *
   * @return		the Y1 position
   */
  public float getY1() {
    return m_Y1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String Y1TipText() {
    return "The absolute Y1 position.";
  }

  /**
   * Sets the absolute X2 position.
   *
   * @param value	the X2 position
   */
  public void setX2(float value) {
    if (getOptionManager().isValid("X2", value)) {
      m_X2 = value;
      reset();
    }
  }

  /**
   * Returns the absolute X2 position.
   *
   * @return		the X2 position
   */
  public float getX2() {
    return m_X2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String X2TipText() {
    return "The absolute X2 position.";
  }

  /**
   * Sets the absolute Y2 position.
   *
   * @param value	the Y2 position
   */
  public void setY2(float value) {
    if (getOptionManager().isValid("Y2", value)) {
      m_Y2 = value;
      reset();
    }
  }

  /**
   * Returns the absolute Y2 position.
   *
   * @return		the Y2 position
   */
  public float getY2() {
    return m_Y2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String Y2TipText() {
    return "The absolute Y2 position.";
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
    PdfContentByte	cb;

    cb = generator.getWriter().getDirectContent();
    cb.saveState();
    cb.setColorStroke(new BaseColor(m_Color.getRGB()));
    cb.setLineWidth(m_LineWidth);
    cb.moveTo(m_X1, m_Y1);
    cb.lineTo(m_X2, m_Y2);
    cb.stroke();
    cb.restoreState();

    return true;
  }

  /**
   * Whether the processor can handle this particular object.
   *
   * @param generator	the context
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public boolean canProcess(PDFGenerator generator, Object obj) {
    return true;
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param obj		the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, Object obj) throws Exception {
    return doProcess(generator, new File("."));
  }
}
