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
 * AbstractPageRangeStamper.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfstamp;

import adams.core.Range;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * Ancestor for stampers that work on a range of pages.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPageRangeStamper
  extends AbstractStamper {

  private static final long serialVersionUID = -8844401168882904723L;

  /** the page range to work on. */
  protected Range m_Pages;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "pages", "pages",
      new Range(Range.ALL));
  }

  /**
   * Sets the page range.
   *
   * @param value	the page range
   */
  public void setPages(Range value) {
    m_Pages = value;
    reset();
  }

  /**
   * Returns the page range.
   *
   * @return 		the page range
   */
  public Range getPages() {
    return m_Pages;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String pagesTipText() {
    return "The pages to stamp.";
  }

  /**
   * Performs the actual stamping.
   *
   * @param stamper	the stamper to use
   * @param page	the page to apply the stamp to
   */
  protected abstract void doStamp(PdfStamper stamper, int page);

  /**
   * Performs the actual stamping.
   *
   * @param stamper	the stamper to use
   */
  @Override
  protected void doStamp(PdfStamper stamper) {
    int[]	pages;

    m_Pages.setMax(stamper.getReader().getNumberOfPages());
    pages = m_Pages.getIntIndices();
    for (int page: pages)
      doStamp(stamper, page);
  }
}
