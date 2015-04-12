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
 * FlowEditor.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.UserMode;
import adams.gui.flow.FlowEditorPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens the Flow Editor.
 * You can load/run flows. If no prefix or prefixed with "load:" a file only
 * gets loaded. If prefixed with "run:" the file gets loaded and executed.
 * Multiple files can be specified.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowEditor
  extends AbstractParameterHandlingMenuItemDefinition
  implements LastWidgetLaunched<FlowEditorPanel> {

  /** for serialization. */
  private static final long serialVersionUID = 7907139922742800770L;
  
  /** the last editor that was launched. */
  protected FlowEditorPanel m_FlowEditorPanel;
  
  /**
   * Initializes the menu item with no owner.
   */
  public FlowEditor() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public FlowEditor(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_FlowEditorPanel = null;
  }
  
  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "flow.gif";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    List<String>    load;
    List<String>    run;

    // parse file list
    load = new ArrayList<>();
    run  = new ArrayList<>();
    for (String param: m_Parameters) {
      if (param.startsWith("run:"))
        run.add(param.substring(4));
      else if (param.startsWith("load:"))
        load.add(param.substring(5));
      else
        load.add(param);
    }

    // load/run flows
    m_FlowEditorPanel = new FlowEditorPanel();
    for (String param: load)
      m_FlowEditorPanel.loadUnsafe(new PlaceholderFile(param));
    for (String param: run)
      m_FlowEditorPanel.runUnsafe(new PlaceholderFile(param));

    createChildFrame(m_FlowEditorPanel, 1000, 700);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Flow editor";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_TOOLS;
  }

  /**
   * Returns the last widget that was launched.
   * 
   * @return		the widget, null if none launched
   */
  @Override
  public FlowEditorPanel getLastWidget() {
    return m_FlowEditorPanel;
  }
}