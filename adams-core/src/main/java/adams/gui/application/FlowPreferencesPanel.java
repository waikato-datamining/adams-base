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
 * FlowPreferencesPanel.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.io.FileUtils;
import adams.env.Environment;
import adams.env.FlowEditorPanelDefinition;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.ToolBarPanel.ToolBarLocation;
import adams.gui.goe.GenericArrayEditorPanel;

/**
 * Preferences for the flow editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowPreferencesPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = 3895159356677639564L;

  @Override
  protected void initGUI() {
    super.initGUI();

    addPropertyType("InitialDir", PropertyType.DIRECTORY);
    addPropertyType("MaxRecentFlows", PropertyType.INTEGER);
    addPropertyType("CheckOnSave", PropertyType.BOOLEAN);
    addPropertyType("LargeFlowMinActors", PropertyType.INTEGER);
    addPropertyType("ShowQuickInfo", PropertyType.BOOLEAN);
    addPropertyType("ShowAnnotations", PropertyType.BOOLEAN);
    addPropertyType("ShowInputOutput", PropertyType.BOOLEAN);
    addPropertyType("DividerLocation", PropertyType.INTEGER);
    addPropertyType("FlowList.DividerLocation", PropertyType.INTEGER);
    addPropertyType("GarbageCollectAfterFinish", PropertyType.BOOLEAN);
    addPropertyType("NewList", PropertyType.ARRAY_EDITOR);
    setChooser("NewList", new GenericArrayEditorPanel(new Actor[]{new Flow()}));
    setArrayClass("NewList", Actor.class);
    setArraySeparator("NewList", ",");
    addPropertyType("Tree.ActorName.Size", PropertyType.INTEGER);
    addPropertyType("Tree.ActorName.Color", PropertyType.COLOR);
    addPropertyType("Tree.QuickInfo.Size", PropertyType.INTEGER);
    addPropertyType("Tree.QuickInfo.Color", PropertyType.COLOR);
    addPropertyType("Tree.Annotations.Size", PropertyType.INTEGER);
    addPropertyType("Tree.Annotations.Color", PropertyType.COLOR);
    addPropertyType("Tree.Annotations.MarkDown", PropertyType.BOOLEAN);
    addPropertyType("Tree.InputOutput.Size", PropertyType.INTEGER);
    addPropertyType("Tree.InputOutput.Color", PropertyType.COLOR);
    addPropertyType("Tree.Placeholders.Size", PropertyType.INTEGER);
    addPropertyType("Tree.Placeholders.Color", PropertyType.COLOR);
    addPropertyType("Tree.VariableHighlight.Background", PropertyType.COLOR);
    addPropertyType("Tree.BookmarkHighlight.Background", PropertyType.COLOR);
    addPropertyType("Tree.StateUsesNested", PropertyType.BOOLEAN);
    addPropertyType("Tree.IgnoreNameChanges", PropertyType.BOOLEAN);
    addPropertyType("Tree.ScaleFactor", PropertyType.DOUBLE);
    addPropertyType("Tree.RecordActor", PropertyType.BOOLEAN);
    addPropertyType("Tree.RecordAdd", PropertyType.BOOLEAN);
    addPropertyType("Tree.RecordEnclose", PropertyType.BOOLEAN);
    addPropertyType("ClassTree.ShowGlobalInfo", PropertyType.BOOLEAN);
    addPropertyType("StatusBar.Width", PropertyType.INTEGER);
    addPropertyType("StatusBar.Height", PropertyType.INTEGER);
    addPropertyType("ToolBar.Location", PropertyType.ENUM);
    addPropertyType("Toolbar.Actions", PropertyType.COMMA_SEPARATED_LIST);
    setEnum("ToolBar.Location", ToolBarLocation.class);
    addPropertyType("Tree.PopupMenu", PropertyType.COMMA_SEPARATED_LIST);
    addPropertyType("Tree.PopupMenuExtensions", PropertyType.COMMA_SEPARATED_LIST);
    addPropertyType("Tree.KeyboardActions", PropertyType.BLANK_SEPARATED_LIST);
    addPropertyType("Tree.InputOutput.Prefixes", PropertyType.COMMA_SEPARATED_LIST);
    addPropertyType("FlowPanelClass", PropertyType.STRING);
    setPreferences(Environment.getInstance().read(FlowEditorPanelDefinition.KEY));
  }
  
  /**
   * The title of the preferences.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Flow";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return false;
  }

  /**
   * Activates the settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    if (Environment.getInstance().write(FlowEditorPanelDefinition.KEY, getPreferences()))
      return null;
    else
      return "Failed to save flow setup!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(FlowEditorPanelDefinition.KEY);
    return (props != null) && FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(FlowEditorPanelDefinition.KEY);
    if ((props != null) && FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
	return "Failed to remove custom flow properties: " + props;
    }

    return null;
  }
}
