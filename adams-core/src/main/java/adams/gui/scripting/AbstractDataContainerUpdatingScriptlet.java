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
 * AbstractDataContainerUpdatingScriptlet.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.data.container.DataContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ColorContainer;
import adams.gui.visualization.container.VisibilityContainer;
import adams.gui.visualization.container.VisibilityContainerManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Abstract ancestor for scriptlets that update spectra in a ChromatogramPanel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainerUpdatingScriptlet
  extends AbstractDataContainerPanelScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = -623707457032656271L;

  /** for post-processing the containers. */
  protected AbstractDataContainerUpdatingPostProcessor m_PostProcessor;

  /**
   * For initializing the member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();

    if (hasOwner())
      m_PostProcessor = getOwner().getDataContainerUpdatingPostProcessor();
  }

  /**
   * Updates the DataContainers in the GUI.
   *
   * @param conts	the processed DataContainers
   * @param overlay	whether to overlay the DataContainers
   */
  protected void updateDataContainers(List<DataContainer> conts, boolean overlay) {
    updateDataContainers(conts, overlay, new HashSet<>(), null);
  }

  /**
   * Updates the DataContainers in the GUI.
   *
   * @param conts	the processed DataContainers
   * @param overlay	whether to overlay the DataContainers
   * @param colors	the colors to use, null if to ignore
   */
  protected void updateDataContainers(List<DataContainer> conts, boolean overlay, List<Color> colors) {
    updateDataContainers(conts, overlay, new HashSet<>(), colors);
  }

  /**
   * Updates the DataContainers in the GUI.
   *
   * @param conts	the processed DataContainers
   * @param overlay	whether to overlay the DataContainers
   * @param exclude	the indices to exclude
   */
  protected void updateDataContainers(List<? extends DataContainer> conts, boolean overlay, HashSet<Integer> exclude) {
    updateDataContainers(conts, overlay, exclude, null);
  }

  /**
   * Updates the DataContainers in the GUI.
   *
   * @param conts	the processed DataContainers
   * @param overlay	whether to overlay the DataContainers
   * @param exclude	the indices to exclude
   * @param colors	the colors to use, null if to ignore
   */
  protected void updateDataContainers(List<? extends DataContainer> conts, boolean overlay, HashSet<Integer> exclude, List<Color> colors) {
    AbstractContainerManager	manager;
    int				i;
    int 			n;
    boolean[] 			visible;
    AbstractContainer		cont;
    List<AbstractContainer> 	data;

    manager = getDataContainerPanel().getContainerManager();
    manager.startUpdate();
    visible = new boolean[manager.count()];
    for (i = 0; i < visible.length; i++)
      visible[i] = ((VisibilityContainerManager) manager).isVisible(i);

    data = new ArrayList<>();
    n    = 0;
    for (i = 0; i < visible.length; i++) {
      if (!visible[i]) {
	data.add(manager.get(i));
	((VisibilityContainer) data.get(data.size() - 1)).setVisible(false);
      }
      else {
	// overlay original data?
	if (overlay) {
	  data.add(manager.get(i));
	  ((VisibilityContainer) data.get(data.size() - 1)).setVisible(true);
	}
	n++;
      }
    }
    n = 0;
    for (i = 0; i < visible.length && n < conts.size(); i++) {
      if (visible[i] && !exclude.contains(i)) {
	cont = manager.newContainer(conts.get(n));
	if (colors != null) {
	  if (cont instanceof ColorContainer)
	    ((ColorContainer) cont).setColor(colors.get(n));
	}
	data.add(cont);
	n++;
      }
    }

    // update
    manager.finishUpdate(false);
    manager.clear();
    manager.addAll(data);

    // post-process data
    if (m_PostProcessor != null)
      m_PostProcessor.postProcess(conts);
  }
}
