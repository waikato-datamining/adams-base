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
 * AbstractFileSystemSearchlet.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import java.util.List;

import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Ancestor for file-system search algorithms.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileSystemSearchlet
  extends AbstractOptionHandler
  implements ShallowCopySupporter<AbstractFileSystemSearchlet>, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5019667028030872568L;
  
  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation just returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs a setup check before search.
   * <p/>
   * Default implementation does nothing.
   * 
   * @throws Exception	if checks failed
   */
  protected void check() throws Exception {
  }
  
  /**
   * Performs the actual search.
   * 
   * @return		the search result
   * @throws Exception	if search failed
   */
  protected abstract List<String> doSearch() throws Exception;

  /**
   * Searches the file system.
   * 
   * @return		the search result
   * @throws Exception	if checks or search failed
   */
  public List<String> search() throws Exception {
    check();
    return doSearch();
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractFileSystemSearchlet shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractFileSystemSearchlet shallowCopy(boolean expand) {
    return (AbstractFileSystemSearchlet) OptionUtils.shallowCopy(this, expand);
  }
}
