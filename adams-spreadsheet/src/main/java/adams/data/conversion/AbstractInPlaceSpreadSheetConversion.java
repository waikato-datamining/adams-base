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
 * AbstractInPlaceSpreadSheetConversion.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.InPlaceProcessing;

/**
 * Ancestor for spreadsheet conversion that allow working on the incoming data
 * rather than on a copy (to conserve memory).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInPlaceSpreadSheetConversion
  extends AbstractSpreadSheetConversion
  implements InPlaceProcessing {
  
  /** for serialization. */
  private static final long serialVersionUID = -4602098724274537009L;
  
  /** whether to skip creating a copy of the spreadsheet. */
  protected boolean m_NoCopy;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "no-copy", "noCopy",
	    false);
  }

  /**
   * Sets whether to skip creating a copy of the spreadsheet before processing it.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the spreadsheet before processing it.
   *
   * @return		true if copying is skipped
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCopyTipText() {
    return "If enabled, no copy of the spreadsheet is created before processing it.";
  }
}
