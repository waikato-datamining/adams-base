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
 * AbstractMetaDataExtractor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.metadata;

import java.io.File;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for algorithms that extract meta-data from image files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 78 $
 */
public abstract class AbstractMetaDataExtractor
  extends AbstractOptionHandler 
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4035633099365011707L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }
  
  /**
   * Checks whether the input can be used.
   * <p/>
   * Default implementation ensures that the file is available.
   * 
   * @param file	the file to check
   */
  protected void check(File file) {
    if (file == null)
      throw new IllegalArgumentException("No file provided!");
    if (!file.exists())
      throw new IllegalArgumentException("File '" + file + "' does not exist!");
    if (file.isDirectory())
      throw new IllegalArgumentException("File '" + file + "' points to a directory!");
  }
  
  /**
   * Performs the actual meta-data extraction.
   * 
   * @param file	the file to process
   * @return		the meta-data
   * @throws Exception	if extraction fails
   */
  protected abstract SpreadSheet doExtract(File file) throws Exception;
  
  /**
   * Extracts the meta-data from the image.
   * 
   * @param file	the file to process
   * @return		the meta-data
   * @throws Exception	if extraction fails
   */
  public SpreadSheet extract(File file) throws Exception {
    SpreadSheet	result;
    
    check(file);
    result = doExtract(file);
    
    return result;
  }
}
