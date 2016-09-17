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
 * PersistentObjectHistory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe;

import adams.core.AbstractPersistentHistory;
import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.event.HistorySelectionEvent;
import adams.gui.event.HistorySelectionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for maintaining a history of objects selected in the GOE.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PersistentObjectHistory
  extends AbstractPersistentHistory<Object> {

  private static final long serialVersionUID = -1470748187005326115L;

  /** the maximum number of characters per line for a single setup. */
  public final static int MAX_LINE_CHARS = 80;

  /** the superclass. */
  protected Class m_Superclass;

  /**
   * Sets the superclass to use.
   *
   * @param value	the class
   */
  public void setSuperclass(Class value) {
    m_Superclass = value;
    setHistoryFile(
      new PlaceholderFile(
	Environment.getInstance().getHome()
	  + File.separator
	  + "History-" + m_Superclass.getName() + ".txt"));
  }

  /**
   * Creates a copy of the object.
   *
   * @param obj		the object to copy
   */
  @Override
  protected Object copy(Object obj) {
    return ObjectCopyHelper.copyObject(obj);
  }

  /**
   * Generates an HTML caption for the an entry in the history menu.
   *
   * @param obj		the object to create the caption for
   * @return		the generated HTML captiopn
   */
  protected String generateMenuItemCaption(Object obj) {
    StringBuilder	result;
    String		cmd;
    String[]		lines;
    int			i;

    result = new StringBuilder();

    cmd    = OptionUtils.getCommandLine(obj);
    if (cmd.length() > MAX_HISTORY_LENGTH)
      cmd = cmd.substring(0, MAX_HISTORY_LENGTH) + "...";

    lines  = Utils.breakUp(cmd, MAX_LINE_CHARS);
    result.append("<html>");
    for (i = 0; i < lines.length; i++) {
      if (i > 0)
	result.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      result.append(lines[i].trim());
    }
    result.append("</html>");

    return result.toString();
  }

  /**
   * Adds a menu item with the history to the popup menu.
   *
   * @param menu	the menu to add the history to
   * @param current	the current object
   * @param listener	the listener to attach to the menu items' ActionListener
   */
  public void customizePopupMenu(JPopupMenu menu, Object current, final HistorySelectionListener listener) {
    JMenu submenu;
    JMenuItem item;
    int			i;

    submenu = new JMenu("History");
    menu.add(submenu);

    // clear history
    item = new JMenuItem("Clear history");
    item.addActionListener((ActionEvent e) -> m_History.clear());
    submenu.add(item);

    // current history
    for (i = 0; i < m_History.size(); i++) {
      if (i == 0)
	submenu.addSeparator();
      final Object history = m_History.get(i);
      item = new JMenuItem(generateMenuItemCaption(history));
      item.addActionListener((ActionEvent e)
        -> listener.historySelected(new HistorySelectionEvent(listener, history)));
      submenu.add(item);
    }
  }

  /**
   * Saves the history to disk.
   *
   * @return		true if successfully saved
   */
  @Override
  protected boolean save() {
    List<String>	list;

    if (getHistoryFile().isDirectory())
      return false;

    list = new ArrayList<>();
    for (Object obj: m_History)
      list.add(OptionUtils.getCommandLine(obj));

    return FileUtils.saveToFile(list, getHistoryFile());
  }

  /**
   * Loads the history from disk.
   *
   * @return		true if successfully loaded
   */
  @Override
  protected boolean load() {
    List<String>	list;

    if (getHistoryFile().isDirectory() || !getHistoryFile().exists())
      return false;
    list = FileUtils.loadFromFile(getHistoryFile());
    if (list == null)
      return false;
    clear();
    for (String item: list) {
      try {
	add(OptionUtils.forAnyCommandLine(m_Superclass, item));
      }
      catch (Exception e) {
	// ignored
      }
    }
    return true;
  }
}
