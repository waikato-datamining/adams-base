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
 * PlainText.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PdfFont;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;

import java.awt.Color;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Adds plain-text files line by line.
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
 * <pre>-page-break-before &lt;boolean&gt; (property: pageBreakBefore)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added before the content of the file is inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-page-break-after &lt;boolean&gt; (property: pageBreakAfter)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added after the content of the file is inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-files &lt;int&gt; (property: numFilesPerPage)
 * &nbsp;&nbsp;&nbsp;The number of files to put on a page before adding an automatic page break;
 * &nbsp;&nbsp;&nbsp; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-font-content &lt;adams.core.io.PdfFont&gt; (property: fontContent)
 * &nbsp;&nbsp;&nbsp;The font to use for the file content.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Normal-12
 * </pre>
 * 
 * <pre>-color-content &lt;java.awt.Color&gt; (property: colorContent)
 * &nbsp;&nbsp;&nbsp;The color to use for the content.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-extension &lt;adams.core.base.BaseString&gt; [-extension ...] (property: extensions)
 * &nbsp;&nbsp;&nbsp;The file extension(s) that the processor will be used for.
 * &nbsp;&nbsp;&nbsp;default: txt
 * </pre>
 * 
 * <pre>-use-absolute-position &lt;boolean&gt; (property: useAbsolutePosition)
 * &nbsp;&nbsp;&nbsp;If enabled, the absolute position is used (from bottom-left corner).
 * &nbsp;&nbsp;&nbsp;default: false
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlainText
  extends AbstractPdfProcletWithPageBreaks
  implements PdfProcletWithVariableFileExtension, PdfProcletWithOptionalAbsolutePosition {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /** the font for the content. */
  protected PdfFont m_FontContent;

  /** the color for the content. */
  protected Color m_ColorContent;

  /** the file extensions. */
  protected BaseString[] m_Extensions;

  /** whether to use absolute position. */
  protected boolean m_UseAbsolutePosition;

  /** the absolute X position. */
  protected float m_X;

  /** the absolute Y position. */
  protected float m_Y;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  @Override
  public String globalInfo() {
    return "Adds plain-text files line by line.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "font-content", "fontContent",
      new PdfFont(PdfFont.HELVETICA, PdfFont.NORMAL, 12.0f));

    m_OptionManager.add(
      "color-content", "colorContent",
      Color.BLACK);

    m_OptionManager.add(
      "extension", "extensions",
      new BaseString[]{new BaseString("txt")});

    m_OptionManager.add(
      "use-absolute-position", "useAbsolutePosition",
      false);

    m_OptionManager.add(
      "x", "X",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "y", "Y",
      0.0f, 0.0f, null);
  }

  /**
   * Sets the font to use for adding the content.
   *
   * @param value	the font
   */
  public void setFontContent(PdfFont value) {
    m_FontContent = value;
    reset();
  }

  /**
   * Returns the font to use for adding the content.
   *
   * @return 		the font
   */
  public PdfFont getFontContent() {
    return m_FontContent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontContentTipText() {
    return "The font to use for the file content.";
  }

  /**
   * Sets the color to use for the content.
   *
   * @param value	the color
   */
  public void setColorContent(Color value) {
    m_ColorContent = value;
    reset();
  }

  /**
   * Returns the color to use for the content.
   *
   * @return 		the color
   */
  public Color getColorContent() {
    return m_ColorContent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorContentTipText() {
    return "The color to use for the content.";
  }

  /**
   * Returns the extensions that the processor can process.
   *
   * @return		the extensions (no dot)
   */
  @Override
  public BaseString[] getExtensions() {
    return m_Extensions;
  }

  /**
   * Sets the extensions that the processor can process.
   *
   * @param value	the extensions (no dot)
   */
  @Override
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
  @Override
  public String extensionsTipText() {
    return "The file extension(s) that the processor will be used for.";
  }

  /**
   * Sets whether to use absolute positioning (from bottom-left corner).
   *
   * @param value	true if absolute
   */
  public void setUseAbsolutePosition(boolean value) {
    m_UseAbsolutePosition = value;
    reset();
  }

  /**
   * Returns whether absolute positioning is used (from bottom-left corner).
   *
   * @return		true if absolute
   */
  public boolean getUseAbsolutePosition() {
    return m_UseAbsolutePosition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAbsolutePositionTipText() {
    return "If enabled, the absolute position is used (from bottom-left corner).";
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
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  @Override
  protected boolean doProcess(PDFGenerator generator, File file) throws Exception {
    boolean		result;
    List<String>	paragraphs;
    ColumnText		ct;

    result = addFilename(generator, file);
    if (result) {
      paragraphs = FileUtils.loadFromFile(file);
      if (m_UseAbsolutePosition) {
	ct = addColumnTextAt(generator, m_X, m_Y);
	ct.addElement(new Paragraph(Utils.flatten(paragraphs, "\n"), m_FontContent.toFont(m_ColorContent)));
	ct.go();
	generator.getState().contentAdded();
      }
      else {
	result = addElement(
	  generator,
	  new Paragraph(Utils.flatten(paragraphs, "\n"), m_FontContent.toFont(m_ColorContent)));
      }
    }

    return result;
  }
}
