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
 * CloseCallableDisplay.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.flow.core.AbstractCallableActor;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.ControlActor;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CloseCallableDisplay
  extends AbstractCallableActor
  implements ControlActor, InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = -7346814880631564292L;

  /** the input token. */
  protected Token m_InputToken;

  /** the output token. */
  protected Token m_OutputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Closes the referenced callable graphical actor whenever a token passes through.";
  }

  /**
   * Configures the callable actor.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String setUpCallableActor() {
    String	result;

    result = super.setUpCallableActor();

    if (result == null) {
      if (m_CallableActor != null) {
	if (!(m_CallableActor instanceof AbstractDisplay))
	  result = "Callable actor '" + m_CallableName + "' is not a graphical actor (" + AbstractDisplay.class.getName() + ")!";
      }
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Does nothing.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    m_InputToken  = token;
    m_OutputToken = null;
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_InputToken;
  }

  /**
   * Executes the callable actor. Derived classes might need to override this
   * method to ensure atomicity.
   *
   * @return		null if no error, otherwise error message
   */
  @Override
  protected String executeCallableActor() {
    if (!isHeadless())
      ((AbstractDisplay) m_CallableActor).closeFrame();
    return null;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String execute() {
    String	result;

    result        = super.execute();
    m_OutputToken = m_InputToken;
    
    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result        = m_OutputToken;
    m_OutputToken = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <br><br>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
  }
}
