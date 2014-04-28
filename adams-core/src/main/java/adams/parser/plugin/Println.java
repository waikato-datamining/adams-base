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
 * Println.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser.plugin;

import adams.core.Utils;

/**
 * Example function that simply outputs the parameters on the command-line.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Println
  extends AbstractParserProcedure {

  /** for serialization. */
  private static final long serialVersionUID = 7658759478874630128L;

  /**
   * Returns the procedure name.
   * Can only consist of letters, underscores, numbers.
   * 
   * @return		the name of the procedure
   */
  @Override
  public String getProcedureName() {
    return "println";
  }

  /**
   * Returns the signature of the procedure.
   * 
   * @return		the signature
   */
  @Override
  public String getProcedureSignature() {
    return getProcedureName() + "(...)";
  }

  /**
   * Returns the help string for the procedure.
   * 
   * @return		the help string
   */
  @Override
  public String getProcedureHelp() {
    return
	getProcedureSignature() + "\n"
	+ "\tOne or more arguments are printed as comma-separated list to stdout.\n"
	+ "\tIf no argument is provided, a simple line feed is output.";
  }

  /**
   * Performs some checks on the input parameters.
   * 
   * @param params	the input parameters
   * @return		always null
   */
  @Override
  protected String check(Object[] params) {
    return null;
  }

  /**
   * Gets called from the parser.
   * 
   * @param params	the parameters obtained through the parser
   */
  @Override
  protected void doCallProcedure(Object[] params) {
    System.out.println(Utils.arrayToString(params));
  }
}
