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
 * Merge.java
 * Copyright (C) 2019-2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.multispreadsheetoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.spreadsheetmethodmerge.AbstractMerge;
import adams.flow.transformer.spreadsheetmethodmerge.Simple;

/**
 <!-- globalinfo-start -->
 * Merges 2 or more spreadsheets into a single spreadsheet, using a selectable merge method.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-method &lt;adams.flow.transformer.spreadsheetmethodmerge.AbstractMerge&gt; (property: mergeMethod)
 * &nbsp;&nbsp;&nbsp;The method that should be used to perform the merge.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.spreadsheetmethodmerge.Simple -class-finder adams.data.spreadsheet.columnfinder.NullFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class Merge
  extends AbstractMultiSpreadSheetOperation<SpreadSheet> {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 3778575114133031631L;

  /** The method to use to perform the merge. */
  protected AbstractMerge m_MergeMethod;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Merges 2 or more spreadsheets into a single spreadsheet, using a selectable merge method.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("method", "mergeMethod", new Simple());
  }

  /**
   * Gets the currently-set merge method.
   *
   * @return The merge method being used currently.
   */
  public AbstractMerge getMergeMethod() {
    return m_MergeMethod;
  }

  /**
   * Sets the merge method to use to perform the merge.
   *
   * @param mergeMethod The merge method to use.
   */
  public void setMergeMethod(AbstractMerge mergeMethod) {
    m_MergeMethod = mergeMethod;
    reset();
  }

  /**
   * Gets the tip-text for the merge method option.
   *
   * @return The tip-text as a String.
   */
  public String mergeMethodTipText() {
    return "The method that should be used to perform the merge.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "mergeMethod", m_MergeMethod, "method: ");
  }

  /**
   * Returns the minimum number of sheets that are required for the operation.
   *
   * @return the number of sheets that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumSheetsRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of sheets that are required for the operation.
   *
   * @return the number of sheets that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumSheetsRequired() {
    return -1;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual processing of the sheets.
   *
   * @param sheets the containers to process
   * @param errors for collecting errors
   * @return the generated data
   */
  @Override
  protected SpreadSheet doProcess(SpreadSheet[] sheets, MessageCollection errors) {
    return m_MergeMethod.merge(sheets);
  }
}
