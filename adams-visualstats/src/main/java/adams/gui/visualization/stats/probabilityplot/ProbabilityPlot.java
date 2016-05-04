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
 * ProbabilityPlot.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.probabilityplot;

import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.StatUtils;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.core.IndexSet;
import adams.gui.visualization.stats.paintlet.AbstractProbabilityPaintlet;
import adams.gui.visualization.stats.paintlet.Normal;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

/**
 * Probability plot visualization.
 *
 * @author msf8
 * @version $Revision$
 */
public class ProbabilityPlot
extends PaintablePanel{

  /** for serialization */
  private static final long serialVersionUID = 5997080502859878659L;

  /**Instances to display */
  protected SpreadSheet m_Data;

  /**Panel to display data on */
  protected ProbabilityPlotPanel m_Plot;

  /**GOE for choosing the regression to fit*/
  protected GenericObjectEditorPanel m_PanelRegression;

  /**Paintlet for plotting the data using a regression */
  protected AbstractProbabilityPaintlet m_val;

  /**Default paintlet for the GOE */
  protected AbstractProbabilityPaintlet m_def;

  /**Index of attribute in the instances */
  protected int m_IntIndex;

  /**Whether a best fit line is drawn */
  protected boolean m_Line;

  /** Panel containing the options for the probability plot */
  protected ParameterPanel m_OptionPanel;

  /** Check box for line overlay */
  protected JCheckBox line;

  /** Check box for grid overlay */
  protected JCheckBox grid;

  /** Whether to display the grid */
  protected boolean m_Grid;

  /**Label showing the mean of the dataset */
  protected JLabel m_Mean;

  /** Label showing the std deviation of the dataset */
  protected JLabel m_Std;

  /** Regular expression for choosing the attribute to plot */
  protected BaseRegExp m_AttReg;

  /**Index for choosing the attribute to plot */
  protected Index m_Index;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());
    m_Plot = new ProbabilityPlotPanel();
    m_Plot.getAxis(Axis.LEFT).setTickGenerator(new FancyTickGenerator());
    m_Plot.getAxis(Axis.LEFT).setNthValueToShow(1);
    m_Plot.getAxis(Axis.LEFT).setNumberFormat("#.##");
    m_Plot.getAxis(Axis.BOTTOM).setTickGenerator(new FancyTickGenerator());
    m_Plot.getAxis(Axis.BOTTOM).setNthValueToShow(2);
    m_Plot.getAxis(Axis.BOTTOM).setNumberFormat("#.##");
    m_Plot.addPaintListener(this);

    m_OptionPanel = new ParameterPanel();
    BaseSplitPane split = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    JPanel middle = new JPanel(new BorderLayout());
    middle.add(m_Plot, BorderLayout.CENTER);
    JPanel key = new JPanel();
    key.setLayout(new BoxLayout(key, BoxLayout.Y_AXIS));
    m_Mean = new JLabel("");
    m_Std = new JLabel("");
    key.add(m_Mean);
    key.add(m_Std);
    key.setBackground(Color.WHITE);
    key.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    JPanel east = new JPanel(new BorderLayout());
    east.add(key, BorderLayout.NORTH);
    middle.add(east, BorderLayout.EAST);
    split.setLeftComponent(middle);
    JPanel hold = new JPanel(new BorderLayout());
    hold.add(m_OptionPanel, BorderLayout.NORTH);
    split.setRightComponent(hold);
    hold.setPreferredSize(new Dimension(600,0));
    split.setOneTouchExpandable(true);
    split.setResizeWeight(1);
    this.add(split, BorderLayout.CENTER);

    line = new JCheckBox();
    if(m_val != null)
      line.setEnabled(m_val.hasFitLine());

    grid = new JCheckBox();
    line.setSelected(false);
    grid.setSelected(false);
    if(m_val == null)
      m_def = new Normal();
    else
      m_def = m_val;
    m_PanelRegression = new GenericObjectEditorPanel(AbstractProbabilityPaintlet.class, m_def, true);
    m_PanelRegression.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	changeRegression();
	changeGrid();
      }
    });
    grid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
	m_Grid = ((JCheckBox)(arg0.getSource())).isSelected();
	changeGrid();
      }
    });
    line.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
	m_Line = ((JCheckBox)(arg0.getSource())).isSelected();
	if(m_val.hasFitLine())
	  changeLine();
      }
    });
    m_OptionPanel.addParameter("Regression", m_PanelRegression);
    m_OptionPanel.addParameter("display grid", grid);
    m_OptionPanel.addParameter("display best fit line", line);
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
    if ((m_Data != null) && (m_AttReg != null) && (m_Index != null)) {
      int temp = -1;
      temp = IndexSet.getIndex(m_AttReg, m_Index, m_Data, temp);
      if(temp == -1) {
        temp = 0;
        System.err.println("changed to 0");
      }
      m_IntIndex = temp;

      DecimalFormat df = new DecimalFormat("#.##");
      //labels showing statistics
      m_Mean.setText("Mean: " + df.format(StatUtils.mean(SpreadSheetUtils.getNumericColumn(m_Data, m_IntIndex))));
      m_Std.setText("Std dev: " + df.format(StatUtils.stddev(SpreadSheetUtils.getNumericColumn(m_Data, m_IntIndex), false)));
      
      m_val.setIndex(m_IntIndex);
      m_val.setData(m_Data);
      m_val.configureAxes();
      m_val.calculateDimensions();
      line.setEnabled(m_val.hasFitLine());
      if(m_val.hasFitLine())
	m_val.setLine(m_Line);
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
   * get the instances used for this probability plot
   * @return			Instances plotted
   */
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * Set the instances to be plotted
   * @param data			instances to be plotted
   */
  public void setData(SpreadSheet data) {
    m_Data = data;
    //initially index set to the last attribute
    m_IntIndex = m_Data.getColumnCount() - 1;
    update();
  }

  /**
   * Called when the regression to plot changes
   */
  protected void changeRegression() {
    removePaintlet(m_val);
    m_val = (AbstractProbabilityPaintlet)m_PanelRegression.getCurrent();
    m_val.setPanel(this);
    m_val.configureAxes();
    m_val.setIndex(m_IntIndex);
    m_val.setData(m_Data);
    line.setEnabled(m_val.hasFitLine());
    if(m_val.hasFitLine()) {
      m_val.setLine(m_Line);
    }
    update();
  }

  /**
   * Set the regression to use for the probability plot
   * @param val			Regression paintlet for transforming the data
   */
  public void setRegression(AbstractProbabilityPaintlet val) {
    m_PanelRegression.setCurrent(val.shallowCopy());
    changeRegression();
  }

  /**
   * Called when the grid checkbox changes, displays or removes
   * the grid overlay
   */
  protected void changeGrid() {
    m_Plot.setGridColor(Color.LIGHT_GRAY);
    m_Plot.getAxis(Axis.LEFT).setShowGridLines(m_Grid);
    m_Plot.getAxis(Axis.BOTTOM).setShowGridLines(m_Grid);
    m_Plot.repaint();
    m_Plot.revalidate();
    update();
  }

  /**
   * Called when the line checkbox changes, Displays or removes
   * the regression line overlay
   */
  protected void changeLine() {
    m_val.setLine(m_Line);
    update();
  }

  /**
   * Set whether the grid overlay is displayed
   * @param val
   */
  public void setGrid(boolean val) {
    m_Grid = val;
    grid.setSelected(val);
    changeGrid();
  }

  /**
   * Set whether the regression linear line is displayed
   * @param val
   */
  public void setRegressionLine(boolean val) {
    m_Line = val;
    line.setSelected(val);
    update();
  }

  /**
   * Set the index to use to choose the attribute to display
   * @param val			Index of attribute
   */
  public void setAttIndex(Index val) {
    m_Index = val;
    update();
  }

  /**
   * Set the regular expression for chossing the attribute to display
   * @param val			Regular expression for name of attribute
   */
  public void setAttReg(BaseRegExp val) {
    m_AttReg = val;
    update();
  }
}