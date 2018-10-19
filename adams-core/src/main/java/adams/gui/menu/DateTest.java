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
 * DateTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.data.DateFormatString;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextField;
import adams.gui.core.BrowserHelper;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Only for testing the date parsing/formatting.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DateTest
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 1947370537357191065L;

  /**
   * Initializes the menu item with no owner.
   */
  public DateTest() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public DateTest(AbstractApplicationFrame owner) {
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
    final BaseTextField 		fieldFormatInput;
    final BaseTextField 		fieldFormatOutput;
    final BaseTextField 		fieldParseInput;
    final BaseTextField 		fieldParseFormat;
    final BaseTextField fieldParseValue;
    BaseButton			buttonTest;
    BaseButton			buttonHelp;
    BaseButton			buttonClose;

    panel = new BasePanel(new BorderLayout());

    tabbedPane = new BaseTabbedPane();
    panel.add(tabbedPane, BorderLayout.CENTER);

    panelButtonsAll = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonClose = new BaseButton("Close");
    buttonClose.setMnemonic('l');
    buttonClose.addActionListener((ActionEvent e) -> panel.closeParent());
    panelButtonsAll.add(buttonClose);
    panel.add(panelButtonsAll, BorderLayout.SOUTH);

    // format
    panelTab = new BasePanel(new BorderLayout());
    tabbedPane.addTab("Format", panelTab);

    panelParams = new ParameterPanel();
    fieldFormatInput = new BaseTextField(30);
    panelParams.addParameter("Format", fieldFormatInput);
    fieldFormatOutput = new BaseTextField(30);
    panelParams.addParameter("Output", fieldFormatOutput);
    panelTab.add(panelParams, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonTest = new BaseButton("Test");
    buttonTest.setMnemonic('T');
    buttonTest.addActionListener((ActionEvent e) -> {
      String input = fieldFormatInput.getText();
      String output = null;
      try {
	SimpleDateFormat dformat = new SimpleDateFormat(input);
	output = dformat.format(new Date());
	fieldFormatOutput.setText(output);
      }
      catch (Exception ex) {
	fieldFormatOutput.setText("");
	GUIHelper.showErrorMessage(
	  null,
	  "Failed to apply date format:\n"
	    + input + "\n"
	    + "to current date!",
	  "DateTest");
      }
    });
    panelButtons.add(buttonTest);
    buttonHelp = new BaseButton("Help");
    buttonHelp.setMnemonic('H');
    buttonHelp.addActionListener((ActionEvent e) -> {
      BrowserHelper.openURL(new DateFormatString().getHelpURL());
    });
    panelButtons.add(buttonHelp);
    panelTab.add(panelButtons, BorderLayout.SOUTH);

    // parse
    panelTab = new BasePanel(new BorderLayout());
    tabbedPane.addTab("Parse", panelTab);

    panelParams = new ParameterPanel();
    fieldParseInput = new BaseTextField(30);
    panelParams.addParameter("Input", fieldParseInput);
    fieldParseFormat = new BaseTextField();
    panelParams.addParameter("Format", fieldParseFormat);
    fieldParseValue = new BaseTextField(30);
    panelParams.addParameter("Value", fieldParseValue);
    panelTab.add(panelParams, BorderLayout.CENTER);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonTest = new BaseButton("Test");
    buttonTest.setMnemonic('T');
    buttonTest.addActionListener((ActionEvent e) -> {
      String input = fieldParseInput.getText();
      String format = fieldParseFormat.getText();
      try {
	SimpleDateFormat dformat = new SimpleDateFormat(format);
	Date value = dformat.parse(input);
	DateFormat dformatVal = DateUtils.getTimestampFormatterMsecs();
	String output = dformatVal.format(value);
	fieldParseValue.setText(output);
      }
      catch (Exception ex) {
	fieldParseValue.setText("");
	GUIHelper.showErrorMessage(
	  null,
	  "Failed to parse input '" + input + "' with date format '" + format + "'!",
	  "DateTest");
      }
    });
    panelButtons.add(buttonTest);
    buttonHelp = new BaseButton("Help");
    buttonHelp.setMnemonic('H');
    buttonHelp.addActionListener((ActionEvent e) -> {
      BrowserHelper.openURL(new DateFormatString().getHelpURL());
    });
    panelButtons.add(buttonHelp);
    panelTab.add(panelButtons, BorderLayout.SOUTH);

    createChildFrame(panel);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "Date test";
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