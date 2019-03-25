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
 * SpreadSheetMethodMerge.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.transformer.spreadsheetmethodmerge.AbstractMerge;
import adams.flow.transformer.spreadsheetmethodmerge.Simple;

/**
 <!-- globalinfo-start -->
 * Merges 2 or more spreadsheets into a single spreadsheet, using a selectable merge method.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet[]<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.DataRow[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetMethodMerge
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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
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
public class SpreadSheetMethodMerge
  extends AbstractTransformer {

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
   * <br><br>
   * Default implementation returns null.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "mergeMethod", m_MergeMethod, "method: ");
  }

  /**
   * Gets the input Spreadsheets to merge.
   *
   * @return The input Spreadsheet objects to merge.
   */
  protected SpreadSheet[] getInput() {
    // The input to return
    SpreadSheet[] datasets;

    // Get the payload from the input token
    Object payload = m_InputToken.getPayload();

    // See what type of payload we have
    if (payload instanceof SpreadSheet[]) {
      // Payload in correct form already, just return it
      datasets = (SpreadSheet[]) payload;
    }
    else if (payload instanceof DataRow[]) {
      // Payload is a set of single Instance objects.
      DataRow[] datarowPayload = (DataRow[]) payload;

      // Wrap the single instances into a dataset each of size one
      datasets = new SpreadSheet[datarowPayload.length];
      for (int i = 0; i < datarowPayload.length; i++) {
        datasets[i] = spreadsheetForSingleDatarow(datarowPayload[i]);
      }
    }
    else {
      // Unsupported input type
      throw new IllegalStateException("Unhandled input type: " + m_InputToken.getPayload().getClass());
    }

    // Return the input
    return datasets;
  }

  /**
   * Creates a spreadsheet, containing a copy of the single datarow
   * provided.
   *
   * @param row The row to create a spreadsheet for.
   * @return The created spreadsheet.
   */
  protected SpreadSheet spreadsheetForSingleDatarow(DataRow row) {
    // Create a copy of the row's original dataset
    SpreadSheet dataset = new DefaultSpreadSheet();

    // Add a copy of the provided row
    row.getClone(dataset);

    // Return the dataset
    return dataset;
  }

  /**
   * Sets the payload of the output token for this transformer.
   *
   * @param output The output payload.
   */
  protected void setOutput(SpreadSheet output) {
    m_OutputToken = new Token();
    m_OutputToken.setPayload(output);
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    try {
      // Get the input token
      SpreadSheet[] spreadsheetsToMerge = getInput();

      // Perform the merge operation
      SpreadSheet mergedSpreadsheet = m_MergeMethod.merge(spreadsheetsToMerge);

      // Set the result against the output token
      setOutput(mergedSpreadsheet);

      return null;
    }
    catch (Exception e) {
      return e.getMessage();
    }
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet[].class, DataRow[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

}