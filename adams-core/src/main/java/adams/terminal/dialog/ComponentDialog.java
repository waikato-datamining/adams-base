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
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.dialog;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LocalizedString;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Dialog for displaying a component.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ComponentDialog
  extends DialogWindow {

  /** the component to display. */
  protected Component component;

  /** the button that was selected. */
  protected MessageDialogButton result;

  /**
   * Default constructor, takes a title for the dialog and runs code shared for dialogs
   *
   * @param title       Title of the window
   * @param description	the optional description, can be null
   * @param component   the component to display
   */
  public ComponentDialog(String title, String description, Component component) {
    this(title, description, component, new ArrayList<>());
  }

  /**
   * Default constructor, takes a title for the dialog and runs code shared for dialogs
   *
   * @param title       Title of the window
   * @param description	the optional description, can be null
   * @param component   the component to display
   * @param hints	the window hints
   */
  public ComponentDialog(String title, String description, Component component, Collection<Hint> hints) {
    super(title);
    Panel panelButtons = new Panel(new GridLayout(2));
    panelButtons.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, true, false, 2, 1));
    panelButtons.addComponent(new Button(LocalizedString.OK.toString(), () -> onOK()));
    panelButtons.addComponent(new Button(LocalizedString.Cancel.toString(), () -> onCancel()));

    Panel contentPanel = new Panel();
    contentPanel.setLayoutManager(new BorderLayout());
    if (description != null)
      contentPanel.addComponent(new Label(description), Location.TOP);
    setHints(hints);
    contentPanel.addComponent(component, Location.CENTER);
    contentPanel.addComponent(panelButtons, Location.BOTTOM);
    setComponent(contentPanel);
  }

  /**
   * Sets the width and height of the dialog according to the percentage of the
   * overall available screen real estate.
   *
   * @param textGUI 	for determining the maximum dimensions
   * @param width	the width percentage (0-1) or number of columns (> 1)
   * @param height	the height percentage (0-1) or number of rows (> 1)
   */
  public void setSize(WindowBasedTextGUI textGUI, double width, double height) {
    int		actWidth;
    int		actHeight;
    
    if (width <= 1.0)
      actWidth = (int) (textGUI.getScreen().getTerminalSize().getColumns() * width);
    else
      actWidth = (int) width;

    if (height <= 1.0)
      actHeight = (int) (textGUI.getScreen().getTerminalSize().getRows() * height);
    else
      actHeight = (int) height;
    
    setSize(new TerminalSize(actWidth, actHeight));
  }

  protected void onOK() {
    result = MessageDialogButton.OK;
    close();
  }

  protected void onCancel() {
    result = MessageDialogButton.Cancel;
    close();
  }

  /**
   * Shows the dialog.
   *
   * @param textGUI	the context
   * @return		the button that was selected
   */
  @Override
  public MessageDialogButton showDialog(WindowBasedTextGUI textGUI) {
    result = MessageDialogButton.Cancel;
    super.showDialog(textGUI);
    return result;
  }

  /**
   * Returns the button that was selected.
   *
   * @return		the button
   */
  public MessageDialogButton getResult() {
    return result;
  }

  /**
   * Shortcut for quickly showing a {@code OptionDialog}.
   *
   * @param textGUI     GUI to show the dialog on
   * @param title       Title of the dialog
   * @param description Description of the dialog
   * @param component	the component
   * @return		the button that was selected
   */
  public static MessageDialogButton showDialog(WindowBasedTextGUI textGUI, String title, String description, Component component) {
    return showDialog(textGUI, title, description, component, new ArrayList<>());
  }

  /**
   * Shortcut for quickly showing a {@code OptionDialog}.
   *
   * @param textGUI     GUI to show the dialog on
   * @param title       Title of the dialog
   * @param description Description of the dialog
   * @param component	the component
   * @param hints	the window hints
   * @return		the button that was selected
   */
  public static MessageDialogButton showDialog(WindowBasedTextGUI textGUI, String title, String description, Component component, Collection<Hint> hints) {
    ComponentDialog dialog = new ComponentDialog(title, description, component, hints);
    return dialog.showDialog(textGUI);
  }
}
