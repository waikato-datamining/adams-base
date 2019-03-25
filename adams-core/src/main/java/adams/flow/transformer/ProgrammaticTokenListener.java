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
 * ProgrammaticTokenListener.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Utils;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.source.ForLoop;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Transformer that allows other, non-flow code to 'listen on the wire' of the data being processed.<br>
 * Typically used for flows that are executed as background jobs in the user interface.<br>
 * Listeners get removed once the flow finishes (wrapUp method).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ExternalTokenListener
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ProgrammaticTokenListener
  extends AbstractTransformer {

  private static final long serialVersionUID = 1905421631753924540L;

  /**
   * Interface for listeners that need to "listen on the wire" of data being processed.
   */
  public interface TokenListener {

    /**
     * For listening to the token that was received.
     *
     * @param token	the token
     */
    public void tokenReceived(Token token);
  }

  /** the token listeners. */
  protected Set<TokenListener> m_TokenListeners;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Transformer that allows other, non-flow code to 'listen on the wire' of the data being processed.\n"
      + "Typically used for flows that are executed as background jobs in the user interface.\n"
      + "Listeners can be added/removed via the addTokenListener/removeTokenListener methods and "
      + "must implement the " + Utils.classToString(TokenListener.class) + " interface.\n"
      + "Listeners get automatically removed once the flow finishes (wrapUp method).";
  }

  /**
   * Initializes the scheme.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TokenListeners = new HashSet<>();
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
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Adds the token listener to its internal list.
   *
   * @param l		the listener
   */
  public void addTokenListener(TokenListener l) {
    m_TokenListeners.add(l);
  }

  /**
   * Removes the token listener from its internal list.
   *
   * @param l		the listener
   */
  public void removeTokenListener(TokenListener l) {
    m_TokenListeners.remove(l);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (isLoggingEnabled())
      getLogger().info("Notifying #" + m_TokenListeners.size() + " token listeners");

    for (TokenListener l: m_TokenListeners)
      l.tokenReceived(m_InputToken);

    if (isLoggingEnabled())
      getLogger().info("Notified #" + m_TokenListeners.size() + " token listeners");

    m_OutputToken = m_InputToken;

    return null;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_TokenListeners != null) {
      m_TokenListeners.clear();
      m_TokenListeners = null;
    }
    super.wrapUp();
  }

  /**
   * Shows how to use the actor.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    String	msg;

    Environment.setEnvironmentClass(Environment.class);

    Flow flow = new Flow();

    ForLoop loop = new ForLoop();
    flow.add(loop);

    ProgrammaticTokenListener listener = new ProgrammaticTokenListener();
    listener.addTokenListener((Token token) -> System.out.println("[WIRE] " + token.getPayload()));
    flow.add(listener);

    msg = flow.setUp();
    if (msg != null)
      throw new IllegalStateException(msg);
    flow.execute();
    flow.wrapUp();
    flow.cleanUp();
  }
}
