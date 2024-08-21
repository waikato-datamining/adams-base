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
 * BaseAction.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import adams.core.CleanUpHandler;
import adams.gui.core.ImageManager;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

/**
 * Extended interface for actions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface BaseAction
  extends Action, CleanUpHandler {

  /**
   * Sets the name of the action, i.e., the menuitem/button caption.
   *
   * @param value	the name
   */
  public void setName(String value);

  /**
   * Returns the name of the action, i.e., the menuitem/button caption.
   *
   * @return		the name
   */
  public String getName();

  /**
   * Sets the icon of the action, i.e., the menuitem/button icon.
   *
   * @param name	the icon
   * @see		#setIcon(Icon)
   * @see                ImageManager#getIcon(String)
   */
  public void setIcon(String name);

  /**
   * Sets the icon of the action, i.e., the menuitem/button icon.
   * Sets SMALL_ICON and LARGE_ICON_KEY at the same time.
   *
   * @param value	the icon
   */
  public void setIcon(Icon value);

  /**
   * Returns the icon the action (stored under SMALL_ICON).
   *
   * @return		the icon
   */
  public Icon getIcon();

  /**
   * Sets the name of the action, i.e., the menuitem/button caption.
   *
   * @param value	the name
   */
  public void setSelected(boolean value);

  /**
   * Returns the name of the action, i.e., the menuitem/button caption.
   *
   * @return		the name
   */
  public boolean isSelected();

  /**
   * Sets the mnemonic to use for the action.
   *
   * @param value	the mnemonic, e.g., KeyEvent.VK_O
   * @see		KeyEvent
   */
  public void setMnemonic(int value);

  /**
   * Checks whether a mnemonic is available for this action.
   *
   * @return		true if a mnemonic is available
   */
  public boolean hasMnemonic();

  /**
   * Returns the mnemonic in use for the action.
   *
   * @return		the mnemonic if available (e.g., KeyEvent.VK_O), otherwise null
   * @see		#hasMnemonic()
   */
  public Integer getMnemonic();

  /**
   * Sets the accelerator (KeyStroke) to use for the action.
   *
   * @param value	the keystroke, e.g., "ctrl pressed O"
   * @see		KeyStroke#getKeyStroke(String)
   */
  public void setAccelerator(String value);

  /**
   * Checks whether a keystroke is available for this action.
   *
   * @return		true if a keystroke is available
   */
  public boolean hasAccelerator();

  /**
   * Returns the accelerator (KeyStroke) in use for the action.
   *
   * @return		the keystroke if available, otherwise null
   * @see		#hasAccelerator()
   */
  public KeyStroke getAccelerator();

  /**
   * Sets the TipText to use for the action, i.e., button.
   *
   * @param value	the tip text
   */
  public void setToolTipText(String value);

  /**
   * Checks whether a tip text is available for this action.
   *
   * @return		true if a tip text is available
   */
  public boolean hasToolTipText();

  /**
   * Returns the TipText in use for the action.
   *
   * @return		the tip text if available, otherwise null
   * @see		#hasToolTipText()
   */
  public String getToolTipText();

  /**
   * Sets whether to launch the menu item asynchronously using a swingworker.
   *
   * @param value	true if asynchronous
   */
  public void setAsynchronous(boolean value);

  /**
   * Returns whether to launch the menu item asynchronously using a swingworker.
   *
   * @return		true if asynchronous
   */
  public boolean isAsynchronous();

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp();
}
