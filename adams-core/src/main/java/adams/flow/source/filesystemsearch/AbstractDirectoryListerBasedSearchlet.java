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
 * AbstractDirectoryListerBasedSearchlet.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import adams.core.io.lister.LocalDirectoryLister;
import adams.core.logging.LoggingLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for search algorithms that use {@link LocalDirectoryLister}
 * under the hood.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDirectoryListerBasedSearchlet
  extends AbstractFileSystemSearchlet {
  
  /** for serialization. */
  private static final long serialVersionUID = -240436041323613527L;
  
  /** for listing the contents. */
  protected LocalDirectoryLister m_Lister;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister = new LocalDirectoryLister();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Lister.setLoggingLevel(value);
  }

  /**
   * Performs the actual search.
   * 
   * @return		the search result
   * @throws Exception	if search failed
   */
  @Override
  protected List<String> doSearch() throws Exception {
    List<String>	result;
    
    result = new ArrayList<>();
    result.addAll(Arrays.asList(m_Lister.list()));
    
    return result;
  }
}
