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
 * PdfProcletWithAbsolutePosition.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

/**
 * Interface for PDF proclets that allow absolute positioning.
 * From bottom-left corner.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PdfProcletWithPageBreaks
  extends PdfProclet {

  /**
   * Whether to add a page break before the file is inserted.
   *
   * @param value 	if true then a page-break is added before the file
   * 			is inserted
   */
  public void setPageBreakBefore(boolean value);

  /**
   * Returns whether a page break is added before the file is inserted.
   *
   * @return 		true if a page break is added before the file is
   * 			inserted
   */
  public boolean getPageBreakBefore();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pageBreakBeforeTipText();

  /**
   * Whether to add a page break after the file is inserted.
   *
   * @param value 	if true then a page-break is added after the file
   * 			is inserted
   */
  public void setPageBreakAfter(boolean value);

  /**
   * Returns whether a page break is added after the file is inserted.
   *
   * @return 		true if a page break is added after the file is
   * 			inserted
   */
  public boolean getPageBreakAfter();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pageBreakAfterTipText();

  /**
   * Sets the number of files per page.
   *
   * @param value 	the number of files
   */
  public void setNumFilesPerPage(int value);

  /**
   * Returns the number of files to put on a single page.
   *
   * @return 		the number of files
   */
  public int getNumFilesPerPage();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFilesPerPageTipText();
}
