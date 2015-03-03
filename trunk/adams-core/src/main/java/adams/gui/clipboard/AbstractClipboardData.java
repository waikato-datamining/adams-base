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
 * AbstractClipboardData.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.clipboard;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Token;

/**
 * Ancestor for classes that copy to and paste from the system's clipboard.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data this class manages
 */
public abstract class AbstractClipboardData<T>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3530762672690723313L;

  /**
   * Returns the class used in the flow.
   * 
   * @return		the class
   */
  public abstract Class getFlowClass();
  
  /**
   * Copies the data to the clipboard.
   * 
   * @param data	the data to copy
   */
  public abstract void copy(T data);
  
  /**
   * Unwraps the actual data from the token.
   * 
   * @param data	the token to unwrap
   * @return		the actual data, null if can't handle
   */
  protected abstract T unwrap(Token data);
  
  /**
   * Copies the data stored in the token to the clipboard.
   * 
   * @param data	the data to copy
   */
  public boolean copyFromToken(Token data) {
    T	obj;
    
    if (data.getPayload() == null)
      return false;
    
    obj = unwrap(data);
    if (obj == null)
      return false;
    
    copy(obj);
    
    return true;
  }
  
  /**
   * Checks whether the required data type is available from the clipboard.
   * 
   * @return		true if data is available
   */
  public abstract boolean canPaste();
  
  /**
   * Pastes the data from the clipboard.
   * 
   * @return		the pasted data, null if failed to paste
   */
  public abstract T paste();
  
  /**
   * Wraps the data from the clipboard in a flow token.
   * 
   * @param 
   * @return		the pasted data, null if failed to paste
   */
  protected abstract Token wrap(T data);
  
  /**
   * Pastes the data from the clipboard as flow token.
   * 
   * @return		the pasted data, null if failed to paste
   */
  public Token pasteAsToken() {
    Token	result;
    T		data;
    
    result = null;
    data   = paste();
    if (data != null)
      result = wrap(data);
    
    return result;
  }
}
