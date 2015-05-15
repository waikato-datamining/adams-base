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
 * GraphVisualizer.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;

/**
 * Displays data in the graph visualizer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.gui.graphvisualizer.GraphVisualizer
 */
public class GraphVisualizer
  extends AbstractParameterHandlingWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -771667287275117680L;

  /** filechooser for GraphVisualizers. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Initializes the menu item with no owner.
   */
  public GraphVisualizer() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public GraphVisualizer(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new BaseFileChooser(new File(System.getProperty("user.dir")));
    m_FileChooser.addChoosableFileFilter(new ExtensionFileFilter("XML BIF file", "xml"));
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    String 					filename;
    int 					retVal;
    weka.gui.graphvisualizer.GraphVisualizer 	panel;
    ChildFrame 					frame;
    Reader					reader;
    InputStream					stream;
    
    if (m_Parameters.length == 0) {
      // choose file
      retVal = m_FileChooser.showOpenDialog(null);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;
      filename = m_FileChooser.getSelectedFile().getAbsolutePath();
    }
    else {
      filename = new PlaceholderFile(m_Parameters[0]).getAbsolutePath();
    }

    // build graph
    reader = null;
    stream = null;
    panel  = new weka.gui.graphvisualizer.GraphVisualizer();
    try{
      if (    filename.toLowerCase().endsWith(".xml")
	   || filename.toLowerCase().endsWith(".bif") ) {
	stream = new FileInputStream(filename);
	panel.readBIF(stream);
      }
      else {
	reader = new FileReader(filename);
	panel.readDOT(reader);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(
	  getOwner(), "Error loading file '" + filename + "':\n" + e.getMessage());
      return;
    }
    finally {
      FileUtils.closeQuietly(reader);
      FileUtils.closeQuietly(stream);
    }

    // create frame
    frame = createChildFrame(panel, 800, 600);
    frame.setTitle(frame.getTitle()  + " - " + filename);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "GraphVisualizer";
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
}