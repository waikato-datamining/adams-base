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
 * AbstractBaseExternalActor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.io.FlowFile;
import adams.core.io.PlaceholderFile;
import adams.flow.control.FlowStructureModifier;

/**
 * Ancestor of actors that load another actor from disk and execute it.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBaseExternalActor
  extends AbstractActor
  implements ExternalActorFileHandler, FlowStructureModifier {

  /** for serialization. */
  private static final long serialVersionUID = 1024129351334661368L;

  /** the file the external actor is stored in. */
  protected FlowFile m_ActorFile;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "file", "actorFile",
	    new FlowFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "actorFile", m_ActorFile, "file: ");
  }

  /**
   * Sets the file containing the external actor.
   *
   * @param value	the actor file
   */
  public void setActorFile(FlowFile value) {
    m_ActorFile = value;
    reset();
  }

  /**
   * Returns the file containing the external actor.
   *
   * @return		the actor file
   */
  public FlowFile getActorFile() {
    return m_ActorFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorFileTipText() {
    return
      "The file containing the external actor; programmatic variables "
	+ "like '" + ActorUtils.FLOW_DIR + "' can be used as part of the file "
	+ "name as they get expanded before attempting to load the file.";
  }

  /**
   * Expands the filename, applying any variables if necessary.
   *
   * @return		the expanded file
   */
  protected PlaceholderFile getActualActorFile() {
    PlaceholderFile result;

    result = m_ActorFile;

    // programmatic variable maybe?
    if (result.toString().contains(Variables.START))
      result = new PlaceholderFile(getVariables().expand(result.toString()));

    return result;
  }

  /**
   * Returns whether the actor is modifying the structure.
   *
   * @return		true if the actor is modifying the structure
   */
  public boolean isModifyingStructure() {
    return !getSkip();
  }
}
