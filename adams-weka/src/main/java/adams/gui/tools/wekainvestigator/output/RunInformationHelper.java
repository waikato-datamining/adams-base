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
 * RunInformationHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.core.Shortening;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for run information.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunInformationHelper {

  /** the maximum length for HTML cells. */
  public final static int MAX_HTML_LENGTH = 80;

  /**
   * Turns the run information into a string representation.
   *
   * @param info	the run information
   * @return		the string representation
   */
  public static String toString(SpreadSheet info) {
    StringBuilder 	result;
    List<String> 	keys;
    List<String>	values;
    int			len;
    int			i;

    result = new StringBuilder();
    keys   = new ArrayList<>();
    values = new ArrayList<>();
    len    = 0;
    for (Row row: info.rows()) {
      keys.add(row.getCell(0).getContent());
      values.add(row.getCell(1).getContent());
      len = Math.max(len, keys.get(keys.size() - 1).length());
    }
    for (i = 0; i < keys.size(); i++) {
      while (keys.get(i).length() < len)
	keys.set(i, keys.get(i) + ".");
      keys.set(i, keys.get(i) + ": ");
    }
    for (i = 0; i < keys.size(); i++) {
      result.append(keys.get(i));
      result.append(values.get(i));
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Turns the run information into an html table.
   *
   * @param info	the run information
   * @return		the html representation
   */
  public static String toHTML(SpreadSheet info) {
    StringBuilder 	result;

    result = new StringBuilder();
    result.append("<table>");
    for (Row row: info.rows()) {
      result.append("<tr>");
      result.append("<td>");
      result.append(row.getCell(0).getContent());
      result.append("</td>");
      result.append("<td>");
      result.append(Shortening.shortenEnd(row.getCell(1).getContent(), MAX_HTML_LENGTH));
      result.append("</td>");
      result.append("</tr>");
    }
    result.append("</table>");

    return result.toString();
  }
}
