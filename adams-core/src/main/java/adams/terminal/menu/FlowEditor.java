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
 * FlowEditor.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.NestedConsumer;
import adams.terminal.dialog.ComponentDialog;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.FileDialog;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Lets the user select a flow, display it in raw format and save it again.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowEditor
  extends AbstractMenuItemDefinition {

  private static final long serialVersionUID = 4850496199819749023L;

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Flow editor";
  }

  /**
   * Checks whether the flow is valid.
   *
   * @param flow	the flow to check
   * @return		true if valid
   */
  protected boolean isValid(String flow) {
    boolean		result;
    NestedConsumer 	consumer;

    try {
      consumer = new NestedConsumer();
      consumer.setQuiet(true);
      consumer.fromString(flow);
      consumer.cleanUp();
      result = true;
    }
    catch (Exception ex) {
      result = false;
    }

    return result;
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(WindowBasedTextGUI context) {
    FileDialogBuilder	builder;
    FileDialog 		dialog;
    File 		file;
    List<String>	lines;
    TextBox		textBox;
    MessageDialogButton	retVal;
    String		flow;

    builder = new FileDialogBuilder();
    builder.setTitle("Open");
    builder.setDescription("Select a flow");
    builder.setActionLabel("Open");
    dialog = builder.build();
    file   = dialog.showDialog(context);
    if (file == null)
      return;

    file  = new PlaceholderFile(file);
    lines = FileUtils.loadFromFile(file);
    if (lines == null) {
      MessageDialog.showMessageDialog(context, "Error loading flow", "Failed to load flow:\n" + file, MessageDialogButton.OK);
      return;
    }

    textBox = new TextBox(new TerminalSize(30, 10));
    textBox.setText(Utils.flatten(lines, "\n"));
    retVal = ComponentDialog.showDialog(context, "Flow editor", "Flow:\n" + file, textBox, Arrays.asList(Hint.CENTERED));
    if (retVal == MessageDialogButton.OK) {
      flow = textBox.getText();
      if (!isValid(flow)) {
	MessageDialog.showMessageDialog(context, "Invalid flow", "Cannot save flow, invalid!", MessageDialogButton.OK);
	return;
      }
      retVal = MessageDialog.showMessageDialog(context, "Save flow?", "Save flow?\n" + file, MessageDialogButton.Yes, MessageDialogButton.No);
      if (retVal == MessageDialogButton.Yes) {
	if (!FileUtils.writeToFile(file.getAbsolutePath(), flow, false)) {
	  MessageDialog.showMessageDialog(context, "Error saving flow", "Failed to save flow to:\n" + file, MessageDialogButton.OK);
	  return;
	}
      }
    }
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_TOOLS;
  }
}
