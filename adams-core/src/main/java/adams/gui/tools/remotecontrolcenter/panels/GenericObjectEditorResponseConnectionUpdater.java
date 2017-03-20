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
 * GenericObjectEditorResponseConnectionUpdater.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditor.PostProcessObjectHandler;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.connection.DefaultConnection;
import adams.scripting.engine.DefaultScriptingEngine;

/**
 * Updates the the response connection.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenericObjectEditorResponseConnectionUpdater
  implements PostProcessObjectHandler {

  /** the application frame to get the scripting engine from. */
  protected AbstractApplicationFrame m_Application;

  /**
   * Initializes the post-processor with no application.
   */
  public GenericObjectEditorResponseConnectionUpdater() {
    this(null);
  }

  /**
   * Initializes the post-processor.
   *
   * @param application	the application to use
   */
  public GenericObjectEditorResponseConnectionUpdater(AbstractApplicationFrame application) {
    m_Application = application;
  }

  /**
   * Sets the application frame to use.
   *
   * @param value	the application
   */
  public void setApplication(AbstractApplicationFrame value) {
    m_Application = value;
  }

  /**
   * Returns the application in use.
   *
   * @return		the application, null if none set
   */
  public AbstractApplicationFrame getApplication() {
    return m_Application;
  }

  /**
   * Gets called just before the object would be set, i.e., updating the UI.
   *
   * @param goe	the generic object editor that triggered it
   * @param o		the object to be set
   * @return		the potentially updated object
   */
  @Override
  public Object postProcessObject(GenericObjectEditor goe, Object o) {
    DefaultScriptingEngine	engine;
    RemoteCommandWithResponse	cmd;
    DefaultConnection		conn;

    if (m_Application == null)
      return o;
    if (!(m_Application.getRemoteScriptingEngine() instanceof DefaultScriptingEngine))
      return o;
    if (!(o instanceof RemoteCommandWithResponse))
      return o;

    engine = (DefaultScriptingEngine) m_Application.getRemoteScriptingEngine();
    cmd    = (RemoteCommandWithResponse) o;
    conn   = new DefaultConnection();
    conn.setPort(engine.getPort());
    cmd.setResponseConnection(conn);

    return cmd;
  }
}
