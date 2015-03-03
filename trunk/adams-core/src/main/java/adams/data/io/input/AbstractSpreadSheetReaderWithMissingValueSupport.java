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
 * AbstractSpreadSheetReaderWithMissingValue.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for spreadsheet readers that allow the specification of a custom
 * missing value string.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSpreadSheetReaderWithMissingValueSupport
  extends AbstractSpreadSheetReader {
  
  /** for serialization. */
  private static final long serialVersionUID = 8681736902001611911L;
  
  /** The placeholder for missing values. */
  protected String m_MissingValue;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "missing", "missingValue",
	    getDefaultMissingValue());
  }

  /**
   * Returns the default missing value to use.
   * 
   * @return		the default
   */
  protected String getDefaultMissingValue() {
    return SpreadSheet.MISSING_VALUE;
  }
  
  /**
   * Sets the placeholder for missing values.
   *
   * @param value	the placeholder
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
  }

  /**
   * Returns the current placeholder for missing values.
   *
   * @return		the placeholder
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String missingValueTipText() {
    return "The placeholder for missing values.";
  }
}
