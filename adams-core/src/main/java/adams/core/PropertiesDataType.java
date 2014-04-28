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
 * PropertiesDataType.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

/**
 * Data types that the {@link Properties} class handles.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum PropertiesDataType {
  /** stock standard string value, as stored. */
  PROPERTY,
  /** performs some replacements, see {@link Properties#expandPlaceHolders(String)}. */
  PATH,
  BOOLEAN,
  INTEGER,
  LONG,
  DOUBLE,
  COLOR,
  FONT,
  TIME,
  DATE,
  DATETIME,
  PASSWORD
}
