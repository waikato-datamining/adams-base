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
 * AbstractFromTemplateAction.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import adams.flow.core.AbstractActor;
import adams.flow.template.AbstractActorTemplate;
import adams.gui.core.GUIHelper;
import adams.gui.flow.tree.ActorTemplateSuggestion;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeHelper;
import adams.gui.flow.tree.TreeOperations;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.tree.TreePath;

/**
 * Ancestor for template actions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFromTemplateAction
  extends AbstractTreePopupMenuItemAction {

  /** blah. */
  private static final long serialVersionUID = -658661047850659634L;

  /**
   * Tries to figure what actor templates fit best in the tree at the given position.
   *
   * @param path	the path where to insert the actor templates
   * @param position	how the actor templates are to be inserted
   * @return		the actor templates
   */
  protected AbstractActorTemplate[] suggestActorTemplates(TreePath path, TreeOperations.InsertPosition position) {
    AbstractActorTemplate[]	result;
    AbstractActor		parent;
    Node			parentNode;
    Node			node;
    int				pos;
    AbstractActor[]		actors;
    int				i;
    AbstractActorTemplate[]	suggestions;

    result = null;

    if (result == null) {
      if (position == TreeOperations.InsertPosition.BENEATH) {
	parentNode = TreeHelper.pathToNode(path);
	pos        = parentNode.getChildCount();
      }
      else {
	node       = TreeHelper.pathToNode(path);
	parentNode = (Node) node.getParent();
	pos        = parentNode.getIndex(node);
	if (position == TreeOperations.InsertPosition.AFTER)
	  pos++;
      }

      parent  = parentNode.getActor();
      actors  = new AbstractActor[parentNode.getChildCount()];
      for (i = 0; i < actors.length; i++)
	actors[i] = ((Node) parentNode.getChildAt(i)).getActor();

      suggestions = ActorTemplateSuggestion.getSingleton().suggest(parent, pos, actors);
      if (suggestions.length > 0)
	result = suggestions;
    }

    // default is "Filter"
    if (result == null)
      result = ActorTemplateSuggestion.getSingleton().getDefaults();

    return result;
  }

  /**
   * Brings up the GOE dialog for adding a template.
   *
   * @param path	the path to the actor to add the new template sibling
   * @param template	the template to use as default in dialog, use null to use suggestion
   * @param position	where to insert the template
   */
  protected void addFromTemplate(TreePath path, AbstractActorTemplate template, TreeOperations.InsertPosition position) {
    AbstractActor		actor;
    AbstractActorTemplate[] 	templates;
    GenericObjectEditorDialog	m_TemplateDialog;

    m_TemplateDialog = GenericObjectEditorDialog.createDialog(m_State.tree);
    m_TemplateDialog.getGOEEditor().setCanChangeClassInDialog(true);
    m_TemplateDialog.getGOEEditor().setClassType(AbstractActorTemplate.class);

    if (template == null) {
      templates = suggestActorTemplates(path, position);
      template  = templates[0];
    }
    else {
      templates = new AbstractActorTemplate[]{template};
    }
    m_TemplateDialog.setProposedClasses(templates);
    m_TemplateDialog.setCurrent(template);
    if (position == TreeOperations.InsertPosition.HERE)
      m_TemplateDialog.setTitle("Add from template here...");
    else if (position == TreeOperations.InsertPosition.AFTER)
      m_TemplateDialog.setTitle("Add from template after...");
    else if (position == TreeOperations.InsertPosition.BENEATH)
      m_TemplateDialog.setTitle("Add from template beneath...");
    m_TemplateDialog.setLocationRelativeTo(GUIHelper.getParentComponent(m_State.tree));
    m_TemplateDialog.setVisible(true);
    if (m_TemplateDialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    template = (AbstractActorTemplate) m_TemplateDialog.getEditor().getValue();

    try {
      actor = template.generate();
      m_State.tree.updateLastTemplate(template, position);
    }
    catch (Exception e) {
      actor = null;
      e.printStackTrace();
      GUIHelper.showErrorMessage(m_State.tree, "Failed to create actor from template: " + e);
    }
    if (actor != null)
      m_State.tree.getOperations().addActor(path, actor, position);
  }
}
