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
 * BaseFlatSplitButton.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;

/**
 * Simple version of a split button: button + drop-down menu.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseFlatSplitButton
    extends BasePanel {

  /** the main button. */
  protected BaseFlatButton m_ButtonMain;

  /** the menu button. */
  protected BaseFlatButtonWithDropDownMenu m_ButtonMenu;

  /**
   * Creates a button with no set text or icon.
   */
  public BaseFlatSplitButton() {
    super();
  }

  /**
   * Creates a button with an icon.
   *
   * @param icon the Icon image to display on the button
   */
  public BaseFlatSplitButton(Icon icon) {
    this();
    setIcon(icon);
  }

  /**
   * Creates a button with text.
   *
   * @param text the text of the button
   */
  public BaseFlatSplitButton(String text) {
    this();
    setText(text);
  }

  /**
   * Creates a button where properties are taken from the
   * <code>Action</code> supplied.
   *
   * @param a the <code>Action</code> used to specify the new button
   * @since 1.3
   */
  public BaseFlatSplitButton(Action a) {
    this();
    setAction(a);
  }

  /**
   * Creates a button with initial text and an icon.
   *
   * @param text the text of the button
   * @param icon the Icon image to display on the button
   */
  public BaseFlatSplitButton(String text, Icon icon) {
    this();
    setText(text);
    setIcon(icon);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());

    m_ButtonMain = new BaseFlatButton();
    add(m_ButtonMain, BorderLayout.CENTER);

    m_ButtonMenu = new BaseFlatButtonWithDropDownMenu();
    m_ButtonMenu.setEnabled(false);
    add(m_ButtonMenu, BorderLayout.EAST);
  }

  /**
   * Sets the text for the main button.
   *
   * @param value	the text
   */
  public void setText(String value) {
    m_ButtonMain.setText(value);
  }

  /**
   * Returns the text of the main button.
   *
   * @return		the text
   */
  public String getText() {
    return m_ButtonMain.getText();
  }

  /**
   * Sets the icon for the main button.
   *
   * @param value	the icon
   */
  public void setIcon(Icon value) {
    m_ButtonMain.setIcon(value);
  }

  /**
   * Returns the icon of the main button.
   *
   * @return		the icon
   */
  public Icon getIcon() {
    return m_ButtonMain.getIcon();
  }

  /**
   * Sets the action for the main button.
   *
   * @param value	the action to use
   */
  public void setAction(Action value) {
    m_ButtonMain.setAction(value);
  }

  /**
   * Returns the action of the main button.
   *
   * @return		the action in use, can be null
   */
  public Action getAction() {
    return m_ButtonMain.getAction();
  }

  /**
   * Adds the menu action.
   *
   * @param value	the action to add
   */
  public void add(Action value) {
    m_ButtonMenu.addToMenu(value);
    if (isEnabled())
      m_ButtonMenu.setEnabled(true);
  }

  /**
   * Adds the menu item.
   *
   * @param value	the item to add
   */
  public void add(JMenuItem value) {
    m_ButtonMenu.addToMenu(value);
    if (isEnabled())
      m_ButtonMenu.setEnabled(true);
  }

  /**
   * Adds a menu separator.
   */
  public void addSeparator() {
    m_ButtonMenu.addSeparatorToMenu();
  }

  /**
   * Returns the state of the button part of the JideSplitButton. True if the button is enabled, false if it's not.
   *
   * @return true if the button is enabled, otherwise false
   */
  public boolean isButtonEnabled() {
    return m_ButtonMain.isEnabled();
  }

  /**
   * Sets the state of the button part of the JideSplitButton.
   *
   * @param value true if the button is enabled, otherwise false
   */
  public void setButtonEnabled(boolean value) {
    m_ButtonMain.setEnabled(value);
  }

  /**
   * Sets the enabled state.
   *
   * @param value	whether enabled or not
   */
  @Override
  public void setEnabled(boolean value) {
    super.setEnabled(value);
    m_ButtonMain.setEnabled(value);
    m_ButtonMenu.setEnabled(value);
  }

  /**
   * Adds the change listener to the menu button.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ButtonMenu.addChangeListener(l);
  }

  /**
   * Removes the change listener from the menu button.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ButtonMenu.removeChangeListener(l);
  }
}
