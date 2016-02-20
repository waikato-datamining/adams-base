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
 * ParserHelper.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.parser.spreadsheetformula;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayLinearRegression;
import adams.data.statistics.StatUtils;
import adams.parser.SpreadSheetFormula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Helper class for spreadsheet formulas.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ParserHelper
  extends adams.parser.ParserHelper {

  /** for serialization. */
  private static final long serialVersionUID = 3412549255873159225L;
  
  /** the underlying spreadsheet. */
  protected SpreadSheet m_Sheet;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Sheet = null;
  }

  /**
   * Sets the spreadsheet to use.
   *
   * @param value 	the spreadsheet
   */
  public void setSheet(SpreadSheet value) {
    m_Sheet = value;
  }

  /**
   * Returns the current spreadsheet in use.
   *
   * @return 		the spreadsheet
   */
  public SpreadSheet getSheet() {
    return m_Sheet;
  }
  
  /**
   * Returns the double value of the specified cell.
   *
   * @param loc 	the cell location
   * @return 		the numeric value, NaN if not found or missing
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double toDouble(String loc) throws Exception {
    Double 	result;
    Cell 	cell;
    int[] 	pos;
    
    result = Double.NaN;
    pos    = SpreadSheetUtils.getCellLocation(loc);
    if (m_Sheet != null) {
      if (m_Sheet.hasCell(pos[0], pos[1])) {
        cell = m_Sheet.getCell(pos[0], pos[1]);
        if (!cell.isMissing() && cell.isNumeric())
          result = cell.toDouble();
      }
    }
    
    return result;
  }

  /**
   * Returns the specified date from the cell.
   *
   * @param loc 	the cell location
   * @param field 	the date field (see constants of Calendar class)
   * @return 		the value or NaN if failed to convert or not present
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double getDateFieldFromCell(String loc, int field) throws Exception {
    Double 	result;
    Cell 	cell;
    
    result = Double.NaN;
    
    cell = getCell(loc);
    if (cell != null) {
      if (cell.isDate())
        result = new Double(getCalendar(cell.toDate()).get(field));
      else if (cell.isDateTime())
        result = new Double(getCalendar(cell.toDateTime()).get(field));
      else if (cell.isTime())
        result = new Double(getCalendar(cell.toTime()).get(field));
      else
        result = new Double(getCalendar(toDate(cell.getContent())).get(field));
    }
      
    return result;
  }

  /**
   * Checks whether the specified cell is available.
   *
   * @param loc 	the cell location
   * @return 		true if available
   * @throws Exception 	if cell location cannot be parsed
   */
  public boolean hasCell(String loc) throws Exception {
    boolean 	result;
    int[] 	pos;
    
    result = false;
    pos    = SpreadSheetUtils.getCellLocation(loc);
    if (m_Sheet != null)
      result = m_Sheet.hasCell(pos[0], pos[1]);
    
    return result;
  }

  /**
   * Returns the cell object of the specified cell.
   *
   * @param loc 	the cell location
   * @return 		the cell object, null if not available
   * @throws Exception 	if cell location cannot be parsed
   */
  public Cell getCell(String loc) throws Exception {
    Cell 	result;
    int[] 	pos;
    
    result = null;
    pos    = SpreadSheetUtils.getCellLocation(loc);
    if (m_Sheet != null) {
      if (m_Sheet.hasCell(pos[0], pos[1]))
        result = m_Sheet.getCell(pos[0], pos[1]);
    }
    
    return result;
  }

  /**
   * Turns the cell range into a list of located cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param onlyNumeric	whether to locate only numeric cells
   * @return 		the list of cells that aren't flagged as missing
   * @throws Exception 	if cell location cannot be parsed
   */
  public List<Cell> rangeToList(String fromCell, String toCell, boolean onlyNumeric) throws Exception {
    ArrayList<Cell> 	result;
    int[] 		from;
    int[] 		to;
    int 		i;
    int 		n;
    Cell 		cell;
    
    result = new ArrayList<Cell>();
    from   = SpreadSheetUtils.getCellLocation(fromCell);
    to     = SpreadSheetUtils.getCellLocation(toCell);

    if (m_Sheet != null) {
      if ((from[0] <= to[0]) && (from[1] <= to[1])) {
        for (i = from[0]; i <= to[0]; i++) {
          for (n = from[1]; n <= to[1]; n++) {
            if (m_Sheet.hasCell(i, n)) {
              cell = m_Sheet.getCell(i, n);
              if (cell.isMissing())
                continue;
              if (onlyNumeric && !cell.isNumeric())
                continue;
              result.add(cell);
            }
          }
        }
      }
    }
    
    return result;
  }

  /**
   * Turns a range of cells into a Double array.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the double array
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double[] rangeToDoubleArray(String fromCell, String toCell) throws Exception {
    Double[] 	result;
    List<Cell> 	cells;
    int 	i;
    
    cells  = rangeToList(fromCell, toCell, true);
    result = new Double[cells.size()];
    for (i = 0; i < cells.size(); i++)
      result[i] = cells.get(i).toDouble();
    
    return result;
  }

  /**
   * Calculates the sum for a range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the sum
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double sum(String fromCell, String toCell) throws Exception {
    return StatUtils.sum(rangeToDoubleArray(fromCell, toCell));
  }

  /**
   * Calculates the minimum for a range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the minimum
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double min(String fromCell, String toCell) throws Exception {
    return StatUtils.min(rangeToDoubleArray(fromCell, toCell)).doubleValue();
  }

  /**
   * Calculates the maximum for a range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the maximum
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double max(String fromCell, String toCell) throws Exception {
    return StatUtils.max(rangeToDoubleArray(fromCell, toCell)).doubleValue();
  }

  /**
   * Calculates the average for a range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the average
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double average(String fromCell, String toCell) throws Exception {
    return StatUtils.mean(rangeToDoubleArray(fromCell, toCell));
  }

  /**
   * Calculates the standard deviation (sample) for a range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the standard deviation
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double stdev(String fromCell, String toCell) throws Exception {
    return StatUtils.stddev(rangeToDoubleArray(fromCell, toCell), true);
  }

  /**
   * Calculates the standard deviation (population) for a range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the standard deviation
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double stdevp(String fromCell, String toCell) throws Exception {
    return StatUtils.stddev(rangeToDoubleArray(fromCell, toCell), false);
  }

  /**
   * Counts how often a number occurs in the range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the numeric value to look for
   * @return 		the count
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double countif(String fromCell, String toCell, Double value) throws Exception {
    Double 	result;
    List<Cell> 	cells;
    
    result = 0.0;
    
    cells = rangeToList(fromCell, toCell, true);
    for (Cell cell: cells) {
      if (!cell.isNumeric())
        continue;
      if (value.equals(cell.toDouble()))
        result++;
    }
    
    return result;
  }

  /**
   * Counts how often a string occurs or expression evalutes to true in the range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the expression to evaluate or string to find
   * @return 		the count
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double countif(String fromCell, String toCell, String value) throws Exception {
    Double 	result;
    List<Cell> 	cells;
    String	expr;
    Object	eval;
    
    result = 0.0;
    
    // inspect expression
    value = value.trim();
    if (value.startsWith(">") || value.startsWith("<") || value.startsWith("=") || value.startsWith("!"))
      expr = value;
    else
      expr = "=" + value;
    
    cells = rangeToList(fromCell, toCell, false);
    for (Cell cell: cells) {
      eval = SpreadSheetFormula.evaluate(cell.getContent() + expr, new HashMap(), getSheet());
      if ((eval instanceof Boolean) && ((Boolean) eval))
        result++;
    }
    
    return result;
  }

  /**
   * Counts how often a boolean occurs in the range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the boolean value to look for
   * @return 		the count
   * @throws Exception if cell location cannot be parsed
   */
  public Double countif(String fromCell, String toCell, Boolean value) throws Exception {
    Double result;
    List<Cell> cells;
    
    result = 0.0;
    
    cells = rangeToList(fromCell, toCell, false);
    for (Cell cell: cells) {
      if (cell.isBoolean())
        continue;
      if (value.equals(cell.toBoolean()))
        result++;
    }
    
    return result;
  }

  /**
   * Counts how often a boolean occurs in the range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the expression to evaluate or value to look for
   * @return 		the count
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double countif(String fromCell, String toCell, Object value) throws Exception {
    if (value instanceof Double)
      return countif(fromCell, toCell, (Double) value); 
    else if (value instanceof String)
      return countif(fromCell, toCell, (String) value); 
    else if (value instanceof Boolean)
      return countif(fromCell, toCell, (Boolean) value); 
    else
      return Double.NaN;
  }

  /**
   * Sums up the numbers of a range of cells if they match the value.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the numeric value to look for
   * @return 		the sum
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double sumif(String fromCell, String toCell, Double value) throws Exception {
    Double 	result;
    List<Cell> 	cells;
    
    result = 0.0;
    
    cells = rangeToList(fromCell, toCell, false);
    for (Cell cell: cells) {
      if (!cell.isNumeric())
        continue;
      if (value.equals(cell.toDouble()))
        result += cell.toDouble();
    }
    
    return result;
  }

  /**
   * Sums up the numbers of a range of cells if they match the value or the
   * expression evaluates to true.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the expression to evaluate or string to find
   * @return 		the sum
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double sumif(String fromCell, String toCell, String value) throws Exception {
    Double 	result;
    List<Cell> 	cells;
    String	expr;
    Object	eval;
    
    result = 0.0;
    
    // inspect expression
    value = value.trim();
    expr  = null;
    if (value.startsWith(">") || value.startsWith("<") || value.startsWith("=") || value.startsWith("!"))
      expr = value;
    
    cells = rangeToList(fromCell, toCell, false);
    for (Cell cell: cells) {
      if (!cell.isNumeric())
	continue;
      if (expr == null) {
	if (value.equals(cell.getContent()))
	  result += cell.toDouble();
      }
      else {
	eval = SpreadSheetFormula.evaluate(cell.getContent() + expr, getSymbols(), getSheet());
	if ((eval instanceof Boolean) && ((Boolean) eval))
	  result += cell.toDouble();
      }
    }
    
    return result;
  }

  /**
   * Sums up the numbers of a range of cells if they match the value or the
   * expression evaluates to true.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the expression to evaluate or value to look for
   * @param fromCell 	the top-left cell to sum up
   * @param toCell 	the bottom-right cell to sum up
   * @return 		the sum
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double sumif(String fromCell, String toCell, Object value) throws Exception {
    if (value instanceof Double)
      return sumif(fromCell, toCell, (Double) value); 
    else if (value instanceof String)
      return sumif(fromCell, toCell, (String) value); 
    else
      return Double.NaN;
  }

  /**
   * Sums up the numbers of a corresponding range of cells if the values in 
   * the range of cells match the value.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the numeric value to look for
   * @param fromSum 	the top-left cell to sum up
   * @param toSum 	the bottom-right cell to sum up
   * @return 		the sum
   * @throws Exception 	if cell location cannot be parsed
   * @throws IllegalArgumentException if search and sum cell range differ in size
   */
  public Double sumif(String fromCell, String toCell, Double value, String fromSum, String toSum) throws Exception {
    Double 	result;
    List<Cell> 	cells;
    List<Cell>	cellsSum;
    int		i;
    
    result = 0.0;
    
    cells    = rangeToList(fromCell, toCell, false);
    cellsSum = rangeToList(fromSum, toSum, false);
    if (cells.size() != cellsSum.size())
      throw new IllegalArgumentException(
	  "Search cell range and summ cell range have different length: " + cells.size() + " != " + cellsSum.size());
    for (i = 0; i < cells.size(); i++) {
      if (!cells.get(i).isNumeric() || !cellsSum.get(i).isNumeric())
        continue;
      if (value.equals(cells.get(i).toDouble()))
        result += cellsSum.get(i).toDouble();
    }
    
    return result;
  }

  /**
   * Sums up the numbers of a corresponding range of cells if the values in 
   * the range of cells match the value or the expression evaluates to true.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the expression to evaluate or string to find
   * @param fromSum 	the top-left cell to sum up
   * @param toSum 	the bottom-right cell to sum up
   * @return 		the sum
   * @throws Exception 	if cell location cannot be parsed
   * @throws IllegalArgumentException if search and sum cell range differ in size
   */
  public Double sumif(String fromCell, String toCell, String value, String fromSum, String toSum) throws Exception {
    Double 	result;
    List<Cell> 	cells;
    List<Cell>	cellsSum;
    int		i;
    String	expr;
    Object	eval;
    
    result = 0.0;
    
    // inspect expression
    value = value.trim();
    expr  = null;
    if (value.startsWith(">") || value.startsWith("<") || value.startsWith("=") || value.startsWith("!"))
      expr = value;
    
    cells    = rangeToList(fromCell, toCell, false);
    cellsSum = rangeToList(fromSum, toSum, false);
    if (cells.size() != cellsSum.size())
      throw new IllegalArgumentException(
	  "Search cell range and summ cell range have different length: " + cells.size() + " != " + cellsSum.size());
    for (i = 0; i < cells.size(); i++) {
      if (!cells.get(i).isNumeric() || !cellsSum.get(i).isNumeric())
        continue;
      if (expr == null) {
	if (value.equals(cells.get(i).getContent()))
	  result += cellsSum.get(i).toDouble();
      }
      else {
	eval = SpreadSheetFormula.evaluate(cells.get(i).getContent() + expr, getSymbols(), getSheet());
	if ((eval instanceof Boolean) && ((Boolean) eval))
	  result += cellsSum.get(i).toDouble();
      }
    }
    
    return result;
  }

  /**
   * Sums up the numbers of a corresponding range of cells if the values in 
   * the range of cells match the value or the expression evaluates to true.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @param value 	the expression to evaluate or value to look for
   * @param fromSum 	the top-left cell to sum up
   * @param toSum 	the bottom-right cell to sum up
   * @return 		the sum
   * @throws Exception 	if cell location cannot be parsed
   * @throws IllegalArgumentException 	if search and sum cell range differ in size
   */
  public Double sumif(String fromCell, String toCell, Object value, String fromSum, String toSum) throws Exception {
    if (value instanceof Double)
      return sumif(fromCell, toCell, (Double) value, fromSum, toSum); 
    else if (value instanceof String)
      return sumif(fromCell, toCell, (String) value, fromSum, toSum); 
    else
      return Double.NaN;
  }

  /**
   * Counts how often an empty string or missing value occurs in the range of cells.
   *
   * @param fromCell 	the top-left cell
   * @param toCell 	the bottom-right cell
   * @return 		the count
   * @throws Exception if cell location cannot be parsed
   */
  public long countblank(String fromCell, String toCell) throws Exception {
    long result;
    List<Cell> cells;
    
    result = 0;
    
    cells = rangeToList(fromCell, toCell, false);
    for (Cell cell: cells) {
      if (cell.isMissing())
        result++;
      else if ((cell.getContentType() == ContentType.STRING) && (cell.getContent().isEmpty()))
        result++;
    }
    
    return result;
  }

  /**
   * Calculates the intercept or slope of linear regression for a range of cells.
   *
   * @param fromCellY 	the top-left cell of Y
   * @param toCellY 	the bottom-right cell of Y
   * @param fromCellX 	the top-left cell of X
   * @param toCellX 	the bottom-right cell of X
   * @return 		the standard deviation
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double linearRegression(boolean intercept, String fromCellY, String toCellY, String fromCellX, String toCellX) throws Exception {
    Double[]				x;
    Double[]				y;
    ArrayLinearRegression<Double>	lr;
    StatisticContainer 			cont;

    x = rangeToDoubleArray(fromCellX, toCellX);
    y = rangeToDoubleArray(fromCellY, toCellY);
    if (x.length != y.length)
      throw new IllegalArgumentException("Intercept: X and Y must have same length: " + x.length + " != " + y.length);

    lr = new ArrayLinearRegression<>();
    lr.add(x);
    lr.add(y);
    cont = lr.calculate();

    if (intercept)
      return (Double) cont.getCell(0, 0);
    else
      return (Double) cont.getCell(0, 1);
  }

  /**
   * Calculates the intercept of linear regression for a range of cells.
   *
   * @param fromCellY 	the top-left cell of Y
   * @param toCellY 	the bottom-right cell of Y
   * @param fromCellX 	the top-left cell of X
   * @param toCellX 	the bottom-right cell of X
   * @return 		the standard deviation
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double intercept(String fromCellY, String toCellY, String fromCellX, String toCellX) throws Exception {
    return linearRegression(true, fromCellY, toCellY, fromCellX, toCellX);
  }

  /**
   * Calculates the slope of linear regression for a range of cells.
   *
   * @param fromCellY 	the top-left cell of Y
   * @param toCellY 	the bottom-right cell of Y
   * @param fromCellX 	the top-left cell of X
   * @param toCellX 	the bottom-right cell of X
   * @return 		the standard deviation
   * @throws Exception 	if cell location cannot be parsed
   */
  public Double slope(String fromCellY, String toCellY, String fromCellX, String toCellX) throws Exception {
    return linearRegression(false, fromCellY, toCellY, fromCellX, toCellX);
  }
}
