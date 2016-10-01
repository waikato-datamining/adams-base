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

/*
 * IndexSet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.core;

import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Class containing a static method for setting an
 * attribute index using provided index or regular expression.
 *
 * @author msf8
 * @version $Revision$
 */
public class IndexHelper {

  /**
   * Returns the position of the desired attribute.
   *
   * @param reg		Regular expression
   * @param ind		Index
   * @param inst	instances
   * @param index	the default index
   * @return		the determined index
   */
  public static int getIndex(BaseRegExp reg, Index ind, SpreadSheet inst, int index) {
    //Sets the index. First check's if a regular expression has been set
    // if not, uses the index.
    if (!reg.isEmpty()) {
      for(int i = 0; i< inst.getColumnCount(); i++) {
	String name = inst.getColumnName(i);
	if(reg.isMatch(name)) {
	  index = i;
	  break;
	}
      }
    }
    else {
      ind.setMax(inst.getColumnCount());
      try {
	index = ind.getIntIndex();
      }
      catch(Exception e) {
      }
    }
    return index;
  }
}