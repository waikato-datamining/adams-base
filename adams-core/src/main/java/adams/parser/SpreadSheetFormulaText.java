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
 * SpreadSheetFormulaText.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import adams.core.Utils;
import adams.gui.core.AbstractScript;
import adams.gui.core.AbstractTextEditorPanelWithSyntaxHighlighting;
import adams.gui.core.SpreadSheetFormulaEditorPanel;

/**
 * Wrapper for a String object to be editable in the GOE. Basically the same
 * as BaseString, but used for longer, multi-line strings. Uses a different
 * GOE editor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFormulaText
  extends AbstractScript {

  /** for serialization. */
  private static final long serialVersionUID = 2838204291702277799L;

  /**
   * Initializes the string with length 0.
   */
  public SpreadSheetFormulaText() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public SpreadSheetFormulaText(String s) {
    super(s);
  }

  /**
   * Returns the backquoted String value.
   *
   * @return		the backquoted String value
   */
  @Override
  public String stringValue() {
    return Utils.backQuoteChars(getValue());
  }

  /**
   * Returns the tip text for the script.
   *
   * @return		the tool tip
   */
  @Override
  protected String getScriptTipText() {
    return "Spreadsheet formula";
  }

  /**
   * Returns the configured text editor panel to use in the GOE.
   *
   * @return		the text editor panel
   */
  @Override
  public AbstractTextEditorPanelWithSyntaxHighlighting getTextEditorPanel() {
    return new SpreadSheetFormulaEditorPanel();
  }
  
  /**
   * Returns whether inline editing in the GOE is allowed.
   * 
   * @return		true if inline editing is allowed
   */
  @Override
  public boolean allowsInlineEditing() {
    return true;
  }
}
