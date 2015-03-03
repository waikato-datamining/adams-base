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
 * FileEncodingSupporter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.core.base.BaseCharset;

/**
 * For classes that support file encoding.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface FileEncodingSupporter {
  
  /**
   * Sets the encoding to use.
   * 
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16"
   */
  public void setEncoding(BaseCharset value);
  
  /**
   * Returns the encoding to use.
   * 
   * @return		the encoding, e.g. "UTF-8" or "UTF-16"
   */
  public BaseCharset getEncoding();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText();
}
