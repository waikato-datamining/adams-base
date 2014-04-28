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
 * TriggerOnce.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.util.Hashtable;

import adams.flow.core.Token;

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
public class TriggerOnce
  extends Trigger {

  /** for serialization. */
  private static final long serialVersionUID = 2591889670602718340L;

  /** the key for storing whether the actor already got executed. */
  public final static String BACKUP_EXECUTEDONCE = "executed once";

  /** whether the actor was executed once. */
  protected boolean m_ExecutedOnce;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Triggers the sub-flow only once.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ExecutedOnce = false;
  }

  /**
   * Returns the class that is the corresponding conditional equivalent.
   * 
   * @return		always null
   */
  @Override
  public Class getConditionalEquivalent() {
    return null;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_EXECUTEDONCE);
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

    result.put(BACKUP_EXECUTEDONCE, m_ExecutedOnce);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
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
  @Override
  protected boolean canProcessInput(Token token) {
    return !m_ExecutedOnce;
  }

  /**
   * Processes the token.
   *
   * @param token	the token to process
   * @return		an optional error message, null if everything OK
   */
  @Override
  protected String processInput(Token token) {
    String	result;

    result = super.processInput(token);

    m_ExecutedOnce = true;

    return result;
  }
}
