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
 * FixLWL.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyTraversal;
import adams.core.discovery.PropertyTraversal.Observer;
import adams.flow.processor.FixLWLSynchroNoUpdate.LWLSynchroObserver;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorPopupMenu;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.LWL;
import weka.classifiers.lazy.LWLSynchro;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

/**
 * For ensuring that LWLSynchro is used and the no update flag is set.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FixLWL
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = 7573235626494111377L;

  /**
   * Sets the noUpdate flag of LWLSynchro classifiers.
   */
  public static class UpdateRequired
    implements Observer {

    /** whether LWL requires updating. */
    protected boolean m_RequiresUpdate;

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path	the path
     * @param desc	the property descriptor
     * @param parent	the parent object
     * @param child	the child object
     * @return		true if to continue observing
     */
    @Override
    public boolean observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
      if (child instanceof LWLSynchro) {
	if (!((LWLSynchro) child).getNoUpdate()) {
	  m_RequiresUpdate = true;
	}
      }
      else if (child instanceof LWL) {
	m_RequiresUpdate = true;
      }
      return true;
    }

    /**
     * Returns whether an update is required.
     *
     * @return		true if update required
     */
    public boolean getRequiresUpdate() {
      return m_RequiresUpdate;
    }
  }

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Fix LWL";
  }

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  @Override
  protected boolean handles(GenericObjectEditorPopupMenu menu, PropertyEditor editor, JComponent comp) {
    UpdateRequired 	observer;
    PropertyTraversal 	traversal;

    if (!(editor.getValue() instanceof Classifier))
      return false;

    observer  = new UpdateRequired();
    traversal = new PropertyTraversal();
    traversal.traverse(observer, editor.getValue());

    return observer.getRequiresUpdate();
  }

  /**
   * Customizes the GOE popup menu.
   *
   * @param menu	the menu to customize
   * @param editor	the current editor
   * @param comp	the GUI context
   */
  @Override
  protected void doCustomize(GenericObjectEditorPopupMenu menu, final PropertyEditor editor, JComponent comp) {
    JMenuItem		item;

    item = new JMenuItem(getName());
    item.addActionListener((ActionEvent e) -> {
      LWLSynchroObserver observer = new LWLSynchroObserver(true);
      PropertyTraversal traversal = new PropertyTraversal();
      Object obj = editor.getValue();
      traversal.traverse(observer, obj);
      if (observer.getUpdates() > 0) {
	editor.setValue(obj);
	comp.repaint();
	menu.notifyChangeListeners();
	GUIHelper.showInformationMessage(comp, "Number of updates: " + observer.getUpdates());
      }
      else {
	GUIHelper.showInformationMessage(comp, "No updates occurred.");
      }
    });
    menu.add(item);
  }
}
