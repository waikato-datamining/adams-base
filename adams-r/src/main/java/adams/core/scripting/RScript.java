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
 * RScript.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.scripting;

import adams.gui.core.AbstractScript;
import adams.gui.core.AbstractTextEditorPanelWithSyntaxHighlighting;
import adams.gui.core.RSyntaxEditorPanel;

/**
 * 
 * Wrapper for a R scripts to be editable in the GOE.
 * 
 * @author rsmith
 * @version $Revision$
 */
public class RScript
  extends AbstractScript {

  /** for serialization. */
  private static final long serialVersionUID = -7498853966972856458L;

  /**
   * Initializes the string with length 0.
   */
  public RScript() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public RScript(String s) {
    super(s);
  }

  /**
   * Returns the tip text for the script.
   * 
   * @return the tool tip
   */
  @Override
  protected String getScriptTipText() {
    return "R script";
  }

  /**
   * Returns the configured text editor panel to use in the GOE.
   * 
   * @return the text editor panel
   */
  @Override
  public AbstractTextEditorPanelWithSyntaxHighlighting getTextEditorPanel() {
    return new RSyntaxEditorPanel();
  }

  /**
   * Returns whether inline editing in the GOE is allowed.
   * 
   * @return true if inline editing is allowed
   */
  @Override
  public boolean allowsInlineEditing() {
    return false;
  }
}
