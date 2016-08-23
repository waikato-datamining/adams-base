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
 * ConsoleHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.console;

import adams.core.base.BasePassword;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LocalizedString;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper for input using <a href="https://github.com/mabe02/lanterna" target="_blank">lanterna</a>.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Lanterna
  implements Console {

  /** the lanterna context. */
  protected static MultiWindowTextGUI m_Context;

  public static class OptionDialog
    extends DialogWindow {

    /** the combobox with the options. */
    protected ComboBox<String> comboBox;

    /** the chosen option. */
    protected String result;

    /**
     * Default constructor, takes a title for the dialog and runs code shared for dialogs
     *
     * @param title Title of the window
     * @param description the description to display
     * @param options the options to display in a combobox
     */
    protected OptionDialog(String title, String description, String[] options, String initial) {
      super(title);
      comboBox = new ComboBox<>(Arrays.asList(options));
      Panel buttonPanel = new Panel();
      buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
      buttonPanel.addComponent(new Button(LocalizedString.OK.toString(), () -> onOK())
	.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
      buttonPanel.addComponent(new Button(LocalizedString.Cancel.toString(), () -> onCancel()));

      Panel mainPanel = new Panel();
      mainPanel.setLayoutManager(
	new GridLayout(1)
	  .setLeftMarginSize(1)
	  .setRightMarginSize(1));
      if(description != null) {
	mainPanel.addComponent(new Label(description));
      }
      mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
      comboBox.setLayoutData(
	GridLayout.createLayoutData(
	  GridLayout.Alignment.FILL,
	  GridLayout.Alignment.CENTER,
	  true,
	  false))
	.addTo(mainPanel);
      mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
      buttonPanel.setLayoutData(
	GridLayout.createLayoutData(
	  GridLayout.Alignment.END,
	  GridLayout.Alignment.CENTER,
	  false,
	  false))
	.addTo(mainPanel);
      setComponent(mainPanel);
    }

    protected void onOK() {
      String text = comboBox.getText();
      result = text;
      close();
    }

    protected void onCancel() {
      close();
    }

    @Override
    public String showDialog(WindowBasedTextGUI textGUI) {
      result = null;
      super.showDialog(textGUI);
      return result;
    }

    /**
     * Shortcut for quickly showing a {@code OptionDialog}.
     *
     * @param textGUI GUI to show the dialog on
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param initial the initial selection
     * @return The option the user selected, or {@code null} if the dialog was cancelled
     */
    public static String showDialog(WindowBasedTextGUI textGUI, String title, String description, String[] options, String initial) {
      OptionDialog dialog = new OptionDialog(title, description, options, initial);
      return dialog.showDialog(textGUI);
    }
  }

  /**
   * Initializes the console helper.
   *
   * @param context	the lanterna context
   */
  public Lanterna(MultiWindowTextGUI context) {
    if (context == null)
      throw new IllegalArgumentException("Lanterna context cannot be null!");

    m_Context = context;
  }

  /**
   * Lets the user enter a multi-line value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterMultiLineValue(String msg) {
    return enterValue(msg);
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterValue(String msg) {
    return enterValue(msg, null);
  }

  /**
   * Lets the user enter a value.
   *
   * @param msg		the message to output before reading the input
   * @param initial	the initial value, ignored if empty or null
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String enterValue(String msg, String initial) {
    if (msg.isEmpty())
      msg = "Please enter a value:";
    return TextInputDialog.showDialog(
      m_Context, "Enter value", msg, (initial == null) ? "" : initial);
  }

  /**
   * Lets the user enter multiple values. Empty string terminates the loop.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public String[] enterMultipleValues(String msg) {
    List<String>	result;
    String		input;

    result = new ArrayList<>();

    if (msg.isEmpty())
      msg = "Please enter one or more values:";
    msg += "\n(empty input will terminate input)";

    do {
      input = enterValue(msg);
      if (input != null) {
	if (!input.isEmpty())
	  result.add(input);
      }
    }
    while ((input != null) && !input.isEmpty());

    if (result.size() > 0)
      return result.toArray(new String[result.size()]);
    else
      return null;
  }

  /**
   * Lets the user enter a password.
   *
   * @param msg		the message to output before reading the input
   * @return		the entered value, null if cancelled or failed to read input
   */
  public BasePassword enterPassword(String msg) {
    BasePassword	result;
    String		input;

    result = null;

    if (msg.isEmpty())
      msg = "Please enter password:";
    input = TextInputDialog.showPasswordDialog(m_Context, "Enter password", msg, "");
    if (input != null)
      result = new BasePassword(input);

    return result;
  }

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @return		the selected option, null if cancelled or failed to read input
   */
  public String selectOption(String msg, String[] options) {
    return selectOption(msg, options, null);
  }

  /**
   * Lets the user select from a number of choices.
   *
   * @param msg		the message to output before reading the input
   * @param options	the options to choose from
   * @param initial	the initial selection (selected if just hitting enter), null or empty string to ignore
   * @return		the selected option, null if cancelled or failed to read input
   */
  public String selectOption(String msg, String[] options, String initial) {
    if (initial == null)
      initial = "";

    if (msg.isEmpty())
      msg = "Please select an option:";

    return OptionDialog.showDialog(m_Context, "Select option", msg, options, initial);
  }
}
