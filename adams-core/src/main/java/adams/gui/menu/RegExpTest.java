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
 * RegExpTest.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.base.BaseRegExp;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.core.RegExpTextField;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Only for testing the regular expressions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegExpTest
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 1947370537357191065L;

  /**
   * Initializes the menu item with no owner.
   */
  public RegExpTest() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public RegExpTest(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    final BasePanel 		panel;
    BasePanel			panelTab;
    BaseTabbedPane		tabbedPane;
    JPanel			panelButtons;
    JPanel			panelButtonsAll;
    ParameterPanel		panelParams;
    final JTextField 		fieldReplaceInput;
    final RegExpTextField 	fieldReplaceFind;
    final JTextField 		fieldReplaceReplace;
    final JCheckBox 		checkboxReplaceLowerCase;
    final JCheckBox 		checkboxReplaceAll;
    final JTextField 		fieldReplaceOutput;
    final JTextField 		fieldMatchInput;
    final RegExpTextField fieldMatchExp;
    final JCheckBox 		checkboxMatchLowerCase;
    final JTextField 		fieldMatchOutput;
    JButton			buttonTest;
    JButton			buttonClose;

    panel = new BasePanel(new BorderLayout());

    tabbedPane = new BaseTabbedPane();
    panel.add(tabbedPane, BorderLayout.CENTER);

    panelButtonsAll = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonClose = new JButton("Close");
    buttonClose.setMnemonic('l');
    buttonClose.addActionListener((ActionEvent e) -> panel.closeParent());
    panelButtonsAll.add(buttonClose);
    panel.add(panelButtonsAll, BorderLayout.SOUTH);

    // replace
    panelTab = new BasePanel(new BorderLayout());
    tabbedPane.addTab("Replace", panelTab);

    panelParams = new ParameterPanel();
    fieldReplaceInput = new JTextField(30);
    panelParams.addParameter("Input", fieldReplaceInput);
    fieldReplaceFind = new RegExpTextField();
    panelParams.addParameter("Find", fieldReplaceFind);
    fieldReplaceReplace = new JTextField();
    panelParams.addParameter("Replace", fieldReplaceReplace);
    checkboxReplaceLowerCase = new JCheckBox();
    panelParams.addParameter("Use lower case", checkboxReplaceLowerCase);
    checkboxReplaceAll = new JCheckBox();
    checkboxReplaceAll.setSelected(true);
    panelParams.addParameter("Replace all", checkboxReplaceAll);
    fieldReplaceOutput = new JTextField(30);
    panelParams.addParameter("Output", fieldReplaceOutput);
    panelTab.add(panelParams, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonTest = new JButton("Test");
    buttonTest.setMnemonic('T');
    buttonTest.addActionListener((ActionEvent e) -> {
      String input = fieldReplaceInput.getText();
      if (checkboxReplaceLowerCase.isSelected())
	input = input.toLowerCase();
      BaseRegExp regexp = fieldReplaceFind.getRegExp();
      String replace = fieldReplaceReplace.getText();
      String output = null;
      try {
	if (checkboxReplaceAll.isSelected())
	  output = input.replaceAll(regexp.getValue(), replace);
	else
	  output = input.replaceFirst(regexp.getValue(), replace);
	fieldReplaceOutput.setText(output);
      }
      catch (Exception ex) {
	fieldReplaceOutput.setText("");
	GUIHelper.showErrorMessage(
	  null,
	  "Failed to apply regular expression:\n"
	    + regexp + "\n"
	    + "to input:\n"
	    + input,
	  "RegExpTest");
      }
    });
    panelButtons.add(buttonTest);
    panelTab.add(panelButtons, BorderLayout.SOUTH);

    // match
    panelTab = new BasePanel(new BorderLayout());
    tabbedPane.addTab("Match", panelTab);

    panelParams = new ParameterPanel();
    fieldMatchInput = new JTextField(30);
    panelParams.addParameter("Input", fieldMatchInput);
    fieldMatchExp = new RegExpTextField();
    panelParams.addParameter("Expression", fieldMatchExp);
    checkboxMatchLowerCase = new JCheckBox();
    panelParams.addParameter("Use lower case", checkboxMatchLowerCase);
    fieldMatchOutput = new JTextField(30);
    panelParams.addParameter("Matches?", fieldMatchOutput);
    panelTab.add(panelParams, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonTest = new JButton("Test");
    buttonTest.setMnemonic('T');
    buttonTest.addActionListener((ActionEvent e) -> {
      String input = fieldMatchInput.getText();
      if (checkboxMatchLowerCase.isSelected())
	input = input.toLowerCase();
      BaseRegExp regexp = fieldMatchExp.getRegExp();
      fieldMatchOutput.setText(regexp.isMatch(input) ? "yes" : "no");
    });
    panelButtons.add(buttonTest);
    panelTab.add(panelButtons, BorderLayout.SOUTH);

    createChildFrame(panel);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "RegExp test";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return true;
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