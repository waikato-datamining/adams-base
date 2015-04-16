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
package adams.parser.spreadsheetquery;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseBoolean;
import adams.data.DateFormatString;
import adams.data.conversion.Conversion;
import adams.data.conversion.MultiConversion;
import adams.data.conversion.RenameSpreadSheetColumn;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.rowfinder.ByIndex;
import adams.data.spreadsheet.rowfinder.RowFinder;
import adams.flow.control.SubProcess;
import adams.flow.core.Token;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SpreadSheetAggregate;
import adams.flow.transformer.SpreadSheetAggregate.Aggregate;
import adams.flow.transformer.SpreadSheetRemoveColumn;
import adams.flow.transformer.SpreadSheetReorderColumns;
import adams.flow.transformer.SpreadSheetRowFilter;
import adams.flow.transformer.SpreadSheetSetCell;
import adams.flow.transformer.SpreadSheetSort;
import adams.flow.transformer.SpreadSheetSubset;

/**
 * Helper class for spreadsheet formulas.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ParserHelper
  extends adams.parser.ParserHelper {

  /** for serialization. */
  private static final long serialVersionUID = 8273216839178554659L;

  /** the underlying spreadsheet. */
  protected SpreadSheet m_Sheet;

  /** whether all columns are retrieved. */
  protected boolean m_AllColumns;

  /** whether to create subset. */
  protected boolean m_Select;

  /** whether to delete rows. */
  protected boolean m_Delete;

  /** whether to update cells. */
  protected boolean m_Update;

  /** whether to aggregate. */
  protected boolean m_Aggregate;

  /** the columns to retrieve. */
  protected List<String> m_Columns;

  /** the columns to rename. */
  protected HashMap<String,String> m_RenameColumns;

  /** the columns to sort on. */
  protected List<String> m_SortColumns;

  /** the columns to update (column - new value). */
  protected HashMap<String,Object> m_UpdateColumns;

  /** the columns to sort on. */
  protected List<Boolean> m_SortAsc;

  /** the row finders to use. */
  protected List<RowFinder> m_RowFinders;
  
  /** the rowfinder to use for generating a subsample. */
  protected RowFinder m_Subsample;
  
  /** the partial flow for converting the spreadsheet. */
  protected SubProcess m_SubProcess;
  
  /** the rows to select. */
  protected int[] m_Rows;
  
  /** the limit. */
  protected int m_LimitMax;
  
  /** the offset for the limit. */
  protected int m_LimitOffset;

  /** the aggregates to generate (aggregate - list of columns). */
  protected HashMap<Aggregate,List<String>> m_Aggregates;
  
  /** the new names for the aggregates (old - new). */
  protected HashMap<String,String> m_RenamedAggregates;

  /** the group by columns to retrieve. */
  protected List<String> m_GroupByColumns;

  /** for formatting dates. */
  protected DateFormat m_DateFormat;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Sheet             = null;
    m_AllColumns        = false;
    m_Select            = false;
    m_Delete            = false;
    m_Update            = false;
    m_Aggregate         = false;
    m_Columns           = new ArrayList<String>();
    m_RenameColumns     = new HashMap<String,String>();
    m_UpdateColumns     = new HashMap<String,Object>();
    m_SortColumns       = new ArrayList<String>();
    m_SortAsc           = new ArrayList<Boolean>();
    m_RowFinders        = new ArrayList<RowFinder>();
    m_Subsample         = null;
    m_Aggregates        = new HashMap<Aggregate,List<String>>();
    m_RenamedAggregates = new HashMap<String,String>();
    m_GroupByColumns    = new ArrayList<String>();
    m_SubProcess        = null;
    m_Rows              = null;
    m_LimitOffset       = 0;
    m_LimitMax          = -1;
    m_DateFormat        = DateUtils.getTimestampFormatterMsecs();
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
   * Returns the date formatter.
   *
   * @return 		the formatter
   */
  public DateFormat getDateFormat() {
    return m_DateFormat;
  }

  /**
   * Returns the format string of the current date/time format.
   * 
   * @return		the format string
   * @see		#getDateFormat()
   */
  public DateFormatString getDateFormatString() {
    return new DateFormatString(m_DateFormat.toPattern());
  }
  
  /**
   * Sets to return all columns.
   */
  public void useAllColumns() {
    m_AllColumns = true;
    if (isLoggingEnabled())
      getLogger().fine("all columns");
  }

  /**
   * Adds the name of a column to return.
   *
   * @param col the column name
   */
  public void addColumn(String col) {
    m_Columns.add(SpreadSheetColumnRange.escapeColumnName(col));
    if (isLoggingEnabled())
      getLogger().fine("column: " + col);
  }

  /**
   * Adds the name of a column to rename.
   *
   * @param col the current column name
   * @param newCol the new column name
   */
  public void renameColumn(String col, String newCol) {
    m_RenameColumns.put(col, newCol);
    
    if (isLoggingEnabled())
      getLogger().fine("rename: " + col + " -> " + newCol);
  }

  /**
   * Adds the name of a sort column.
   *
   * @param col the column name
   * @param asc whether to sort ascending
   */
  public void addSortColumn(String col, boolean asc) {
    m_SortColumns.add(col);
    m_SortAsc.add(asc);
    
    if (isLoggingEnabled())
      getLogger().fine("sort: " + col + ", asc: " + asc);
  }

  /**
   * Adds the name of a column to update with a new value.
   *
   * @param col the column name
   * @param value the new value
   */
  public void addUpdateColumn(String col, Object value) {
    m_UpdateColumns.put(col, value);
    
    if (isLoggingEnabled())
      getLogger().fine("update: " + col + " = " + value);
  }

  /**
   * Adds the aggregate to generate from a column.
   *
   * @param aggregate the aggregate
   * @param col the column name, null if COUNT
   */
  public void addAggregate(Aggregate agg, String col) {
    List<String>	cols;
    
    if (!m_Aggregates.containsKey(agg))
      m_Aggregates.put(agg, new ArrayList<String>());
    
    if (agg != Aggregate.COUNT) {
      col  = SpreadSheetColumnRange.escapeColumnName(col);
      cols = m_Aggregates.get(agg);
      if (!cols.contains(col))
	cols.add(col);
    }
    if (isLoggingEnabled())
      getLogger().fine("aggregate: " + agg + "/" + col);
  }

  /**
   * Sets the new column name for the aggregate generated from a column.
   *
   * @param aggregate the aggregate
   * @param col the column name, null if COUNT
   * @param newCol the new column name
   */
  public void renameAggregate(Aggregate agg, String col, String newCol) {
    if (agg == Aggregate.COUNT)
      m_RenamedAggregates.put(agg.toString(), newCol);
    else
      m_RenamedAggregates.put(col + "-" + agg, newCol);
    if (isLoggingEnabled())
      getLogger().fine("rename aggregate: " + agg + "/" + col + " -> " + newCol);
  }

  /**
   * Adds the name of a group by column.
   *
   * @param col the column name
   * @param value the new value
   */
  public void addGroupByColumn(String col) {
    m_GroupByColumns.add(SpreadSheetColumnRange.escapeColumnName(col));
    
    if (isLoggingEnabled())
      getLogger().fine("group by: " + col);
  }

  /**
   * Applies the row finder.
   *
   * @param finder the row finder to apply
   * @param log a logging message
   * @return the selected rows
   */
  public int[] applyRowFinder(RowFinder finder, String log) {
    int[]	result;
    
    result = finder.findRows(m_Sheet);
    if (isLoggingEnabled())
      getLogger().fine(log + ": " + Utils.arrayToString(result));
    
    return result;
  }

  /**
   * Sets the row finder to generate a subsample.
   *
   * @param finder the row finder to apply
   * @param log a logging message
   * @return the selected rows
   */
  public void setSubsampleRowFinder(RowFinder finder, String log) {
    m_Subsample = finder;
    if (isLoggingEnabled())
      getLogger().fine(log);
  }

  /**
   * Sets to create subset.
   */
  public void select() {
    m_Select = true;
    if (isLoggingEnabled())
      getLogger().fine("select");
  }

  /**
   * Sets to delete rows.
   */
  public void delete() {
    m_Delete = true;
    if (isLoggingEnabled())
      getLogger().fine("delete");
  }

  /**
   * Sets to update cells.
   */
  public void update() {
    m_Update = true;
    if (isLoggingEnabled())
      getLogger().fine("update");
  }

  /**
   * Sets to create subset from aggregates.
   */
  public void aggregate() {
    m_Aggregate = true;
    if (isLoggingEnabled())
      getLogger().fine("aggregate");
  }

  /**
   * Combines the row finders with logical AND.
   * 
   * @param c1 the first set of rows
   * @param c2 the second set of rows
   * @return the combined rows
   */
  public int[] combineWithAnd(int[] c1, int[] c2) {
    int[]	result;
    TIntHashSet	set;

    set = new TIntHashSet(c1);
    set.retainAll(c2);
    result = set.toArray();
    Arrays.sort(result);
    
    if (isLoggingEnabled())
      getLogger().fine("and: " + Utils.arrayToString(result));
    
    return result;
  }

  /**
   * Combines the row finders with logical OR.
   * 
   * @param c1 the first set of rows
   * @param c2 the second set of rows
   * @return the combined rows
   */
  public int[] combineWithOr(int[] c1, int[] c2) {
    int[]	result;
    TIntHashSet	set;

    set = new TIntHashSet(c1);
    set.addAll(c2);
    result = set.toArray();
    Arrays.sort(result);

    if (isLoggingEnabled())
      getLogger().fine("or: " + Utils.arrayToString(result));

    return result;
  }

  /**
   * Inverts the row finders.
   * 
   * @param c the rows to invert
   * @return the inverted rows
   */
  public int[] invert(int[] c) {
    int[]		result;
    TIntHashSet		set;
    TIntArrayList	list;
    int			i;
    
    set  = new TIntHashSet(c);
    list = new TIntArrayList(m_Sheet.getRowCount() - c.length);
    for (i = 0; i < m_Sheet.getRowCount(); i++) {
      if (!set.contains(i))
	list.add(i);
    }
    result = list.toArray();

    if (isLoggingEnabled())
      getLogger().fine("not: " + Utils.arrayToString(result));
    
    return result;
  }
  
  /**
   * Sets the limit.
   * 
   * @param offset	the offset (0 is offset for first row)
   * @param max		the maximum number of rows (>= 1)
   */
  public void setLimit(int offset, int max) {
    if (offset < 0)
      offset = 0;
    if (max < 1)
      max = 1;
    m_LimitOffset = offset;
    m_LimitMax    = max;
  }
  
  /**
   * Sets the rows to use.
   * 
   * @param value the rows
   */
  public void setRows(int[] value) {
    m_Rows = value;
  }
  
  /**
   * Returns the rows to use.
   * 
   * @return the rows
   */
  public int[] getRows() {
    return m_Rows;
  }

  /**
   * Returns the partial flow that was generated to process the spreadsheet.
   *
   * @return the partial flow, null if none available
   */
  public SubProcess getSubProcess() {
    return m_SubProcess;
  }

  /**
   * Returns the result of the evaluation.
   *
   * @return the result
   */
  public SpreadSheet getResult() {
    SpreadSheet result;
    String msg;
    Range rows;
  
    result       = null;
    m_SubProcess = null;
    
    // final rows selection
    SpreadSheetRowFilter rowFilter = new SpreadSheetRowFilter();
    ByIndex byIndex = new ByIndex();
    rows = new Range();
    if (m_Rows == null)
      rows.setRange(Range.ALL);
    else
      rows.setIndices(m_Rows);
    byIndex.setRows(rows);
    rowFilter.setFinder(byIndex);
    
    SubProcess sub = new SubProcess();
    if (m_Select) {
      // subset of columns?
      if (!m_AllColumns) {
        SpreadSheetReorderColumns reorder = new SpreadSheetReorderColumns();
        reorder.setOrder(Utils.flatten(m_Columns, ","));
        sub.add(reorder);
      }
      // rename columns?
      if (m_RenameColumns.size() > 0) {
	Convert conv = new Convert();
	MultiConversion multi = new MultiConversion();
	List<Conversion> list = new ArrayList<Conversion>();
	for (String col: m_RenameColumns.keySet()) {
	  RenameSpreadSheetColumn ren = new RenameSpreadSheetColumn();
	  ren.setNoCopy(true);
	  ren.setColumn(new SpreadSheetColumnIndex(col));
	  ren.setNewName(m_RenameColumns.get(col));
          list.add(ren);
        }
        multi.setSubConversions(list.toArray(new Conversion[list.size()]));
        conv.setConversion(multi);
        sub.add(conv);
      }
      sub.add(rowFilter);
      // subsample?
      if (m_Subsample != null) {
	SpreadSheetRowFilter subsample = new SpreadSheetRowFilter();
	subsample.setFinder(m_Subsample);
	sub.add(subsample);
      }
      // sorting?
      if (m_SortColumns.size() > 0) {
        SpreadSheetSort sort = new SpreadSheetSort();
        SpreadSheetColumnIndex[] cols = new SpreadSheetColumnIndex[m_SortColumns.size()];
        BaseBoolean[] order = new BaseBoolean[m_SortColumns.size()];
        for (int i = 0; i < m_SortColumns.size(); i++) {
          cols[i]  = new SpreadSheetColumnIndex(m_SortColumns.get(i));
          order[i] = new BaseBoolean("" + m_SortAsc.get(i));
        }
        sort.setNoCopy(true);
        sort.setSortColumn(cols);
        sort.setSortOrder(order);
        sub.add(sort);
      }
      // limit?
      if (m_LimitMax > 0) {
	Range limit = new Range((m_LimitOffset + 1) + Range.RANGE + (m_LimitOffset + m_LimitMax));
	SpreadSheetSubset subset = new SpreadSheetSubset();
	subset.setRows(limit);
	subset.setColumns(new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
	sub.add(subset);
      }
    }
    else if (m_Update) {
      for (String col: m_UpdateColumns.keySet()) {
	SpreadSheetSetCell setcell = new SpreadSheetSetCell();
	setcell.setNoCopy(true);
	setcell.setRow(rows);
	setcell.setColumn(new SpreadSheetColumnRange(col));
	setcell.setValue(m_UpdateColumns.get(col).toString());
	sub.add(setcell);
      }
    }
    else if (m_Delete) {
      rows.setInverted(true);
      byIndex.setRows(rows);
      sub.add(rowFilter);
    }
    else if (m_Aggregate) {
      sub.add(rowFilter);
      // create aggregate column range
      HashSet<String> cols = new HashSet<String>();
      for (Aggregate a: m_Aggregates.keySet())
	cols.addAll(m_Aggregates.get(a));
      List<String> all = new ArrayList<String>(cols);
      Collections.sort(all);
      SpreadSheetColumnRange aggRange = new SpreadSheetColumnRange();
      aggRange.setRange(Utils.flatten(all, ","));
      // create group by range
      SpreadSheetColumnRange keyRange = new SpreadSheetColumnRange();
      if (m_GroupByColumns.size() > 0)
	keyRange.setRange(Utils.flatten(m_GroupByColumns, ","));
      // aggregates
      List<Aggregate> aggs = new ArrayList<Aggregate>(m_Aggregates.keySet());
      Collections.sort(aggs);
      if (aggs.contains(Aggregate.COUNT)) {
	aggs.remove(Aggregate.COUNT);
	aggs.add(0, Aggregate.COUNT);
      }
      // aggregate
      SpreadSheetAggregate agg = new SpreadSheetAggregate();
      agg.setAggregateColumns(aggRange);
      agg.setKeyColumns(keyRange);
      agg.setAggregates(aggs.toArray(new Aggregate[aggs.size()]));
      sub.add(agg);
      // delete columns
      List<String> remove;
      for (Aggregate a: m_Aggregates.keySet()) {
	if (a == Aggregate.COUNT) {
	  remove = new ArrayList<String>(all);
	  remove.remove(0);
	  if (remove.size() > 0) {
	    SpreadSheetColumnRange remRange = new SpreadSheetColumnRange();
	    remRange.setRange(Utils.flatten(remove, ","));
	    SpreadSheetRemoveColumn remCol = new SpreadSheetRemoveColumn();
	    remCol.setPosition(remRange);
	    sub.add(remCol);
	  }
	  Convert conv = new Convert();
	  RenameSpreadSheetColumn ren = new RenameSpreadSheetColumn();
	  ren.setColumn(new SpreadSheetColumnIndex("" + (m_GroupByColumns.size() + 1)));
	  ren.setNewName(Aggregate.COUNT.toString());
	  conv.setConversion(ren);
	  sub.add(conv);
	}
	else {
	  remove = new ArrayList<String>(all);
	  remove.removeAll(m_Aggregates.get(a));
	  if (remove.size() > 0) {
	    SpreadSheetColumnRange remRange = new SpreadSheetColumnRange();
	    remRange.setRange(Utils.flatten(remove, ","));
	    SpreadSheetRemoveColumn remCol = new SpreadSheetRemoveColumn();
	    remCol.setPosition(remRange);
	    sub.add(remCol);
	  }
	}
      }
      // rename aggregates
      if (m_RenamedAggregates.size() > 0) {
	Convert conv = new Convert();
	MultiConversion multi = new MultiConversion();
	List<Conversion> list = new ArrayList<Conversion>();
	for (String col: m_RenamedAggregates.keySet()) {
	  RenameSpreadSheetColumn ren = new RenameSpreadSheetColumn();
	  ren.setNoCopy(true);
	  ren.setColumn(new SpreadSheetColumnIndex(col));
	  ren.setNewName(m_RenamedAggregates.get(col));
          list.add(ren);
        }
        multi.setSubConversions(list.toArray(new Conversion[list.size()]));
        conv.setConversion(multi);
        sub.add(conv);
      }
    }
    
    // transform spreadsheet
    m_SubProcess = (SubProcess) sub.shallowCopy();
    msg = sub.setUp();
    if (msg == null) {
      sub.input(new Token(getSheet().getClone()));
      msg = sub.execute();
      if ((msg == null) && (sub.hasPendingOutput()))
        result = (SpreadSheet) sub.output().getPayload();
    }

    if (msg != null)
      throw new IllegalStateException(msg);

    return result;
  }
}
