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
 * AbstractSpreadSheetConversion.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for conversion schemes that transform one spreadsheet into another
 * one.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSpreadSheetConversion
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4733940137387662202L;

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Generates the new spreadsheet from the input.
   * 
   * @param input	the incoming spreadsheet
   * @return		the generated spreadsheet
   * @throws Exception	if conversion fails for some reason
   */
  protected abstract SpreadSheet convert(SpreadSheet input) throws Exception;
  
  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return convert((SpreadSheet) m_Input);
  }
}
