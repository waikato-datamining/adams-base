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
 * AggregateSpreadSheet.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;
import java.util.List;

import adams.core.Range;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowIdentifier;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Aggregates rows (min, max, avg, etc) in a spreadsheet using key columns.<br/>
 * All numeric columns in the specified aggregrate range (excluding the key columns) get aggregated. For each of the specified aggregates a new column is generated.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-key-columns &lt;adams.core.Range&gt; (property: keyColumns)
 * &nbsp;&nbsp;&nbsp;The columns to use as keys for identifying rows in the spreadsheets; A range 
 * &nbsp;&nbsp;&nbsp;is a comma-separated list of single 1-based indices or sub-ranges of indices 
 * &nbsp;&nbsp;&nbsp;('start-en2d'); 'inv(...)' inverts the range '...'; the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-aggregate-columns &lt;adams.core.Range&gt; (property: aggregateColumns)
 * &nbsp;&nbsp;&nbsp;The columns to aggregate (only numeric ones will be used); A range is a 
 * &nbsp;&nbsp;&nbsp;comma-separated list of single 1-based indices or sub-ranges of indices 
 * &nbsp;&nbsp;&nbsp;('start-end'); 'inv(...)' inverts the range '...'; the following placeholders 
 * &nbsp;&nbsp;&nbsp;can be used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 * <pre>-aggregate &lt;SUM|MIN|MAX|AVERAGE|MEDIAN|STDEV|STDEVP|INTERQUARTILE&gt; [-aggregate ...] (property: aggregates)
 * &nbsp;&nbsp;&nbsp;The aggregates to calculate and introduce as columns.
 * &nbsp;&nbsp;&nbsp;default: SUM
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AggregateSpreadSheet
  extends AbstractSpreadSheetConversion {

  /** for serialization. */
  private static final long serialVersionUID = -1789320708357341617L;

  /**
   * The types of aggregates to generate.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Aggregate {
    /** the sum. */
    SUM,
    /** the minimum. */
    MIN,
    /** the maximum. */
    MAX,
    /** the average. */
    AVERAGE,
    /** the median. */
    MEDIAN,
    /** the std deviation (sample). */
    STDEV,
    /** the std deviation (population). */
    STDEVP,
    /** the interquartile (IQR3 - IQR1). */
    INTERQUARTILE,
  }

  /** the range of column indices to use as key for identifying a row. */
  protected SpreadSheetColumnRange m_KeyColumns;

  /** the range of columns to aggregate. */
  protected SpreadSheetColumnRange m_AggregateColumns;
  
  /** the aggregates to generate. */
  protected Aggregate[] m_Aggregates;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Aggregates rows (min, max, avg, etc) in a spreadsheet using key "
	+ "columns.\n"
	+ "All numeric columns in the specified aggregrate range (excluding the "
	+ "key columns) get aggregated. For each of the specified aggregates a "
	+ "new column is generated.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "key-columns", "keyColumns",
	    new SpreadSheetColumnRange("first"));

    m_OptionManager.add(
	    "aggregate-columns", "aggregateColumns",
	    new SpreadSheetColumnRange(Range.ALL));

    m_OptionManager.add(
	    "aggregate", "aggregates",
	    new Aggregate[]{Aggregate.SUM});
  }

  /**
   * Sets the colums that identify a row.
   *
   * @param value	the range
   */
  public void setKeyColumns(SpreadSheetColumnRange value) {
    m_KeyColumns = value;
    reset();
  }

  /**
   * Returns the colums that identify a rowx
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getKeyColumns() {
    return m_KeyColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keyColumnsTipText() {
    return
        "The columns to use as keys for identifying rows in the spreadsheets; " + m_KeyColumns.getExample();
  }

  /**
   * Sets the colums that should get aggregated.
   *
   * @param value	the range
   */
  public void setAggregateColumns(SpreadSheetColumnRange value) {
    m_AggregateColumns = value;
    reset();
  }

  /**
   * Returns the colums that should get aggregated.
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getAggregateColumns() {
    return m_AggregateColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String aggregateColumnsTipText() {
    return
        "The columns to aggregate (only numeric ones will be used); " + m_KeyColumns.getExample();
  }

  /**
   * Sets the aggregates to calculate.
   *
   * @param value	the aggregates
   */
  public void setAggregates(Aggregate[] value) {
    m_Aggregates = value;
    reset();
  }

  /**
   * Returns the aggregates to calculate.
   *
   * @return		the aggregates
   */
  public Aggregate[] getAggregates() {
    return m_Aggregates;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String aggregatesTipText() {
    return
        "The aggregates to calculate and introduce as columns.";
  }

  /**
   * Computes the aggregate.
   * 
   * @param input	the original sheet
   * @param subset	the subset of rows to use for the computation
   * @param rowNew	the row to add the aggregate to
   * @param index	the column in the original spreadsheet
   * @param agg		the aggregate to compute
   * @return		the computed value
   */
  protected double computeAggregate(SpreadSheet input, List<Integer> subset, Row rowNew, int index, Aggregate agg) {
    double	result;
    double[]	values;
    int		i;
    Cell	cell;
    
    result = Double.NaN;
    values = new double[subset.size()];
    for (i = 0; i < subset.size(); i++) {
      cell = input.getCell(subset.get(i), index);
      if ((cell != null) && (cell.isNumeric()))
	values[i] = cell.toDouble();
      else
	values[i] = 0;
    }
    
    if (values.length > 0) {
      switch (agg) {
	case SUM:
	  result = StatUtils.sum(values);
	  break;
	case MIN:
	  result = StatUtils.min(values);
	  break;
	case MAX:
	  result = StatUtils.max(values);
	  break;
	case AVERAGE:
	  result = StatUtils.mean(values);
	  break;
	case MEDIAN:
	  result = StatUtils.median(values);
	  break;
	case STDEV:
	  result = StatUtils.stddev(values, true);
	  break;
	case STDEVP:
	  result = StatUtils.stddev(values, false);
	  break;
	case INTERQUARTILE:
	  result = StatUtils.iqr(values);
	  break;
	default:
	  throw new IllegalStateException("Unhandled aggregate: " + agg);
      }
    }
    
    return result;
  }
  
  /**
   * Generates the new spreadsheet from the input.
   * 
   * @param input	the incoming spreadsheet
   * @return		the generated spreadsheet
   * @throws Exception	if conversion fails for some reason
   */
  @Override
  protected SpreadSheet convert(SpreadSheet input) throws Exception {
    SpreadSheet		result;
    int[]		keys;
    int[]		agg;
    TIntHashSet		numeric;
    RowIdentifier	rows;
    List<Integer>	subset;
    Row			row;
    Row			rowNew;
    
    // columns to use as key
    m_KeyColumns.setSpreadSheet(input);
    keys = m_KeyColumns.getIntIndices();
    if (keys.length == 0)
      throw new IllegalStateException("No key columns defined!");
    rows = new RowIdentifier(m_KeyColumns);

    // determine columns to aggregate
    m_AggregateColumns.setSpreadSheet(input);
    agg     = m_AggregateColumns.getIntIndices();
    numeric = new TIntHashSet();
    for (int index: agg) {
      if (m_KeyColumns.isInRange(index))
	continue;
      if (!input.isNumeric(index))
	continue;
      numeric.add(index);
    }
    agg = numeric.toArray();
    Arrays.sort(agg);
    
    // create output
    rows.identify(input);
    result = input.newInstance();
    result.setDataRowClass(input.getDataRowClass());
    
    // header
    row = result.getHeaderRow();
    for (int index: keys) {
      row.addCell("" + index).setContent(
	  input.getHeaderRow().getCell(index).getContent());
    }
    for (int index: agg) {
      for (Aggregate a: m_Aggregates) {
	row.addCell("" + index + "-" + a).setContent(
	    input.getHeaderRow().getCell(index).getContent() + "-" + a);
      }
    }
    
    // data
    for (String key: rows.getKeys()) {
      rowNew = result.addRow();
      subset = rows.getRows(key);
      // keys
      for (int index: keys) {
	rowNew.addCell("" + index).setContent(
	    input.getRow(subset.get(0)).getCell(index).getContent());
      }
      // aggregates
      for (int index: agg) {
	for (Aggregate a: m_Aggregates) {
	  rowNew.addCell("" + index + "-" + a).setContent(
	      computeAggregate(input, subset, rowNew, index, a));
	}
      }
    }
    
    return result;
  }
}
