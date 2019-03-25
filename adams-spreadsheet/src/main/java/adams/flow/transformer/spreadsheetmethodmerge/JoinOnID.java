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
 * JoinOnID.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.spreadsheetmethodmerge;

import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.SpreadSheet;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Joins the spreadsheets by concatenating rows that share a unique ID.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-class-finder &lt;adams.data.spreadsheet.columnfinder.ColumnFinder&gt; (property: classFinder)
 * &nbsp;&nbsp;&nbsp;The method to use to find class columns in the spreadsheets.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.columnfinder.NullFinder
 * </pre>
 *
 * <pre>-spreadsheet-names &lt;adams.core.base.BaseString&gt; [-spreadsheet-names ...] (property: spreadsheetNames)
 * &nbsp;&nbsp;&nbsp;The list of spreadsheet names to use in column renaming.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-column-renames-exp &lt;adams.core.base.BaseRegExp&gt; [-column-renames-exp ...] (property: columnRenamesExp)
 * &nbsp;&nbsp;&nbsp;The expressions to use to select column names for renaming (one per spreadsheet
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-column-renames-format &lt;adams.core.base.BaseString&gt; [-column-renames-format ...] (property: columnRenamesFormat)
 * &nbsp;&nbsp;&nbsp;One format string for each renaming expression to specify how to rename
 * &nbsp;&nbsp;&nbsp;the column. Can contain the {SPREADSHEET} keyword which will be replaced
 * &nbsp;&nbsp;&nbsp;by the spreadsheet name, and also group identifiers which will be replaced
 * &nbsp;&nbsp;&nbsp;by groups from the renaming regex.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-output-name &lt;java.lang.String&gt; (property: outputName)
 * &nbsp;&nbsp;&nbsp;The name to use for the merged spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: output
 * </pre>
 *
 * <pre>-ensure-equal-values &lt;boolean&gt; (property: ensureEqualValues)
 * &nbsp;&nbsp;&nbsp;Whether multiple column being merged into a single column require equal
 * &nbsp;&nbsp;&nbsp;values from all sources.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-unique-id &lt;java.lang.String&gt; (property: uniqueID)
 * &nbsp;&nbsp;&nbsp;The name of the column to use as the joining key for the merge.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-complete-rows-only &lt;boolean&gt; (property: completeRowsOnly)
 * &nbsp;&nbsp;&nbsp;Whether only those IDs that have source data in all spreadsheets should
 * &nbsp;&nbsp;&nbsp;be merged.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class JoinOnID
  extends AbstractMerge {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = -481246610037807743L;

  /** The name of the column to use as the merge key. */
  protected String m_UniqueID;

  /** Whether or not to skip IDs that don't exist in all source spreadsheets. */
  protected boolean m_CompleteRowsOnly;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Joins the spreadsheets by concatenating rows that share a unique ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    // Define the default options
    super.defineOptions();

    // Define the unique ID option
    m_OptionManager.add(
      "unique-id", "uniqueID",
      "");

    // Define the complete-rows-only option
    m_OptionManager.add(
      "complete-rows-only", "completeRowsOnly",
      false);
  }

  /**
   * Gets the name of the unique ID column that the merge is joining on.
   *
   * @return The name of the unique ID column.
   */
  public String getUniqueID() {
    return m_UniqueID;
  }

  /**
   * Sets the name of the unique ID column that the merge is joining on.
   *
   * @param value The name of the unique ID column.
   */
  public void setUniqueID(String value) {
    m_UniqueID = value;
    reset();
  }

  /**
   * Gets the tip-text for the unique ID option.
   *
   * @return The tip-text as a String.
   */
  public String uniqueIDTipText() {
    return "The name of the column to use as the joining key for the merge.";
  }

  /**
   * Gets whether incomplete rows should be skipped.
   *
   * @return Whether incomplete rows should be skipped.
   */
  public boolean getCompleteRowsOnly() {
    return m_CompleteRowsOnly;
  }

  /**
   * Sets whether incomplete rows should be skipped.
   *
   * @param value Whether incomplete rows should be skipped.
   */
  public void setCompleteRowsOnly(boolean value) {
    m_CompleteRowsOnly = value;
    reset();
  }

  /**
   * Gets the tip-text for the complete-rows-only option.
   *
   * @return The tip-text as a String.
   */
  public String completeRowsOnlyTipText() {
    return "Whether only those IDs that have source data in all spreadsheets should be merged.";
  }

  /**
   * Checks that each of the given spreadsheets has the unique ID column.
   *
   * @param spreadsheets The spreadsheets that are to be merged.
   * @return Null if all spreadsheeet have the unique ID column, otherwise an error message.
   */
  protected String checkAllSpreadsheetsHaveIDColumn(SpreadSheet[] spreadsheets) {
    // Check each spreadsheet in turn
    for (SpreadSheet spreadsheet : spreadsheets) {
      // Get the merge column
      int idColumnIndex = findColumnIndexOfUniqueID(spreadsheet);

      // Check the merge column exists
      if (idColumnIndex == -1)
        return "Dataset " +
          spreadsheet.getName() +
          " does not have the ID column (" +
          m_UniqueID +
          ")";
    }

    // If we get here, all spreadsheets check out
    return null;
  }

  /**
   * Whether the given column name is the name of the unique ID
   * column.
   *
   * @param columnName The column name to check.
   * @return True if the given column name is the unique ID name,
   * false otherwise.
   */
  protected boolean isUniqueIDName(String columnName) {
    return columnName.equals(m_UniqueID);
  }

  /**
   * Finds the index of the unique ID column in the given spreadsheet.
   *
   * @param spreadsheet The spreadsheet to search.
   * @return The index of the unique ID column, or -1 if not found.
   */
  protected int findColumnIndexOfUniqueID(SpreadSheet spreadsheet) {
    return spreadsheet.getHeaderRow().indexOfContent(m_UniqueID);
  }

  /**
   * Compares two lists of source columns to determine the order in which their
   * mapped columns should appear in the merged spreadsheet.
   *
   * @param sources1 The source columns of the first mapped column.
   * @param sources2 The source columns of the second mapped column.
   * @return sources1 < sources2 => -1,
   * sources1 > sources2 => 1,
   * otherwise 0;
   */
  @Override
  protected int compare(List<SourceColumn> sources1, List<SourceColumn> sources2) {
    // Check if the column is the unique ID column
    boolean idName1 = isUniqueIDName(sources1.get(0).columnName);
    boolean idName2 = isUniqueIDName(sources2.get(0).columnName);

    // Put the ID column before all other columns
    if (idName1 && !idName2) {
      return -1;
    }
    else if (!idName1 && idName2) {
      return 1;
    }
    else {
      // Otherwise, just use the default ordering
      return super.compare(sources1, sources2);
    }
  }

  /**
   * Gets the name of the column in the merged spreadsheet that the given
   * source column maps to.
   *
   * @param source  The source column.
   * @return The name of the mapped column in the merged spreadsheet.
   */
  @Override
  protected String getMappedColumnName(SourceColumn source) {
    if (isUniqueIDName(source.columnName)) {
      // The unique ID name can't be renamed
      return source.columnName;
    }
    else {
      // Otherwise, default
      return super.getMappedColumnName(source);
    }
  }

  /**
   * Allows specific merge methods to specify the order in which rows are placed
   * into the merged spreadsheet, and which rows from the source spreadsheets are used
   * for the source data.
   *
   * @return An enumeration of the source rows, one row for each spreadsheet.
   */
  @Override
  protected Enumeration<int[]> getRowSetEnumeration() {
    return new UniqueIDEnumeration(m_Spreadsheets);
  }

  /**
   * Hook method for performing checks before attempting the merge.
   *
   * @param datasets the spreadsheets to merge
   * @return null if successfully checked, otherwise error message
   */
  @Override
  protected String check(SpreadSheet[] datasets) {
    // Perform the standard checks first
    String result = super.check(datasets);
    if (result != null) return result;

    // Check all spreadsheets have the ID attribute
    return checkAllSpreadsheetsHaveIDColumn(datasets);
  }

  /**
   * Makes sure the source data for each mapped column is the same type.
   *
   * @param columnMapping  The column mapping.
   * @return  Null if all mappings are okay, or an error message if not.
   */
  @Override
  protected String checkColumnMapping(Map<String, List<SourceColumn>> columnMapping) {
    // Perform the standard checks first
    String result = super.checkColumnMapping(columnMapping);
    if (result != null) return result;

    // Make sure the unique ID is not also a class column
    if (isAnyClassColumn(columnMapping.get(m_UniqueID)))
      result = "The provided unique ID (" + getUniqueID() + ") is also a class column.";

    return result;
  }

  /**
   * Enumeration class that returns the rows from the source spreadsheets
   * joined on the unique ID column.
   */
  public class UniqueIDEnumeration implements Enumeration<int[]> {

    /** The set of all unique IDs across the spreadsheets. */
    private Map<Object, int[]> m_UniqueIDRowMap;

    /** Iterator over the unique IDs. */
    private Iterator<Object> m_InternalIterator;

    /**
     * Constructs an enumeration over the unique keys in the
     * given spreadsheets, and the rows that they appear in.
     *
     * @param spreadsheets The spreadsheets being merged.
     */
    private UniqueIDEnumeration(SpreadSheet[] spreadsheets) {
      // Generate the lookup map
      recordUniqueIDs(spreadsheets);

      // Initialise the internal iterator
      m_InternalIterator = m_UniqueIDRowMap.keySet().iterator();
    }

    /**
     * Records the set of unique IDs that exist in the given spreadsheets, and
     * also maps them to the rows in which they appear in the individual
     * spreadsheets.
     *
     * @param spreadsheetsToMerge The set of datasets being merged.
     */
    private void recordUniqueIDs(SpreadSheet[] spreadsheetsToMerge) {
      // Create the map
      m_UniqueIDRowMap = new LinkedHashMap<>();

      // Process each spreadsheet in turn
      for (int spreadsheetIndex = 0; spreadsheetIndex < spreadsheetsToMerge.length; spreadsheetIndex++) {
        // Get the next spreadsheet to process
        SpreadSheet spreadsheet = spreadsheetsToMerge[spreadsheetIndex];

        // Find the index of the column that contains the unique ID
        int uniqueIDColumnIndex = findColumnIndexOfUniqueID(spreadsheet);

        // If the unique ID column doesn't exist in this spreadsheet, we can skip it
        if (uniqueIDColumnIndex == -1) continue;

        // Process the unique IDs one-by-one
        for (int rowIndex = 0; rowIndex < spreadsheet.getRowCount(); rowIndex++) {
          // Get the next row
          DataRow row = spreadsheet.getRow(rowIndex);

          // Get the unique ID for this row
          Object id = getValue(row, uniqueIDColumnIndex);

          // If the unique ID is missing, skip this row
          if (id == null) continue;

          // Make sure the mapping has an entry for this ID (create it
          // if it doesn't exist)
          if (!m_UniqueIDRowMap.containsKey(id))
            m_UniqueIDRowMap.put(id, initialiseRowSet(spreadsheetsToMerge.length));

          // Get the mapping for the unique ID
          int[] rowInstanceTable = m_UniqueIDRowMap.get(id);

          // Record which row contains the unique ID for this spreadsheet
          rowInstanceTable[spreadsheetIndex] = rowIndex;
        }
      }

      // If we only want complete rows, remove any incomplete ones
      if (getCompleteRowsOnly()) removeIncompleteRows();
    }

    /**
     * Creates an empty row-set where all rows are missing.
     *
     * @param size The size of the row-set (should equal the number of merging spreadsheets).
     * @return The initialised row-set.
     */
    private int[] initialiseRowSet(int size) {
      // Create the row-set
      int[] rowSet = new int[size];

      // Set all rows to missing
      for (int i = 0; i < size; i++) {
        rowSet[i] = ROW_MISSING;
      }

      // Return the row-set
      return rowSet;
    }

    /**
     * Removes any entries from the ID map that don't have source
     * data in all source spreadsheets.
     */
    private void removeIncompleteRows() {
      // Get an iterator over the ID map
      Iterator<Object> idIterator = m_UniqueIDRowMap.keySet().iterator();

      // Check each entry in turn
      while (idIterator.hasNext()) {
        // Get the ID for this row
        Object id = idIterator.next();

        // Get the source data for this row
        int[] rowSet = m_UniqueIDRowMap.get(id);

        // Check that there are no null entries
        for (int rowEntry : rowSet) {
          // If there is a null entry, remove this row
          if (rowEntry == ROW_MISSING) {
            idIterator.remove();
            break;
          }
        }
      }
    }

    @Override
    public boolean hasMoreElements() {
      return m_InternalIterator.hasNext();
    }

    @Override
    public int[] nextElement() {
      Object nextID = m_InternalIterator.next();

      return m_UniqueIDRowMap.get(nextID);
    }
  }
}