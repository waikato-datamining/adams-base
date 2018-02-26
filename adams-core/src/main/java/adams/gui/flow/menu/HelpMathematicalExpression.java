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
 * HelpMathematicalExpression.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.gui.help.HelpFrame;
import adams.parser.MathematicalExpression;

import java.awt.event.ActionEvent;

/**
 * Shows help on mathematical expressions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HelpMathematicalExpression
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
    return "Mathematical expressions";
  }
  
  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    StringBuilder	help;

    help = new StringBuilder();

    // general
    help.append("Mathematical expressions\n");
    help.append("========================\n");
    help.append("\n");
    help.append("Mathematical expressions, as used by the 'MathExpression' \n");
    help.append("or the 'SetVariable' actors, use the following grammar:\n");
    help.append("\n");
    help.append(new MathematicalExpression().getGrammar());

    HelpFrame.showHelp(
      "Flow editor - Mathematical expressions",
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
