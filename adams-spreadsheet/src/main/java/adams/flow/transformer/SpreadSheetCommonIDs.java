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
 * SpreadSheetCommonIDs.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;

import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Extracts the common IDs from two or more spreadsheets.<br>
 * Matching sense can be inverted, i.e., the IDs not in common are output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetCommonIDs
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-index &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: index)
 * &nbsp;&nbsp;&nbsp;The index of the column with the IDs in the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-invert &lt;boolean&gt; (property: invert)
 * &nbsp;&nbsp;&nbsp;Whether to invert the matching sense and return the IDs 'not' in common.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetCommonIDs
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3363405805013155845L;

  /** the column in the spreadsheet. */
  protected SpreadSheetColumnIndex m_Index;

  /** whether to invert the matching (= return the IDs not in common). */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Extracts the common IDs from two or more spreadsheets.\n"
      + "Matching sense can be inverted, i.e., the IDs not in common are output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "index", "index",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
      "invert", "invert",
      false);
  }

  /**
   * Sets the index of the column in the spreadsheet.
   *
   * @param value	the index
   */
  public void setIndex(SpreadSheetColumnIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index of the column in the spreadsheet.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the column with the IDs in the spreadsheet.";
  }

  /**
   * Sets whether to invert the matchin, i.e., return IDs not in common.
   *
   * @param value	true if to invert matching
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matchin, i.e., return IDs not in common.
   *
   * @return		true if to invert matching
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "Whether to invert the matching sense and return the IDs 'not' in common.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = QuickInfoHelper.toString(this, "index", m_Index, "col: ");
    value  = QuickInfoHelper.toString(this, "invert", m_Invert, "invert", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    int			i;
    SpreadSheet[] 	sheets;
    HashSet<String>[]	ids;
    HashSet<String>	subset;
    SpreadSheet		output;
    Row			row;

    result = null;

    // get filenames
    if (m_InputToken.getPayload() instanceof SpreadSheet[]) {
      sheets = (SpreadSheet[]) m_InputToken.getPayload();
      if (sheets.length < 2)
	result = "At least two spreadsheets required, provided: " + sheets.length;
    }
    else {
      throw new IllegalStateException("Unhandled input type: " + m_InputToken.getPayload().getClass());
    }

    // init IDs
    if (result == null) {
      ids = new HashSet[sheets.length];
      for (i = 0; i < sheets.length; i++) {
	m_Index.setData(sheets[i]);
	if (m_Index.getIntIndex() == -1) {
	  result = "Sheet #" + (i + 1) + " does not have column: " + m_Index;
	}
	ids[i] = new HashSet<>(sheets[i].getCellValues(m_Index.getIntIndex()));
      }

      // create ID subset
      if (result == null) {
	subset = new HashSet<>(ids[0]);
	for (i = 1; i < sheets.length; i++) {
	  if (m_Invert)
	    subset.removeAll(ids[i]);
	  else
	    subset.retainAll(ids[i]);
	}

	// create output
	output = new SpreadSheet();
	row    = output.getHeaderRow();
	row.addCell("I").setContent("ID");
	for (String id: subset)
	  output.addRow().addCell("I").setContentAsString(id);
	m_OutputToken = new Token(output);
      }
    }

    return result;
  }
}
