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
 * Container.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.core.CloneHandler;
import adams.core.GlobalInfoSupporter;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Interface for containers. A container allows the access to the stored
 * values via their names.
 * <br><br>
 * NB: containers need to declare a default constructor. The default constructor
 * is used to generate help information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface Container
  extends Serializable, CloneHandler<Container>, SpreadSheetSupporter, GlobalInfoSupporter {

  /**
   * Returns a clone of itself using serialization.
   *
   * @return		the clone
   */
  public Container getClone();

  /**
   * Stores the help for the given name if valid name.
   *
   * @param name	the name to store the help under
   * @param desc	the help description
   */
  public void addHelp(String name, String desc);

  /**
   * Stores the help for the given name if valid name.
   *
   * @param name	the name to store the help under
   * @param desc	the help description
   * @param type	the type of the data
   */
  public void addHelp(String name, String desc, Class type);

  /**
   * Stores the help for the given name if valid name.
   *
   * @param name	the name to store the help under
   * @param desc	the help description
   * @param types	the types of the data
   */
  public void addHelp(String name, String desc, Class[] types);

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  public Iterator<String> names();

  /**
   * Returns all available stored value names.
   *
   * @return		enumeration over all stored value names
   */
  public Iterator<String> stored();

  /**
   * Checks whether a given value is non-null.
   *
   * @param name	the name of the value to check
   * @return		true if the value is non-null
   */
  public boolean hasValue(String name);

  /**
   * Returns the value associated with the given name.
   *
   * @param name	the name of the value
   * @return		the associated value or null if not available
   */
  public Object getValue(String name);

  /**
   * Returns the value associated with the given name.
   *
   * @param name	the name of the value
   * @param cls		for casting
   * @return		the associated value or null if not available
   */
  public <T> T getValue(String name, Class<T> cls);

  /**
   * Checks whether a given help is non-null.
   *
   * @param name	the name of the help item to check
   * @return		true if the help is non-null
   */
  public boolean hasHelp(String name);

  /**
   * Returns the help associated with the given name.
   *
   * @param name	the name of the help item
   * @return		the associated help or null if not available
   */
  public String getHelp(String name);

  /**
   * Sets the named value.
   *
   * @param name	the name of the value
   * @param value	the value to store in the container
   * @return		true if the name was recognized and the value was
   * 			stored successfully, false otherwise
   */
  public boolean setValue(String name, Object value);

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  public boolean isValid();

  /**
   * Adds the specified name as valid name for a value.
   * 
   * @param name	the name to add
   * @return		true if the additional names list changed
   */
  public boolean addAdditionalName(String name);

  /**
   * Removes the additional name again.
   * 
   * @param name	the name to remove
   * @return		true if the name was present and got removed
   */
  public boolean removeAdditionalName(String name);

  /**
   * Returns a short description of the stored data.
   *
   * @return		short description
   */
  public String toString();

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  public SpreadSheet toSpreadSheet();
}
