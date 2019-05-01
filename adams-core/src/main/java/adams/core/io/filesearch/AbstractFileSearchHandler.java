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
 * AbstractSearchHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filesearch;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for search handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileSearchHandler
  extends AbstractOptionHandler
  implements FileSearchHandler, ShallowCopySupporter<FileSearchHandler> {

  private static final long serialVersionUID = -5947868410377097406L;

  /** the available handlers. */
  protected static List<AbstractFileSearchHandler> m_Handlers;

  /** for logging in static context. */
  protected static Logger LOGGER = LoggingHelper.getLogger(AbstractFileSearchHandler.class);

  /** whether the search was stopped. */
  protected boolean m_Stopped;

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  public FileSearchHandler shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public FileSearchHandler shallowCopy(boolean expand) {
    return (FileSearchHandler) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns the best-suited handler for the file.
   * {@link TextFileSearchHandler} is used as backup.
   *
   * @param file	the file to get a handler for
   * @return		the handler, null if none available
   */
  public static synchronized FileSearchHandler getHandlerForFile(String file) {
    FileSearchHandler     	result;
    TextFileSearchHandler 	text;
    Class[]			classes;

    result = null;

    // init handlers?
    if (m_Handlers == null) {
      m_Handlers = new ArrayList<>();
      classes    = ClassLister.getSingleton().getClasses(FileSearchHandler.class);
      for (Class cls: classes) {
        if (cls.equals(TextFileSearchHandler.class))
          continue;
        try {
          m_Handlers.add((AbstractFileSearchHandler) cls.newInstance());
	}
	catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Failed to instantiate: " + cls.getName(), e);
	}
      }
    }

    for (AbstractFileSearchHandler handler: m_Handlers) {
      if (handler.handles(file)) {
        result = handler.shallowCopy();
        break;
      }
    }

    // use fallback?
    if (result == null) {
      text = new TextFileSearchHandler();
      if (text.handles(file))
        result = text;
    }

    return result;
  }
}
