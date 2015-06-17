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
 * ListDatabaseConnections.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;


import adams.core.option.OptionHandler;
import adams.core.option.OptionTraversalPath;

/**
 * Processor that lists database connections.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListDatabaseConnections
  extends AbstractListingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists all database connections.";
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Database connections";
  }

  /**
   * Checks whether the object is valid and should be added to the list.
   * 
   * @param handler	the option handler this object belongs to
   * @param obj		the object to check
   * @param path	the traversal path of properties
   * @return		true if valid
   */
  @Override
  protected boolean isValid(OptionHandler handler, Object obj, OptionTraversalPath path) {
    return 
	   (obj instanceof adams.flow.standalone.AbstractDatabaseConnection) 
	|| (obj instanceof adams.db.AbstractDatabaseConnection);
  }

  /**
   * Returns the string representation of the object that is added to the list.
   * 
   * @param handler	the option handler this object belongs to
   * @param obj		the object to turn into a string
   * @param path	the traversal path of properties
   * @return		the string representation, null if to ignore the item
   */
  @Override
  protected String objectToString(OptionHandler handler, Object obj, OptionTraversalPath path) {
    if (obj instanceof adams.flow.standalone.AbstractDatabaseConnection) {
      adams.flow.standalone.AbstractDatabaseConnection conn = (adams.flow.standalone.AbstractDatabaseConnection) obj;
      return conn.getURL();
    }
    
    if (obj instanceof adams.db.AbstractDatabaseConnection) {
      adams.db.AbstractDatabaseConnection conn = (adams.db.AbstractDatabaseConnection) obj;
      return conn.getURL();
    }
    
    return null;
  }

  /**
   * Returns whether the list should be sorted.
   * 
   * @return		true if the list should get sorted
   */
  @Override
  protected boolean isSortedList() {
    return true;
  }

  /**
   * Returns whether the list should not contain any duplicates.
   * 
   * @return		true if the list contains no duplicates
   */
  @Override
  protected boolean isUniqueList() {
    return true;
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   * 
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Connections in use:";
  }
}
