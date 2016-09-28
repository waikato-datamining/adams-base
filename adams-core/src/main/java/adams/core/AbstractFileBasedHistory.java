/**
 * AbstractFileBasedHistory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.io.FileUtils;
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
 * History for PlaceholderDirectory objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6358 $
 */
public abstract class AbstractFileBasedHistory<T extends File>
  extends AbstractPersistentHistory<T> {

  /** for serialization. */
  private static final long serialVersionUID = -5716154035144840331L;

  /**
   * Creates a new file object from the string.
   *
   * @param path 	the path to create the object from
   */
  protected abstract T newInstance(String path);

  /**
   * Creates a copy of the object.
   *
   * @param obj		the object to copy
   */
  @Override
  protected T copy(T obj) {
    return newInstance(obj.getAbsolutePath());
  }

  /**
   * Saves the history to disk.
   *
   * @return		true if successfully saved
   */
  @Override
  protected boolean save() {
    List<String>	list;

    if (m_HistoryFile.isDirectory())
      return false;

    // make sure strings are only single-line
    list = new ArrayList<>();
    for (T item: m_History)
      list.add(item.getAbsolutePath());

    return FileUtils.saveToFile(list, m_HistoryFile);
  }

  /**
   * Loads the history from disk.
   *
   * @return		true if successfully loaded
   */
  @Override
  protected boolean load() {
    List<String>	list;

    if (m_HistoryFile.isDirectory())
      return false;
    if (!m_HistoryFile.exists())
      return false;

    // convert strings back from single-line
    list = FileUtils.loadFromFile(m_HistoryFile);
    if (list != null) {
      m_History.clear();
      for (String item: list)
	m_History.add(newInstance(item));
    }

    return (list != null);
  }

  /**
   * Generates an HTML caption for the an entry in the history menu.
   *
   * @param obj		the object to create the caption for
   * @return		the generated HTML captiopn
   */
  protected String generateMenuItemCaption(T obj) {
    return "<html>" + obj.getName() + "</html>";
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
      item = new JMenuItem(generateMenuItemCaption((T) history));
      item.addActionListener((ActionEvent e)
        -> listener.historySelected(new HistorySelectionEvent(listener, history)));
      submenu.add(item);
    }
  }
}
