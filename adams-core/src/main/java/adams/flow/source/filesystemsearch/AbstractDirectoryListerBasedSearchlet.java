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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for search algorithms that use {@link adams.core.io.DirectoryLister}
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
  protected adams.core.io.DirectoryLister m_Lister;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister = new adams.core.io.DirectoryLister();
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
    
    result = new ArrayList<String>();
    result.addAll(Arrays.asList(m_Lister.list()));
    
    return result;
  }
}
