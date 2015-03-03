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
 * AbstractSpreadSheetTransformer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for transformers that work on spreadsheets and also output
 * spreadsheets again.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSpreadSheetTransformer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8602738223834097328L;

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.core.io.SpreadSheet.class
   */
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		adams.core.io.SpreadSheet.class
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }
}
