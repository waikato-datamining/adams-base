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
 * MouseUtils.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for mouse events.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MouseUtils {

  public static final String MODIFIER_SHIFT = "SHIFT";

  public static final String MODIFIER_CTRL = "CTRL";

  public static final String MODIFIER_META = "META";

  public static final String MODIFIER_ALT = "ALT";

  public static final String MODIFIER_ALT_GRAPH = "ALT_GRAPH";

  /**
   * Checks whether the mouse event is a left-click event.
   * Ctrl/Alt/Shift are allowed.
   *
   * @param e		the event
   * @return		true if a left-click event
   */
  public static boolean isLeftClick(MouseEvent e) {
    return ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 1));
  }

  /**
   * Checks whether the left mouse button is down.
   * Ctrl/Alt/Shift are allowed.
   *
   * @param e		the event
   * @return		true if left button down
   */
  public static boolean isLeftDown(MouseEvent e) {
    return (e.getButton() == MouseEvent.BUTTON1);
  }

  /**
   * Checks whether the mouse event is a double-click event (with the left
   * mouse button).
   * Ctrl/Alt/Shift are allowed.
   *
   * @param e		the event
   * @return		true if a double-click event
   */
  public static boolean isDoubleClick(MouseEvent e) {
    return ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2));
  }

  /**
   * Checks whether the mouse event is a middle/wheel-click event.
   * Ctrl/Alt/Shift are allowed.
   *
   * @param e		the event
   * @return		true if a middle/wheel-click event
   */
  public static boolean isMiddleClick(MouseEvent e) {
    return ((e.getButton() == MouseEvent.BUTTON2) && (e.getClickCount() == 1));
  }

  /**
   * Checks whether the middle mouse button is down.
   * Ctrl/Alt/Shift are allowed.
   *
   * @param e		the event
   * @return		true if middle/wheel down
   */
  public static boolean isMiddleDown(MouseEvent e) {
    return (e.getButton() == MouseEvent.BUTTON2);
  }

  /**
   * Checks whether the mouse event is a right-click event.
   * Alt+Left-Click is also interpreted as right-click.
   *
   * @param e		the event
   * @return		true if a right-click event
   */
  public static boolean isRightClick(MouseEvent e) {
    boolean	result;

    result = false;

    if ((e.getButton() == MouseEvent.BUTTON3) && (e.getClickCount() == 1))
      result = true;
    else if ((e.getButton() == MouseEvent.BUTTON1) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown())
      result = true;

    return result;
  }

  /**
   * Checks whether the right mouse button is down.
   *
   * @param e		the event
   * @return		true if right button down
   */
  public static boolean isRightDown(MouseEvent e) {
    return (e.getButton() == MouseEvent.BUTTON3);
  }

  /**
   * Checks whether the mouse event is a "print screen" event:
   * Alt+Shift+Left-Click.
   *
   * @param e		the event
   * @return		true if a "print screen" event
   */
  public static boolean isPrintScreenClick(MouseEvent e) {
    return ((e.getButton() == MouseEvent.BUTTON1) && e.isAltDown() && e.isShiftDown() && !e.isControlDown());
  }

  /**
   * Checks whether no modified key is pressed.
   *
   * @param e		the event
   * @return		true if no modifier key pressed
   */
  public static boolean hasNoModifierKey(MouseEvent e) {
    return (!e.isAltDown() && !e.isShiftDown() && !e.isControlDown());
  }

  /**
   * Sets the wait cursor for the specified component.
   *
   * @param comp	the component to update
   */
  public static void setWaitCursor(Component comp) {
    comp.setCursor(new Cursor(Cursor.WAIT_CURSOR));
  }

  /**
   * Sets the default cursor for the specified component.
   *
   * @param comp	the component to update
   */
  public static void setDefaultCursor(Component comp) {
    comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Turns the extended modifiers associated with this mouse event to a list of strings.
   *
   * @param e		the event to process
   * @return		the generated list of human-readable modifiers
   * @see		#MODIFIER_SHIFT
   * @see		#MODIFIER_CTRL
   * @see		#MODIFIER_META
   * @see		#MODIFIER_ALT
   * @see		#MODIFIER_ALT_GRAPH
   */
  public static List<String> modifiersToStr(MouseEvent e) {
    return modifiersToStr(e.getModifiersEx());
  }

  /**
   * Turns the extended modifiers associated with a mouse event to a list of strings.
   *
   * @param modifiers	the modifiers to process
   * @return		the generated list of human-readable modifiers
   * @see		#MODIFIER_SHIFT
   * @see		#MODIFIER_CTRL
   * @see		#MODIFIER_META
   * @see		#MODIFIER_ALT
   * @see		#MODIFIER_ALT_GRAPH
   */
  public static List<String> modifiersToStr(int modifiers) {
    List<String>  result;

    result = new ArrayList<>();
    if ((modifiers & MouseEvent.SHIFT_DOWN_MASK) != 0)
      result.add(MODIFIER_SHIFT);
    if ((modifiers & MouseEvent.CTRL_DOWN_MASK) != 0)
      result.add(MODIFIER_CTRL);
    if ((modifiers & MouseEvent.META_DOWN_MASK) != 0)
      result.add(MODIFIER_META);
    if ((modifiers & MouseEvent.ALT_DOWN_MASK) != 0)
      result.add(MODIFIER_ALT);
    if ((modifiers & MouseEvent.ALT_GRAPH_DOWN_MASK) != 0)
      result.add(MODIFIER_ALT_GRAPH);

    return result;
  }
}
