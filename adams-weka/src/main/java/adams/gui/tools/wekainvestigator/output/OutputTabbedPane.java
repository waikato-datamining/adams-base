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
 * OutputTabbedPane.java
 * Copyright (C) 2016-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.core.CleanUpHandler;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ButtonTabComponent;
import adams.gui.core.DragAndDropTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.multiobjectexport.AbstractMultiObjectExport;
import adams.gui.visualization.multiobjectexport.DirectoryExport;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Dialog.ModalityType;

/**
 * Tabbed pane for the output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OutputTabbedPane
  extends DragAndDropTabbedPane
  implements CleanUpHandler {

  private static final long serialVersionUID = -7694010290845155428L;

  /** the last export scheme. */
  protected AbstractMultiObjectExport m_LastExport;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    setShowCloseTabButton(true);
  }

  /**
   * Adds the component as tab to the result item.
   *
   * @param title	the title of the tab
   * @param comp	the component to add
   * @return		the index of the new tab
   */
  public int newTab(String title, JComponent comp) {
    ButtonTabComponent button;

    addTab(title, comp);
    button = (ButtonTabComponent) getTabComponentAt(getTabCount() - 1);
    button.setIcon(ImageManager.getIcon("menu.gif"));

    return getTabCount() - 1;
  }

  /**
   * Returns the actual component at the position.
   *
   * @param index	the tab index
   * @return		the component
   */
  protected Component getActualComponentAt(int index) {
    Component 	result;

    result = getComponentAt(index);
    if (result instanceof BaseScrollPane)
      result = ((BaseScrollPane) result).getViewport().getView();

    return result;
  }

  /**
   * Removes the tab.
   *
   * @param index	the index of the tab to remove
   */
  @Override
  public void removeTabAt(int index) {
    Component comp;

    comp = getActualComponentAt(index);

    super.removeTabAt(index);

    if (comp instanceof CleanUpHandler)
      ((CleanUpHandler) comp).cleanUp();
  }

  /**
   * Exports the components using a {@link AbstractMultiObjectExport}
   * scheme.
   */
  public void export() {
    GenericObjectEditorDialog	dialog;
    String[]			names;
    Object[]			objects;
    int				i;
    String			msg;

    if (m_LastExport == null)
      m_LastExport = new DirectoryExport();

    if (GUIHelper.getParentDialog(this) != null)
      dialog = new GenericObjectEditorDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(GUIHelper.getParentFrame(this), true);
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Export output");
    dialog.setUISettingsPrefix(AbstractMultiObjectExport.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(AbstractMultiObjectExport.class);
    dialog.setCurrent(m_LastExport);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    m_LastExport = (AbstractMultiObjectExport) dialog.getCurrent();
    names        = new String[getTabCount()];
    objects      = new Object[getTabCount()];
    for (i = 0; i < getTabCount(); i++) {
      names[i]   = getTitleAt(i);
      objects[i] = getActualComponentAt(i);
    }

    msg = m_LastExport.export(names, objects);
    if (msg != null)
      GUIHelper.showErrorMessage(dialog.getParent(), "Failed to export outputs!\n" + msg);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    removeAll();
  }
}
