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
 * Preferences.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.option.UserMode;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.PreferencesManagerPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.GUIHelper;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * For managing the system-wide preferences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Preferences
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 3773253573560315512L;

  /**
   * Initializes the menu item with no owner.
   */
  public Preferences() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public Preferences(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "preferences.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    final PreferencesManagerPanel prefs = new PreferencesManagerPanel();
    final ChildFrame frame = createChildFrame(prefs, GUIHelper.getDefaultDialogDimension());
    JPanel panelButtons = new JPanel(new BorderLayout());
    prefs.add(panelButtons, BorderLayout.SOUTH);
    // left
    JPanel panelLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelButtons.add(panelLeft, BorderLayout.WEST);
    BaseButton buttonReset = new BaseButton("Reset all");
    buttonReset.addActionListener((ActionEvent e) -> {
      prefs.reset();
      frame.setVisible(false);
      frame.dispose();
    });
    panelLeft.add(buttonReset);
    // right
    JPanel panelRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panelRight, BorderLayout.EAST);
    BaseButton buttonApply = new BaseButton("Apply all");
    buttonApply.addActionListener((ActionEvent e) -> {
      prefs.activate();
      frame.setVisible(false);
      frame.dispose();
    });
    panelRight.add(buttonApply);
    BaseButton buttonCancel = new BaseButton("Close");
    buttonCancel.addActionListener((ActionEvent e) -> {
      frame.setVisible(false);
      frame.dispose();
    });
    panelRight.add(buttonCancel);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Preferences";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return true;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_PROGRAM;
  }
}