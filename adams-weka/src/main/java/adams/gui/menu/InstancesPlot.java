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
 * InstancesPlot.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.gui.ConverterFileChooser;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

import javax.swing.JFileChooser;
import java.io.File;
import java.util.logging.Level;

/**
 * Displays plot of Instances.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstancesPlot
  extends AbstractParameterHandlingWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -771667287275117680L;

  /** filechooser for Plots. */
  protected ConverterFileChooser m_FileChooser;

  /**
   * Initializes the menu item with no owner.
   */
  public InstancesPlot() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public InstancesPlot(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new ConverterFileChooser(new File(System.getProperty("user.dir")));
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    File file;
    AbstractFileLoader loader;
    if (m_Parameters.length == 0) {
      // choose file
      int retVal = m_FileChooser.showOpenDialog(getOwner());
      if (retVal != JFileChooser.APPROVE_OPTION)
	return;
      file   = m_FileChooser.getSelectedFile();
      loader = m_FileChooser.getLoader();
    }
    else {
      file   = new PlaceholderFile(m_Parameters[0]).getAbsoluteFile();
      loader = ConverterUtils.getLoaderForFile(file);
    }

    // build plot
    VisualizePanel panel = new VisualizePanel();
    getLogger().severe("Loading instances from " + file);
    try {
      loader.setFile(file);
      Instances i = loader.getDataSet();
      i.setClassIndex(i.numAttributes() - 1);
      PlotData2D pd1 = new PlotData2D(i);
      pd1.setPlotName("Master plot");
      panel.setMasterPlot(pd1);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load: " + file, e);
      GUIHelper.showErrorMessage(
        getOwner(), "Error loading file '" + file + "':\n" + Utils.throwableToString(e));
      return;
    }

    // create frame
    ChildFrame frame = createChildFrame(panel, GUIHelper.getDefaultDialogDimension());
    frame.setTitle(frame.getTitle()  + " - " + file);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "WEKA Instances plot";
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