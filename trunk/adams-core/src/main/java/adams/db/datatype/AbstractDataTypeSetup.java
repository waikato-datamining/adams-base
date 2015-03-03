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
 * AbstractDataTypeSetup.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db.datatype;

import java.sql.Connection;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that update a database connection, configuring
 * additional data types.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataTypeSetup
  extends AbstractOptionHandler {
  
  /** for serialization. */
  private static final long serialVersionUID = 6940789084605225155L;

  /**
   * Configures the data types.
   * 
   * @return		null if OK, otherwise error message
   */
  public abstract String setupDataTypes(Connection conn);
}
