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
 * Once.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.util.Hashtable;

import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Tees off a token only once to the tee actor.
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
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Once
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-progress (property: showProgress)
 * &nbsp;&nbsp;&nbsp;If set to true, progress information will be output to stdout ('.').
 * </pre>
 *
 * <pre>-tee &lt;adams.flow.core.AbstractActor [options]&gt; (property: teeActor)
 * &nbsp;&nbsp;&nbsp;The actor to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Null
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Once
  extends Tee {

  /** for serialization. */
  private static final long serialVersionUID = 6101027874139099046L;

  /** the key for storing whether the actor already got executed. */
  public final static String BACKUP_EXECUTEDONCE = "executed once";

  /** whether the actor was executed once. */
  protected boolean m_ExecutedOnce;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Tees off a token only once to the tee actor.";
  }

  /**
   * Resets the scheme.
   */
  protected void reset() {
    super.reset();

    m_ExecutedOnce = false;
  }

  /**
   * Removes entries from the backup.
   */
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_EXECUTEDONCE);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_EXECUTEDONCE, m_ExecutedOnce);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_EXECUTEDONCE)) {
      m_ExecutedOnce = (Boolean) state.get(BACKUP_EXECUTEDONCE);
      state.remove(BACKUP_EXECUTEDONCE);
    }

    super.restoreState(state);
  }

  /**
   * Returns whether the token can be processed in the tee actor.
   *
   * @param token	the token to process
   * @return		true if token can be processed
   */
  protected boolean canProcessInput(Token token) {
    return !m_ExecutedOnce;
  }

  /**
   * Processes the token.
   *
   * @param token	the token to process
   * @return		an optional error message, null if everything OK
   */
  protected String processInput(Token token) {
    String	result;

    result = super.processInput(token);

    m_ExecutedOnce = true;

    return result;
  }
}
