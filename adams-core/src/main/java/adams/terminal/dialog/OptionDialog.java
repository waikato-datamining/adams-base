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
 * OptionDialog.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal.dialog;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LocalizedString;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Dialog for displaying a combobox with options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionDialog
  extends DialogWindow {

  /** the combobox with the options. */
  protected ComboBox<String> comboBox;

  /** the chosen option. */
  protected String result;

  /**
   * Default constructor, takes a title for the dialog and runs code shared for dialogs
   *
   * @param title       Title of the window
   * @param description the description to display
   * @param options     the options to display in a combobox
   * @param initial	the initial value
   */
  public OptionDialog(String title, String description, String[] options, String initial) {
    this(title, description, options, initial, new ArrayList<>());
  }

  /**
   * Default constructor, takes a title for the dialog and runs code shared for dialogs
   *
   * @param title       Title of the window
   * @param description the description to display
   * @param options     the options to display in a combobox
   * @param initial	the initial value
   * @param hints	the window hints
   */
  public OptionDialog(String title, String description, String[] options, String initial, Collection<Hint> hints) {
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
    if (description != null) {
      mainPanel.addComponent(new Label(description));
    }
    setHints(hints);
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
   * @param textGUI     GUI to show the dialog on
   * @param title       Title of the dialog
   * @param description Description of the dialog
   * @param initial     the initial selection
   * @return The option the user selected, or {@code null} if the dialog was cancelled
   */
  public static String showDialog(WindowBasedTextGUI textGUI, String title, String description, String[] options, String initial) {
    return showDialog(textGUI, title, description, options, initial, new ArrayList<>());
  }

  /**
   * Shortcut for quickly showing a {@code OptionDialog}.
   *
   * @param textGUI     GUI to show the dialog on
   * @param title       Title of the dialog
   * @param description Description of the dialog
   * @param initial     the initial selection
   * @param hints	the window hints
   * @return The option the user selected, or {@code null} if the dialog was cancelled
   */
  public static String showDialog(WindowBasedTextGUI textGUI, String title, String description, String[] options, String initial, Collection<Hint> hints) {
    OptionDialog dialog = new OptionDialog(title, description, options, initial);
    return dialog.showDialog(textGUI);
  }
}
