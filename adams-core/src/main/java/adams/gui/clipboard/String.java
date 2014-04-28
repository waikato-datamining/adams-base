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
 * String.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.clipboard;

import java.awt.datatransfer.DataFlavor;

import adams.flow.core.Token;
import adams.gui.core.GUIHelper;

/**
 * Copies/pastes strings.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class String
  extends AbstractClipboardData<java.lang.String> {

  /** for serialization. */
  private static final long serialVersionUID = -3438585893626588114L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public java.lang.String globalInfo() {
    return "Copies/pastes strings.";
  }

  /**
   * Returns the supported class.
   * 
   * @return		the supported class
   */
  public Class getFlowClass() {
    return String.class;
  }
  
  /**
   * Unwraps the actual data from the token.
   * 
   * @param data	the token to unwrap
   * @return		the actual data, null if can't handle
   */
  protected java.lang.String unwrap(Token data) {
    if (data.getPayload() instanceof java.lang.String)
      return (java.lang.String) data.getPayload();
    else
      return data.getPayload().toString();
  }

  /**
   * Copies the data to the clipboard.
   * 
   * @param data	the data to copy
   */
  public void copy(java.lang.String data) {
    GUIHelper.copyToClipboard(data);
  }
  
  /**
   * Checks whether the required data type is available from the clipboard.
   * 
   * @return		true if data is available
   */
  public boolean canPaste() {
    return GUIHelper.canPasteFromClipboard(DataFlavor.stringFlavor);
  }

  /**
   * Pastes the data from the clipboard.
   * 
   * @return		the pasted data, null if failed to paste
   */
  public java.lang.String paste() {
    return GUIHelper.pasteStringFromClipboard();
  }
  
  /**
   * Wraps the data from the clipboard in a flow token.
   * 
   * @param 
   * @return		the pasted data, null if failed to paste
   */
  protected Token wrap(java.lang.String data) {
    return new Token(data);
  }
}
