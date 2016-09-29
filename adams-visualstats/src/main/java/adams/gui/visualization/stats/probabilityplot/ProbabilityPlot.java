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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
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
  protected AbstractProbabilityPaintlet m_Paintlet;

  /**Default paintlet for the GOE */
  protected AbstractProbabilityPaintlet m_DefaultPaintlet;

  /**Index of attribute in the instances */
  protected int m_IntIndex;

  /**Whether a best fit line is drawn */
  protected boolean m_Line;

  /** Panel containing the options for the probability plot */
  protected ParameterPanel m_OptionPanel;

  /** Check box for line overlay */
  protected JCheckBox m_CheckBoxLine;

  /** Check box for grid overlay */
  protected JCheckBox m_CheckBoxGrid;

  /** Whether to display the grid */
  protected boolean m_Grid;

  /**Label showing the mean of the dataset */
  protected JLabel m_LabelMean;

  /** Label showing the std deviation of the dataset */
  protected JLabel m_LabelStd;

  /** Regular expression for choosing the attribute to plot */
  protected BaseRegExp m_AttReg;

  /**Index for choosing the attribute to plot */
  protected Index m_Index;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel 	panel;
    JPanel	panel2;
    JPanel 	panelKey;

    super.initGUI();

    setLayout(new BorderLayout());

    BaseSplitPane splitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    splitPane.setOneTouchExpandable(true);
    add(splitPane, BorderLayout.CENTER);

    // options
    m_OptionPanel = new ParameterPanel();

    m_CheckBoxLine = new JCheckBox();
    if(m_Paintlet != null)
      m_CheckBoxLine.setEnabled(m_Paintlet.hasFitLine());

    m_CheckBoxGrid = new JCheckBox();
    m_CheckBoxLine.setSelected(false);
    m_CheckBoxGrid.setSelected(false);
    if(m_Paintlet == null)
      m_DefaultPaintlet = new Normal();
    else
      m_DefaultPaintlet = m_Paintlet;
    m_PanelRegression = new GenericObjectEditorPanel(AbstractProbabilityPaintlet.class, m_DefaultPaintlet, true);
    m_PanelRegression.addChangeListener((ChangeEvent e) -> {
      changeRegression();
      changeGrid();
    });
    m_CheckBoxGrid.addActionListener((ActionEvent arg0) -> {
      m_Grid = ((JCheckBox)(arg0.getSource())).isSelected();
      changeGrid();
    });
    m_CheckBoxLine.addItemListener((ItemEvent arg0) -> {
      m_Line = ((JCheckBox)(arg0.getSource())).isSelected();
      if(m_Paintlet.hasFitLine())
	changeLine();
    });
    m_OptionPanel.addParameter("Regression", m_PanelRegression);
    m_OptionPanel.addParameter("display grid", m_CheckBoxGrid);
    m_OptionPanel.addParameter("display best fit line", m_CheckBoxLine);

    panel = new JPanel(new BorderLayout());
    panel.add(m_OptionPanel, BorderLayout.CENTER);
    splitPane.setTopComponent(panel);

    // plot/key
    m_Plot = new ProbabilityPlotPanel();
    m_Plot.getAxis(Axis.LEFT).setTickGenerator(new FancyTickGenerator());
    m_Plot.getAxis(Axis.LEFT).setNthValueToShow(1);
    m_Plot.getAxis(Axis.LEFT).setNumberFormat("#.##");
    m_Plot.getAxis(Axis.BOTTOM).setTickGenerator(new FancyTickGenerator());
    m_Plot.getAxis(Axis.BOTTOM).setNthValueToShow(2);
    m_Plot.getAxis(Axis.BOTTOM).setNumberFormat("#.##");
    m_Plot.addPaintListener(this);

    panelKey = new JPanel();
    panelKey.setLayout(new BoxLayout(panelKey, BoxLayout.Y_AXIS));
    m_LabelMean = new JLabel("");
    m_LabelStd = new JLabel("");
    panelKey.add(m_LabelMean);
    panelKey.add(m_LabelStd);
    panelKey.setBackground(Color.WHITE);
    panelKey.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    panel2.add(panelKey, BorderLayout.NORTH);

    panel = new JPanel(new BorderLayout());
    panel.add(m_Plot, BorderLayout.CENTER);
    panel.add(panel2, BorderLayout.EAST);
    splitPane.setBottomComponent(panel);
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
      m_LabelMean.setText("Mean: " + df.format(StatUtils.mean(SpreadSheetUtils.getNumericColumn(m_Data, m_IntIndex))));
      m_LabelStd.setText("Std dev: " + df.format(StatUtils.stddev(SpreadSheetUtils.getNumericColumn(m_Data, m_IntIndex), false)));

      m_Paintlet.setIndex(m_IntIndex);
      m_Paintlet.setData(m_Data);
      m_Paintlet.configureAxes();
      m_Paintlet.calculateDimensions();
      m_CheckBoxLine.setEnabled(m_Paintlet.hasFitLine());
      if(m_Paintlet.hasFitLine())
	m_Paintlet.setLine(m_Line);
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
    removePaintlet(m_Paintlet);
    m_Paintlet = (AbstractProbabilityPaintlet)m_PanelRegression.getCurrent();
    m_Paintlet.setPanel(this);
    m_Paintlet.configureAxes();
    m_Paintlet.setIndex(m_IntIndex);
    m_Paintlet.setData(m_Data);
    m_CheckBoxLine.setEnabled(m_Paintlet.hasFitLine());
    if(m_Paintlet.hasFitLine()) {
      m_Paintlet.setLine(m_Line);
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
    m_Paintlet.setLine(m_Line);
    update();
  }

  /**
   * Set whether the grid overlay is displayed.
   *
   * @param val		true if to display
   */
  public void setGrid(boolean val) {
    m_Grid = val;
    m_CheckBoxGrid.setSelected(val);
    changeGrid();
  }

  /**
   * Returns whether the grid is displayed.
   *
   * @return		true if displayed
   */
  public boolean getGrid() {
    return m_Grid;
  }

  /**
   * Set whether the regression linear line is displayed.
   *
   * @param val		true if to display
   */
  public void setRegressionLine(boolean val) {
    m_Line = val;
    m_CheckBoxLine.setSelected(val);
    update();
  }

  /**
   * Returns whether the regression line is displayed.
   *
   * @return		true if displayed
   */
  public boolean getRegressionLine() {
    return m_Line;
  }

  /**
   * Set the index to use to choose the attribute to display
   * @param val		Index of attribute
   */
  public void setAttIndex(Index val) {
    m_Index = val;
    update();
  }

  /**
   * Returns the index of the attribute to display.
   *
   * @return		the index
   */
  public Index getAttIndex() {
    return m_Index;
  }

  /**
   * Set the regular expression for chossing the attribute to display.
   *
   * @param val		Regular expression for name of attribute
   */
  public void setAttRegExp(BaseRegExp val) {
    m_AttReg = val;
    update();
  }

  /**
   * Returns the regular expression for choosing the attribute to display.
   *
   * @return		the expression
   */
  public BaseRegExp getAttRegExp() {
    return m_AttReg;
  }
}