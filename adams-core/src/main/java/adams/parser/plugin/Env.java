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
 * Env.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser.plugin;


/**
 * Example function that simply outputs the parameters on the command-line.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Env
  extends AbstractParserFunction {

  /** for serialization. */
  private static final long serialVersionUID = 7658759478874630128L;

  /**
   * Returns the function name.
   * Can only consist of letters, underscores, numbers.
   * 
   * @return		the name of the function
   */
  @Override
  public String getFunctionName() {
    return "env";
  }

  /**
   * Returns the signature of the function.
   * 
   * @return		the signature
   */
  @Override
  public String getFunctionSignature() {
    return getFunctionName() + "(String): String";
  }

  /**
   * Returns the help string for the function.
   * 
   * @return		the help string
   */
  @Override
  public String getFunctionHelp() {
    return 
	getFunctionSignature() + "\n"
	+ "\tFirst argument is the name of the environment variable to retrieve.\n"
	+ "\tThe result is the value of the environment variable.";
  }

  /**
   * Performs some checks on the input parameters.
   * 
   * @param params	the input parameters
   * @return		always null
   */
  @Override
  protected String check(Object[] params) {
    if (params.length != 1)
      return "Only accepts single parameter, which must be name of the environment variable to retrieve!";
    return null;
  }

  /**
   * Gets called from the parser.
   * 
   * @param params	the parameters obtained through the parser
   */
  @Override
  protected Object doCallFunction(Object[] params) {
    return System.getenv().get(params[0]);
  }
}
