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
 * AbstractStamper.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfstamp;

import adams.core.option.AbstractOptionHandler;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * Ancestor for stampers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractStamper
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -1659556085362111076L;

  /**
   * Hook method for checks before stamping.
   * <p/>
   * Default method only ensures that stamper present.
   *
   * @param stamper	the stamper to check
   */
  protected void check(PdfStamper stamper) {
    if (stamper == null)
      throw new IllegalStateException("No stamper provided!");
  }

  /**
   * Performs the actual stamping.
   *
   * @param stamper	the stamper to use
   */
  protected abstract void doStamp(PdfStamper stamper);

  /**
   * Performs the stamping. Caller must close the stamper.
   *
   * @param stamper	the stamper to use
   */
  public void stamp(PdfStamper stamper) {
    check(stamper);
    doStamp(stamper);
  }
}
