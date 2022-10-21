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
 * EncloseClassifier.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe.popupmenu;

import adams.core.ClassLister;
import adams.gui.core.BaseMenu;
import adams.gui.core.ConsolePanel;
import adams.gui.goe.GenericObjectEditorPopupMenu;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

/**
 * For enclosing classifiers in SingeClassifierEnhancer wrappers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EncloseClassifier
  extends AbstractGenericObjectEditorPopupMenuCustomizer {

  private static final long serialVersionUID = 7573235626494111377L;

  /**
   * The name used for sorting.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Enclose";
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
    return (editor.getValue() instanceof Classifier);
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
    JMenu 		submenu;
    List<JMenuItem> 	items;
    JMenuItem		item;
    Class[] 		classes;

    classes = ClassLister.getSingleton().getClasses(SingleClassifierEnhancer.class);
    if (classes.length > 0) {
      items = new ArrayList<>();
      for (Class cls: classes) {
	final Class fCls = cls;
	item = new JMenuItem(cls.getName());
	item.addActionListener((ActionEvent e) -> {
	  try {
	    SingleClassifierEnhancer wrapper = (SingleClassifierEnhancer) fCls.getDeclaredConstructor().newInstance();
	    Classifier base = (Classifier) editor.getValue();
	    wrapper.setClassifier(base);
	    editor.setValue(wrapper);
	  }
	  catch (Exception ex) {
	    ConsolePanel.getSingleton().append("Failed to wrap classifier in " + fCls.getName() + "!", ex);
	  }
	});
	items.add(item);
      }
      submenu = BaseMenu.createCascadingMenu(items, -1, "More...");
      submenu.setText("Enclose");
      menu.add(submenu);
    }
  }
}
