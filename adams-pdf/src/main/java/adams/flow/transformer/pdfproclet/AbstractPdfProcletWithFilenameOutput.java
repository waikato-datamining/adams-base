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
 * AbstractPdfProcletWithFilenameOutput.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.io.PdfFont;
import com.itextpdf.text.Paragraph;

import java.awt.Color;
import java.io.File;

/**
 * Abstract ancestor for processors that can add the filename as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPdfProcletWithFilenameOutput
  extends AbstractPdfProclet
  implements PdfProcletWithFilenameOutput {

  /** for serialization. */
  private static final long serialVersionUID = -9041126884910193987L;

  /** add the filename as header. */
  protected boolean m_AddFilename;

  /** the font for the filename header. */
  protected PdfFont m_FontFilename;

  /** the color for the filename header. */
  protected Color m_ColorFilename;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "add-filename", "addFilename",
      false);

    m_OptionManager.add(
      "font-filename", "fontFilename",
      new PdfFont(PdfFont.HELVETICA, PdfFont.BOLD, 12.0f));

    m_OptionManager.add(
      "color-filename", "colorFilename",
      Color.BLACK);
  }

  /**
   * Sets whether to output the filename as well.
   *
   * @param value	if true then the filename gets added as well
   */
  public void setAddFilename(boolean value) {
    m_AddFilename = value;
    reset();
  }

  /**
   * Returns whether to output the filename as well.
   *
   * @return 		true if the filename gets added as well
   */
  public boolean getAddFilename() {
    return m_AddFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addFilenameTipText() {
    return "Whether to add the file name before the actual file content as separate paragraph.";
  }

  /**
   * Sets the font to use for adding the filename header.
   *
   * @param value	the font
   */
  public void setFontFilename(PdfFont value) {
    m_FontFilename = value;
    reset();
  }

  /**
   * Returns the font to use for adding the filename header.
   *
   * @return 		the font
   */
  public PdfFont getFontFilename() {
    return m_FontFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontFilenameTipText() {
    return "The font to use for printing the file name header.";
  }

  /**
   * Sets the color to use for adding the filename header.
   *
   * @param value	the color
   */
  public void setColorFilename(Color value) {
    m_ColorFilename = value;
    reset();
  }

  /**
   * Returns the color to use for adding the filename header.
   *
   * @return 		the color
   */
  public Color getColorFilename() {
    return m_ColorFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorFilenameTipText() {
    return "The color to use for printing the file name header.";
  }

  /**
   * Adds the filename to the page as header, if necessary.
   *
   * @param generator	the context
   * @param file	the plain text file
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean addFilename(PDFGenerator generator, File file) throws Exception {
    boolean	result;

    result = true;

    // add filename?
    if (m_AddFilename)
      result = addElement(
	generator,
	new Paragraph(file.getName() + "\n", m_FontFilename.toFont(m_ColorFilename)));

    return result;
  }
}
