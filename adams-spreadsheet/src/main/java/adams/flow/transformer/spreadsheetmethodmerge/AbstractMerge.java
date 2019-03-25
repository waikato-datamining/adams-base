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
 * AbstractMerge.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.spreadsheetmethodmerge;

import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.columnfinder.ColumnFinder;
import adams.data.spreadsheet.columnfinder.NullFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Base class for all spreadsheet merge methods.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public abstract class AbstractMerge
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 6105022032625366441L;

  /** The keyword to replace with the spreadsheet name in column renaming. */
  protected static final String SPREADSHEET_KEYWORD = "{SPREADSHEET}";

  /** The constant value for spreadsheets that do not have an input row for this output row. */
  protected static final int ROW_MISSING = -1;

  /** The column finder for selecting class columns. */
  protected ColumnFinder m_ClassFinder;

  /** The name of each spreadsheet to use in column renaming. */
  protected BaseString[] m_SpreadsheetNames;

  /** The regexs to use to find columns that require renaming. */
  protected BaseRegExp[] m_ColumnRenameFindRegexs;

  /** The format strings specifying how to rename columns. */
  protected BaseString[] m_ColumnRenameFormatStrings;

  /** The name to give the resulting spreadsheet. */
  protected String m_MergedSpreadsheetName;

  /** Whether to check columns with multiple sources for equal values among those sources. */
  protected boolean m_EnsureEqualValues;

  /** The source spreadsheets we are merging. */
  protected SpreadSheet[] m_Spreadsheets;

  /** The set of class columns for the given spreadsheets. */
  protected int[][] m_ClassColumns;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "class-finder", "classFinder",
      new NullFinder());

    m_OptionManager.add(
      "spreadsheet-names", "spreadsheetNames",
      new BaseString[0]);

    m_OptionManager.add(
      "column-renames-exp", "columnRenamesExp",
      new BaseRegExp[0]);

    m_OptionManager.add(
      "column-renames-format", "columnRenamesFormat",
      new BaseString[0]);

    m_OptionManager.add(
      "output-name", "outputName",
      "output");

    m_OptionManager.add(
      "ensure-equal-values", "ensureEqualValues",
      false);
  }

  /**
   * Gets the method to use for finding class columns in the
   * source spreadsheets.
   *
   * @return The class-matching method to use.
   */
  public ColumnFinder getClassFinder() {
    return m_ClassFinder;
  }

  /**
   * Sets the method to use for finding class columns in the
   * source spreadsheets.
   *
   * @param value The method to use.
   */
  public void setClassFinder(ColumnFinder value) {
    m_ClassFinder = value;
    reset();
  }

  /**
   * Gets the tip-text for the class-matching method option.
   *
   * @return The tip-text as a String.
   */
  public String classFinderTipText() {
    return "The method to use to find class columns in the spreadsheets.";
  }

  /**
   * Gets the list of names to use in column renaming in place of the
   * {SPREADSHEET} keyword.
   *
   * @return The list of spreadsheet names.
   */
  public BaseString[] getSpreadsheetNames() {
    return m_SpreadsheetNames;
  }

  /**
   * Sets the list of names to use in column renaming in place of the
   * {SPREADSHEET} keyword.
   *
   * @param value The list of spreadsheet names.
   */
  public void setSpreadsheetNames(BaseString[] value) {
    if (value == null) value = new BaseString[0];

    if (m_ColumnRenameFindRegexs != null && value.length < m_ColumnRenameFindRegexs.length) {
      BaseString[] expandedValue = new BaseString[m_ColumnRenameFindRegexs.length];

      for (int i = 0; i < m_ColumnRenameFindRegexs.length; i++) {
	if (i < value.length) {
	  expandedValue[i] = value[i];
	} else {
	  expandedValue[i] = new BaseString("Spreadsheet" + i);
	}
      }

      value = expandedValue;
    }

    m_SpreadsheetNames = value;

    reset();
  }

  /**
   * Gets the tip-text for the spreadsheet names option.
   *
   * @return The tip-text as a String.
   */
  public String spreadsheetNamesTipText() {
    return "The list of spreadsheet names to use in column renaming.";
  }

  /**
   * Gets the array of column rename expressions.
   *
   * @return The array of regexs.
   */
  public BaseRegExp[] getColumnRenamesExp() {
    return m_ColumnRenameFindRegexs;
  }

  /**
   * Sets the array of column rename expressions.
   *
   * @param value The array of regexs.
   */
  public void setColumnRenamesExp(BaseRegExp[] value) {
    m_ColumnRenameFindRegexs = value;
    setColumnRenamesFormat(m_ColumnRenameFormatStrings);
    setSpreadsheetNames(m_SpreadsheetNames);
    reset();
  }

  /**
   * Gets the tip-text for the column-renaming regexs option.
   *
   * @return The tip-text as a String.
   */
  public String columnRenamesExpTipText() {
    return "The expressions to use to select column names for renaming (one per spreadsheet).";
  }

  /**
   * Gets the array of format strings used for column renaming.
   *
   * @return The array of format strings.
   */
  public BaseString[] getColumnRenamesFormat() {
    return m_ColumnRenameFormatStrings;
  }

  /**
   * Sets the array of format strings used for column renaming.
   *
   * @param value The array of format strings.
   */
  public void setColumnRenamesFormat(BaseString[] value) {
    if (value == null) value = new BaseString[0];

    if (m_ColumnRenameFindRegexs != null && value.length < m_ColumnRenameFindRegexs.length) {
      value = (BaseString[]) Utils.adjustArray(value, m_ColumnRenameFindRegexs.length, new BaseString("$0"));
    }

    m_ColumnRenameFormatStrings = value;

    reset();
  }

  /**
   * Gets the tip-text for the column renaming format strings option.
   *
   * @return The tip-text as a String.
   */
  public String columnRenamesFormatTipText() {
    return "One format string for each renaming expression to specify how to rename the column. " +
      "Can contain the " + SPREADSHEET_KEYWORD + " keyword which will be replaced by the spreadsheet name, " +
      "and also group identifiers which will be replaced by groups from the renaming regex.";
  }

  /**
   * Gets the name to use for the merged spreadsheet.
   *
   * @return The name to use.
   */
  public String getOutputName() {
    return m_MergedSpreadsheetName;
  }

  /**
   * Sets the name to use for the merged spreadsheet.
   *
   * @param value The name to use.
   */
  public void setOutputName(String value) {
    m_MergedSpreadsheetName = value;
    reset();
  }

  /**
   * Gets the tip-text for the output name option.
   *
   * @return The tip-text as a String.
   */
  public String outputNameTipText() {
    return "The name to use for the merged spreadsheet.";
  }

  /**
   * Gets whether to check all data-sources for a merged column have
   * the same value.
   *
   * @return True if value equality should be checked, false if not.
   */
  public boolean getEnsureEqualValues() {
    return m_EnsureEqualValues;
  }

  /**
   * Sets whether to check all data-sources for a merged column have
   * the same value.
   *
   * @param value True if value equality should be checked, false if not.
   */
  public void setEnsureEqualValues(boolean value) {
    m_EnsureEqualValues = value;
    reset();
  }

  /**
   * Gets the tip-text for the ensure-equal-values option.
   *
   * @return The tip-text as a String.
   */
  public String ensureEqualValuesTipText() {
    return "Whether multiple column being merged into a single column require equal values " +
      "from all sources.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns just null.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Sets the value of the given column in the given row to
   * the given value (handles object conversion).
   *
   * @param toSet          The row against which the value should be set.
   * @param columnIndex    The index of the column against which to
   *                       set the value.
   * @param value          The value to set the column to.
   */
  protected void setValue(DataRow toSet, int columnIndex, Object value) {
    if (toSet == null) return;

    toSet.getCell(columnIndex).setNative(value);
  }

  /**
   * Gets the value of the specified column from the given row.
   *
   * @param toGetFrom      The row to get a value from.
   * @param columnIndex    The index of the value's column.
   * @return The value of the row at the given index.
   */
  protected Object getValue(DataRow toGetFrom, int columnIndex) {
    if (toGetFrom == null) return null;

    return toGetFrom.getCell(columnIndex).getNative();
  }

  /**
   * Hook method for performing checks before attempting the merge.
   *
   * @param spreadsheets The spreadsheets to merge.
   * @return null if successfully checked, otherwise error message.
   */
  protected String check(SpreadSheet[] spreadsheets) {
    // Check the source spreadsheets were provided
    if (spreadsheets == null)
      return "No spreadsheets to merge!";

    // Check that at least 2 spreadsheets are available to merge
    if (spreadsheets.length < 2)
      return "Require at least 2 spreadsheets to merge, but " + spreadsheets.length + " were provided.";

    // Make sure an output name is provided
    if (m_MergedSpreadsheetName.length() == 0)
      return "Must provide a name for the output spreadsheet.";

    // Check that there are enough spreadsheet names provided
    if (m_SpreadsheetNames.length < m_ColumnRenameFindRegexs.length) {
      return "Not enough spreadsheet names supplied for column renaming (require " +
	m_ColumnRenameFindRegexs.length + ", have " + m_SpreadsheetNames.length + ").";
    }

    // Check that there are enough renaming format strings provided
    if (m_ColumnRenameFormatStrings.length < m_ColumnRenameFindRegexs.length) {
      return "Not enough format strings supplied for column renaming (require " +
	m_ColumnRenameFindRegexs.length + ", have " + m_ColumnRenameFormatStrings.length + ").";
    }

    // All checks passed
    return null;
  }

  /**
   * Checks the column mapping for correctness.
   *
   * @param columnMapping  The column mapping.
   * @return  Null if all mappings are okay, or an error message if not.
   */
  protected String checkColumnMapping(Map<String, List<SourceColumn>> columnMapping) {
    return null;
  }

  /**
   * Merges the spreadsheets.
   *
   * @param spreadsheets The spreadsheets to merge.
   * @return The merged spreadsheet.
   */
  public SpreadSheet merge(SpreadSheet[] spreadsheets) {
    // Reset the internal state
    resetInternalState(spreadsheets);

    // Validate the input
    String msg = check(spreadsheets);
    if (msg != null) throw new IllegalStateException(msg);

    // Create the column mapping
    Map<String, List<SourceColumn>> columnMapping = createColumnMapping();

    // Make sure the mapping is valid
    msg = checkColumnMapping(columnMapping);
    if (msg != null) throw new IllegalStateException(msg);

    // Create the empty resulting spreadsheet
    SpreadSheet mergedSpreadsheet = createEmptyResultantSpreadsheet(columnMapping);

    // Get the row-set iterator over the spreadsheets
    Enumeration<int[]> rowSetEnumeration = getRowSetEnumeration();

    // Add the data to the merged spreadsheet
    while (rowSetEnumeration.hasMoreElements()) {
      // Get the row-set to work from
      int[] rowSet = rowSetEnumeration.nextElement();

      // Create a new row for the merged spreadsheet
      DataRow mergedRow = mergedSpreadsheet.addRow();

      // Process each column of the merged spreadsheet in turn
      for (int columnIndex = 0; columnIndex < mergedSpreadsheet.getColumnCount(); columnIndex++) {
	// Get the next column to copy
	String column = mergedSpreadsheet.getColumnName(columnIndex);

	// Find the source(s) of the column's data
	List<SourceColumn> sourceColumns = columnMapping.get(column);

	// Get the value of this column from it's source(s)
	Object value = m_EnsureEqualValues ?
	  getValueEnsureEqual(rowSet, sourceColumns) :
	  getValueFirstAvailable(rowSet, sourceColumns);

	// Copy the value to the merged spreadsheet if it's found
	if (value != null) setValue(mergedRow, columnIndex, value);
      }
    }

    // Return the resulting merged spreadsheet
    return mergedSpreadsheet;
  }

  /**
   * Gets the first encountered source value for a merged column.
   *
   * @param rowSet                  The row-set of source data.
   * @param sourceColumnElements The source column mapping elements.
   * @return The value of the merged column.
   */
  protected Object getValueFirstAvailable(int[] rowSet, List<SourceColumn> sourceColumnElements) {
    // Try each source in turn
    for (SourceColumn element : sourceColumnElements) {
      // Get the row from the row-set
      int rowIndex = rowSet[element.spreadsheetIndex];

      // Skip spreadsheets that don't have source data for this column
      if (rowIndex == ROW_MISSING) continue;

      // Get the source spreadsheet row
      DataRow row = m_Spreadsheets[element.spreadsheetIndex].getRow(rowIndex);

      // Skip spreadsheets that don't have source data for this column
      if (row.getCell(element.columnIndex).isMissing()) continue;

      // Return the value of the source data for this column
      return getValue(row, element.columnIndex);
    }

    // No value found
    return null;
  }

  /**
   * Gets the value of the mapped column, ensuring that all possible sources either provide
   * a missing value or the same value as each other.
   *
   * @param rowSet                  The row-set of source data.
   * @param sources 		    The source columns.
   * @return The value of the merged column.
   */
  protected Object getValueEnsureEqual(int[] rowSet, List<SourceColumn> sources) {
    Object value = null;
    SourceColumn valueElement = null;
    int valueRowIndex = ROW_MISSING;

    for (SourceColumn element : sources) {
      // Get the row from the row-set
      int rowIndex = rowSet[element.spreadsheetIndex];

      // Skip spreadsheets that don't have source data for this column
      if (rowIndex == ROW_MISSING) continue;

      // Get the source spreadsheet row
      DataRow row = m_Spreadsheets[element.spreadsheetIndex].getRow(rowIndex);

      // Skip spreadsheets that don't have source data for this column
      if (row.getCell(element.columnIndex).isMissing()) continue;

      // Get the value of the source data for this column element
      Object currentValue = getValue(row, element.columnIndex);

      // Make sure it equals any previously found values
      if (value == null) {
	value = currentValue;
	valueElement = element;
	valueRowIndex = rowIndex;
      }
      else if (!value.equals(currentValue)) {
	throw new IllegalStateException("Merging columns have multiple different source values! " +
	  "(" + currentValue + " in " + m_SpreadsheetNames[element.spreadsheetIndex] + ", column " + element.columnName + ", row " + rowIndex + " " +
	  "instead of " + value + " in " + m_SpreadsheetNames[valueElement.spreadsheetIndex] + ", column " + valueElement.columnName + ", row " + valueRowIndex + ")");
      }
    }

    return value;
  }

  /**
   * Creates a mapping from the columns in each input spreadsheet to the corresponding
   * column in the merged spreadsheet.
   *
   * @return The mapping from input column names to output column names.
   */
  protected Map<String, List<SourceColumn>> createColumnMapping() {
    // Create the mapping
    Map<String, List<SourceColumn>> mapping = new HashMap<>();

    // Process each input column in turn
    for (int spreadsheetIndex = 0; spreadsheetIndex < m_Spreadsheets.length; spreadsheetIndex++) {
      // Get the next spreadsheet to process
      SpreadSheet spreadsheet = m_Spreadsheets[spreadsheetIndex];

      // Go through each column of the spreadsheet in turn
      for (int columnIndex = 0; columnIndex < spreadsheet.getColumnCount(); columnIndex++) {
	// Get the next column to process
	String columnName = spreadsheet.getColumnName(columnIndex);

	// Create the mapping element
	SourceColumn mappingElement = new SourceColumn(spreadsheetIndex, columnIndex, columnName);

	// Get the column's mapped name in the merged spreadsheet
	String mappedColumnName = getMappedColumnName(mappingElement);

	// Initialise the mapping list if there isn't one already
	if (!mapping.containsKey(mappedColumnName))
	  mapping.put(mappedColumnName, new LinkedList<>());

	// Put the mapping into the return value
	mapping.get(mappedColumnName).add(mappingElement);
      }
    }

    // Return the mapping
    return mapping;
  }

  /**
   * Checks if any of the source columns in the given list is a class
   * column.
   *
   * @param sources The source columns to check.
   * @return  True if a source column is a class, false if none are.
   */
  protected boolean isAnyClassColumn(List<SourceColumn> sources) {
    // Check each source in turn
    for (SourceColumn source : sources) {
      // If this source is a class column, return a positive hit
      if (isClassColumn(source)) return true;
    }

    // No sources were class columns
    return false;
  }

  /**
   * Checks if the given source column is a class column.
   *
   * @param source  The source column to check.
   * @return  True if the source is a class column, false if not.
   */
  protected boolean isClassColumn(SourceColumn source) {
    // Defer
    return isClassColumn(source.spreadsheetIndex, source.columnIndex);
  }

  /**
   * Whether the given column is the name of a class column.
   *
   * @param spreadsheetIndex  The spreadsheet the column is in.
   * @param columnIndex  The index of the column in the spreadsheet.
   * @return True if the given column name is the name of a class column,
   * 	     false otherwise.
   */
  protected boolean isClassColumn(int spreadsheetIndex, int columnIndex) {
    if (m_ClassColumns == null) recordClassColumns();

    return Arrays.binarySearch(m_ClassColumns[spreadsheetIndex], columnIndex) >= 0;
  }

  /**
   * Scans the spreadsheets for columns that should be considered classes,
   * and keeps a record of them.
   */
  protected void recordClassColumns() {
    // Create the set of class columns
    m_ClassColumns = new int[m_Spreadsheets.length][];

    // Process each spreadsheet for class columns
    for (int i = 0; i < m_Spreadsheets.length; i++) {
      // Get the next spreadsheet
      SpreadSheet spreadsheet = m_Spreadsheets[i];

      // Find the class columns
      int[] classColumns = m_ClassFinder.findColumns(spreadsheet);

      // Make sure the array is sorted (so we can do binary search)
      Arrays.sort(classColumns);

      // Record the class columns
      m_ClassColumns[i] = classColumns;
    }
  }

  /**
   * Creates the resultant spreadsheet, ready to be filled with data.
   *
   * @param columnMapping The mapping from merged column names to their
   *                      original names.
   * @return The empty Spreadsheet object for the merged spreadsheet.
   */
  protected SpreadSheet createEmptyResultantSpreadsheet(Map<String, List<SourceColumn>> columnMapping) {
    // Create a list for ordering the columns, and a mapping to the actual
    // columns
    List<List<SourceColumn>> orderingList = new LinkedList<>();
    Map<List<SourceColumn>, String> columns = new HashMap<>();

    // Copy the mapped columns from their respective spreadsheets
    for (String mappedColumnName : columnMapping.keySet()) {
      List<SourceColumn> sources = columnMapping.get(mappedColumnName);
      columns.put(sources, mappedColumnName);
      orderingList.add(sources);
    }

    // Order the new columns
    orderingList.sort(this::compare);

    // Create the ordered array-list of the columns
    ArrayList<String> orderedColumns = new ArrayList<>(orderingList.size());
    for (List<SourceColumn> orderingElement : orderingList) {
      orderedColumns.add(columns.get(orderingElement));
    }

    // Create the spreadsheet
    SpreadSheet result = new DefaultSpreadSheet();
    result.setName(m_MergedSpreadsheetName);
    for (int i = 0; i < orderedColumns.size(); i++) {
      result.insertColumn(i, orderedColumns.get(i));
    }

    return result;
  }

  /**
   * Compares two ColumnMappingElements to determine the order in which their
   * mapped columns should appear in the merged spreadsheet.
   *
   * @param sources1 The first element to compare.
   * @param sources2 The second element to compare.
   * @return sources1 < sources2 => -1,
   * sources1 > sources2 => 1,
   * otherwise 0;
   */
  protected int compare(List<SourceColumn> sources1, List<SourceColumn> sources2) {
    // Check if either column is a class column
    boolean className1 = isAnyClassColumn(sources1);
    boolean className2 = isAnyClassColumn(sources2);

    // Put class columns after everything else
    if (className1 && !className2) {
      return 1;
    }
    else if (!className1 && className2) {
      return -1;
    }
    else {
      // Otherwise, just order by first source
      return sources1.get(0).compareTo(sources2.get(0));
    }
  }

  /**
   * Gets the name of the column in the merged spreadsheet that the given
   * source column maps to.
   *
   * @param source  The source column.
   * @return The name of the mapped column in the merged spreadsheet.
   */
  protected String getMappedColumnName(SourceColumn source) {
    // Can't rename class names
    if (isClassColumn(source)) return source.columnName;

    // See if we have a rename expression for the spreadsheet
    if (source.spreadsheetIndex < m_ColumnRenameFindRegexs.length) {
      // Get the rename expression for the spreadsheet
      BaseRegExp renameRegex = m_ColumnRenameFindRegexs[source.spreadsheetIndex];

      // Get the regex matcher for the column name
      Matcher columnNameMatcher = renameRegex.patternValue().matcher(source.columnName);

      // Rename the column if it is matched
      if (columnNameMatcher.matches()) {
	// Initialise the mapped name with the format string
	String mappedString = m_ColumnRenameFormatStrings[source.spreadsheetIndex].stringValue();

	// Replace the {SPREADSHEET} keyword with the spreadsheet name
	mappedString = mappedString.replace(SPREADSHEET_KEYWORD, m_SpreadsheetNames[source.spreadsheetIndex].stringValue());

	// Replace any group identifiers with the corresponding group match string
	for (int groupIndex = columnNameMatcher.groupCount(); groupIndex >= 0; groupIndex--) {
	  String groupMatch = columnNameMatcher.group(groupIndex);

	  mappedString = mappedString.replace("$" + groupIndex, groupMatch);
	}

	// Return the renamed column name
	return mappedString;
      }
    }

    // Couldn't rename, return the original column name
    return source.columnName;
  }

  /**
   * Resets the internal state of the merge method when new spreadsheets are supplied.
   *
   * @param spreadsheets The spreadsheets being merged.
   */
  protected void resetInternalState(SpreadSheet[] spreadsheets) {
    m_Spreadsheets = spreadsheets;
    m_ClassColumns = null;
  }

  /**
   * Allows specific merge methods to specify the order in which rows are placed
   * into the merged spreadsheet, and which rows from the source spreadsheets are used
   * for the source data.
   *
   * @return An enumeration of the source rows, one row for each spreadsheet.
   */
  protected abstract Enumeration<int[]> getRowSetEnumeration();

  /**
   * Helper class for determining the mapping from input columns in the
   * source spreadsheets to output columns in the merged spreadsheet.
   */
  protected static class SourceColumn implements Comparable<SourceColumn> {

    /** The index of the source spreadsheet. */
    public final int spreadsheetIndex;

    /** The index of the source column in the source spreadsheet. */
    public final int columnIndex;

    /** The name of the source column in the source spreadsheet. */
    public final String columnName;

    /**
     * Standard constructor.
     *
     * @param spreadsheetIndex     The index of the source spreadsheet.
     * @param columnIndex   The index of the source column in the source spreadsheet.
     * @param columnName    The name of the source column in the source spreadsheet.
     */
    public SourceColumn(int spreadsheetIndex, int columnIndex, String columnName) {
      this.spreadsheetIndex = spreadsheetIndex;
      this.columnIndex = columnIndex;
      this.columnName = columnName;
    }

    @Override
    public String toString() {
      return columnName +
	'[' +
	spreadsheetIndex +
	", " +
	columnIndex +
	']';
    }

    @Override
    public int compareTo(SourceColumn o) {
      if (spreadsheetIndex < o.spreadsheetIndex) {
	return -1;
      } else if (spreadsheetIndex > o.spreadsheetIndex) {
	return 1;
      } else {
	return Integer.compare(columnIndex, o.columnIndex);
      }
    }

  }
}
