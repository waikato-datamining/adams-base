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
 * SystemInfo.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.terminal.menu;

import adams.terminal.application.AbstractTerminalApplication;
import adams.terminal.dialog.ComponentDialog;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Shows information about the system.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SystemInfo
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -6548349613973153076L;

  /**
   * Initializes the menu item with no owner.
   */
  public SystemInfo() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public SystemInfo(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Actual execution.
   *
   * @param context	the context to use
   */
  protected void doRun(WindowBasedTextGUI context) {
    final ActionListBox 	keys;
    final TextBox		value;
    final adams.core.SystemInfo	info;
    List<String>		sorted;
    Panel 			panel;

    info   = new adams.core.SystemInfo();
    sorted = new ArrayList<>(info.getInfo().keySet());
    Collections.sort(sorted);
    keys  = new ActionListBox();
    keys.setPreferredSize(new TerminalSize(40, 10));
    value = new TextBox(new TerminalSize(40, 5));
    value.setCaretWarp(true);
    value.setReadOnly(true);
    for (final String key: sorted)
      keys.addItem(key, () -> {
	value.setText(info.getInfo().get(key));
	value.setCaretPosition(0, 0);
      });
    panel = new Panel(new BorderLayout());
    panel.addComponent(keys, Location.CENTER);
    panel.addComponent(value, Location.BOTTOM);
    panel.setPreferredSize(new TerminalSize(40, 15));

    ComponentDialog.showDialog(context, "System info", null, panel, Arrays.asList(Hint.CENTERED));
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "System info";
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_HELP;
  }
}