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
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericArrayEditorPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.stats.core.IndexSet;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCircle;
import adams.gui.visualization.stats.scatterplot.action.MouseClickAction;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
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
  extends AbstractScatterPlot {

  /** for serialization */
  private static final long serialVersionUID = -7798200657432959204L;

  /**Regular expression given as an option to choose x attribute */
  protected BaseRegExp m_XIndexReg;

  /**Index object given as an option to choose x object */
  protected Index m_XInd;

  /**regular expression given as an option to choose y attribute */
  protected BaseRegExp m_YIndexReg;

  /**Index given as an option to choose y attribute */
  protected Index m_YInd;

  /**For choosing attribute to display on x axis */
  protected JComboBox<String> m_AttY;

  /**For choosing attribute to display on y axis */
  protected JComboBox<String> m_AttX;

  /**Model for the comboBox choosing the x attribute to display */
  protected DefaultComboBoxModel<String> m_ModelX;

  /**Model for the comboBox choosing the y attribute to display */
  protected DefaultComboBoxModel<String> m_ModelY;

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

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();
    m_XIndex = 0;
    m_YIndex = 0;
    m_XIndexReg = new BaseRegExp();
    m_YIndexReg = new BaseRegExp();
    m_XInd = new Index();
    m_YInd = new Index();
  }

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
    for(int i =0; i< m_Data.getColumnCount(); i++) {
      m_ModelX.addElement(m_Data.getColumnName(i));
      m_ModelY.addElement(m_Data.getColumnName(i));
    }
    //set the indices for attribute positions
    int temp = -1;
    temp = IndexSet.getIndex(m_XIndexReg, m_XInd, m_Data, temp);
    if(temp == -1) {
      temp = 0;
      System.err.println("changed to 0");
    }
    m_XIndex = temp;

    temp = -1;
    temp = IndexSet.getIndex(m_YIndexReg, m_YInd, m_Data, temp);
    if(temp == -1) {
      temp = 0;
      System.err.println("changed to 0");
    }
    m_YIndex = temp;

    m_AttY.setSelectedIndex(m_YIndex);
    m_AttX.setSelectedIndex(m_XIndex);
    if(m_Overlays == null)
      m_Overlays = new AbstractScatterPlotOverlay[]{};
    m_Paintlet.setPanel(this);
    m_Paintlet.setData(m_Data);
    change();
  }

  /**
   * called when new overlays have been chosen
   */
  private void changeOverlay() {
    removeOverlays();
    int len = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent()).length;
    m_Overlays = new AbstractScatterPlotOverlay[len];
    for(int i = 0; i < len; i++) {
      m_Overlays[i] = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent())[i].shallowCopy(true);
    }
    for(int i = 0; i< m_Overlays.length; i++) {
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
  private void changePaintlet() {
    removePaintlet(m_Paintlet);
    m_Paintlet = (AbstractScatterPlotPaintlet)m_PanelPaintlet.getCurrent();
    m_Paintlet.setPanel(this);
    m_Paintlet.setYIndex(m_YIndex);
    m_Paintlet.setXIndex(m_XIndex);
    m_Paintlet.setData(m_Data);
    change();
  }

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

    m_ModelX = new DefaultComboBoxModel<>();
    m_ModelY = new DefaultComboBoxModel<>();
    m_AttY = new JComboBox<>(m_ModelY);
    m_AttX = new JComboBox<>(m_ModelX);

    // Create and register listeners for the JComboBox's
    AttYListener listenY = new AttYListener(this);
    m_AttY.addItemListener(listenY);
    AttXListener listenX = new AttXListener(this);
    m_AttX.addItemListener(listenX);

    if(m_Paintlet == null) {
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

    optionPanel.addParameter("Y attribute", m_AttY);
    optionPanel.addParameter("X attribute", m_AttX);
    optionPanel.addParameter("Overlays", m_PanelOverlay);
    optionPanel.addParameter("Paintlet", m_PanelPaintlet);
  }

  /**
   * Get the index object for choosing the attribute to display
   * @return		chosen index
   */
  public Index getXIndex() {
    return m_XInd;
  }

  /**
   * Set the x attribute index
   * @param val		chosen index
   */
  public void setXIndex(Index val) {
    m_XInd = val;
  }

  /**
   * Get the index object for choosing the y attribute
   * @return		chosen index
   */
  public Index getYIndex() {
    return m_YInd;
  }

  /**
   * Set the index for the y attribute
   * @param val		chosen y index
   */
  public void setYIndex(Index val) {
    m_YInd = val;
  }

  /**
   * Set the regular expression to use when determining the x attribute
   * @param val		regular expression for choosing x attribute
   */
  public void setXRegExp(BaseRegExp val) {
    m_XIndexReg = val;
  }

  /**
   * Gets the regular expression used when determining the x attribute
   * @return		regular expression for choosing x attribute
   */
  public BaseRegExp getXRegExp() {
    return m_XIndexReg;
  }

  /**
   * Set the regular expression to use when determining the y attribute
   * @param val		regular expression for choosing y attribute
   */
  public void setYRegExp(BaseRegExp val) {
    m_YIndexReg = val;
  }

  /**
   * Gets the regular expression used when determining the y attribute
   * @return		regular expression for choosing y attribute
   */
  public BaseRegExp getYRegExp() {
    return m_YIndexReg;
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
   * Listener for when the y attribute JComboBox selection changes
   * @author msf8
   */
  protected class AttYListener implements ItemListener {
    ScatterPlot m_parent;
    public AttYListener(ScatterPlot parent) {
      m_parent = parent;
    }
    public void itemStateChanged(ItemEvent arg0) {
      SpreadSheet data = m_parent.getData();
      int ind = m_parent.getY_Index();
      if(arg0.getStateChange() == ItemEvent.SELECTED) {
	String chose = (String)arg0.getItem();
	//finds position of attribute
	for(int t = 0; t< data.getColumnCount(); t++)
	{
	  if(data.getColumnName(t).equals(chose)) {
	    for(int i = 0; i< m_Overlays.length; i++)
	      m_Overlays[i].getPaintlet().setCalculated(false);
	    m_YIndex = t;
	    change();
	    break;
	  }
	}
      }
    }
  }

  /**
   * Listener for when the x attribute JComboBox selection changes
   * @author msf8
   *
   */
  protected class AttXListener implements ItemListener {

    ScatterPlot m_parent;
    public AttXListener(ScatterPlot parent) {
      m_parent = parent;
    }

    public void itemStateChanged(ItemEvent arg0) {
      SpreadSheet data = m_parent.getData();
      if(arg0.getStateChange() == ItemEvent.SELECTED) {
	String chose = (String)arg0.getItem();
	//finds position of attribute
	for(int t = 0; t< data.getColumnCount(); t++)
	{
	  if(data.getColumnName(t).equals(chose)) {
	    for(int i = 0; i< m_Overlays.length; i++) {
	      m_Overlays[i].getPaintlet().setCalculated(false);
	    }
	    m_XIndex = t;
	    change();
	    break;
	  }
	}
      }
    }
  }

  /**
   * called when a field has changed, updates all paintlets etc
   */
  public void change() {
    if(m_Data != null)
      m_Plot.setData(m_Data);
    m_Plot.setX(m_XIndex);
    m_Plot.setY(m_YIndex);
    m_Plot.reset();
    m_Paintlet.setXIndex(m_XIndex);
    m_Paintlet.setYIndex(m_YIndex);
    m_Paintlet.setData(m_Data);
    update();
  }

  /**
   * Updates the overlays, calculates each
   */
  public void prepareUpdate() {
    for(int i = 0; i< m_Overlays.length; i++) {
      if(m_Overlays[i].getPaintlet() != null) {
	m_Overlays[i].getPaintlet().parameters(m_Data, m_XIndex, m_YIndex);
	if(!m_Overlays[i].getPaintlet().getCalculated())
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
}