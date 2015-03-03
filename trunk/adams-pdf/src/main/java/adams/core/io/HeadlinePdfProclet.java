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
 * HeadlinePdfProclet.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import java.awt.Color;
import java.io.File;

import adams.core.base.BaseString;
import adams.core.base.BaseText;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;

/**
 <!-- globalinfo-start -->
 * Adds a simple headline, but no file content.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-page-break-before (property: pageBreakBefore)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added before the content of the file is inserted.
 * </pre>
 *
 * <pre>-page-break-after (property: pageBreakAfter)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added after the content of the file is inserted.
 * </pre>
 *
 * <pre>-num-files &lt;int&gt; (property: numFilesPerPage)
 * &nbsp;&nbsp;&nbsp;The number of files to put on a page before adding an automatic page break;
 * &nbsp;&nbsp;&nbsp; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-add-filename (property: addFilename)
 * &nbsp;&nbsp;&nbsp;Whether to add the file name before the actual file content as separate
 * &nbsp;&nbsp;&nbsp;paragraph.
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
 * <pre>-headline &lt;adams.core.base.BaseText&gt; (property: headline)
 * &nbsp;&nbsp;&nbsp;The headline to add, can be multi-line.
 * &nbsp;&nbsp;&nbsp;default: Fill in headline
 * </pre>
 *
 * <pre>-font-headline &lt;adams.core.io.PdfFont&gt; (property: fontHeadline)
 * &nbsp;&nbsp;&nbsp;The font to use for the headline.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Bold-14
 * </pre>
 *
 * <pre>-color-headline &lt;java.awt.Color&gt; (property: colorHeadline)
 * &nbsp;&nbsp;&nbsp;The color to use for the headline.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-extension &lt;adams.core.base.BaseString&gt; [-extension ...] (property: extensions)
 * &nbsp;&nbsp;&nbsp;The file extension(s) that the processor will be used for.
 * &nbsp;&nbsp;&nbsp;default: *
 * </pre>
 *
 * <pre>-first-page-only (property: firstPageOnly)
 * &nbsp;&nbsp;&nbsp;If set to true, then the headline is only added to the first page.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeadlinePdfProclet
  extends AbstractPdfProclet
  implements VariableFileExtensionPdfProclet {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /** the headline to add. */
  protected BaseText m_Headline;

  /** the font for the headline. */
  protected PdfFont m_FontHeadline;

  /** the color for the headline. */
  protected Color m_ColorHeadline;

  /** the file extensions. */
  protected BaseString[] m_Extensions;

  /** whether to add it only on the first page. */
  protected boolean m_FirstPageOnly;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  public String globalInfo() {
    return "Adds a simple headline, but no file content.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "headline", "headline",
	    new BaseText("Fill in headline"));

    m_OptionManager.add(
	    "font-headline", "fontHeadline",
	    new PdfFont(PdfFont.HELVETICA, PdfFont.BOLD, 14.0f));

    m_OptionManager.add(
	    "color-headline", "colorHeadline",
	    Color.BLACK);

    m_OptionManager.add(
	    "extension", "extensions",
	    new BaseString[]{new BaseString(MATCH_ALL_EXTENSION)});

    m_OptionManager.add(
	    "first-page-only", "firstPageOnly",
	    false);
  }

  /**
   * Sets the headline to add.
   *
   * @param value	the headline
   */
  public void setHeadline(BaseText value) {
    m_Headline = value;
    reset();
  }

  /**
   * Returns the headline to add.
   *
   * @return 		the headline
   */
  public BaseText getHeadline() {
    return m_Headline;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headlineTipText() {
    return "The headline to add, can be multi-line.";
  }

  /**
   * Sets the font to use for the headline.
   *
   * @param value	the font
   */
  public void setFontHeadline(PdfFont value) {
    m_FontHeadline = value;
    reset();
  }

  /**
   * Returns the font to use for the headline.
   *
   * @return 		the font
   */
  public PdfFont getFontHeadline() {
    return m_FontHeadline;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontHeadlineTipText() {
    return "The font to use for the headline.";
  }

  /**
   * Sets the color to use for the headline.
   *
   * @param value	the color
   */
  public void setColorHeadline(Color value) {
    m_ColorHeadline = value;
    reset();
  }

  /**
   * Returns the color to use for the headline.
   *
   * @return 		the color
   */
  public Color getColorHeadline() {
    return m_ColorHeadline;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorHeadlineTipText() {
    return "The color to use for the headline.";
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
   * Sets the whether to add the headline only on the first page.
   *
   * @param value	if true then the headline is only added on the first page
   */
  public void setFirstPageOnly(boolean value) {
    m_FirstPageOnly = value;
    reset();
  }

  /**
   * Returns the whether to add the headline only on the first page.
   *
   * @return 		true if the headline is only on the first page
   */
  public boolean getFirstPageOnly() {
    return m_FirstPageOnly;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstPageOnlyTipText() {
    return "If set to true, then the headline is only added to the first page.";
  }

  /**
   * Whether the processor can handle this particular file.
   *
   * @param state	the document state
   * @param file	the file to check
   * @return		true if the file can be handled
   */
  public boolean canProcess(DocumentState state, File file) {
    if (m_FirstPageOnly) {
      if (state.numTotalFiles() == 0)
	return super.canProcess(state, file);
      else
	return false;
    }
    else {
      return super.canProcess(state, file);
    }
  }

  /**
   * The actual processing of the document.
   *
   * @param doc		the PDF document to add the file content to
   * @param state	the current document state
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(Document doc, DocumentState state, File file) throws Exception {
    boolean	result;
    String[]	paragraphs;
    int		i;

    result = addFilename(doc, state, file);
    if (!result)
      return result;

    paragraphs = m_Headline.getValue().split("\n");
    for (i = 0; i < paragraphs.length; i++) {
      result = doc.add(new Paragraph(paragraphs[i], m_FontHeadline.toFont(m_ColorHeadline)));
      if (result)
	state.contentAdded();
      else
	break;
    }

    return result;
  }
}
