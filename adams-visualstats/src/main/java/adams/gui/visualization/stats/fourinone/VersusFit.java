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
 * VersusFit.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.fourinone;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.StatUtils;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.paintlet.VsFitPaintlet;

import java.awt.BorderLayout;
import java.awt.Graphics;

/**
 * Class that displays a versus fit graph. Plotting the residuals on the
 * y axis and the predicted value on the x axis.
 *
 * @author msf8
 * @version $Revision$
 */
public class VersusFit
extends PaintablePanel{

  /** for serialization */
  private static final long serialVersionUID = 2542241196305925848L;

  /** Instances containing the data */
  protected SpreadSheet m_Data;

  /** Panel for displaying the data */
  protected VersusFitPanel m_Plot;

  /** Paintlet for plotting */
  protected VsFitPaintlet m_val;

  /**Options for the plot */
  protected VersusFitOptions m_VsFitOptions;

  /**Index of the residuals attribute in the instances */
  protected int m_Index;

  /** Index of the predicted attribute in the instances */
  protected int m_PredInd;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());
    m_Plot = new VersusFitPanel();
    m_Plot.addPaintListener(this);
    add(m_Plot, BorderLayout.CENTER);
    m_val = new VsFitPaintlet();
    m_val.setPanel(this);
  }

  /**
   * Set the options for this versus fit
   * @param val			Versusfitoptions object containing the options
   */
  public void setOptions(VersusFitOptions val) {
    m_VsFitOptions = val;
    m_VsFitOptions.getAxisX().configure(m_Plot, Axis.BOTTOM);
    m_VsFitOptions.getAxisY().configure(m_Plot, Axis.LEFT);
    removePaintlet(m_val);
    m_val = (VsFitPaintlet) m_VsFitOptions.getPaintlet().shallowCopy(true);
    m_val.setPanel(this);
  }

  /**
   * Returns the plot panel of the panel, null if no panel present.
   *
   * @return		the plot panel
   */
  @Override
  public PlotPanel getPlot() {
    return m_Plot;
  }

  /**
   * Prepares the update, i.e., calculations etc.
   */
  @Override
  public void prepareUpdate() {
    if (m_Data != null) {
      m_val.setData(m_Data);
      m_val.setIndices(m_Index, m_PredInd);
      m_val.setRepaintOnChange(true);

      AxisPanel axisBottom = getPlot().getAxis(Axis.BOTTOM);
      AxisPanel axisLeft = getPlot().getAxis(Axis.LEFT);

      double[] predicted = SpreadSheetUtils.getNumericColumn(m_Data, m_PredInd);
      double[] residuals = SpreadSheetUtils.getNumericColumn(m_Data, m_Index);
      double minY = StatUtils.min(residuals);
      double maxY = StatUtils.max(residuals);
      double maxX = StatUtils.max(predicted);
      double minX = StatUtils.min(predicted);
      axisBottom.setMinimum(minX);
      axisBottom.setMaximum(maxX);
      axisLeft.setMinimum(minY);
      axisLeft.setMaximum(maxY);
    }
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  @Override
  protected boolean canPaint(Graphics g) {
    return (m_Plot != null) && (m_Data != null);
  }

  /**
   * get the instances being plotted
   * @return		Instances for plotting
   */
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * Set the instances to be plotted
   * @param value		Instances for plotting
   */
  public void setData(SpreadSheet value) {
    m_Data = value;
  }

  /**
   * Set the index of the residuals attribute in the instances
   */
  public void setIndices(int res, int pred) {
    m_Index = res;
    m_PredInd = pred;
  }
}