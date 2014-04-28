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
 * ProgrammaticSinkTokenEvent.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.event;

import java.util.EventObject;

import adams.flow.core.Token;
import adams.flow.core.TokenEventHandler;

/**
 * Event that gets sent by a {@link TokenEventHandler} actor when a token
 * arrived.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TokenEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -4038178765519090138L;
  
  /** the token. */
  protected Token m_Token;
  
  /**
   * Initializes the event.
   * 
   * @param source	the actor that triggered the event
   * @param token	the token
   */
  public TokenEvent(TokenEventHandler source, Token token) {
    super(source);
    
    m_Token = token;
  }
  
  /**
   * Returns the {@link TokenEventHandler} actor that triggered the event.
   * 
   * @return		the actor
   */
  public TokenEventHandler getHandler() {
    return (TokenEventHandler) getSource();
  }
  
  /**
   * Returns the token.
   * 
   * @return		the token
   */
  public Token getToken() {
    return m_Token;
  }
}
