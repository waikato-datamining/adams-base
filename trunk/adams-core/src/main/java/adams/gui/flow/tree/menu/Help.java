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
 * Help.java
 * Copyright (C) 2014 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import adams.core.option.HtmlHelpProducer;
import adams.flow.core.AbstractActor;
import adams.gui.dialog.HelpDialog;
import adams.gui.flow.tree.TreeHelper;

/**
 * For showing the help dialog for an actor.
 * 
 * @author fracpete
 * @version $Revision$
 */
public class Help
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Help...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.isSingleSel);
  }

  /**
   * Displays the help for the selected actor.
   *
   * @param path	the path to the actor
   */
  protected void help(TreePath path) {
    HelpDialog		dialog;
    HtmlHelpProducer 	producer;
    AbstractActor	actor;

    actor = TreeHelper.pathToActor(path);
    if (getParentDialog() != null)
      dialog = new HelpDialog(getParentDialog());
    else
      dialog = new HelpDialog(getParentFrame());
    producer = new HtmlHelpProducer();
    producer.produce(actor);
    dialog.setHelp(producer.getOutput(), true);
    dialog.setTitle("Help on " + actor.getClass().getName());
    dialog.setLocation(
	m_State.tree.getTopLevelAncestor().getLocationOnScreen().x + m_State.tree.getTopLevelAncestor().getSize().width,
	m_State.tree.getTopLevelAncestor().getLocationOnScreen().y);
    dialog.setSize(800, 600);
    dialog.setVisible(true);
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    help(m_State.selPath);
  }
}
