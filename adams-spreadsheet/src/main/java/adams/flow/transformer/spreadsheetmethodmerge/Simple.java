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
 * Simple.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.spreadsheetmethodmerge;

import adams.data.spreadsheet.SpreadSheet;

import java.util.Enumeration;

/**
 <!-- globalinfo-start -->
 * Just merges the spreadsheets side by side. Requires the spreadsheets to have the same number of rows.
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
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class Simple
  extends AbstractMerge {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = -6094521682120542873L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just merges the spreadsheets side by side. Requires the spreadsheets to have the same number of rows.";
  }

  /**
   * Hook method for performing checks before attempting the merge.
   *
   * @param spreadsheets The spreadsheets to merge.
   * @return null if successfully checked, otherwise error message.
   */
  @Override
  protected String check(SpreadSheet[] spreadsheets) {
    String result;
    int i;

    result = super.check(spreadsheets);

    if (result == null) {
      for (i = 1; i < spreadsheets.length; i++) {
        if (spreadsheets[0].getRowCount() != spreadsheets[i].getRowCount())
          return "Spreadsheet #" + (i + 1) + " has " + spreadsheets[i].getRowCount() + " rows instead of " + spreadsheets[0].getRowCount();
      }
    }

    return result;
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
    return new SimpleRowSetIterator(m_Spreadsheets);
  }

  /**
   * Enumeration class which just returns the concatenation of the source
   * data rows in order.
   */
  public static class SimpleRowSetIterator implements Enumeration<int[]> {

    /** The internal row counter. */
    private int m_NextRow = 0;

    /** The number of rows to output. */
    private int m_RowCount;

    /** The number of spreadsheets. */
    private int m_SpreadsheetCount;

    /**
     * Constructs an enumeration over the rows in the source spreadsheets.
     *
     * @param spreadsheets The source spreadsheets to enumerate.
     */
    private SimpleRowSetIterator(SpreadSheet[] spreadsheets) {
      m_RowCount = spreadsheets[0].getRowCount();
      m_SpreadsheetCount = spreadsheets.length;
    }

    @Override
    public boolean hasMoreElements() {
      return m_NextRow < m_RowCount;
    }

    @Override
    public int[] nextElement() {
      // Create the return array
      int[] nextElement = new int[m_SpreadsheetCount];

      // Add the row from each source spreadsheet
      for (int i = 0; i < nextElement.length; i++) {
        nextElement[i] = m_NextRow;
      }

      // Move to the next row
      m_NextRow++;

      // Return the row-set
      return nextElement;
    }
  }
}