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

import java.awt.event.InputEvent;
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

  /**
   * Returns whether the CTRL key is down.
   *
   * @param modifiersEx		the extended modifiers bitmask
   * @return			true if CTRL is down
   */
  public static boolean isCtrlDown(int modifiersEx) {
    return ((modifiersEx & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK);
  }

  /**
   * Returns whether the ALT key is down.
   *
   * @param modifiersEx		the extended modifiers bitmask
   * @return			true if ALT is down
   */
  public static boolean isAltDown(int modifiersEx) {
    return ((modifiersEx & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK);
  }

  /**
   * Returns whether the META key is down.
   *
   * @param modifiersEx		the extended modifiers bitmask
   * @return			true if META is down
   */
  public static boolean isMetaDown(int modifiersEx) {
    return ((modifiersEx & InputEvent.META_DOWN_MASK) == InputEvent.META_DOWN_MASK);
  }

  /**
   * Returns whether the SHIFT key is down.
   *
   * @param modifiersEx		the extended modifiers bitmask
   * @return			true if SHIFT is down
   */
  public static boolean isShiftDown(int modifiersEx) {
    return ((modifiersEx & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK);
  }

  /**
   * Checks whether neither CTRL/ALT/META/SHIFT is down.
   *
   * @param modifiersEx		the extended modifiers bitmask
   * @return			true if none is down
   */
  public static boolean isNoneDown(int modifiersEx) {
    return !isCtrlDown(modifiersEx)
      && !isAltDown(modifiersEx)
      && !isMetaDown(modifiersEx)
      && !isShiftDown(modifiersEx);
  }

  /**
   * Checks whether only CTRL is not, not ALT/META/SHIFT.
   *
   * @param modifiersEx		the extended modifiers bitmask
   * @return			true if only CTRL is down
   */
  public static boolean isOnlyCtrlDown(int modifiersEx) {
    return isCtrlDown(modifiersEx)
      && !isAltDown(modifiersEx)
      && !isMetaDown(modifiersEx)
      && !isShiftDown(modifiersEx);
  }
}
