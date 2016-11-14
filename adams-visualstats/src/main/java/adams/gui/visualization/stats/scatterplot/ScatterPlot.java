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
 * ScatterPlot.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericArrayEditorPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.container.DataHelper;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.core.IndexHelper;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCircle;
import adams.gui.visualization.stats.scatterplot.action.MouseClickAction;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * create a paintable panel displaying a scatter plot
 * panel as well as combo boxes for choosing attributes.
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterPlot
  extends AbstractScatterPlot
  implements PopupMenuCustomizer  {

  /** for serialization */
  private static final long serialVersionUID = -7798200657432959204L;

  /**
   * Listener for when the y attribute JComboBox selection changes.
   *
   * @author msf8
   */
  protected class AttYListener implements ItemListener {

    protected ScatterPlot m_parent;

    public AttYListener(ScatterPlot parent) {
      m_parent = parent;
    }

    public void itemStateChanged(ItemEvent e) {
      SpreadSheet data = m_parent.getData();
      if (e.getStateChange() == ItemEvent.SELECTED) {
	String chose = (String) e.getItem();
	//finds position of attribute
	for (int t = 0; t < data.getColumnCount(); t++) {
	  if (data.getColumnName(t).equals(chose)) {
	    for (int i = 0; i< m_Overlays.length; i++)
	      m_Overlays[i].getPaintlet().setCalculated(false);
	    m_YIntIndex = t;
	    change();
	    break;
	  }
	}
      }
    }
  }

  /**
   * Listener for when the x attribute JComboBox selection changes.
   *
   * @author msf8
   */
  protected class AttXListener implements ItemListener {

    protected ScatterPlot m_parent;

    public AttXListener(ScatterPlot parent) {
      m_parent = parent;
    }

    public void itemStateChanged(ItemEvent e) {
      SpreadSheet data = m_parent.getData();
      if (e.getStateChange() == ItemEvent.SELECTED) {
	String chose = (String) e.getItem();
	//finds position of attribute
	for (int t = 0; t< data.getColumnCount(); t++) {
	  if (data.getColumnName(t).equals(chose)) {
	    for (int i = 0; i < m_Overlays.length; i++)
	      m_Overlays[i].getPaintlet().setCalculated(false);
	    m_XIntIndex = t;
	    change();
	    break;
	  }
	}
      }
    }
  }

  /**
   * Listener for when the color attribute JComboBox selection changes
   * @author fracpete
   */
  protected class AttColorListener implements ItemListener {

    protected ScatterPlot m_parent;

    public AttColorListener(ScatterPlot parent) {
      m_parent = parent;
    }

    public void itemStateChanged(ItemEvent e) {
      SpreadSheet data = m_parent.getData();
      if (e.getStateChange() == ItemEvent.SELECTED) {
	m_ColorIntIndex = -1;
	String chose = (String) e.getItem();
	//finds position of attribute
	for (int t = 0; t < data.getColumnCount(); t++) {
	  if (data.getColumnName(t).equals(chose)) {
	    for (int i = 0; i < m_Overlays.length; i++)
	      m_Overlays[i].getPaintlet().setCalculated(false);
	    m_ColorIntIndex = t;
	    break;
	  }
	}
	change();
      }
    }
  }

  /**Regular expression given as an option to choose x attribute */
  protected BaseRegExp m_XIndexReg;

  /**Index object given as an option to choose x object */
  protected Index m_XIndex;

  /**regular expression given as an option to choose y attribute */
  protected BaseRegExp m_YIndexReg;

  /**Index given as an option to choose y attribute */
  protected Index m_YIndex;

  /**regular expression given as an option to choose color attribute */
  protected BaseRegExp m_ColorIndexReg;

  /**Index given as an option to choose color attribute */
  protected Index m_ColorIndex;

  /** the index of the color attribute. */
  protected int m_ColorIntIndex;

  /**For choosing attribute to display on x axis */
  protected JComboBox<String> m_ComboBoxY;

  /**For choosing attribute to display on y axis */
  protected JComboBox<String> m_ComboBoxX;

  /**For choosing attribute to use for color. */
  protected JComboBox<String> m_ComboBoxColor;

  /**Model for the comboBox choosing the x attribute to display */
  protected DefaultComboBoxModel<String> m_ModelX;

  /**Model for the comboBox choosing the y attribute to display */
  protected DefaultComboBoxModel<String> m_ModelY;

  /**Model for the comboBox choosing the color attribute to display */
  protected DefaultComboBoxModel<String> m_ModelColor;

  /**for displaying a genericarrayEditor for choosing overlays */
  protected GenericArrayEditorPanel m_PanelOverlay;

  /**for displaying a genericobjecteditor for choosing paintlet */
  protected GenericObjectEditorPanel m_PanelPaintlet;

  /**default object for the GAE */
  protected AbstractScatterPlotOverlay[] m_DefaultOverlays;

  /**default paintlet for GOEpanel */
  protected AbstractScatterPlotPaintlet m_DefaultPaintlet;

  /** the mouse click action. */
  protected MouseClickAction m_MouseClickAction;

  /** the file chooser for saving a specific sequence. */
  protected SpreadSheetFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    m_XIntIndex     = 0;
    m_YIntIndex     = 0;
    m_ColorIntIndex = -1;
    m_XIndexReg     = new BaseRegExp("");
    m_YIndexReg     = new BaseRegExp("");
    m_ColorIndexReg = new BaseRegExp("");
    m_XIndex        = new Index("");
    m_YIndex        = new Index("");
    m_ColorIndex    = new Index("");
    m_ModelX        = new DefaultComboBoxModel<>();
    m_ModelY        = new DefaultComboBoxModel<>();
    m_ModelColor    = new DefaultComboBoxModel<>();
    m_FileChooser   = null;
  }

  /**
   * Returns the plot.
   *
   * @return		the plot
   */
  public PlotPanel getPlot() {
    return m_Plot;
  }

  /**
   * Called by the class that creates this scatterplot
   * called after fields have been set
   */
  public void reset() {
    //add the attributes to combo box models
    m_ModelX.removeAllElements();
    m_ModelY.removeAllElements();
    m_ModelColor.removeAllElements();
    m_ModelColor.addElement("");
    for (int i = 0; i< m_Data.getColumnCount(); i++) {
      m_ModelX.addElement(m_Data.getColumnName(i));
      m_ModelY.addElement(m_Data.getColumnName(i));
      m_ModelColor.addElement(m_Data.getColumnName(i));
    }

    //set the indices for attribute positions
    int temp;
    temp = IndexHelper.getIndex(m_XIndexReg, m_XIndex, m_Data, -1);
    if (temp == -1)
      temp = 0;
    m_XIntIndex = temp;

    temp = IndexHelper.getIndex(m_YIndexReg, m_YIndex, m_Data, -1);
    if (temp == -1)
      temp = 0;
    m_YIntIndex = temp;

    m_ColorIntIndex = IndexHelper.getIndex(m_ColorIndexReg, m_ColorIndex, m_Data, -1);

    m_ComboBoxY.setSelectedIndex(m_YIntIndex);
    m_ComboBoxX.setSelectedIndex(m_XIntIndex);
    m_ComboBoxColor.setSelectedIndex(m_ColorIntIndex + 1);
    if (m_Overlays == null)
      m_Overlays = new AbstractScatterPlotOverlay[]{};
    m_Paintlet.setPanel(this);
    m_Paintlet.setData(m_Data);
    change();
  }

  /**
   * called when new overlays have been chosen
   */
  protected void changeOverlay() {
    removeOverlays();
    int len = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent()).length;
    m_Overlays = new AbstractScatterPlotOverlay[len];
    for (int i = 0; i < len; i++) {
      m_Overlays[i] = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent())[i].shallowCopy(true);
    }
    for (int i = 0; i< m_Overlays.length; i++) {
      AbstractScatterPlotOverlay temp = m_Overlays[i];
      temp.inst(m_Data);
      temp.setParent(this);
      temp.setUp();
    }
    repaint();
  }

  /**
   * Called when the paintlet used has been changed
   */
  protected void changePaintlet() {
    removePaintlet(m_Paintlet);
    m_Paintlet = (AbstractScatterPlotPaintlet)m_PanelPaintlet.getCurrent();
    m_Paintlet.setPanel(this);
    m_Paintlet.setYIndex(m_YIntIndex);
    m_Paintlet.setXIndex(m_XIntIndex);
    m_Paintlet.setColorIndex(m_ColorIntIndex);
    m_Paintlet.setData(m_Data);
    change();
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    BaseSplitPane splitPane;
    splitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    splitPane.setResizeWeight(0.0);
    splitPane.setOneTouchExpandable(true);
    add(splitPane, BorderLayout.CENTER);

    ParameterPanel optionPanel = new ParameterPanel();
    splitPane.setTopComponent(optionPanel);

    m_Plot = new ScatterPlotPanel();
    m_Plot.addPaintListener(this);
    m_Plot.setTipTextCustomizer(this);
    m_Plot.setPopupMenuCustomizer(this);
    m_Plot.setBorder(BorderFactory.createLineBorder(Color.black));
    m_Plot.addMouseClickListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (m_MouseClickAction != null)
	  m_MouseClickAction.mouseClickOccurred(ScatterPlot.this, e);
      }
    });
    panel = new JPanel(new BorderLayout());
    panel.add(m_Plot, BorderLayout.CENTER);
    splitPane.setBottomComponent(panel);

    m_ComboBoxY     = new JComboBox<>(m_ModelY);
    m_ComboBoxX     = new JComboBox<>(m_ModelX);
    m_ComboBoxColor = new JComboBox<>(m_ModelColor);

    // Create and register listeners for the JComboBox's
    AttYListener listenY = new AttYListener(this);
    m_ComboBoxY.addItemListener(listenY);
    AttXListener listenX = new AttXListener(this);
    m_ComboBoxX.addItemListener(listenX);
    AttColorListener listenColor = new AttColorListener(this);
    m_ComboBoxColor.addItemListener(listenColor);

    if (m_Paintlet == null) {
      m_DefaultPaintlet = new ScatterPaintletCircle();
      m_Paintlet = new ScatterPaintletCircle();
    }
    else
      m_DefaultPaintlet = m_Paintlet;
    m_PanelPaintlet = new GenericObjectEditorPanel(AbstractScatterPlotPaintlet.class, m_DefaultPaintlet, true);
    m_PanelPaintlet.addChangeListener((ChangeEvent e) -> changePaintlet());

    //Choose the overlays
    m_Overlays = new AbstractScatterPlotOverlay[]{};
    m_DefaultOverlays = new AbstractScatterPlotOverlay[]{};
    m_PanelOverlay = new GenericArrayEditorPanel(m_DefaultOverlays);
    m_PanelOverlay.addChangeListener((ChangeEvent e) -> changeOverlay());

    optionPanel.addParameter("X attribute", m_ComboBoxX);
    optionPanel.addParameter("Y attribute", m_ComboBoxY);
    optionPanel.addParameter("Color attribute", m_ComboBoxColor);
    optionPanel.addParameter("Overlays", m_PanelOverlay);
    optionPanel.addParameter("Paintlet", m_PanelPaintlet);
  }

  /**
   * Get the index object for choosing the attribute to display.
   *
   * @return		chosen index
   */
  public Index getXIndex() {
    return m_XIndex;
  }

  /**
   * Set the x attribute index.
   *
   * @param val		chosen index
   */
  public void setXIndex(Index val) {
    m_XIndex = val;
  }

  /**
   * Set the regular expression to use when determining the x attribute.
   *
   * @param val		regular expression for choosing x attribute
   */
  public void setXRegExp(BaseRegExp val) {
    m_XIndexReg = val;
  }

  /**
   * Gets the regular expression used when determining the x attribute.
   *
   * @return		regular expression for choosing x attribute
   */
  public BaseRegExp getXRegExp() {
    return m_XIndexReg;
  }

  /**
   * Get the index object for choosing the y attribute.
   *
   * @return		chosen index
   */
  public Index getYIndex() {
    return m_YIndex;
  }

  /**
   * Set the index for the y attribute.
   *
   * @param val		chosen y index
   */
  public void setYIndex(Index val) {
    m_YIndex = val;
  }

  /**
   * Set the regular expression to use when determining the y attribute.
   *
   * @param val		regular expression for choosing y attribute
   */
  public void setYRegExp(BaseRegExp val) {
    m_YIndexReg = val;
  }

  /**
   * Gets the regular expression used when determining the y attribute.
   *
   * @return		regular expression for choosing y attribute
   */
  public BaseRegExp getYRegExp() {
    return m_YIndexReg;
  }

  /**
   * Get the index object for choosing the color attribute.
   *
   * @return		chosen index
   */
  public Index getColorIndex() {
    return m_ColorIndex;
  }

  /**
   * Set the index for the color attribute.
   *
   * @param val		chosen color index
   */
  public void setColorIndex(Index val) {
    m_ColorIndex = val;
  }

  /**
   * Set the regular expression to use when determining the color attribute.
   *
   * @param val		regular expression for choosing color attribute
   */
  public void setColorRegExp(BaseRegExp val) {
    m_ColorIndexReg = val;
  }

  /**
   * Gets the regular expression used when determining the color attribute.
   *
   * @return		regular expression for choosing color attribute
   */
  public BaseRegExp getColorRegExp() {
    return m_ColorIndexReg;
  }

  /**
   * Set the overlays array containing overlays to apply
   * @param val		Array containing overlays to apply to the scatter plot
   */
  public void setOverlays(AbstractScatterPlotOverlay[] val) {
    AbstractScatterPlotOverlay[]	overlays;
    int					i;
    
    overlays = new AbstractScatterPlotOverlay[val.length];
    for (i = 0; i < val.length; i++)
      overlays[i] = val[i].shallowCopy();
    m_PanelOverlay.setCurrent(overlays);
    changeOverlay();
  }

  /**
   * Get the overlays array containing overlays to apply
   * @return		Array containing overlays to apply to the scatter plot
   */
  public AbstractScatterPlotOverlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Get the paintlet used initially to plot the data
   * @return		Paintlet used
   */
  public AbstractScatterPlotPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Set the paintlet to use initially to plot the data
   * @param val				Initial paintlet to use
   */
  public void setPaintlet(AbstractScatterPlotPaintlet val) {
    m_PanelPaintlet.setCurrent(val.shallowCopy());
    changePaintlet();
  }

  /**
   * called when a field has changed, updates all paintlets etc
   */
  public void change() {
    if (m_Data != null)
      m_Plot.setData(m_Data);
    m_Plot.setX(m_XIntIndex);
    m_Plot.setY(m_YIntIndex);
    m_Plot.reset();
    m_Paintlet.setXIndex(m_XIntIndex);
    m_Paintlet.setYIndex(m_YIntIndex);
    m_Paintlet.setColorIndex(m_ColorIntIndex);
    m_Paintlet.setData(m_Data);
    update();
  }

  /**
   * Updates the overlays, calculates each
   */
  public void prepareUpdate() {
    for (int i = 0; i< m_Overlays.length; i++) {
      if (m_Overlays[i].getPaintlet() != null) {
	m_Overlays[i].getPaintlet().parameters(m_Data, m_XIntIndex, m_YIntIndex);
	if (!m_Overlays[i].getPaintlet().getCalculated())
	  m_Overlays[i].getPaintlet().calculate();
      }
    }
  }

  /**
   * Sets the mouse click action to use.
   *
   * @param value	the action
   */
  public void setMouseClickAction(MouseClickAction value) {
    m_MouseClickAction = value;
  }

  /**
   * Returns the mouse click action in use.
   *
   * @return		the action, null if non set
   */
  public MouseClickAction getMouseClickAction() {
    return m_MouseClickAction;
  }

  /**
   * Saves the data as spreadsheet.
   *
   * @param xRange	the optional limits for X
   * @param yRange	the optional limits for Y
   */
  protected void save(double[] xRange, double[] yRange) {
    SpreadSheet		data;
    int			retVal;
    SpreadSheetWriter 	writer;

    data = m_Data;
    if ((xRange != null) && (yRange != null)) {
      data = DataHelper.filter(
	data,
	"" + m_ComboBoxX.getSelectedItem(), xRange,
	"" + m_ComboBoxY.getSelectedItem(), yRange);
    }

    if (m_FileChooser == null)
      m_FileChooser = new SpreadSheetFileChooser();

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    writer = m_FileChooser.getWriter();
    if (!writer.write(data, m_FileChooser.getSelectedFile()))
      GUIHelper.showErrorMessage(
	  this, "Failed to save data to file:\n" + m_FileChooser.getSelectedFile());
  }

  /**
   * Saves all data points to a spreadsheet.
   */
  protected void save() {
    save(null, null);
  }

  /**
   * Saves only the visible data points to a spreadsheet.
   */
  protected void saveVisible() {
    save(
      new double[]{
	m_Plot.getAxis(Axis.BOTTOM).getActualMinimum(),
	m_Plot.getAxis(Axis.BOTTOM).getActualMaximum(),
      },
      new double[]{
	m_Plot.getAxis(Axis.LEFT).getActualMinimum(),
	m_Plot.getAxis(Axis.LEFT).getActualMaximum(),
      }
    );
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		The mouse event
   * @param menu	The menu to customize.
   */
  @Override
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    JMenuItem	menuitem;

    menuitem = new JMenuItem("Save data...", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> save());
    menu.add(menuitem);

    menuitem = new JMenuItem("Save visible data...", GUIHelper.getEmptyIcon());
    menuitem.addActionListener((ActionEvent ae) -> saveVisible());
    menu.add(menuitem);
  }
}