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
 * BaseButtonWithDropDownMenu.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Button that opens a dropdown menu when clicked.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseButtonWithDropDownMenu
  extends JButton {

  private static final long serialVersionUID = 6616758688778479716L;

  /** the menu items. */
  protected List<JMenuItem> m_MenuItems;

  /**
   * Initializes the button.
   */
  public BaseButtonWithDropDownMenu() {
    super();
    initialize();
  }

  /**
   * Initializes the button, using the specified button text.
   *
   * @param text	the text for the button
   */
  public BaseButtonWithDropDownMenu(String text) {
    super(text);
    initialize();
  }

  /**
   * Initializes the button, using the specified icon.
   *
   * @param icon	the icon for the button
   */
  public BaseButtonWithDropDownMenu(Icon icon) {
    super(icon);
    initialize();
  }

  /**
   * Initializes the button, using the specified button text and icon.
   *
   * @param text	the text for the button
   * @param icon	the icon for the button
   */
  public BaseButtonWithDropDownMenu(String text, Icon icon) {
    super(text, icon);
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_MenuItems = new ArrayList<>();
    addActionListener((ActionEvent ae) -> showMenu());
  }

  /**
   * Shows the menu.
   */
  protected void showMenu() {
    BasePopupMenu menu = new BasePopupMenu();
    for (JMenuItem menuitem: m_MenuItems)
      menu.add(menuitem);
    menu.show(this, 0, this.getHeight());
  }

  /**
   * Adds the menu item for the dropdown menu.
   *
   * @param item	the item to add
   */
  public void addToMenu(JMenuItem item) {
    m_MenuItems.add(item);
  }

  /**
   * Adds the action for the dropdown menu.
   *
   * @param action	the action to add
   */
  public void addToMenu(Action action) {
    m_MenuItems.add(new JMenuItem(action));
  }
}
