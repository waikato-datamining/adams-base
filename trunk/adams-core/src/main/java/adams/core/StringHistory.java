/**
 * StringHistory.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.util.ArrayList;
import java.util.List;

import adams.core.io.FileUtils;

/**
 * History for arbitrary strings.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringHistory
  extends AbstractPersistentHistory<String> {

  /** for serialization. */
  private static final long serialVersionUID = -5716154035144840331L;

  /**
   * Creates a copy of the object.
   *
   * @param obj		the object to copy
   */
  @Override
  protected String copy(String obj) {
    return new String(obj);
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
    list = new ArrayList<String>();
    for (String item: m_History)
      list.add(Utils.backQuoteChars(item));

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
	m_History.add(Utils.unbackQuoteChars(item));
    }

    return (list != null);
  }
}
