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
 * NotesErrors.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.data.NotesHandler;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Evaluates to true if the notes handler passing through has any errors recorded.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NotesErrors
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -7927342245398106669L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates to true if the notes handler passing through has any errors recorded.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		always null
   */
  @Override
  public String getQuickInfo() {
    return "error in notes?";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{NotesHandler.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    NotesHandler	handler;
    
    handler = (NotesHandler) token.getPayload();
    
    return handler.getNotes().hasError();
  }
}
