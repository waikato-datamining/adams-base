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
 * NormalPlot.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.probabilityplot;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.paintlet.Normal;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Probability plot that only displays the normal distribution,
 * used in the 4-in1 plot.
 *
 * @author msf8
 * @version $Revision$
 */
public class NormalPlot
  extends PaintablePanel
  implements PopupMenuCustomizer {

  /** for serialization */
  private static final long serialVersionUID = 2806317665479264377L;

  /** Instances to plot */
  protected SpreadSheet m_Data;

  /**Panel for displaying data */
  protected ProbabilityPlotPanel m_Plot;

  /** Normal distribution paintlet for the regression fitting */
  protected Normal m_Val;

  /** Options for this normal distribution*/
  protected NormalPlotOptions m_NormOptions;

  /** Index of the residuals attribute in the instances */
  protected int m_Index;

  /** the file chooser for saving a specific sequence. */
  protected SpreadSheetFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_NormOptions = new NormalPlotOptions();
    m_Data        = null;
    m_Index       = 0;
    m_FileChooser = null;
  }
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Plot = new ProbabilityPlotPanel();
    m_Plot.addPaintListener(this);
    m_Plot.setPopupMenuCustomizer(this);
    add(m_Plot, BorderLayout.CENTER);

    m_Val = new Normal();
    m_Val.setPanel(this);
  }

  /**
   * Set the options for this plot
   * @param val		Normalplotoptions object containing the options
   */
  public void setOptions(NormalPlotOptions val) {
    m_NormOptions = val;
    m_NormOptions.getAxisX().configure(m_Plot, Axis.BOTTOM);
    m_NormOptions.getAxisY().configure(m_Plot, Axis.LEFT);
    update();
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
      m_Val.setLine(m_NormOptions.m_RegLine);
      m_Val.setIndex(m_Index);
      m_Val.setData(m_Data);
      m_Val.configureAxes();
      m_Val.calculateDimensions();
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
   * Set the data to plot.
   *
   * @param data	data to plot
   */
  public void setData(SpreadSheet data) {
    m_Data = data;
    update();
  }

  /**
   * Returns the plot data.
   *
   * @return		the data
   */
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * Set the index of the residuals column in the spreadsheet.
   *
   * @param val		Residuals column index
   */
  public void setIndex(int val) {
    m_Index = val;
    update();
  }

  /**
   * Returns the index of the residuals column in the spreadsheet.
   *
   * @return		the column
   */
  public int getIndex() {
    return m_Index;
  }

  /**
   * Saves the data as spreadsheet.
   */
  protected void save() {
    int			retVal;
    SpreadSheetWriter writer;

    if (m_FileChooser == null)
      m_FileChooser = new SpreadSheetFileChooser();

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    writer = m_FileChooser.getWriter();
    if (!writer.write(m_Data, m_FileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(
        this, "Failed to save data to file:\n" + m_FileChooser.getSelectedFile());
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		The mouse event
   * @param menu	The menu to customize.
   */
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    JMenuItem menuitem;

    menuitem = new JMenuItem("Save data...", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> save());
    menu.add(menuitem);
  }
}