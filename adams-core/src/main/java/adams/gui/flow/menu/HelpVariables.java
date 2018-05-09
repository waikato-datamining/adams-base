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
 * HelpVariables.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.Utils;
import adams.flow.control.UpdateProperties;
import adams.flow.core.ActorUtils;
import adams.flow.transformer.GetProperty;
import adams.flow.transformer.GetPropertyValue;
import adams.flow.transformer.SetProperty;
import adams.flow.transformer.SetPropertyValue;
import adams.flow.transformer.UpdateProperty;
import adams.gui.help.HelpFrame;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Shows help on variables.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HelpVariables
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Variables";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    StringBuilder	help;
    Class[]		properties;

    help = new StringBuilder();

    // general
    help.append("General\n");
    help.append("=======\n");
    help.append("\n");
    help.append("Variables can be attached to any option of an ADAMS object.\n");
    help.append("For non-ADAMS ones, you can use 'property paths' to retrieve and update properties.\n");
    help.append("The following (incomplete) list of actors can be used for managing properties:\n");
    properties = new Class[]{
      UpdateProperties.class,
      UpdateProperty.class,
      SetProperty.class,
      GetProperty.class,
      GetPropertyValue.class,
      SetPropertyValue.class,
    };
    Arrays.sort(properties, new Comparator<Class>() {
      @Override
      public int compare(Class o1, Class o2) {
	return o1.getName().compareTo(o2.getName());
      }
    });
    for (Class property: properties)
      help.append("- ").append(Utils.classToString(property)).append("\n");

    // programmatic variables
    help.append("\n");
    help.append("Programmatic variables\n");
    help.append("======================\n");
    help.append("\n");
    help.append("The following variables are reserved and get filled in at runtime:\n");
    for (String v: ActorUtils.PROGRAMMATIC_VARIABLES) {
      help.append("- ").append(v).append("\n");
      help.append("  ").append(ActorUtils.PROGRAMMATIC_VARIABLES_HELP.get(v)).append("\n");
    }

    HelpFrame.showHelp(
      "Flow editor - Variables",
      help.toString(),
      false);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(true);
  }
}
