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
 * FindClass.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;

/**
 * For locating a class on the classpath and the associated classpath
 * components.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FindClass
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 1947370537357191065L;

  /**
   * Initializes the menu item with no owner.
   */
  public FindClass() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public FindClass(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "duke.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    final BasePanel 		panel;
    BasePanel 			panelSearch;
    BaseTabbedPane		tabbedPane;
    JPanel panelButtons;
    ParameterPanel		panelParams;
    final JTextField 		fieldSearch;
    final JCheckBox 		checkboxRegExp;
    final BaseTextArea 		areaOutput;
    JButton 			buttonSearch;
    JButton			buttonClose;

    panel = new BasePanel(new BorderLayout());

    tabbedPane = new BaseTabbedPane();
    panel.add(tabbedPane, BorderLayout.CENTER);

    panelSearch = new BasePanel(new BorderLayout());
    panel.add(panelSearch, BorderLayout.CENTER);

    panelParams = new ParameterPanel();
    fieldSearch = new JTextField(30);
    panelParams.addParameter("Search", fieldSearch);
    checkboxRegExp = new JCheckBox();
    panelParams.addParameter("Regular expression?", checkboxRegExp);
    areaOutput = new BaseTextArea(5, 40);
    areaOutput.setTextFont(Fonts.getMonospacedFont());
    areaOutput.setEditable(false);

    panelSearch.add(panelParams, BorderLayout.NORTH);
    panelSearch.add(new BaseScrollPane(areaOutput), BorderLayout.CENTER);

    // buttons
    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panelButtons, BorderLayout.SOUTH);

    buttonSearch = new JButton("Search");
    buttonSearch.setMnemonic('S');
    buttonSearch.addActionListener((ActionEvent e) -> {
      String search = fieldSearch.getText();
      adams.core.FindClass find = new adams.core.FindClass();
      List<URL> urls = find.search(search, checkboxRegExp.isSelected());
      StringBuilder output = new StringBuilder();
      for (URL url: urls) {
	if (output.length() > 0)
	  output.append("\n");
	output.append(url.toString());
      }
      areaOutput.setText(output.toString());
      areaOutput.setCaretPosition(0);
    });
    panelButtons.add(buttonSearch);

    buttonClose = new JButton("Close");
    buttonClose.setMnemonic('l');
    buttonClose.addActionListener((ActionEvent e) -> panel.closeParent());
    panelButtons.add(buttonClose);

    createChildFrame(panel, GUIHelper.makeWider(GUIHelper.getDefaultTinyDialogDimension()));
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Find class";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.DEVELOPER;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_MAINTENANCE;
  }
}