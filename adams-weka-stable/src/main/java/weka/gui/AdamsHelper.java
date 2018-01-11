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
 * AdamsHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import adams.gui.chooser.FileChooserBookmarksPanel;

/**
 * Helper class to make Weka GUI more ADAMS-like.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdamsHelper {

  /**
   * Removes the label in the accessory component, to make space for the
   * bookmarks.
   * 
   * @param cont	the container to inspect
   * @return		the parent container of the label, null if not found
   */
  protected static Container removeAccessoryLabel(Container cont) {
    Container	result;
    int		i;

    result = null;
    
    for (i = 0; i < cont.getComponentCount(); i++) {
      if (cont.getComponent(i) instanceof JLabel) {
	result = cont;
	cont.remove(i);
	break;
      }
      else if (cont.getComponent(i) instanceof Container) {
	result = removeAccessoryLabel((Container) cont.getComponent(i));
      }
    }
    
    return result;
  }

  /**
   * Updates the accessory panel of the filechooser.
   * 
   * @param chooser	the chooser to update
   */
  public static void updateFileChooserAccessory(JFileChooser chooser) {
    FileChooserBookmarksPanel 	bookmarks;
    JComponent			accessory;
    Container			cont;
    Container			labelParent;
    
    accessory = chooser.getAccessory();
    if ((accessory == null) || !(accessory instanceof Container))
      return;
    
    cont = (Container) accessory;
    labelParent = removeAccessoryLabel(cont);
    if (labelParent == null)
      return;
    
    bookmarks = new FileChooserBookmarksPanel();
    bookmarks.setOwner(chooser);
    labelParent.add(bookmarks, BorderLayout.CENTER);
  }
}
