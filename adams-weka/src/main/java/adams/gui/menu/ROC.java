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
 * ROC.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.ConverterFileChooser;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;
import adams.core.io.PlaceholderFile;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;

/**
 * Displays ROC curve data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see weka.classifiers.evaluation.ThresholdCurve
 */
public class ROC
  extends AbstractParameterHandlingWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -771667287275117680L;

  /** filechooser for ROCs. */
  protected ConverterFileChooser m_FileChooser;

  /**
   * Initializes the menu item with no owner.
   */
  public ROC() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ROC(AbstractApplicationFrame owner) {
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
    if (m_Parameters.length == 0) {
      // choose file
      int retVal = m_FileChooser.showOpenDialog(null);
      if (retVal != JFileChooser.APPROVE_OPTION)
	return;
      file = m_FileChooser.getSelectedFile();
    }
    else {
      file = new PlaceholderFile(m_Parameters[0]).getAbsoluteFile();
      m_FileChooser.setSelectedFile(file);
    }

    // create plot
    Instances result;
    try {
      result = m_FileChooser.getLoader().getDataSet();
    }
    catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(
	  getOwner(), "Error loading file '" + file + "':\n" + e.getMessage());
      return;
    }
    result.setClassIndex(result.numAttributes() - 1);
    ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
    vmc.setROCString("(Area under ROC = " +
	Utils.doubleToString(ThresholdCurve.getROCArea(result), 4) + ")");
    vmc.setName(result.relationName());
    PlotData2D tempd = new PlotData2D(result);
    tempd.setPlotName(result.relationName());
    tempd.addInstanceNumberAttribute();
    // specify which points are connected
    boolean[] cp = new boolean[result.numInstances()];
    for (int n = 1; n < cp.length; n++)
      cp[n] = true;
    try {
      tempd.setConnectPoints(cp);
      vmc.addPlot(tempd);
    }
    catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(
	  getOwner(), "Error adding plot:\n" + e.getMessage());
      return;
    }

    ChildFrame frame = createChildFrame(vmc, 800, 600);
    frame.setTitle(frame.getTitle()  + " - " + file);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "ROC";
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