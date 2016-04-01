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
 * SpreadSheetAppendComments.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Appends the comments of the spreadsheet. Complete replacement is possible as well.
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
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetAppendComments
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-comments &lt;adams.core.base.BaseText&gt; (property: comments)
 * &nbsp;&nbsp;&nbsp;The comments to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-replace &lt;boolean&gt; (property: replace)
 * &nbsp;&nbsp;&nbsp;If enabled, any existing comments get replaced with the currently specified 
 * &nbsp;&nbsp;&nbsp;ones.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetAppendComments
  extends AbstractInPlaceSpreadSheetTransformer {

  private static final long serialVersionUID = 3734828865205265883L;

  /** the comments to append. */
  protected BaseText m_Comments;

  /** whether to replace any existing comments with the new one. */
  protected boolean m_Replace;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the comments of the spreadsheet. Complete replacement is possible as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "comments", "comments",
	    new BaseText());

    m_OptionManager.add(
	    "replace", "replace",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "comments", m_Comments);
    result += QuickInfoHelper.toString(this, "replace", m_Replace, "replace", ", ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");

    return result;
  }

  /**
   * Sets the comments to use.
   *
   * @param value	the comments
   */
  public void setComments(BaseText value) {
    m_Comments = value;
    reset();
  }

  /**
   * Returns the comments to use.
   *
   * @return		the comments
   */
  public BaseText getComments() {
    return m_Comments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commentsTipText() {
    return "The comments to use.";
  }

  /**
   * Sets whether to replace any existing comments rather than append.
   *
   * @param value	true if to replace any existing comments
   */
  public void setReplace(boolean value) {
    m_Replace = value;
    reset();
  }

  /**
   * Returns whether to replace any existing comments rather than append.
   *
   * @return		true if to replace any existing comments
   */
  public boolean getReplace() {
    return m_Replace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "If enabled, any existing comments get replaced with the currently specified ones.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet 	sheetOld;
    SpreadSheet		sheetNew;
    String[]		lines;

    result   = null;
    sheetOld = (SpreadSheet) m_InputToken.getPayload();

    // init sheet
    if (m_NoCopy)
      sheetNew = sheetOld;
    else
      sheetNew = sheetOld.getClone();

    if (m_Replace)
      sheetNew.getComments().clear();
    if (!m_Comments.isEmpty()) {
      lines = Utils.split(m_Comments.getValue(), "\n");
      sheetNew.getComments().addAll(Arrays.asList(lines));
    }

    m_OutputToken = new Token(sheetNew);

    return result;
  }
}
