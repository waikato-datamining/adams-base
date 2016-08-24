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
 * ComponentDialog.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal.dialog;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LocalizedString;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;

/**
 * Dialog for displaying a component. Only has an OK button.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ComponentDialog
  extends DialogWindow {

  /** the component to display. */
  protected Component component;

  /**
   * Default constructor, takes a title for the dialog and runs code shared for dialogs
   *
   * @param title       Title of the window
   * @param component   the component to display
   */
  protected ComponentDialog(String title, String description, Component component) {
    super(title);
    Panel buttonPanel = new Panel();
    buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
    buttonPanel.addComponent(new Button(LocalizedString.OK.toString(), () -> onOK())
      .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));

    Panel mainPanel = new Panel();
    mainPanel.setLayoutManager(
      new GridLayout(1)
	.setLeftMarginSize(1)
	.setRightMarginSize(1));
    if (description != null) {
      mainPanel.addComponent(new Label(description));
    }
    mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
    component.setLayoutData(
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
    close();
  }

  /**
   * Shortcut for quickly showing a {@code OptionDialog}.
   *
   * @param textGUI     GUI to show the dialog on
   * @param title       Title of the dialog
   * @param description Description of the dialog
   * @param component	the component
   */
  public static void showDialog(WindowBasedTextGUI textGUI, String title, String description, Component component) {
    ComponentDialog dialog = new ComponentDialog(title, description, component);
    dialog.showDialog(textGUI);
  }
}
