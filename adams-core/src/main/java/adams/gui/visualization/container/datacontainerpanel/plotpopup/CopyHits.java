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
 * AntiAliasing.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container.datacontainerpanel.plotpopup;

import adams.core.Utils;
import adams.data.container.DataContainer;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.core.plot.HitDetector;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * For enabling/disabling the anti-aliasing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CopyHits<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  extends AbstractPlotPopupCustomizer<T,M,C> {

  private static final long serialVersionUID = 4374831204149159338L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Copy hits";
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
  public boolean handles(DataContainerPanelWithContainerList<T, M, C> panel) {
    return (panel instanceof HitDetectorSupporter)
      || (panel.getDataPaintlet() instanceof HitDetectorSupporter);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param panel	the affected panel
   * @param e		the mouse event
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final DataContainerPanelWithContainerList<T, M, C> panel, MouseEvent e, JPopupMenu menu) {
    JMenuItem 		  	item;

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    item.setText(getName());
    item.addActionListener((ActionEvent ae) -> {
      HitDetector detector;
      if (panel instanceof HitDetectorSupporter)
        detector = ((HitDetectorSupporter) panel).getHitDetector();
      else
	detector = ((HitDetectorSupporter) panel.getDataPaintlet()).getHitDetector();
      Object hits = detector.detect(e);
      String hitsStr;
      if (hits instanceof List)
        hitsStr = Utils.flatten((List) hits, "\n");
      else
        hitsStr = "" + hits;
      hitsStr = hitsStr.trim();
      if (hitsStr.startsWith("(") && hitsStr.endsWith(")"))
        hitsStr = hitsStr.substring(1, hitsStr.length() - 1);
      ClipboardHelper.copyToClipboard(hitsStr);
    });
    menu.add(item);
  }
}
