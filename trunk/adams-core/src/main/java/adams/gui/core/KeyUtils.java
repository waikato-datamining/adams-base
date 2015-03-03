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
 * KeyUtils.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.event.KeyEvent;

/**
 * A helper class for key events.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class KeyUtils {
  
  /**
   * Checks whether the key event was a "copy to clipboard" combination
   * (Ctrl+C or Ctrl+Ins).
   * 
   * @param e		the event
   * @return		true if a "copy" event
   */
  public static boolean isCopy(KeyEvent e) {
    boolean	result;
    
    result = false;
    
    if (e.isControlDown() && !e.isAltDown() && !e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_C))
      result = true;
    else if (e.isControlDown() && !e.isAltDown() && !e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_INSERT))
      result = true;
    
    return result;
  }
  
  /**
   * Checks whether the key event was a "cut to clipboard" combination
   * (Ctrl+X or Shift+Delete).
   * 
   * @param e		the event
   * @return		true if a "cut" event
   */
  public static boolean isCut(KeyEvent e) {
    boolean	result;
    
    result = false;
    
    if (e.isControlDown() && !e.isAltDown() && !e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_X))
      result = true;
    else if (!e.isControlDown() && !e.isAltDown() && e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_DELETE))
      result = true;
    
    return result;
  }
  
  /**
   * Checks whether the key event was a "insert from clipboard" combination
   * (Ctrl+V or Shift+Ins).
   * 
   * @param e		the event
   * @return		true if a "paste" event
   */
  public static boolean isPaste(KeyEvent e) {
    boolean	result;
    
    result = false;
    
    if (e.isControlDown() && !e.isAltDown() && !e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_V))
      result = true;
    else if (!e.isControlDown() && !e.isAltDown() && e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_INSERT))
      result = true;
    
    return result;
  }
}
