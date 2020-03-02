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
 * RegisteredDisplaysHandler.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tabhandler;

import adams.flow.core.AbstractDisplay;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tab.RegisteredDisplaysTab;

import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.Map;

/**
 * For managing registered displays.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RegisteredDisplaysHandler
  extends AbstractTabHandler {

  private static final long serialVersionUID = -6801903616813756519L;

  /** the registered panels: class of panel - (name of panel - AbstractDisplay instance). */
  protected Map<Class,HashMap<String,AbstractDisplay>> m_Displays;

  /**
   * Initializes the tab handler
   *
   * @param owner	the owning panel
   */
  public RegisteredDisplaysHandler(FlowPanel owner) {
    super(owner);
  }

  @Override
  protected void initialize() {
    super.initialize();
    m_Displays = new HashMap<>();
  }

  /**
   * Notifies the {@link RegisteredDisplaysTab} instance of a change.
   *
   * @param show	whether to show the tab or leave as is
   */
  protected void update(boolean show) {
    Runnable	run;

    run = () -> {
      if (!getEditor().getTabs().isVisible(RegisteredDisplaysTab.class) && show)
	getEditor().getTabs().setVisible(RegisteredDisplaysTab.class, true, false);
      RegisteredDisplaysTab registered = (RegisteredDisplaysTab) getEditor().getTabs().getTab(RegisteredDisplaysTab.class);
      if (registered != null)
	registered.update();
    };
    SwingUtilities.invokeLater(run);

    // close displays?
    run = () -> {
      if (!hasDisplays())
	getEditor().getTabs().setVisible(RegisteredDisplaysTab.class, false, false);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Registers a display.
   *
   * @param cls		the class to register the display for
   * @param name	the name of the display
   * @param panel	the AbstractDisplay instance
   * @return		the previously registered display, if any
   */
  public AbstractDisplay register(Class cls, String name, AbstractDisplay panel) {
    AbstractDisplay			result;
    HashMap<String,AbstractDisplay>	panels;

    if (!m_Displays.containsKey(cls))
      m_Displays.put(cls, new HashMap<>());

    panels = m_Displays.get(cls);
    result = panels.put(name, panel);

    // notify panel
    update(true);

    return result;
  }

  /**
   * Deregisters a display.
   *
   * @param cls		the class to register the display for
   * @param name	the name of the display
   * @return		the deregistered display, if any
   */
  public AbstractDisplay deregister(Class cls, String name) {
    AbstractDisplay	result;

    if (m_Displays.containsKey(cls))
      result = m_Displays.get(cls).remove(name);
    else
      result = null;

    // notify panel
    if (result != null)
      update(false);

    return result;
  }

  /**
   * Removes all registered displays.
   */
  public void clear() {
    RegisteredDisplaysTab registered;

    registered = (RegisteredDisplaysTab) getEditor().getTabs().getTab(RegisteredDisplaysTab.class);
    if (registered != null) {
      if (registered.getTabbedDisplays() != null)
        registered.getTabbedDisplays().removeAll();
    }

    m_Displays.clear();
    // notify panel
    update(false);
  }

  /**
   * Returns all currently registered displays.
   *
   * @return		the displays
   */
  public Map<Class,HashMap<String,AbstractDisplay>> getDisplays() {
    return m_Displays;
  }

  /**
   * Returns whether there are any registered displays open.
   *
   * @return		true if at least one open
   */
  public boolean hasDisplays() {
    int		count;

    count = 0;
    for (Class cls: m_Displays.keySet())
      count += m_Displays.get(cls).size();

    return (count > 0);
  }

  /**
   * Gets called when the page changes.
   */
  public void display() {
    getEditor().getTabs().setVisible(
      RegisteredDisplaysTab.class,
      hasDisplays(),
      false);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    clear();
  }
}
