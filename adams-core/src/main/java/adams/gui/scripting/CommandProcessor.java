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
 * CommandProcessor.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import adams.db.AbstractDatabaseConnection;


/**
 <!-- globalinfo-start -->
 * General actions:<br>
 * <br>
 * connect &lt;driver&gt; &lt;URL&gt; &lt;user&gt; [password]<br>
 * &nbsp;&nbsp;&nbsp;Connects to the database.<br>
 * <br>
 * disconnect<br>
 * &nbsp;&nbsp;&nbsp;Disconnects from the database.<br>
 * <br>
 * run-tool &lt;tool + options&gt;<br>
 * &nbsp;&nbsp;&nbsp;Runs the specified tool.<br>
 * <br>
 * <br>
 * Actions for adams.gui.core.UndoHandler:<br>
 * <br>
 * disable-undo<br>
 * &nbsp;&nbsp;&nbsp;Disables the undo support, if available.<br>
 * <br>
 * enable-undo<br>
 * &nbsp;&nbsp;&nbsp;Enables the undo support, if available.<br>
 * <br>
 * <br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CommandProcessor
  extends AbstractCommandProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3273834981579030456L;

  /**
   * Initializes the processor. Still needs to set the owner.
   *
   * @see	#setOwner(AbstractScriptingEngine)
   */
  public CommandProcessor() {
    super();
  }

  /**
   * Initializes the processor.
   *
   * @param owner	the owning scripting engine
   */
  public CommandProcessor(AbstractScriptingEngine owner) {
    super(owner);
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return getOwner().getDatabaseConnection();
  }

  /**
   * Returns the object that is to be used for the undo point.
   *
   * @return		always null
   */
  protected Object getUndoObject() {
    return null;
  }
}
