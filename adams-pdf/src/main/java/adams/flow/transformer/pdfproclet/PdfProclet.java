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
 * PdfProclet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.OptionHandler;

import java.io.File;

/**
 * Interface for PDF proclets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13483 $
 */
public interface PdfProclet
  extends OptionHandler {

  /**
   * Returns the extensions that the processor can process.
   *
   * @return		the extensions (no dot)
   */
  public BaseString[] getExtensions();

  /**
   * Sets the regular expression that the filename must match.
   *
   * @param value	the expression
   */
  public void setRegExpFilename(BaseRegExp value);

  /**
   * Returns the regular expression that the filename must match.
   *
   * @return 		the expression
   */
  public BaseRegExp getRegExpFilename();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpFilenameTipText();

  /**
   * Whether the processor can handle this particular file.
   *
   * @param generator	the context
   * @param file	the file to check
   * @return		true if the file can be handled
   */
  public boolean canProcess(PDFGenerator generator, File file);

  /**
   * Processes the given file.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   */
  public boolean process(PDFGenerator generator, File file);
}
