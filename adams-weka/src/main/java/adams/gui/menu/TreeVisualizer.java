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
 * TreeVisualizer.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import weka.gui.treevisualizer.Node;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeBuild;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileReader;

/**
 * Displays data in the tree visualizer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.gui.treevisualizer.TreeVisualizer
 */
public class TreeVisualizer
  extends AbstractParameterHandlingWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -771667287275117680L;

  /** filechooser for TreeVisualizers. */
  protected JFileChooser m_FileChooser;

  /**
   * Initializes the menu item with no owner.
   */
  public TreeVisualizer() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public TreeVisualizer(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes members.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter	filter;
    
    super.initialize();

    m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
    m_FileChooser.setDialogTitle("Open DOT graph file...");
    filter        = new ExtensionFileFilter("DOT graph", new String[]{"gv", "dot"});
    m_FileChooser.addChoosableFileFilter(filter);
    m_FileChooser.setFileFilter(filter);
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    String 	filename;
    int 	retVal;
    TreeBuild 	builder;
    Node 	top;
    NodePlace 	arrange;
    ChildFrame 	frame;
    FileReader	reader;
    
    if (m_Parameters.length == 0) {
      // choose file
      retVal = m_FileChooser.showOpenDialog(null);
      if (retVal != JFileChooser.APPROVE_OPTION)
	return;
      filename = m_FileChooser.getSelectedFile().getAbsolutePath();
    }
    else {
      filename = new PlaceholderFile(m_Parameters[0]).getAbsolutePath();
    }

    // build tree
    builder = new TreeBuild();
    top     = null;
    arrange = new PlaceNode2();
    reader  = null;
    try {
      reader = new FileReader(filename);
      top = builder.create(reader);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
        getOwner(), "Error loading file '" + filename + "':\n" + Utils.throwableToString(e));
      return;
    }
    finally {
      if (reader != null) {
	try {
	  reader.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    // create frame
    frame = createChildFrame(
	new weka.gui.treevisualizer.TreeVisualizer(null, top, arrange), 800, 600);
    frame.setTitle(frame.getTitle()  + " - " + filename);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "[Weka] Tree visualizer";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_VISUALIZATION;
  }
}