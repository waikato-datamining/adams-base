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
 * MakeConditional.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.ActorUtils;
import adams.flow.core.ActorWithConditionalEquivalent;

import java.awt.event.ActionEvent;

/**
 * For turning an actor into its conditonal equivalent.
 *
 * @author fracpete
 */
public class MakeConditional
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
    return "Make conditional...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    boolean	enabled;

    enabled = m_State.editable
	&& m_State.isSingleSel
	&& (m_State.tree.getOwner() != null);

    if (enabled) {
      enabled = (m_State.selNode.getActor() instanceof ActorWithConditionalEquivalent)
          || ActorUtils.isSource(m_State.selNode.getActor())
          || ActorUtils.isTransformer(m_State.selNode.getActor())
          || ActorUtils.isSink(m_State.selNode.getActor());
    }

    setEnabled(enabled);
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    m_State.tree.getOperations().makeConditional(m_State.selPath);
  }
}
