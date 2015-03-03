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
 * ProgrammaticSink.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.util.HashSet;

import adams.env.Environment;
import adams.event.TokenEvent;
import adams.event.TokenListener;
import adams.flow.control.Flow;
import adams.flow.core.Token;
import adams.flow.core.TokenEventHandler;
import adams.flow.core.Unknown;
import adams.flow.source.ForLoop;

/**
 * For programmatically hooking into a flow and receive tokens.
 * This actor needs to get added in the flow and then {@link TokenListener}s
 * added.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProgrammaticSink
  extends AbstractSink
  implements TokenEventHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5521515998670431523L;
  
  /** the listeners to use. */
  protected HashSet<TokenListener> m_TokenListeners;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For programmatically hooking into a flow and receive tokens.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_TokenListeners = new HashSet<TokenListener>();
  }
  
  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    TokenEvent	e;
    
    e = new TokenEvent(this, m_InputToken);
    notifyTokenListeners(e);
    
    return null;
  }

  /**
   * Adds the specified listener.
   * 
   * @param l		the listener to add
   */
  public void addTokenListener(TokenListener l) {
    m_TokenListeners.add(l);
  }

  /**
   * Removes the specified listener.
   * 
   * @param l		the listener to remove
   */
  public void removeTokenListener(TokenListener l) {
    m_TokenListeners.remove(l);
  }

  /**
   * Returns the current listeners.
   * 
   * @param l		the listeners
   */
  public TokenListener[] tokenListeners() {
    synchronized(m_TokenListeners) {
      return m_TokenListeners.toArray(new TokenListener[m_TokenListeners.size()]);
    }
    
  }
  
  /**
   * Notifies all the {@link TokenListener}s with the event.
   * 
   * @param e		the event to send
   */
  public void notifyTokenListeners(TokenEvent e) {
    TokenListener[]	listeners;
    
    listeners = tokenListeners();
    for (TokenListener l: listeners)
      l.processToken(e);
  }
  
  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    
    m_TokenListeners.clear();
  }
  
  /**
   * Shows how to use this actor.
   * 
   * @param args	command-line arguments - ignored
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    Flow flow = new Flow();
    ForLoop forloop = new ForLoop();
    flow.add(forloop);
    ProgrammaticSink psink = new ProgrammaticSink();
    psink.addTokenListener(new TokenListener() {
      @Override
      public void processToken(TokenEvent e) {
	Token token = e.getToken();
	TokenEventHandler handler = e.getHandler();
	System.out.println("handler=" + handler.getFullName() + ", token=" + token.getPayload());
      }
    });
    flow.add(psink);
    String result = flow.setUp();
    if (result != null) {
      System.err.println("Failed to set up flow: " + result);
      return;
    }
    result = flow.execute();
    if (result != null) {
      System.err.println("Failed to execute flow: " + result);
      flow.wrapUp();
      flow.cleanUp();
      return;
    }
    flow.wrapUp();
    flow.cleanUp();
  }
}
