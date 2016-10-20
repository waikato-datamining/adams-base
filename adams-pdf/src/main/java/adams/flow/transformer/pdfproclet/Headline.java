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
 * Headline.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.io.PdfFont;
import com.itextpdf.text.Paragraph;

import java.awt.Color;
import java.io.File;

/**
 <!-- globalinfo-start -->
 * Adds a simple headline, but no file content.
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
 * <pre>-first-page-only &lt;boolean&gt; (property: firstPageOnly)
 * &nbsp;&nbsp;&nbsp;If set to true, then the headline is only added to the first page.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Headline
  extends AbstractPdfProclet
  implements PdfProcletWithPageBreaks, PdfProcletWithVariableFileExtension {

  /** for serialization. */
  private static final long serialVersionUID = 3962046484864891107L;

  /** whether to add a page-break before adding the file. */
  protected boolean m_PageBreakBefore;

  /** whether to add a page-break after adding the file. */
  protected boolean m_PageBreakAfter;

  /** the number of files per page. */
  protected int m_NumFilesPerPage;

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
      "page-break-before", "pageBreakBefore",
      false);

    m_OptionManager.add(
      "page-break-after", "pageBreakAfter",
      false);

    m_OptionManager.add(
      "num-files", "numFilesPerPage",
      -1, -1, null);

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
   * Whether to add a page break before the file is inserted.
   *
   * @param value 	if true then a page-break is added before the file
   * 			is inserted
   */
  public void setPageBreakBefore(boolean value) {
    m_PageBreakBefore = value;
    reset();
  }

  /**
   * Returns whether a page break is added before the file is inserted.
   *
   * @return 		true if a page break is added before the file is
   * 			inserted
   */
  public boolean getPageBreakBefore() {
    return m_PageBreakBefore;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pageBreakBeforeTipText() {
    return "If true, then a page-break is added before the content of the file is inserted.";
  }

  /**
   * Whether to add a page break after the file is inserted.
   *
   * @param value 	if true then a page-break is added after the file
   * 			is inserted
   */
  public void setPageBreakAfter(boolean value) {
    m_PageBreakAfter = value;
    reset();
  }

  /**
   * Returns whether a page break is added after the file is inserted.
   *
   * @return 		true if a page break is added after the file is
   * 			inserted
   */
  public boolean getPageBreakAfter() {
    return m_PageBreakAfter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pageBreakAfterTipText() {
    return "If true, then a page-break is added after the content of the file is inserted.";
  }

  /**
   * Sets the number of files per page.
   *
   * @param value 	the number of files
   */
  public void setNumFilesPerPage(int value) {
    if (getOptionManager().isValid("numFilesPerPage", value)) {
      m_NumFilesPerPage = value;
      reset();
    }
  }

  /**
   * Returns the number of files to put on a single page.
   *
   * @return 		the number of files
   */
  public int getNumFilesPerPage() {
    return m_NumFilesPerPage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFilesPerPageTipText() {
    return "The number of files to put on a page before adding an automatic page break; use -1 for unlimited.";
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
   * @param generator	the context
   * @param file	the file to check
   * @return		true if the file can be handled
   */
  public boolean canProcess(PDFGenerator generator, File file) {
    if (m_FirstPageOnly) {
      if (generator.getState().numTotalFiles() == 0)
	return super.canProcess(generator, file);
      else
	return false;
    }
    else {
      return super.canProcess(generator, file);
    }
  }

  /**
   * For pre-processing the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean preProcess(PDFGenerator generator, File file) throws Exception {
    boolean	result;

    result = super.preProcess(generator, file);

    if (result) {
      if (m_PageBreakBefore)
	result = generator.newPage();
    }

    return result;
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
    return addElement(generator, new Paragraph(m_Headline.getValue(), m_FontHeadline.toFont(m_ColorHeadline)));
  }

  /**
   * For post-processing the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean postProcess(PDFGenerator generator, File file) throws Exception {
    boolean	result;

    result = super.postProcess(generator, file);

    if (result) {
      if (m_PageBreakAfter || (generator.getState().numCurrentFiles() == m_NumFilesPerPage)) {
        result = generator.getDocument().newPage();
        generator.getState().resetCurrentFiles();
      }
    }

    return result;
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
   * For pre-processing the document.
   *
   * @param generator	the context
   * @param obj 	the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean preProcess(PDFGenerator generator, Object obj) throws Exception {
    boolean	result;

    result = super.preProcess(generator, obj);

    if (result) {
      if (m_PageBreakBefore)
	result = generator.newPage();
    }

    return result;
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param obj 	the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, Object obj) throws Exception {
    return addElement(generator, new Paragraph(m_Headline.getValue(), m_FontHeadline.toFont(m_ColorHeadline)));
  }

  /**
   * For post-processing the document.
   *
   * @param generator	the context
   * @param obj 	the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean postProcess(PDFGenerator generator, Object obj) throws Exception {
    boolean	result;

    result = super.postProcess(generator, obj);

    if (result) {
      if (m_PageBreakAfter || (generator.getState().numCurrentFiles() == m_NumFilesPerPage)) {
        result = generator.getDocument().newPage();
        generator.getState().resetCurrentFiles();
      }
    }

    return result;
  }
}
