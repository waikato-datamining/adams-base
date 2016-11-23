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

package adams.gui.visualization.instance.plotpopup;

import adams.data.instance.Instance;
import adams.gui.chooser.WekaFileChooser;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.plotpopup.AbstractPlotPopupCustomizer;
import adams.gui.visualization.instance.InstanceContainer;
import adams.gui.visualization.instance.InstanceContainerManager;
import adams.gui.visualization.instance.InstancePanel;
import weka.core.converters.AbstractFileSaver;

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
  extends AbstractPlotPopupCustomizer<Instance, InstanceContainerManager, InstanceContainer> {

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
  public boolean handles(DataContainerPanelWithContainerList<Instance, InstanceContainerManager, InstanceContainer> panel) {
    return (panel instanceof InstancePanel);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param panel	the affected panel
   * @param e		the mouse event
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(DataContainerPanelWithContainerList<Instance, InstanceContainerManager, InstanceContainer> panel, MouseEvent e, JPopupMenu menu) {
    JMenuItem		item;

    item = new JMenuItem("Save visible...", GUIHelper.getIcon("save.gif"));
    item.addActionListener((ActionEvent ae) -> {
      WekaFileChooser fc = new WekaFileChooser();
      int retval = fc.showSaveDialog(panel);
      if (retval != WekaFileChooser.APPROVE_OPTION)
        return;
      weka.core.Instances dataset = null;
      for (InstanceContainer c: panel.getTableModelContainers(true)) {
        if (dataset == null)
          dataset = new weka.core.Instances(c.getData().getDatasetHeader(), 0);
        dataset.add((weka.core.Instance) c.getData().toInstance().copy());
      }
      if (dataset == null)
        return;
      AbstractFileSaver saver = fc.getWriter();
      saver.setInstances(dataset);
      try {
        saver.setFile(fc.getSelectedFile().getAbsoluteFile());
        saver.writeBatch();
      }
      catch (Exception ex) {
        ex.printStackTrace();
        GUIHelper.showErrorMessage(
          panel, "Error saving instances:\n" + ex);
      }
    });
    menu.add(item);
  }
}
