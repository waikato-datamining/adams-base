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
 * SaveVisible.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.spreadsheet.plotpopup;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.plotpopup.AbstractPlotPopupCustomizer;
import adams.gui.visualization.spreadsheet.SpreadSheetRow;
import adams.gui.visualization.spreadsheet.SpreadSheetRowContainer;
import adams.gui.visualization.spreadsheet.SpreadSheetRowContainerManager;
import adams.gui.visualization.spreadsheet.SpreadSheetRowPanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Allows the user to save the visible containers as ARFF.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SaveVisible
  extends AbstractPlotPopupCustomizer<SpreadSheetRow, SpreadSheetRowContainerManager, SpreadSheetRowContainer> {

  private static final long serialVersionUID = 3295471324320509106L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Save visible";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "export";
  }

  /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
  @Override
  public boolean handles(DataContainerPanelWithContainerList<SpreadSheetRow, SpreadSheetRowContainerManager, SpreadSheetRowContainer> panel) {
    return (panel instanceof SpreadSheetRowPanel);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param panel	the affected panel
   * @param e		the mouse event
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(DataContainerPanelWithContainerList<SpreadSheetRow, SpreadSheetRowContainerManager, SpreadSheetRowContainer> panel, MouseEvent e, JPopupMenu menu) {
    JMenuItem		item;

    item = new JMenuItem("Save visible...", GUIHelper.getIcon("save.gif"));
    item.addActionListener((ActionEvent ae) -> {
      SpreadSheetFileChooser fc = new SpreadSheetFileChooser();
      int retval = fc.showSaveDialog(panel);
      if (retval != SpreadSheetFileChooser.APPROVE_OPTION)
        return;
      SpreadSheet sheet = null;
      for (int i = 0; i < panel.getContainerManager().count(); i++) {
        SpreadSheetRowContainer cont = panel.getContainerManager().get(i);
        if (i == 0)
          sheet = cont.getData().getDatasetHeader().getHeader();
        if (cont.isVisible())
          sheet.addRow().assign(cont.getData().toRow());
      }
      if (sheet == null)
        return;
      SpreadSheetWriter writer = fc.getWriter();
      if (!writer.write(sheet, fc.getSelectedFile()))
        GUIHelper.showErrorMessage(
          panel, "Error saving spreadsheet to:\n" + fc.getSelectedFile());
    });
    menu.add(item);
  }
}
