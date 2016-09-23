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
 * BoxPlotManager.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.boxplot;

import adams.core.Range;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.StatUtils;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ParameterPanel;
import adams.gui.event.PaintEvent;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.event.PaintListener;
import adams.gui.visualization.core.plot.Axis;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that displays box plots for a number of attributes
 *
 * @author msf8
 * @version $Revision$
 *
 */
public class BoxPlotManager
  extends BasePanel
  implements PaintListener {

  /** for serialization */
  private static final long serialVersionUID = -7912792208597490973L;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** attributes that can be chosen */
  protected DefaultListModel<String> m_ModelAvailableAttributes;

  /** attributes that have been chosen */
  protected DefaultListModel<String> m_ModelChosenAttributes;

  /** Panel for displaying box plots */
  protected JPanel m_PanelRight;

  /** Instances to be graphed */
  protected SpreadSheet m_Data;

  /** width of left hand panel of the box plot manager */
  protected int m_WidthLeft;

  /** width of graphs drawn */
  protected int m_Width;

  /** Height of graphs drawn */
  protected int m_Height;

  /** width of the axis of each box plot */
  protected int m_AxisWidth;

  /** Number of box plots to display horizontally */
  protected int m_NumHorizontal;

  /** whether the axis should have the same scale */
  protected boolean m_AxisSame;

  /** Color to fill boxes with */
  protected Color m_Color;

  /** Whether boxes should be filled */
  protected boolean m_Fill;

  /** spinner to choose the width of each box plot */
  protected JSpinner m_SpinnerWidth;

  /** Spinner to choose the height of each box plot */
  protected JSpinner m_SpinnerHeight;

  /** spinner to choose the axis width of each box plot */
  protected JSpinner m_SpinnerAxisWid;

  /** select whether graphs have the same axis */
  protected JCheckBox m_CheckBoxSameAxis;

  /** Range of box plots to display initially */
  protected Range m_Range;

  /** Button to add selected attribute to chosen attributes list */
  protected JButton m_ButtonAdd;

  /** Button to add all attributes to chosen attributes list */
  protected JButton m_ButtonAddAll;

  /** Object that contains a list and a group of buttons for choosing attributes */
  protected BaseListWithButtons m_ListAvailableAttributes;

  /** Object that contains a list and a group of buttons for removing attributes */
  protected BaseListWithButtons m_ListChosenAttributes;

  /** Button to remove a selected attribute from chosen attributes list */
  protected JButton m_ButtonRemove;

  /** Button to remove all attributes from the chosen attributes list */
  protected JButton m_ButtonRemoveAll;

  /** Object that contains a set of label and component objects, aids displaying */
  protected ParameterPanel m_PanelParams;

  /** Spinner for choosing the number of box plots to be displayed horizontally */
  protected JSpinner m_SpinnerGrid;

  /** Check box to choose if boxes should b filled */
  protected JCheckBox m_CheckBoxFill;

  /** Button to choose color of boxes */
  protected JButton m_ButtonFillColor;

  @Override
  protected void initialize() {
    super.initialize();
    m_Width = 200;
    m_Height = 200;
    m_AxisWidth = 60;
    m_NumHorizontal = 3;
    m_ModelAvailableAttributes = new DefaultListModel<>();
    m_ModelChosenAttributes = new DefaultListModel<>();
  }

  /**
   * Initializes the gui
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    // contains plot and options
    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_PanelRight = new JPanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PanelRight);
    add(m_SplitPane, BorderLayout.CENTER);
    m_SplitPane.setResizeWeight(0.3);
    m_SplitPane.setOneTouchExpandable(true);

    // left panel for choosing attributes, sizes and axis scale
    JPanel panelLeft = new JPanel(new BorderLayout());
    m_SplitPane.setLeftComponent(panelLeft);

    // north panel on left panel
    JPanel top = new JPanel(new BorderLayout());
    m_ListAvailableAttributes = new BaseListWithButtons();
    m_ListAvailableAttributes.setPreferredSize(new Dimension(m_WidthLeft, 140));
    m_ListAvailableAttributes.setModel(m_ModelAvailableAttributes);
    top.add(m_ListAvailableAttributes, BorderLayout.NORTH);
    m_ButtonAdd = new JButton("Add");
    m_ButtonAdd.setMnemonic('A');
    m_ButtonAdd.setEnabled(false);
    // add listener for add button
    m_ButtonAdd.addActionListener((ActionEvent e) -> addClicked());
    m_ButtonAddAll = new JButton("Add all");
    m_ButtonAddAll.setMnemonic('d');
    // add listener for add all button
    m_ButtonAddAll.addActionListener((ActionEvent e) -> addAllClicked());
    m_ListAvailableAttributes.setBorder(BorderFactory.createTitledBorder("Attributes"));
    m_ListAvailableAttributes.addListSelectionListener((ListSelectionEvent e) -> updateButtons());
    m_ListAvailableAttributes.addToButtonsPanel(m_ButtonAdd);
    m_ListAvailableAttributes.addToButtonsPanel(m_ButtonAddAll);

    // South panel on left panel
    JPanel bottom = new JPanel(new BorderLayout());
    m_ListChosenAttributes = new BaseListWithButtons();
    m_ListChosenAttributes.setPreferredSize(new Dimension(m_WidthLeft, 140));
    m_ListChosenAttributes.setModel(m_ModelChosenAttributes);
    bottom.add(m_ListChosenAttributes, BorderLayout.CENTER);
    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.setMnemonic('R');
    m_ButtonRemove.setEnabled(false);
    // add listener for remove button
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeClicked());
    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.setMnemonic('m');
    m_ButtonRemoveAll.setEnabled(false);
    // add listener for remove all button
    m_ButtonRemoveAll.addActionListener((ActionEvent e) -> removeAllClicked());
    m_ListChosenAttributes.setBorder(BorderFactory.createTitledBorder("Chosen attributes"));
    m_ListChosenAttributes.addListSelectionListener((ListSelectionEvent e) -> updateButtons());
    m_ListChosenAttributes.addToButtonsPanel(m_ButtonRemoveAll);
    m_ListChosenAttributes.addToButtonsPanel(m_ButtonRemove);

    // middle panel
    m_PanelParams = new ParameterPanel();
    JPanel centre = new JPanel(new BorderLayout());
    SpinnerModel spinHeight = new SpinnerNumberModel(m_Height, 50, 500, 2);
    SpinnerModel spinWidth = new SpinnerNumberModel(m_Width, 50, 500, 2);
    SpinnerModel spinAxis = new SpinnerNumberModel(m_AxisWidth, 20, 300, 2);
    SpinnerModel spinGrid = new SpinnerNumberModel(m_NumHorizontal, -1, null, 1);
    m_SpinnerWidth = new JSpinner(spinWidth);
    m_SpinnerHeight = new JSpinner(spinHeight);
    m_SpinnerAxisWid = new JSpinner(spinAxis);
    m_SpinnerGrid = new JSpinner(spinGrid);
    m_SpinnerWidth.addChangeListener((ChangeEvent e) -> spinWidthChange(e));
    m_SpinnerHeight.addChangeListener((ChangeEvent e) -> spinHeightChange(e));
    m_SpinnerAxisWid.addChangeListener((ChangeEvent e) -> spinAxisChange(e));
    m_SpinnerGrid.addChangeListener((ChangeEvent e) -> spinHorizontalChange(e));
    m_CheckBoxSameAxis = new JCheckBox();
    m_CheckBoxSameAxis.addItemListener((ItemEvent e) -> sameAxisChange(e));

    m_CheckBoxFill = new JCheckBox();
    m_CheckBoxFill.addItemListener((ItemEvent e) -> fillChange(e));

    m_ButtonFillColor = new JButton("Choose");
    m_ButtonFillColor.addActionListener((ActionEvent e) -> colorChange());

    m_PanelParams.addParameter("Width of plot", m_SpinnerWidth);
    m_PanelParams.addParameter("Height of plot", m_SpinnerHeight);
    m_PanelParams.addParameter("Width of axis", m_SpinnerAxisWid);
    m_PanelParams.addParameter("Number in each row", m_SpinnerGrid);
    m_PanelParams.addParameter("Use same axis", m_CheckBoxSameAxis);
    m_PanelParams.addParameter("Fill box", m_CheckBoxFill);
    m_PanelParams.addParameter("Fill color", m_ButtonFillColor);
    centre.add(m_PanelParams, BorderLayout.CENTER);

    // add panels to left panel
    panelLeft.add(top, BorderLayout.NORTH);
    JPanel panel2 = new JPanel(new BorderLayout());
    panelLeft.add(panel2, BorderLayout.CENTER);
    panel2.add(centre, BorderLayout.NORTH);
    JPanel panel3 = new JPanel(new BorderLayout());
    panel2.add(panel3, BorderLayout.CENTER);
    panel3.add(bottom, BorderLayout.NORTH);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    updateButtons();
  }

  /**
   * sets the instances to be displayed
   *
   * @param inst
   *          Instances object for displaying
   */
  public void setData(SpreadSheet inst) {
    m_Data = inst;
    updateButtons();
  }

  /**
   * Updates whether each of the buttons can be pressed depending on the
   * selected list objects
   */
  protected void updateButtons() {
    m_ButtonAdd.setEnabled(m_ListAvailableAttributes.getSelectedIndex() != -1);
    m_ButtonAddAll.setEnabled(m_ModelAvailableAttributes.size() != 0);
    m_ButtonRemove.setEnabled(m_ListChosenAttributes.getSelectedIndex() != -1);
    m_ButtonRemoveAll.setEnabled(m_ModelChosenAttributes.size() != 0);
  }

  /**
   * Fill boxes check box is changed
   *
   * @param val		the event
   */
  protected void fillChange(ItemEvent val) {
    m_Fill = ((JCheckBox) val.getSource()).isSelected();
    update();
  }

  /**
   * Color for box plots changes
   */
  protected void colorChange() {
    Color newCol = JColorChooser.showDialog(BoxPlotManager.this,
      "Choose box color", m_Color);
    if (newCol != null) {
      m_Color = newCol;
    }
    update();
  }

  /**
   * Updates the state of gui components after options have been set
   */
  protected void updateGui() {
    m_CheckBoxSameAxis.setSelected(m_AxisSame);
    m_SpinnerAxisWid.setValue(m_AxisWidth);
    m_SpinnerHeight.setValue(m_Height);
    m_SpinnerGrid.setValue(m_NumHorizontal);
    m_SpinnerWidth.setValue(m_Width);
    m_CheckBoxFill.setSelected(m_Fill);
    if (m_Data != null)
      update();
    updateButtons();
  }

  /**
   * Called when the fields set from the class that created this boxplotmanager
   * object
   */
  public void reset() {
    m_ModelChosenAttributes.removeAllElements();
    updateGui();
    // add attributes to list, including class attribute
    for (int g = 0; g < m_Data.getColumnCount(); g++) {
      m_ModelAvailableAttributes.addElement(m_Data.getColumnName(g));
    }
    // If options have been set specifying the initial box plots to display
    // creates an arraylist of the indices of box attributes to display
    if (m_Range != null) {
      ArrayList<Integer> indices = new ArrayList<>();
      m_Range.setMax(m_Data.getColumnCount());
      for (int t = 0; t < m_Data.getRowCount(); t++) {
	if (m_Range.isInRange(t)) {
	  indices.add(t);
	}
      }
      ArrayList<String> chosen = new ArrayList<>();
      for (int t: indices) {
	chosen.add(m_ModelAvailableAttributes.get(t));
      }
      for (String s: chosen) {
	m_ModelChosenAttributes.addElement(s);
	m_ModelAvailableAttributes.removeElement(s);
      }
    }
    update();
    updateButtons();
  }

  /**
   * adds attributes to selected attributes list
   */
  protected void addClicked() {
    // if no attribute selected
    if (m_ListAvailableAttributes.getSelectedIndex() == -1)
      return;
    int[] indices = m_ListAvailableAttributes.getSelectedIndices();
    // takes from attributes list and puts in chosen list
    Arrays.sort(indices);
    for (int index: indices) {
      m_ModelChosenAttributes.addElement((String) m_ListAvailableAttributes.getModel().getElementAt(index));
    }
    for (int i = indices.length - 1; i >= 0; i--) {
      m_ModelAvailableAttributes.remove(indices[i]);
    }
    updateButtons();
    update();
  }

  /**
   * removes attribute from selected list and places in attribute list
   */
  protected void removeClicked() {
    // if no attribute selected
    if (m_ListChosenAttributes.getSelectedIndex() == -1)
      return;
    // takes from chosen list, places in attributes list
    int[] indices = m_ListChosenAttributes.getSelectedIndices();
    Arrays.sort(indices);
    for (int index: indices) {
      m_ModelAvailableAttributes.addElement((String) m_ListChosenAttributes.getModel().getElementAt(index));
    }
    for (int i = indices.length - 1; i >= 0; i--) {
      m_ModelChosenAttributes.remove(indices[i]);
    }
    updateButtons();
    update();
  }

  /**
   * removes all attributes from the chosen attribute list
   */
  protected void removeAllClicked() {
    m_ModelAvailableAttributes.removeAllElements();
    m_ModelChosenAttributes.removeAllElements();
    for (int g = 0; g < m_Data.getColumnCount(); g++) {
      m_ModelAvailableAttributes.addElement(m_Data.getColumnName(g));
    }
    updateButtons();
    update();
  }

  /**
   * Displays box plots for all attributes
   */
  protected void addAllClicked() {
    m_ModelAvailableAttributes.removeAllElements();
    m_ModelChosenAttributes.removeAllElements();
    for (int g = 0; g < m_Data.getColumnCount(); g++) {
      m_ModelChosenAttributes.addElement(m_Data.getColumnName(g));
    }
    updateButtons();
    update();
  }

  /**
   * Changes the width of the box plots
   *
   * @param e
   *          The event that occurred
   */
  protected void spinWidthChange(ChangeEvent e) {
    JSpinner spin = (JSpinner) e.getSource();
    m_Width = (Integer) spin.getModel().getValue();
    if (m_Data != null)
      update();
  }

  /**
   * Changes the height of the box plots
   *
   * @param e
   *          The event that occurred
   */
  protected void spinHeightChange(ChangeEvent e) {
    JSpinner spin = (JSpinner) e.getSource();
    m_Height = (Integer) spin.getModel().getValue();
    if (m_Data != null)
      update();
  }

  /**
   * Changes the width of the left axis on each box plot
   *
   * @param e
   *          The event that occurred
   */
  protected void spinAxisChange(ChangeEvent e) {
    JSpinner spin = (JSpinner) e.getSource();
    m_AxisWidth = (Integer) spin.getModel().getValue();
    if (m_Data != null)
      update();
  }

  /**
   * Change the number of box plots to be displayed in each row
   *
   * @param e
   *          The event that occured
   */
  protected void spinHorizontalChange(ChangeEvent e) {
    JSpinner spin = (JSpinner) e.getSource();
    m_NumHorizontal = (Integer) spin.getModel().getValue();
    if (m_Data != null)
      update();
  }

  /**
   * Sets the axis scale to be the same for each box plot
   *
   * @param e
   *          The event that occured
   */
  protected void sameAxisChange(ItemEvent e) {
    m_AxisSame = (e.getStateChange() == ItemEvent.SELECTED);
    if (m_Data != null)
      update();

  }

  /**
   * Set the width of each box plot
   *
   * @param val
   *          Width in pixels
   */
  public void setBoxWidth(int val) {
    m_Width = val;
  }

  /**
   * Get the width of each box plot drawn
   *
   * @return Width in pixels
   */
  public int getBoxWidth() {
    return m_Width;
  }

  /**
   * Set the height of each box plot drawn
   *
   * @param val
   *          Height in pixels
   */
  public void setBoxHeight(int val) {
    m_Height = val;
  }

  /**
   * Get the height of each box plot
   *
   * @return Height in pixels
   */
  public int getBoxHeight() {
    return m_Height;
  }

  /**
   * Set the width of the left axis for each box plot
   *
   * @param val
   *          Width in pixels
   */
  public void setAxisWidth(int val) {
    m_AxisWidth = val;
  }

  /**
   * Get the width of the left axis for each boxplot
   *
   * @return Width in pixels
   */
  public int getAxisWidth() {
    return m_AxisWidth;
  }

  /**
   * Set the number of box plots to display on each row
   *
   * @param val
   *          number on each row
   */
  public void setNumHorizontal(int val) {
    m_NumHorizontal = val;
  }

  /**
   * Get the number of box plots to display on each row
   *
   * @return Number in each row
   */
  public int getNumHorizontal() {
    return m_NumHorizontal;
  }

  /**
   * Set whether each box plot should have the same axis scale
   *
   * @param val
   *          true if same axis
   */
  public void setSameAxis(boolean val) {
    m_AxisSame = val;
  }

  /**
   * get whether the box plots should all use the same axis scale
   *
   * @return true if they use the same axis scale
   */
  public boolean getSameAxis() {
    return m_AxisSame;
  }

  /**
   * Set the range of box plots to display initially
   *
   * @param val
   *          Range object containing range of attributes
   */
  public void setRange(Range val) {
    m_Range = val;
  }

  /**
   * Get the range of box plots to be displayed initially
   *
   * @return The range of box plots to display
   */
  public Range getRange() {
    return m_Range;
  }

  /**
   * Set whether the boxes should be filled with color
   *
   * @param val
   *          True if filled
   */
  public void setFill(boolean val) {
    m_Fill = val;
  }

  /**
   * Returns whether the boxes should be filled with color.
   *
   * @return		true if filled
   */
  public boolean getFill() {
    return m_Fill;
  }

  /**
   * Set the color to fill the boxes with
   *
   * @param val
   *          Color to fill
   */
  public void setColor(Color val) {
    m_Color = val;
  }

  /**
   * Returns the color to fill the boxes with.
   *
   * @return		the fill color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Update the display with box plots required
   */
  protected void update() {
    int numGraphs = m_ModelChosenAttributes.size();
    if (numGraphs != 0) {
      // grid for displaying the rows of box plots
      JPanel grid;
      if (m_NumHorizontal == 0 || m_NumHorizontal == -1)
	grid = new JPanel(new GridLayout(1, 0));
      else
	grid = new JPanel(new GridLayout(0, m_NumHorizontal));
      // remove existing boxplots
      m_PanelRight.removeAll();
      // scroll pane for the display grid
      BaseScrollPane scrollPane;
      scrollPane = new BaseScrollPane(grid);
      // dimensions of each box plot determined by the size chosen
      Dimension dim = new Dimension(m_Width, m_Height);
      // boundary values for displaying all graphs with same axis scales
      Double max = null;
      Double min = null;
      // if all graphs to be drawn with same axis
      //This has been changed to use max/min of attributes chosen, not all attributes.
      if (m_AxisSame) {
	boolean contains;
	String chosen;
	String inst;

	for (int i = 0; i < m_Data.getColumnCount(); i++) {
	  contains = false;
	  for(int j = 0; j < m_ModelChosenAttributes.size(); j++) {
	    chosen = m_ModelChosenAttributes.get(j);
	    inst = m_Data.getColumnName(i);
	    if(chosen.equals(inst)) {
	      contains = true;
	      break;
	    }
	  }
	  if(contains) {
	    double[] data = SpreadSheetUtils.getNumericColumn(m_Data, i);
	    double tempMax = StatUtils.max(data);
	    double tempMin = StatUtils.min(data);
	    if (max == null)
	      max = tempMax;
	    if (min == null)
	      min = tempMin;
	    if (tempMax > max)
	      max = tempMax;
	    if (tempMin < min)
	      min = tempMin;
	  }
	}

	//old version
	// finding max and min values
	//	for (int i = 0; i < m_Instances.getColumnCount(); i++) {
	//	  double[] data = m_Instances.attributeToDoubleArray(i);
	//	  double tempMax = StatUtils.max(data);
	//	  double tempMin = StatUtils.min(data);
	//	  if (max == null)
	//	    max = tempMax;
	//	  if (min == null)
	//	    min = tempMin;
	//	  if (tempMax > max)
	//	    max = tempMax;
	//	  if (tempMin < min)
	//	    min = tempMin;
	//	}
      }
      // Jpanel with flowlayout for the scrollpane
      JPanel scrollHold = new JPanel();
      scrollHold.add(scrollPane);
      // scrollpane to hold the new panel if larger than the available area
      BaseScrollPane holdScroll = new BaseScrollPane(scrollHold);
      m_PanelRight.add(holdScroll, BorderLayout.CENTER);
      // displaying each of the graphs
      for (int r = 0; r < m_ModelChosenAttributes.size(); r++) {
	String toPlot = m_ModelChosenAttributes.getElementAt(r);
	BoxPlotGraph graph = new BoxPlotGraph();
	graph.pass(m_Data, toPlot);
	graph.setPreferredSize(dim);
	// set axis the same if required
	if (m_AxisSame) {
	  graph.axisSame(max, min);
	}
	graph.setColor(m_Color);
	graph.setFill(m_Fill);
	graph.setAxisWidth(Axis.LEFT, m_AxisWidth);
	graph.addPaintListener(this);
	JPanel graphPanel = new JPanel(new BorderLayout());
	JLabel title = new JLabel(toPlot, null, JLabel.CENTER);
	graphPanel.add(title, BorderLayout.NORTH);
	graphPanel.add(graph, BorderLayout.CENTER);
	// need to put a border around the panel so they can be separated easily
	graphPanel.setBorder(BorderFactory.createLineBorder(Color.black));
	grid.add(graphPanel);
      }
    }
    else
      m_PanelRight.removeAll();
    repaint();
    revalidate();
  }

  /**
   * Controls the painting of the box plots
   */
  public void painted(PaintEvent e) {
    Graphics g;
    // graphics object for central panel
    g = e.getGraphics();
    // graph being painted
    BoxPlotGraph graph = (BoxPlotGraph) e.getSource();
    // paint the specific graph on the graphics component
    if (e.getPaintMoment() == PaintMoment.PAINT)
      graph.paintPlot(g);
  }
}