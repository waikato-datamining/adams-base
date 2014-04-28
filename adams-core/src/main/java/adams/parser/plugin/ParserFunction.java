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
 * ParserFunction.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser.plugin;

import java.io.Serializable;

/**
 * Interface for functions to be used in parsers. 
 * Functions must be stateless, as only a single instance is kept in memory.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ParserFunction
  extends Serializable {

  /**
   * Returns the name of the function. 
   * Can only consist of letters, underscores, numbers.
   * 
   * @return		the name
   */
  public String getFunctionName();

  /**
   * Returns the signature of the function.
   * 
   * @return		the signature
   */
  public String getFunctionSignature();

  /**
   * Returns the help string for the function.
   * 
   * @return		the help string
   */
  public String getFunctionHelp();

  /**
   * Gets called from the parser.
   * 
   * @param params	the parameters obtained through the parser
   * @return		the result to be used further in the parser
   */
  public Object callFunction(Object[] params);
}
