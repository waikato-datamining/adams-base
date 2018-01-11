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
 * SaveTree.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.visualize.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenuItem;

import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;

/**
 * Saves a tree in dotty notation as file.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SaveTree
  implements TreeVisualizePlugin {

  /**
   * Get a JMenu or JMenuItem which contain action listeners
   * that perform the visualization of the tree in GraphViz's dotty format.  
   * Exceptions thrown because of changes in Weka since compilation need to 
   * be caught by the implementer.
   *
   * @see NoClassDefFoundError
   * @see IncompatibleClassChangeError
   *
   * @param dotty 	the tree in dotty format
   * @param name	the name of the item (in the Explorer's history list)
   * @return menuitem 	for opening visualization(s), or null
   *         		to indicate no visualization is applicable for the input
   */
  @Override
  public JMenuItem getVisualizeMenuItem(final String dotty, final String name) {
    JMenuItem			result;
    final BaseFileChooser	chooser;
    ExtensionFileFilter		filter;
    
    filter  = new ExtensionFileFilter("DOT graph", new String[]{"gv", "dot"});
    chooser = new BaseFileChooser();
    chooser.setDialogTitle("Save tree - " + name);
    chooser.addChoosableFileFilter(filter);
    chooser.setAcceptAllFileFilterUsed(true);
    chooser.setFileFilter(filter);
    chooser.setDefaultExtension("gv");
    
    result = new JMenuItem("Save tree...");
    result.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int retVal = chooser.showSaveDialog(null);
	if (retVal != BaseFileChooser.APPROVE_OPTION)
	  return;
	File file = chooser.getSelectedFile();
	if (!FileUtils.writeToFile(file.getAbsolutePath(), dotty, false))
	  GUIHelper.showErrorMessage(null, "Failed to save tree as " + file + "!");
      }
    });
    
    return result;
  }

  /**
   * Get the minimum version of Weka, inclusive, the class
   * is designed to work with.  eg: <code>3.5.0</code>
   *
   * @return		the minimum version
   */
  public String getMinVersion() {
    return "3.5.9";
  }

  /**
   * Get the maximum version of Weka, exclusive, the class
   * is designed to work with.  eg: <code>3.6.0</code>
   *
   * @return		the maximum version
   */
  public String getMaxVersion() {
    return "3.8.0";
  }

  /**
   * Get the specific version of Weka the class is designed for.
   * eg: <code>3.5.1</code>
   *
   * @return		the version the plugin was designed for
   */
  public String getDesignVersion() {
    return "3.6.0";
  }
}
