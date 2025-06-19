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
 * SpreadSheetAggregate.java
 * Copyright (C) 2014-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.RowIdentifier;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.statistics.StatUtils;
import adams.flow.core.Token;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Aggregates rows (min, max, avg, etc) in a spreadsheet using key columns.<br>
 * All numeric columns in the specified aggregrate range (excluding the key columns) get aggregated. For each of the specified aggregates a new column is generated.<br>
 * If no key column(s) provided, the complete spreadsheet is used for aggregation.<br>
 * Missing cells get ignored.<br>
 * COUNT doesn't need any numeric columns.<br>
 * Note: A single non-numeric cell makes a column a non-numeric one!
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetAggregate
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-key-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: keyColumns)
 * &nbsp;&nbsp;&nbsp;The columns to use as keys for identifying rows in the spreadsheets; if
 * &nbsp;&nbsp;&nbsp;left empty, all rows are used.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-aggregate-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: aggregateColumns)
 * &nbsp;&nbsp;&nbsp;The columns to aggregate (only numeric ones will be used).
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-aggregate &lt;COUNT|SUM|MIN|MAX|RANGE|AVERAGE|MEDIAN|STDEV|STDEVP|INTERQUARTILE|QUARTILE25|QUARTILE75&gt; [-aggregate ...] (property: aggregates)
 * &nbsp;&nbsp;&nbsp;The aggregates to calculate and introduce as columns.
 * &nbsp;&nbsp;&nbsp;default: SUM
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetAggregate
  extends AbstractSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 444466366407383727L;

  /**
   * The types of aggregates to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 10093 $
   */
  public enum Aggregate {
    /** the count. */
    COUNT,
    /** the sum. */
    SUM,
    /** the minimum. */
    MIN,
    /** the maximum. */
    MAX,
    /** the range (max-min). */
    RANGE,
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
    /** 25th quartile. */
    QUARTILE25,
    /** 75th quartile. */
    QUARTILE75,
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
	+ "new column is generated.\n"
	+ "If no key column(s) provided, the complete spreadsheet is used for aggregation.\n"
	+ "Missing cells get ignored.\n"
	+ "COUNT doesn't need any numeric columns (leave 'aggregateColumns' empty if there are no numeric columns).\n"
	+ "Note: A single non-numeric cell makes a column a non-numeric one!";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "key-columns", "keyColumns",
      new SpreadSheetColumnRange());

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
      "The columns to use as keys for identifying rows in the spreadsheets; "
	+ "if left empty, all rows are used.";
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
    return "The columns to aggregate (only numeric ones will be used).";
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
    return "The aggregates to calculate and introduce as columns.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "aggregateColumns", m_AggregateColumns, "cols: ");
    result += QuickInfoHelper.toString(this, "keyColumns", (m_KeyColumns.isEmpty() ? "-none-" : m_KeyColumns), ", key: ");
    result += QuickInfoHelper.toString(this, "aggregates", m_Aggregates, ", agg: ");

    return result;
  }

  /**
   * Computes the aggregates.
   *
   * @param input	the original sheet
   * @param subset	the subset of rows to use for the computation, null if all rows
   * @param index	the column in the original spreadsheet
   * @return		the computed values
   */
  protected HashMap<Aggregate,Number> computeAggregates(SpreadSheet input, List<Integer> subset, int index) {
    HashMap<Aggregate,Number>	result;
    ArrayList<Double> 		list;
    Double[]			values;
    int				i;
    Cell			cell;
    Number			max;
    Number			min;

    result = new HashMap<>();
    for (Aggregate agg: m_Aggregates)
      result.put(agg, Double.NaN);
    list = new ArrayList<>();
    if (subset != null) {
      for (i = 0; i < subset.size(); i++) {
	cell = input.getCell(subset.get(i), index);
	if ((cell != null) && (cell.isNumeric()))
	  list.add(cell.toDouble());
      }
    }
    else {
      for (i = 0; i < input.getRowCount(); i++) {
	cell = input.getCell(i, index);
	if ((cell != null) && (cell.isNumeric()))
	  list.add(cell.toDouble());
      }
    }
    values = list.toArray(new Double[0]);

    if (values.length > 0) {
      for (Aggregate agg: m_Aggregates) {
	switch (agg) {
	  case COUNT:
	    result.put(agg, values.length);
	    break;
	  case SUM:
	    result.put(agg, StatUtils.sum(values));
	    break;
	  case MIN:
	    result.put(agg, StatUtils.min(values));
	    break;
	  case MAX:
	    result.put(agg, StatUtils.max(values));
	    break;
	  case RANGE:
	    max = StatUtils.max(values);
	    min = StatUtils.min(values);
	    if ((max != null) && (min != null))
	      result.put(agg, max.doubleValue() - min.doubleValue());
	    else
	      result.put(agg, null);
	    break;
	  case AVERAGE:
	    result.put(agg, StatUtils.mean(values));
	    break;
	  case MEDIAN:
	    result.put(agg, StatUtils.median(values));
	    break;
	  case STDEV:
	    result.put(agg, StatUtils.stddev(values, true));
	    break;
	  case STDEVP:
	    result.put(agg, StatUtils.stddev(values, false));
	    break;
	  case INTERQUARTILE:
	    result.put(agg, StatUtils.iqr(values));
	    break;
	  case QUARTILE25:
	    result.put(agg, StatUtils.quartile(values, 0.25));
	    break;
	  case QUARTILE75:
	    result.put(agg, StatUtils.quartile(values, 0.75));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled aggregate: " + agg);
	}
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			input;
    SpreadSheet			aggregated;
    int[]			keys;
    int[]			agg;
    TIntHashSet			numeric;
    RowIdentifier		rows;
    List<Integer>		subset;
    Row				row;
    Row				rowNew;
    HashMap<Aggregate,Number>	aggs;
    boolean			onlyCount;

    result     = null;
    input      = (SpreadSheet) m_InputToken.getPayload();
    aggregated = null;
    onlyCount  = (m_Aggregates.length == 1) && (m_Aggregates[0] == Aggregate.COUNT) && m_AggregateColumns.isEmpty();

    // columns to use as key
    if (!m_KeyColumns.isEmpty()) {
      m_KeyColumns.setSpreadSheet(input);
      keys = m_KeyColumns.getIntIndices();
      if (keys.length == 0)
	result = "No key columns defined!";
      rows = new RowIdentifier(m_KeyColumns);
    }
    else {
      keys = new int[0];
      rows = null;
    }

    if (result == null) {
      // determine columns to aggregate
      m_AggregateColumns.setSpreadSheet(input);
      agg = m_AggregateColumns.getIntIndices();
      numeric = new TIntHashSet();
      for (int index : agg) {
	if ((keys.length > 0) && m_KeyColumns.isInRange(index))
	  continue;
	if (!input.isNumeric(index, true))
	  continue;
	numeric.add(index);
      }
      agg = numeric.toArray();
      Arrays.sort(agg);

      // create output
      if (rows != null)
	rows.identify(input);
      aggregated = input.newInstance();
      aggregated.setDataRowClass(input.getDataRowClass());

      // header
      row = aggregated.getHeaderRow();
      for (int index : keys) {
	row.addCell("" + index).setContentAsString(
	  input.getHeaderRow().getCell(index).getContent());
      }
      for (int index : agg) {
	for (Aggregate a : m_Aggregates) {
	  row.addCell(index + "-" + a).setContentAsString(
	    input.getHeaderRow().getCell(index).getContent() + "-" + a);
	}
      }
      if (onlyCount) {
	row.addCell(Aggregate.COUNT.toString()).setContentAsString(
	  Aggregate.COUNT.toString());
      }

      // data
      if (input.getRowCount() > 0) {
	if (rows != null) {
	  for (String key : rows.getKeys()) {
	    if (isStopped())
	      return null;
	    rowNew = aggregated.addRow();
	    subset = rows.getRows(key);
	    // keys
	    for (int index : keys) {
	      rowNew.addCell("" + index).setContent(
		input.getRow(subset.get(0)).getCell(index).getContent());
	    }
	    // aggregates
	    for (int index : agg) {
	      aggs = computeAggregates(input, subset, index);
	      for (Aggregate a : m_Aggregates) {
		if (aggs.get(a) == null)
		  rowNew.addCell(index + "-" + a).setMissing();
		else if (aggs.get(a) instanceof Integer)
		  rowNew.addCell(index + "-" + a).setContent((Integer) aggs.get(a));
		else if (aggs.get(a) instanceof Long)
		  rowNew.addCell(index + "-" + a).setContent((Long) aggs.get(a));
		else
		  rowNew.addCell(index + "-" + a).setContent(aggs.get(a).doubleValue());
	      }
	    }
	    if (onlyCount)
	      rowNew.addCell(Aggregate.COUNT.toString()).setContent(subset.size());
	  }
	}
	else {
	  rowNew = aggregated.addRow();
	  // keys
	  for (int index : keys) {
	    rowNew.addCell("" + index).setContent(
	      input.getRow(0).getCell(index).getContent());
	  }
	  // aggregates
	  for (int index : agg) {
	    if (isStopped())
	      return null;
	    aggs = computeAggregates(input, null, index);
	    for (Aggregate a : m_Aggregates) {
	      if (aggs.get(a) == null)
		rowNew.addCell(index + "-" + a).setMissing();
	      else if (aggs.get(a) instanceof Integer)
		rowNew.addCell(index + "-" + a).setContent((Integer) aggs.get(a));
	      else if (aggs.get(a) instanceof Long)
		rowNew.addCell(index + "-" + a).setContent((Long) aggs.get(a));
	      else
		rowNew.addCell(index + "-" + a).setContent(aggs.get(a).doubleValue());
	    }
	    if (onlyCount)
	      rowNew.addCell(Aggregate.COUNT.toString()).setContent(input.getRowCount());
	  }
	}
      }
    }

    if (aggregated != null)
      m_OutputToken = new Token(aggregated);

    return result;
  }
}
