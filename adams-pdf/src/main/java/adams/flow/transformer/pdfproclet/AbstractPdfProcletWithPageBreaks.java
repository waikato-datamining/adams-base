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
 * AbstractPdfProcletWithPageBreaks.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import java.io.File;

/**
 * Ancestor for processors handle page breaks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPdfProcletWithPageBreaks
  extends AbstractPdfProcletWithFilenameOutput
  implements PdfProcletWithPageBreaks {

  /** for serialization. */
  private static final long serialVersionUID = -9041126884910193987L;

  /** whether to add a page-break before adding the file. */
  protected boolean m_PageBreakBefore;

  /** whether to add a page-break after adding the file. */
  protected boolean m_PageBreakAfter;

  /** the number of files per page. */
  protected int m_NumFilesPerPage;

  /**
   * Adds options to the internal list of options.
   */
  @Override
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
}
