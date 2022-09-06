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
 * GoTo.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActorReference;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorUser;
import adams.gui.action.AbstractPropertiesAction;
import adams.gui.core.ImageManager;
import adams.gui.flow.tree.Node;
import adams.gui.goe.FlowHelper;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Jumps to the callable actor reference by this actor.
 * 
 * @author fracpete
 */
public class GoTo
  extends AbstractTreePopupSubMenuAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;
  
  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Go to";
  }

  /**
   * Returns all the actor references for this actor.
   *
   * @param actor	the actor to inspect
   * @return		the references
   */
  protected AbstractActorReference[] getReferences(Actor actor) {
    List<AbstractActorReference> 	result;

    result = new ArrayList<>();
    actor.getOptionManager().traverse(new OptionTraverser() {
      @Override
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
      }
      @Override
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
      }
      @Override
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
        if (ClassLocator.matches(AbstractActorReference.class, option.getBaseClass())) {
          Object current = option.getCurrentValue();
          if (option.isMultiple()) {
            for (int i = 0; i < Array.getLength(current); i++)
	      result.add((AbstractActorReference) Array.get(current, i));
	  }
	  else {
            result.add((AbstractActorReference) current);
	  }
	}
      }
      @Override
      public boolean canHandle(AbstractOption option) {
	return (option instanceof AbstractArgumentOption);
      }
      @Override
      public boolean canRecurse(Class cls) {
	return true;
      }
      @Override
      public boolean canRecurse(Object obj) {
	return true;
      }
    });

    return result.toArray(new AbstractActorReference[result.size()]);
  }

  /**
   * Creates a new menu.
   */
  @Override
  public JMenu createMenu() {
    JMenu 			result;
    JMenuItem			item;
    Actor 			actor;
    AbstractActorReference[]	refs;
    List<Node>			nodes;
    String[]			paths;
    int				i;
    int				n;

    if (m_State.selNode == null)
      return null;

    result = new JMenu(getName());
    if (getIcon() != null)
      result.setIcon(getIcon());
    else
      result.setIcon(ImageManager.getEmptyIcon());
    actor = m_State.selNode.getActor();
    refs  = getReferences(actor);
    paths = new String[refs.length];
    if (refs.length > 0) {
      nodes = FlowHelper.findCallableActorsHandler(m_State.selNode);
      for (i = 0; i < refs.length; i++) {
	for (n = 0; n < nodes.size(); n++) {
	  int index = nodes.get(i).indexOf(refs[i].getValue());
	  if (index > -1) {
	    paths[i] = ((Node) nodes.get(i).getChildAt(index)).getFullName();
	    break;
	  }
	}
      }
    }
    for (i = 0; i < refs.length; i++) {
      if (paths[i] == null)
        continue;
      final String path = paths[i];
      item = new JMenuItem(refs[i].getValue());
      item.addActionListener((ActionEvent e) -> m_State.tree.locateAndDisplay(path, true));
      result.add(item);
    }

    result.setEnabled(result.getItemCount() > 0);

    return result;
  }

  /**
   * Ignored.
   *
   * @return		always null
   */
  @Override
  protected AbstractPropertiesAction[] getSubMenuActions() {
    return null;
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    boolean	enabled;
    Actor 	actor;

    enabled = false;
    if (m_State.isSingleSel) {
      actor   = m_State.selNode.getActor();
      enabled = actor instanceof CallableActorUser;
    }

    setEnabled(enabled);
  }
}
