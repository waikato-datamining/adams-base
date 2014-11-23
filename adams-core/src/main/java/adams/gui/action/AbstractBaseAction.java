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
 * AbstractBaseAction.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import adams.gui.core.GUIHelper;

/**
 * Action with some methods added for convenience.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBaseAction
  extends AbstractAction
  implements BaseAction {

  /** for serialization. */
  private static final long serialVersionUID = -7695092075151409689L;

  /**
   * Defines an <code>Action</code> object with a default
   * description string and default icon.
   */
  public AbstractBaseAction() {
    super();
    initialize();
  }

  /**
   * Defines an <code>Action</code> object with the specified
   * description string and a default icon.
   *
   * @param name	the description
   */
  public AbstractBaseAction(String name) {
    this();
    setName(name);
  }

  /**
   * Defines an <code>Action</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon
   */
  public AbstractBaseAction(String name, Icon icon) {
    this(name);
    setIcon(icon);
  }

  /**
   * Defines an <code>Action</code> object with the specified
   * description string and a the specified icon.
   *
   * @param name	the description
   * @param icon	the icon file (without path)
   */
  public AbstractBaseAction(String name, String icon) {
    this(name, GUIHelper.getIcon(icon));
  }

  /**
   * Initializes the action.
   */
  protected void initialize() {
    putValue(SELECTED_KEY, false);
  }

  /**
   * Sets the name of the action, i.e., the menuitem/button caption.
   *
   * @param value	the name
   */
  public void setName(String value) {
    putValue(NAME, value);
  }

  /**
   * Returns the name of the action, i.e., the menuitem/button caption.
   *
   * @return		the name
   */
  public String getName() {
    return (String) getValue(NAME);
  }

  /**
   * Sets the icon of the action, i.e., the menuitem/button icon.
   * Sets SMALL_ICON and LARGE_ICON_KEY at the same time.
   *
   * @param value	the icon
   */
  public void setIcon(Icon value) {
    putValue(SMALL_ICON, value);
    putValue(LARGE_ICON_KEY, value);
  }

  /**
   * Returns the icon the action (stored under SMALL_ICON).
   *
   * @return		the icon
   */
  public Icon getIcon() {
    return (Icon) getValue(SMALL_ICON);
  }

  /**
   * Sets the name of the action, i.e., the menuitem/button caption.
   *
   * @param value	the name
   */
  public void setSelected(boolean value) {
    putValue(SELECTED_KEY, value);
  }

  /**
   * Returns the name of the action, i.e., the menuitem/button caption.
   *
   * @return		the name
   */
  public boolean isSelected() {
    return (Boolean) getValue(SELECTED_KEY);
  }

  /**
   * Sets the mnemonic to use for the action.
   *
   * @param value	the mnemonic, e.g., KeyEvent.VK_O
   * @see		KeyEvent
   */
  public void setMnemonic(int value) {
    putValue(MNEMONIC_KEY, value);
  }

  /**
   * Checks whether a mnemonic is available for this action.
   *
   * @return		true if a mnemonic is available
   */
  public boolean hasMnemonic() {
    return (getValue(MNEMONIC_KEY) != null);
  }

  /**
   * Returns the mnemonic in use for the action.
   *
   * @return		the mnemonic if available (e.g., KeyEvent.VK_O), otherwise null
   * @see		#hasMnemonic()
   */
  public KeyStroke getMnemonic() {
    if (hasMnemonic())
      return (KeyStroke) getValue(MNEMONIC_KEY);
    else
      return null;
  }

  /**
   * Sets the accelerator (KeyStroke) to use for the action.
   *
   * @param value	the keystroke, e.g., "ctrl pressed O"
   * @see		KeyStroke#getKeyStroke(String)
   */
  public void setAccelerator(String value) {
    putValue(ACCELERATOR_KEY, GUIHelper.getKeyStroke(value));
  }

  /**
   * Checks whether a keystroke is available for this action.
   *
   * @return		true if a keystroke is available
   */
  public boolean hasAccelerator() {
    return (getValue(ACCELERATOR_KEY) != null);
  }

  /**
   * Returns the accelerator (KeyStroke) in use for the action.
   *
   * @return		the keystroke if available, otherwise null
   * @see		#hasAccelerator()
   */
  public KeyStroke getAccelerator() {
    if (hasAccelerator())
      return (KeyStroke) getValue(ACCELERATOR_KEY);
    else
      return null;
  }

  /**
   * Sets the TipText to use for the action, i.e., button.
   *
   * @param value	the tip text
   */
  public void setToolTipText(String value) {
    putValue(SHORT_DESCRIPTION, value);
  }

  /**
   * Checks whether a tip text is available for this action.
   *
   * @return		true if a tip text is available
   */
  public boolean hasToolTipText() {
    return (getValue(SHORT_DESCRIPTION) != null);
  }

  /**
   * Returns the TipText in use for the action.
   *
   * @return		the tip text if available, otherwise null
   * @see		#hasTipText()
   */
  public String getToolTipText() {
    if (hasToolTipText())
      return (String) getValue(SHORT_DESCRIPTION);
    else
      return null;
  }

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  protected abstract void doActionPerformed(ActionEvent e);

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    doActionPerformed(e);
  }
  
  /**
   * Cleans up data structures, frees up memory.
   * <p/>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }
}
