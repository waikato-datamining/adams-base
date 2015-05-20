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
 * UniqueID.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.logging.Level;

import adams.core.logging.LoggingHelper;
import adams.data.id.MutableIDHandler;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Ensures that all passing through tokens that implement adams.data.id.MutableIDHandler have a unique ID.<br>
 * All other tokens are just forwarded as is.
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
 * &nbsp;&nbsp;&nbsp;default: UniqueID
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UniqueID
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5763179484838892202L;

  /** for backing up the IDs. */
  public final static String BACKUP_IDS = "ids";

  /** the separator between original ID and suffix to make it unique. */
  public final static String SEPARATOR = "#";

  /** for storing the IDs seen so far. */
  protected HashSet<String> m_IDs;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Ensures that all passing through tokens that implement "
      + MutableIDHandler.class.getName() + " have a unique ID.\n"
      + "All other tokens are just forwarded as is.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_IDs = new HashSet<String>();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_IDs.clear();
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_IDS);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_IDS, m_IDs);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_IDS)) {
      m_IDs = (HashSet<String>) state.get(BACKUP_IDS);
      state.remove(BACKUP_IDS);
    }

    super.restoreState(state);
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
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Creates a unique ID.
   *
   * @param id		the initial ID
   * @return		the unique ID
   */
  protected String generateUniqueID(String id) {
    String	result;
    int		count;

    result = id;
    count  = 1;
    while (m_IDs.contains(result)) {
      count++;
      result = id + SEPARATOR + count;
    }
    m_IDs.add(result);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    MutableIDHandler	handler;
    String		id;

    result = null;

    if (m_InputToken.getPayload() instanceof MutableIDHandler) {
      handler = (MutableIDHandler) m_InputToken.getPayload();
      id      = generateUniqueID(handler.getID());
      if (!handler.getID().equals(id)) {
	if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	  getLogger().info(handler.getID() + " -> " + id + " for: " + handler);
	else if (LoggingHelper.isAtLeast(getLogger(), Level.INFO))
	  getLogger().info(handler.getID() + " -> " + id);
	handler.setID(id);
      }
      m_OutputToken = new Token(handler);
    }
    else {
      m_OutputToken = m_InputToken;
    }

    return result;
  }
}
