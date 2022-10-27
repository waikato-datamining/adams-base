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
 * UpdateID.java
 * Copyright (C) 2017-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container.datacontainerpanel.containerlistpopup;

import adams.data.container.DataContainer;
import adams.data.idextraction.IDExtractor;
import adams.data.idextraction.ReportField;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.NamedContainer;
import adams.gui.visualization.container.NamedContainerManager;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * For updating the IDs of the selected containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class UpdateID<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractContainerListPopupCustomizer<T,M,C> {

  private static final long serialVersionUID = 4973341996386365675L;

  /** the last extraction used. */
  protected IDExtractor m_LastExtractor;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Update ID";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "view";
  }

  /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
  @Override
  public boolean handles(DataContainerPanelWithContainerList<T, M, C> panel) {
    return (panel.getContainerManager() instanceof NamedContainerManager);
  }

  /**
   * Allows the user to choose extraction/updating schemes for updating the IDs
   * and the applies them.
   *
   * @param context	the current context
   */
  protected void update(final Context<T,M,C> context) {
    GenericObjectEditorDialog	dialog;
    int[]			indices;
    C 				cont;
    NamedContainer 		named;
    String			id;

    if (m_LastExtractor == null)
      m_LastExtractor = new ReportField();

    if (context.panel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(context.panel.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(context.panel.getParentFrame(), true);
    dialog.setUISettingsPrefix(IDExtractor.class);
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle(getName());
    dialog.getGOEEditor().setClassType(IDExtractor.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.setCurrent(m_LastExtractor);
    dialog.setLocationRelativeTo(context.panel.getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    m_LastExtractor = (IDExtractor) dialog.getCurrent();

    context.panel.getContainerManager().startUpdate();
    indices = context.actualSelectedContainerIndices;
    for (int index: indices) {
      cont  = (C) context.panel.getContainerManager().get(index);
      named = (NamedContainer) cont;
      id    = m_LastExtractor.extractID(cont.getPayload());
      named.setID(id);
    }
    context.panel.getContainerManager().finishUpdate();
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param context	the context
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final Context<T,M,C> context, JPopupMenu menu) {
    JMenuItem		item;
    final int[] 	indices;

    indices = context.actualSelectedContainerIndices;
    item    = new JMenuItem("Update ID" + (indices.length > 1 ? "s" : ""));
    item.setEnabled(indices.length > 0);
    item.addActionListener((ActionEvent e) -> update(context));
    menu.add(item);
  }
}
