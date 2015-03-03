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
 * AbstractSimpleScript.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.Utils;
import adams.core.base.AbstractBaseString;

/**
 * Ancestor for scripts with syntax highlighting in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSimpleScript
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = 4309078655122480376L;

  /**
   * Initializes the string with length 0.
   */
  public AbstractSimpleScript() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public AbstractSimpleScript(String s) {
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
  protected abstract String getScriptTipText();

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return getScriptTipText();
  }

  /**
   * Returns the configured text editor panel to use in the GOE.
   *
   * @return		the text editor panel
   */
  public abstract AbstractTextEditorPanelWithSimpleSyntaxHighlighting getTextEditorPanel();
  
  /**
   * Returns whether inline editing in the GOE is allowed.
   * 
   * @return		true if inline editing is allowed
   */
  public abstract boolean allowsInlineEditing();
}
