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
 * AbstractParserProcedure.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser.plugin;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.logging.LoggingObject;

/**
 * Ancestor for custom procedures to be used in parsers.
 * Procedures must be stateless, as only a single instance is kept in memory.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractParserProcedure
  extends LoggingObject
  implements ParserProcedure {

  /** for serialization. */
  private static final long serialVersionUID = 1027603593417267206L;

  /**
   * Returns the procedure name.
   * Can only consist of letters, underscores, numbers.
   * 
   * @return		the name of the procedure
   */
  public abstract String getProcedureName();

  /**
   * Returns the signature of the procedure.
   * 
   * @return		the signature
   */
  public abstract String getProcedureSignature();

  /**
   * Returns the help string for the procedure.
   * 
   * @return		the help string
   */
  public abstract String getProcedureHelp();

  /**
   * Performs some checks on the input parameters.
   * 
   * @param params	the input parameters
   * @return		null if OK, otherwise error message
   */
  protected abstract String check(Object[] params);
  
  /**
   * Gets called from the parser.
   * 
   * @param params	the parameters obtained through the parser
   */
  protected abstract void doCallProcedure(Object[] params);

  /**
   * Gets called from the parser.
   * 
   * @param params	the parameters obtained through the parser
   * @return		the result to be used further in the parser
   */
  public void callProcedure(Object[] params) {
    String	msg;
    
    msg = check(params);
    if (msg != null)
      throw new IllegalArgumentException(
	  "Invalid parameters (" + Utils.arrayToString(params) + "): " 
	      + msg + "\n"
	      + "Usage:\n" 
	      + getProcedureHelp());
    
    doCallProcedure(params);
  }

  /**
   * Returns a list with classnames of procedures.
   *
   * @return		the filter classnames
   */
  public static String[] getProcedures() {
    return ClassLister.getSingleton().getClassnames(ParserProcedure.class);
  }
}
