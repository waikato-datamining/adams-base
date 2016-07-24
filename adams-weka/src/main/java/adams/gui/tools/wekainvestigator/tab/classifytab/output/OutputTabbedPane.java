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
 * OutputTabbedPane.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.gui.core.ButtonTabComponent;
import adams.gui.core.DragAndDropTabbedPane;
import adams.gui.core.GUIHelper;

import javax.swing.JComponent;

/**
 * Tabbed pane for the output generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutputTabbedPane
  extends DragAndDropTabbedPane {

  private static final long serialVersionUID = -7694010290845155428L;

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
    button.setIcon(GUIHelper.getIcon("menu.gif"));

    return getTabCount() - 1;
  }

}
