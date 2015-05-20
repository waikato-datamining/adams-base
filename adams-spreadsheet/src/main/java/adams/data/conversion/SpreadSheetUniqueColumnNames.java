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
 * SpreadSheetUniqueColumnNames.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.HashSet;

import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Ensures that column names are unique. Appends '-X' to the name, with X being a number.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-no-copy (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetUniqueColumnNames
  extends AbstractInPlaceSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4937434241898052790L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ensures that column names are unique. Appends '-X' to the name, with X being a number.";
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet		result;
    HashSet<String>	names;
    int			i;
    String		base;
    String		name;
    int			count;

    if (m_NoCopy)
      result = input;
    else
      result = input.getClone();
    
    names = new HashSet<String>();
    for (i = 0; i < result.getColumnCount(); i++) {
      base = result.getHeaderRow().getCell(i).getContent();
      if (names.contains(base)) {
	count = 1;
	do {
	  count++;
	  name = base + "-" + count;
	}
	while (names.contains(name));
	if (isLoggingEnabled())
	  getLogger().info("column #" + (i+1) + ": '" + base + "' -> '" + name + "'");
	result.getHeaderRow().getCell(i).setContentAsString(name);
      }
      else {
	name = base;
      }
      names.add(name);
    }
    
    return result;
  }
}
