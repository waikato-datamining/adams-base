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
 * CostCurvePanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.weka;

import adams.core.logging.LoggingHelper;
import adams.gui.chooser.DatasetFileChooserPanel;
import adams.gui.core.GUIHelper;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Displays cost curve data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CostCurvePanel
  extends AbstractPanelWithFile<DatasetFileChooserPanel> {

  private static final long serialVersionUID = -485114976896228362L;

  /** for displaying the cost curve. */
  protected ThresholdVisualizePanel m_PanelCurve;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelCurve = new ThresholdVisualizePanel();
    add(m_PanelCurve, BorderLayout.CENTER);
  }

  /**
   * Generates the panel to use.
   *
   * @return		the generated panel
   */
  protected DatasetFileChooserPanel createChooserPanel() {
    DatasetFileChooserPanel	result;

    result = new DatasetFileChooserPanel();
    result.addChangeListener((ChangeEvent e) -> display());

    return result;
  }

  /**
   * Sets the current file to use.
   *
   * @param file	the file
   */
  public void setCurrent(File file) {
    m_PanelChooser.setCurrent(file);
  }

  /**
   * Loads/displays the data.
   */
  protected void display() {
    File	file;
    Instances 	result;
    PlotData2D 	plot;
    boolean[] 	connectPoints;
    int		cp;

    file = m_PanelChooser.getCurrent();
    if (!file.exists())
      return;
    if (file.isDirectory())
      return;

    try {
      result = m_PanelChooser.getLoader().getDataSet();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	getParent(), "Error loading file '" + file + "':\n" + LoggingHelper.throwableToString(e));
      return;
    }
    result.setClassIndex(result.numAttributes() - 1);
    m_PanelCurve.removeAllPlots();
    plot = new PlotData2D(result);
    plot.setPlotName(result.relationName());
    plot.m_displayAllPoints = true;
    connectPoints = new boolean[result.numInstances()];
    for (cp = 1; cp < connectPoints.length; cp++)
      connectPoints[cp] = true;
    try {
      plot.setConnectPoints(connectPoints);
      m_PanelCurve.addPlot(plot);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	getParent(), "Error adding plot:\n" + LoggingHelper.throwableToString(e));
      return;
    }
  }
}
