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
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.table.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Displays the SystemInfo.
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
    Table<String> 		table;
    adams.core.SystemInfo	info;
    List<String>		keys;

    info  = new adams.core.SystemInfo();
    table = new Table<>("Key", "Value");
    keys  = new ArrayList<>(info.getInfo().keySet());
    Collections.sort(keys);
    for (String key: keys)
      table.getTableModel().addRow(key, info.getInfo().get(key));
    ComponentDialog.showDialog(context, "System info", null, table);
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