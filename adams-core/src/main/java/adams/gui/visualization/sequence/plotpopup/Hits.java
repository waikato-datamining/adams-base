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
 * Hits.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence.plotpopup;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.plotpopup.AbstractPlotPopupCustomizer;
import adams.gui.visualization.core.plot.ContainerHitDetector;
import adams.gui.visualization.core.plot.HitDetector;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePanel;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows operations on the "hits", ie the spectra currently under the mouse cursor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Hits
  extends AbstractPlotPopupCustomizer<XYSequence, XYSequenceContainerManager, XYSequenceContainer> {

  private static final long serialVersionUID = 3295471324320509106L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Hits";
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
  public boolean handles(DataContainerPanelWithContainerList<XYSequence, XYSequenceContainerManager, XYSequenceContainer> panel) {
    return (panel instanceof XYSequencePanel);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param panel	the affected panel
   * @param e		the mouse event
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(final DataContainerPanelWithContainerList<XYSequence, XYSequenceContainerManager, XYSequenceContainer> panel, MouseEvent e, JPopupMenu menu) {
    JMenu				submenu;
    JMenuItem				item;
    HitDetector				detector;
    final XYSequenceContainer[] 	hits;
    final Set<XYSequenceContainer> 	set;

    if (!(panel.getDataPaintlet() instanceof HitDetectorSupporter))
      return;
    detector = ((HitDetectorSupporter) panel.getDataPaintlet()).getHitDetector();
    if (!(detector instanceof ContainerHitDetector))
      return;

    hits    = ((ContainerHitDetector<List<XYSequencePoint>, String, XYSequenceContainer>) detector).containers(e);
    submenu = new JMenu("Hits");
    submenu.setEnabled(hits.length > 0);
    menu.add(submenu);

    if (!submenu.isEnabled())
      return;

    set = new HashSet<>(Arrays.asList(hits));

    item = new JMenuItem("Copy ID" + (hits.length == 1 ? "" : "s"));
    item.addActionListener((ActionEvent ae) -> {
      StringBuilder ids = new StringBuilder();
      for (XYSequenceContainer hit : hits) {
        if (ids.length() > 0)
          ids.append("\n");
	ids.append(hit.getID());
      }
      ClipboardHelper.copyToClipboard(ids.toString());
    });
    submenu.add(item);

    item = new JMenuItem("Choose color...");
    item.addActionListener((ActionEvent ae) -> {
      String msg = "Choose color";
      XYSequenceContainer cont;
      Color color = Color.BLUE;
      if (hits.length == 1) {
        cont = hits[0];
	msg += " for " + cont.getID();
        color = cont.getColor();
      }
      Color c = JColorChooser.showDialog(
        panel,
        msg,
        color);
      if (c == null)
        return;
      panel.getContainerManager().startUpdate();
      for (XYSequenceContainer hit : hits)
        hit.setColor(c);
      panel.getContainerManager().finishUpdate();
    });
    submenu.add(item);

    submenu.addSeparator();

    item = new JMenuItem("Hide " + (hits.length == 1 ? "this" : "these"));
    item.addActionListener((ActionEvent ae) -> {
      panel.getContainerManager().startUpdate();
      for (XYSequenceContainer hit: hits)
	hit.setVisible(false);
      panel.getContainerManager().finishUpdate();
    });
    submenu.add(item);

    item = new JMenuItem("Hide others");
    item.addActionListener((ActionEvent ae) -> {
      panel.getContainerManager().startUpdate();
      for (int i = 0; i < panel.getContainerManager().count(); i++) {
        if (!set.contains(panel.getContainerManager().get(i)))
	  panel.getContainerManager().get(i).setVisible(false);
      }
      panel.getContainerManager().finishUpdate();
    });
    submenu.add(item);
  }
}
