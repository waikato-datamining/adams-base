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
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

  /** attributes that can be chosen */
  protected DefaultListModel m_Attributes;

  /** attributes that have been chosen */
  protected DefaultListModel m_Chosen;

  /** Panel for displaying box plots */
  protected JPanel m_Centre;

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
  protected JSpinner m_WidthSpin;

  /** Spinner to choose the height of each box plot */
  protected JSpinner m_HeightSpin;

  /** spinner to choose the axis width of each box plot */
  protected JSpinner m_AxisWidSpin;

  /** select whether graphs have the same axis */
  protected JCheckBox m_SameAxis;

  /** Range of box plots to display initially */
  protected Range m_Range;

  /** Button to add selected attribute to chosen attributes list */
  protected JButton m_Add;

  /** Button to add all attributes to chosen attributes list */
  protected JButton m_AddAll;

  /** Object that contains a list and a group of buttons for choosing attributes */
  protected BaseListWithButtons m_AttributesDis;

  /** Object that contains a list and a group of buttons for removing attributes */
  protected BaseListWithButtons m_ChosenDis;

  /** Button to remove a selected attribute from chosen attributes list */
  protected JButton m_Remove;

  /** Button to remove all attributes from the chosen attributes list */
  protected JButton m_RemoveAll;

  /** Object that contains a set of label and component objects, aids displaying */
  protected ParameterPanel m_PanelParams;

  /** Spinner for choosing the number of box plots to be displayed horizontally */
  protected JSpinner m_GridSpin;

  /** Check box to choose if boxes should b filled */
  protected JCheckBox m_FillCheck;

  /** Button to choose color of boxes */
  protected JButton m_Colorbutton;

  @Override
  protected void initialize() {
    super.initialize();
    m_Width = 200;
    m_Height = 200;
    m_AxisWidth = 60;
    m_NumHorizontal = -1;
  }

  /**
   * Constructor
   */
  public BoxPlotManager() {
    super(new BorderLayout());
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
    m_Add.setEnabled(m_AttributesDis.getSelectedIndex() != -1);
    m_AddAll.setEnabled(m_Attributes.size() != 0);
    m_Remove.setEnabled(m_ChosenDis.getSelectedIndex() != -1);
    m_RemoveAll.setEnabled(m_Chosen.size() != 0);
  }

  /**
   * Initializes the gui
   */
  @Override
  protected void initGUI() {
    // contains plot and options
    BaseSplitPane splitPane;
    splitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_Centre = new JPanel(new BorderLayout());
    splitPane.setRightComponent(m_Centre);
    add(splitPane, BorderLayout.CENTER);
    splitPane.setResizeWeight(0.3);
    splitPane.setOneTouchExpandable(true);
    m_Attributes = new DefaultListModel();
    m_Chosen = new DefaultListModel();

    // west panel for choosing attributes, sizes and axis scale
    JPanel west = new JPanel(new BorderLayout());
    west.setPreferredSize(new Dimension(m_WidthLeft, 1000));

    // north panel on west panel
    JPanel top = new JPanel(new BorderLayout());
    m_AttributesDis = new BaseListWithButtons();
    m_AttributesDis.setPreferredSize(new Dimension(m_WidthLeft, 140));
    m_AttributesDis.setModel(m_Attributes);
    top.add(m_AttributesDis, BorderLayout.NORTH);
    m_Add = new JButton("Add");
    m_Add.setMnemonic('A');
    m_Add.setEnabled(false);
    // add listener for add button
    m_Add.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addClicked();
      }
    });
    m_AddAll = new JButton("Add all");
    m_AddAll.setMnemonic('d');
    // add listener for add all button
    m_AddAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addAllClicked();
      }
    });
    m_AttributesDis.setBorder(BorderFactory.createTitledBorder("Attributes"));
    m_AttributesDis.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
      }
    });
    m_Add.setPreferredSize(new Dimension(110, 30));
    m_AddAll.setPreferredSize(new Dimension(110, 30));
    m_AttributesDis.addToButtonsPanel(m_Add);
    m_AttributesDis.addToButtonsPanel(m_AddAll);

    // South panel on west panel
    JPanel bottom = new JPanel(new BorderLayout());
    m_ChosenDis = new BaseListWithButtons();
    m_ChosenDis.setPreferredSize(new Dimension(m_WidthLeft, 140));
    m_ChosenDis.setModel(m_Chosen);
    bottom.add(m_ChosenDis, BorderLayout.CENTER);
    m_Remove = new JButton("Remove");
    m_Remove.setMnemonic('R');
    m_Remove.setEnabled(false);
    // add listener for remove button
    m_Remove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeClicked();
      }
    });
    m_RemoveAll = new JButton("Remove all");
    m_RemoveAll.setMnemonic('m');
    m_RemoveAll.setEnabled(false);
    // add listener for remove all button
    m_RemoveAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeAllClicked();
      }
    });
    m_ChosenDis.setBorder(BorderFactory.createTitledBorder("Chosen attributes"));
    m_ChosenDis.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
      }
    });
    m_Remove.setPreferredSize(new Dimension(110, 30));
    m_RemoveAll.setPreferredSize(new Dimension(110, 30));
    m_ChosenDis.addToButtonsPanel(m_RemoveAll);
    m_ChosenDis.addToButtonsPanel(m_Remove);

    // middle panel
    m_PanelParams = new ParameterPanel();
    JPanel centre = new JPanel(new BorderLayout());
    centre.setPreferredSize(new Dimension(m_WidthLeft, 350));
    SpinnerModel spinHeight = new SpinnerNumberModel(m_Height, 50, 500, 2);
    SpinnerModel spinWidth = new SpinnerNumberModel(m_Width, 50, 500, 2);
    SpinnerModel spinAxis = new SpinnerNumberModel(m_AxisWidth, 20, 300, 2);
    SpinnerModel spinGrid = new SpinnerNumberModel(m_NumHorizontal, -1, null, 1);
    m_WidthSpin = new JSpinner(spinWidth);
    m_HeightSpin = new JSpinner(spinHeight);
    m_AxisWidSpin = new JSpinner(spinAxis);
    m_GridSpin = new JSpinner(spinGrid);
    m_WidthSpin.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
	spinWidthChange(arg0);
      }
    });
    m_HeightSpin.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
	spinHeightChange(arg0);
      }
    });
    m_AxisWidSpin.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
	spinAxisChange(arg0);
      }
    });
    m_GridSpin.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	spinHorizontalChange(e);
      }
    });
    m_SameAxis = new JCheckBox();
    m_SameAxis.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent arg0) {
	sameAxisChange(arg0);
      }
    });

    m_FillCheck = new JCheckBox();
    m_FillCheck.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
	fillChange(e);
      }
    });

    m_Colorbutton = new JButton("color");
    m_Colorbutton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
	colorChange();
      }
    });

    m_PanelParams.addParameter("Width of plot", m_WidthSpin);
    m_PanelParams.addParameter("Height of plot", m_HeightSpin);
    m_PanelParams.addParameter("Width of axis", m_AxisWidSpin);
    m_PanelParams.addParameter("Number in each row", m_GridSpin);
    m_PanelParams.addParameter("Use same axis", m_SameAxis);
    m_PanelParams.addParameter("Fill box", m_FillCheck);
    m_PanelParams.addParameter("Change color", m_Colorbutton);
    centre.add(m_PanelParams, BorderLayout.CENTER);

    // add panels to west panel
    west.add(top, BorderLayout.NORTH);
    JPanel panel2 = new JPanel(new BorderLayout());
    west.add(panel2, BorderLayout.CENTER);
    panel2.add(centre, BorderLayout.NORTH);
    JPanel panel3 = new JPanel(new BorderLayout());
    panel2.add(panel3, BorderLayout.CENTER);
    panel3.add(bottom, BorderLayout.NORTH);
    // add west panel to main panel
    splitPane.setLeftComponent(west);
    
    setPreferredSize(new Dimension(1000, 700));
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
   * Fill boxes check box is changed
   * 
   * @param val
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
    m_SameAxis.setSelected(m_AxisSame);
    m_AxisWidSpin.setValue(m_AxisWidth);
    m_HeightSpin.setValue(m_Height);
    m_GridSpin.setValue(m_NumHorizontal);
    m_WidthSpin.setValue(m_Width);
    m_FillCheck.setSelected(m_Fill);
    if (m_Data != null)
      update();
    updateButtons();
  }

  /**
   * Called when the fields set from the class that created this boxplotmanager
   * object
   */
  public void reset() {
	  m_Chosen.removeAllElements();
	  updateGui();
    // add attributes to list, including class attribute
    for (int g = 0; g < m_Data.getColumnCount(); g++) {
      m_Attributes.addElement(m_Data.getColumnName(g));
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
	chosen.add((String) m_Attributes.get(t));
      }
      for (String s: chosen) {
	m_Chosen.addElement(s);
	m_Attributes.removeElement(s);
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
    if (m_AttributesDis.getSelectedIndex() == -1)
      return;
    int[] indices = m_AttributesDis.getSelectedIndices();
    // takes from attributes list and puts in chosen list
    Arrays.sort(indices);
    for (int index: indices) {
      m_Chosen.addElement(m_AttributesDis.getModel().getElementAt(index));
    }
    for (int i = indices.length - 1; i >= 0; i--) {
      m_Attributes.remove(indices[i]);
    }
    updateButtons();
    update();
  }

  /**
   * removes attribute from selected list and places in attribute list
   */
  protected void removeClicked() {
    // if no attribute selected
    if (m_ChosenDis.getSelectedIndex() == -1)
      return;
    // takes from chosen list, places in attributes list
    int[] indices = m_ChosenDis.getSelectedIndices();
    Arrays.sort(indices);
    for (int index: indices) {
      m_Attributes.addElement(m_ChosenDis.getModel().getElementAt(index));
    }
    for (int i = indices.length - 1; i >= 0; i--) {
      m_Chosen.remove(indices[i]);
    }
    updateButtons();
    update();
  }

  /**
   * removes all attributes from the chosen attribute list
   */
  protected void removeAllClicked() {
    m_Attributes.removeAllElements();
    m_Chosen.removeAllElements();
    for (int g = 0; g < m_Data.getColumnCount(); g++) {
      m_Attributes.addElement(m_Data.getColumnName(g));
    }
    updateButtons();
    update();
  }

  /**
   * Displays box plots for all attributes
   */
  protected void addAllClicked() {
    m_Attributes.removeAllElements();
    m_Chosen.removeAllElements();
    for (int g = 0; g < m_Data.getColumnCount(); g++) {
      m_Chosen.addElement(m_Data.getColumnName(g));
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
   * Set the color to fill the boxes with
   * 
   * @param val
   *          Color to fill
   */
  public void setColor(Color val) {
    m_Color = val;
  }

  /**
   * Update the display with box plots required
   */
  protected void update() {
    int numGraphs = m_Chosen.size();
    if (numGraphs != 0) {
      // grid for displaying the rows of box plots
      JPanel grid;
      if (m_NumHorizontal == 0 || m_NumHorizontal == -1)
	grid = new JPanel(new GridLayout(1, 0));
      else
	grid = new JPanel(new GridLayout(0, m_NumHorizontal));
      // remove existing boxplots
      m_Centre.removeAll();
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
    		  for(int j = 0; j < m_Chosen.size(); j++) {
    			  chosen = m_Chosen.get(j).toString();
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
      m_Centre.add(holdScroll, BorderLayout.CENTER);
      // displaying each of the graphs
      for (int r = 0; r < m_Chosen.size(); r++) {
	String toPlot = (String) m_Chosen.getElementAt(r);
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
      m_Centre.removeAll();
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