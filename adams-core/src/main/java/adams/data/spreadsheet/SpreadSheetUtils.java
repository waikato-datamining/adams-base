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
 * SpreadSheetUtils.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

import adams.core.Utils;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for spreadsheet related functionality.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetUtils {

  public static final String PREFIX_COL = "Col-";

  /**
   * Attempts to split a string, using the specified delimiter character.
   * A delimiter gets ignored if inside double quotes.
   *
   * @param s		the string to split
   * @param delimiter	the delimiting character
   * @return		the parts (single array element if no range)
   */
  public static String[] split(String s, char delimiter) {
    return split(s, delimiter, false);
  }

  /**
   * Attempts to split a string, using the specified delimiter character.
   * A delimiter gets ignored if inside double quotes.
   *
   * @param s		the string to split
   * @param delimiter	the delimiting character
   * @param unquote	whether to remove double quotes
   * @return		the parts (single array element if no range)
   */
  public static String[] split(String s, char delimiter, boolean unquote) {
    return split(s, delimiter, unquote, '"', false);
  }

  /**
   * Attempts to split a string, using the specified delimiter character.
   * A delimiter gets ignored if inside double quotes.
   *
   * @param s		the string to split
   * @param delimiter	the delimiting character
   * @param unquote	whether to remove single/double quotes
   * @param quoteChar	the quote character to use
   * @param escaped	if true then quotes preceded by backslash ('escaped') get ignored
   * @return		the parts (single array element if no range)
   */
  public static String[] split(String s, char delimiter, boolean unquote, char quoteChar, boolean escaped) {
    List<String>	result;
    int			i;
    StringBuilder	current;
    boolean 		quoted;
    char		c;
    boolean		backslash;

    result = new ArrayList<>();

    current   = new StringBuilder();
    quoted    = false;
    backslash = false;
    for (i = 0; i < s.length(); i++) {
      c = s.charAt(i);
      if (c == quoteChar) {
	if (!backslash)
	  quoted = !quoted;
	current.append(c);
      }
      else if (c == delimiter) {
	if (quoted) {
	  current.append(c);
	}
	else {
	  if (unquote) {
	    if (quoteChar == '"')
	      result.add(Utils.unDoubleQuote(current.toString()));
	    else if (quoteChar == '\'')
	      result.add(Utils.unquote(current.toString()));
	    else
	      result.add(current.toString());
	  }
	  else {
	    result.add(current.toString());
	  }
	  current.delete(0, current.length());
	}
      }
      else {
	current.append(c);
      }

      if (escaped)
	backslash = (c == '\\');
    }

    // add last string
    if (current.length() > 0) {
      if (unquote) {
	if (quoteChar == '"')
	  result.add(Utils.unDoubleQuote(current.toString()));
	else if (quoteChar == '\'')
	  result.add(Utils.unquote(current.toString()));
	else
	  result.add(current.toString());
      }
      else {
	result.add(current.toString());
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Creates list of column header names. Either using the comma-separated
   * list or, if that is empty, a made up list using "Col-" plus the index.
   *
   * @param numCols         the number of column headers to generate
   * @param customCols      the comma-separated list of custom headers, can be empty
   * @return                the generated list of headers
   */
  public static List<String> createHeader(int numCols, String customCols) {
    List<String>      result;

    result     = new ArrayList<>();
    customCols = customCols.trim();

    if (!customCols.isEmpty())
      result.addAll(Arrays.asList(customCols.split(",")));

    while (result.size() < numCols)
      result.add(PREFIX_COL + (result.size() + 1));

    while (result.size() > numCols)
      result.remove(result.size() - 1);

    return result;
  }

  /**
   * Returns the position letter(s) of the column.
   *
   * @param col		the column index of the cell (0-based)
   * @return		the position string
   */
  public static String getColumnPosition(int col) {
    String		result;
    List<Integer>	digits;
    int			i;

    result = null;

    // A-Z, AA-ZZ, AAA-ZZZ, AAAA-ZZZZ, AAAAA-ZZZZZ, AAAAAA-ZZZZZZ
    if (col >= 26 + 676 + 17576 + 456976 + 11881376 + 308915776)
      throw new IllegalArgumentException("Column of cell too large: " + col + " >= " + (26 + 676 + 17576 + 456976 + 11881376 + 308915776));

    result = "";

    // A-Z
    if (col < 26) {
      digits = Utils.toBase(col, 26);
    }
    // AA-ZZ
    else if (col < 26 + 676) {
      digits = Utils.toBase(col - 26, 26);
      while (digits.size() < 2)
	digits.add(0);
    }
    // AAA-ZZZ
    else if (col < 26 + 676 + 17576) {
      digits = Utils.toBase(col - 26 - 676, 26);
      while (digits.size() < 3)
	digits.add(0);
    }
    // AAAA-ZZZZ
    else if (col < 26 + 676 + 17576 + 456976) {
      digits = Utils.toBase(col - 26 - 676 - 17576, 26);
      while (digits.size() < 4)
	digits.add(0);
    }
    // AAAAA-ZZZZZ
    else if (col < 26 + 676 + 17576 + 456976 + 11881376) {
      digits = Utils.toBase(col - 26 - 676 - 17576 - 456976, 26);
      while (digits.size() < 5)
	digits.add(0);
    }
    // AAAAAA-ZZZZZZ
    else /*if (col < 26 + 676 + 17576 + 456976 + 11881376 + 308915776)*/ {
      digits = Utils.toBase(col - 26 - 676 - 17576 - 456976 - 11881376, 26);
      while (digits.size() < 6)
	digits.add(0);
    }

    for (i = digits.size() - 1; i >= 0; i--)
      result += (char) ('A' + digits.get(i));

    return result;
  }

  /**
   * Returns the position of the cell. A position is a combination of a number
   * of letters (for the column) and number (for the row).
   * <br><br>
   * Note: add "1" to the row indices, since the header row does not count
   * towards the row count.
   *
   * @param row		the row index of the cell (0-based)
   * @param col		the column index of the cell (0-based)
   * @return		the position string or null if not found
   */
  public static String getCellPosition(int row, int col) {
    String	result;

    result = getColumnPosition(col);

    if ((row == -1) || (col == -1))
      return result;

    result += (row + 2);

    return result;
  }

  /**
   * Returns row/column index based on the provided position string (e.g., A12).
   *
   * @param position	the position string to parse
   * @return		the array with row and column index (0-based indices)
   * @throws Exception	in case of an invalid position string
   */
  public static int[] getCellLocation(String position) throws Exception {
    int[]	result;
    String	row;
    String	col;
    int		i;
    boolean	isCol;
    char	chr;
    int		factor;

    result = new int[2];

    isCol = true;
    row   = "";
    col   = "";
    for (i = 0; i < position.length(); i++) {
      chr = position.charAt(i);
      if ((chr >= '0') && (chr <= '9')) {
	isCol = false;
	row += chr;
      }
      else if ((chr >= 'A') && (chr <= 'Z') && isCol) {
	col += chr;
      }
      else {
	throw new Exception("Invalid character in cell position string: " + chr);
      }
    }

    // row
    result[0] = Integer.parseInt(row) - 2;

    // col
    factor = 1;
    for (i = col.length() - 1; i >= 0; i--) {
      result[1] += (col.charAt(i) - 'A' + 1) * factor;
      factor *= 26;
    }
    result[1]--;

    return result;
  }

  /**
   * Returns the content of a numeric column as double array.
   *
   * @param sheet	the sheet to use
   * @param col		the index of the numeric column
   * @return		the numeric data, elements are NaN if missing or not numeric
   */
  public static double[] getNumericColumn(SpreadSheet sheet, int col) {
    TDoubleArrayList result;
    int			i;
    Row			row;
    Cell		cell;
    double		val;

    result = new TDoubleArrayList(sheet.getRowCount());
    for (i = 0; i < sheet.getRowCount(); i++) {
      row = sheet.getRow(i);
      val = Double.NaN;
      if (row.hasCell(col)) {
	cell = row.getCell(col);
	if (cell.isNumeric())
	  val = cell.toDouble();
      }
      result.add(val);
    }

    return result.toArray();
  }
}
