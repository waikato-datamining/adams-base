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
 * AbstractTabResponseHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.scripting.command.flow.ListFlows;
import adams.scripting.responsehandler.AbstractResponseHandler;

/**
 * Custom handler for intercepting the responses from the {@link ListFlows}
 * remote command.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTabResponseHandler<T extends AbstractRemoteControlCenterTab>
  extends AbstractResponseHandler {

  private static final long serialVersionUID = 6205405220037007365L;

  /** the owner. */
  protected T m_Tab;

  /**
   * Initializes the handler.
   *
   * @param tab	the tab this handler belongs to
   */
  public AbstractTabResponseHandler(T tab) {
    super();
    m_Tab = tab;
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ties into " + AbstractRemoteControlCenterTab.class.getName() + " derived tabs.";
  }

  /**
   * Returns the tab this handler belongs to.
   *
   * @return		the tab, null if none set
   */
  public T getTab() {
    return m_Tab;
  }
}
