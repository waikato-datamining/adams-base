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
 * ChangeFilterUpdates.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.data.NotesHandler;
import adams.data.filter.AbstractFilter;
import adams.data.id.IDHandler;
import adams.flow.core.Actor;

/**
 * Changes whether filters update their ID and/or processing info.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeFilterUpdates
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;

  /** whether to suppress updating of ID. */
  protected boolean m_DontUpdateID;

  /** whether to suppress updating of processing information. */
  protected boolean m_DontUpdateProcessingInfo;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Changes whether filters update their ID and/or processing info.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "no-id-update", "dontUpdateID",
      false);

    m_OptionManager.add(
      "no-processing-info-update", "dontUpdateProcessingInfo",
      false);
  }

  /**
   * Sets whether ID update is suppressed.
   *
   * @param value 	true if to suppress
   */
  public void setDontUpdateID(boolean value) {
    m_DontUpdateID = value;
    reset();
  }

  /**
   * Returns whether ID update is suppressed.
   *
   * @return 		true if suppressed
   */
  public boolean getDontUpdateID() {
    return m_DontUpdateID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dontUpdateIDTipText() {
    return "If enabled, suppresses updating the ID of " + IDHandler.class.getName() + " data containers.";
  }

  /**
   * Sets whether processing information update is suppressed.
   *
   * @param value 	true if to suppress
   */
  public void setDontUpdateProcessingInfo(boolean value) {
    m_DontUpdateProcessingInfo = value;
    reset();
  }

  /**
   * Returns whether processing information update is suppressed.
   *
   * @return 		true if suppressed
   */
  public boolean getDontUpdateProcessingInfo() {
    return m_DontUpdateProcessingInfo;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dontUpdateProcessingInfoTipText() {
    return "If enabled, suppresses updating the processing information of " + NotesHandler.class.getName() + " data containers.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(Actor actor) {
    m_Modified = AbstractFilter.changeUpdateHandling(actor, m_DontUpdateID, m_DontUpdateProcessingInfo);
  }
}
