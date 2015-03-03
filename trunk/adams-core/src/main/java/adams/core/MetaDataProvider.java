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
 * MetaDataProvider.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

/**
 * For classes that provide additional information for their objects. These
 * objects act as keys in the retrieval of the meta-data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MetaDataProvider {

  /**
   * Retrieves the meta-data for the given object. Can return null if no
   * meta-data available.
   * 
   * @param key		the object to retrieve the meta-data for
   * @return		the meta-data or null if none available
   */
  public Object getMetaData(Object key);
}
