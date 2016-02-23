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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.base.BaseRegExp;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BasePanel;
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
import java.awt.event.ActionListener;

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
    JPanel			panelButtons;
    ParameterPanel		panelParams;
    final JTextField		fieldInput;
    final RegExpTextField	fieldFind;
    final JTextField		fieldReplace;
    final JCheckBox 		checkboxLowerCase;
    final JCheckBox 		checkboxAll;
    final JTextField		fieldOutput;
    JButton			buttonTest;
    JButton			buttonClose;

    panel = new BasePanel(new BorderLayout());

    panelParams = new ParameterPanel();
    fieldInput = new JTextField(30);
    panelParams.addParameter("Input", fieldInput);
    fieldFind = new RegExpTextField();
    panelParams.addParameter("Find", fieldFind);
    fieldReplace = new JTextField();
    panelParams.addParameter("Replace", fieldReplace);
    checkboxLowerCase = new JCheckBox();
    panelParams.addParameter("Use lower case", checkboxLowerCase);
    checkboxAll = new JCheckBox();
    checkboxAll.setSelected(true);
    panelParams.addParameter("Replace all", checkboxAll);
    fieldOutput = new JTextField(30);
    panelParams.addParameter("Output", fieldOutput);
    panel.add(panelParams, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonTest = new JButton("Test");
    buttonTest.setMnemonic('T');
    buttonTest.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String input = fieldInput.getText();
        if (checkboxLowerCase.isSelected())
          input = input.toLowerCase();
	BaseRegExp regexp = fieldFind.getRegExp();
	String replace = fieldReplace.getText();
	String output = null;
	try {
	  if (checkboxAll.isSelected())
	    output = input.replaceAll(regexp.getValue(), replace);
	  else
	    output = input.replaceFirst(regexp.getValue(), replace);
	  fieldOutput.setText(output);
	}
	catch (Exception ex) {
	  fieldOutput.setText("");
	  GUIHelper.showErrorMessage(
	    null,
	    "Failed to apply regular expression:\n"
	      + regexp + "\n"
	      + "to input:\n"
	      + input,
	    "RegExpTest");
	}
      }
    });
    panelButtons.add(buttonTest);
    buttonClose = new JButton("Close");
    buttonClose.setMnemonic('l');
    buttonClose.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	panel.closeParent();
      }
    });
    panelButtons.add(buttonClose);
    panel.add(panelButtons, BorderLayout.SOUTH);

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