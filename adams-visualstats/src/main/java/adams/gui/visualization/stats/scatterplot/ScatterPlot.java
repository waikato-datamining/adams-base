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

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
  protected JComboBox m_AttY;

  /**For choosing attribute to display on y axis */
  protected JComboBox m_AttX;

  /**Model for the comboBox choosing the x attribute to display */
  protected DefaultComboBoxModel m_ComboBoxX;

  /**Model for the comboBox choosing the y attribute to display */
  protected DefaultComboBoxModel m_ComboBoxY;

  /**for displaying a genericarrayEditor for choosing overlays */
  protected GenericArrayEditorPanel m_PanelOverlay;

  /**for displaying a genericobjecteditor for choosing paintlet */
  protected GenericObjectEditorPanel m_PanelPaintlet;

  /**default object for the GAE */
  protected AbstractScatterPlotOverlay[] m_Default;

  /**default paintlet for GOEpanel */
  protected AbstractScatterPlotPaintlet m_Def;

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
    for(int i =0; i< m_Data.getColumnCount(); i++)
    {
      m_ComboBoxX.addElement(m_Data.getColumnName(i));
      m_ComboBoxY.addElement(m_Data.getColumnName(i));
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
    if(m_Array == null)
      m_Array = new AbstractScatterPlotOverlay[]{};
    m_Val.setPanel(this);
    m_Val.setData(m_Data);
    change();
  }

  /**
   * called when new overlays have been chosen
   */
  private void changeOverlay() {
    removeOverlays();
    int len = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent()).length;
    m_Array = new AbstractScatterPlotOverlay[len];
    for(int i = 0; i < len; i++) {
      m_Array[i] = ((AbstractScatterPlotOverlay[]) m_PanelOverlay.getCurrent())[i].shallowCopy(true);
    }
    for(int i = 0; i< m_Array.length; i++) {
      AbstractScatterPlotOverlay temp = m_Array[i];
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
    removePaintlet(m_Val);
    m_Val = (AbstractScatterPlotPaintlet)m_PanelPaintlet.getCurrent();
    m_Val.setPanel(this);
    m_Val.setY_Index(m_YIndex);
    m_Val.setX_Index(m_XIndex);
    m_Val.setData(m_Data);
    change();
  }

  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());

    //plot panel for displaying data
    m_Plot = new ScatterPlotPanel();
    m_Plot.addPaintListener(this);

    ParameterPanel optionPanel = new ParameterPanel();
    BaseSplitPane splitPane;
    splitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(m_Plot);
    JPanel hold = new JPanel(new BorderLayout());
    hold.add(optionPanel, BorderLayout.NORTH);
    splitPane.setRightComponent(hold);
    splitPane.setResizeWeight(1.0);
    splitPane.setOneTouchExpandable(true);
    add(splitPane, BorderLayout.CENTER);
    hold.setPreferredSize(new Dimension(600,0));

    m_ComboBoxX = new DefaultComboBoxModel();
    m_ComboBoxY = new DefaultComboBoxModel();
    m_AttY = new JComboBox(m_ComboBoxY);
    m_AttX = new JComboBox(m_ComboBoxX);

    // Create and register listeners for the JComboBox's
    AttYListener listenY = new AttYListener(this);
    m_AttY.addItemListener(listenY);
    AttXListener listenX = new AttXListener(this);
    m_AttX.addItemListener(listenX);

    if(m_Val == null) {
      m_Def = new ScatterPaintletCircle();
      m_Val = new ScatterPaintletCircle();
    }
    else
      m_Def = m_Val;
    m_PanelPaintlet = new GenericObjectEditorPanel(AbstractScatterPlotPaintlet.class, m_Def, true);
    m_PanelPaintlet.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	changePaintlet();
      }
    });

    //Choose the overlays
    m_Array = new AbstractScatterPlotOverlay[]{};
    m_Default = new AbstractScatterPlotOverlay[]{};
    m_PanelOverlay = new GenericArrayEditorPanel(m_Default);
    m_PanelOverlay.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	changeOverlay();
      }});

    m_Plot.setBorder(BorderFactory.createLineBorder(Color.black));

    optionPanel.addParameter("Y attribute", m_AttY);
    optionPanel.addParameter("X attribute", m_AttX);
    optionPanel.addParameter("Overlays", m_PanelOverlay);
    optionPanel.addParameter("Paintlet", m_PanelPaintlet);
  }

  /**
   * Get the index object for choosing the attribute to display
   * @return		chosen index
   */
  public Index getX_Ind() {
    return m_XInd;
  }

  /**
   * Set the x attribute index
   * @param val		chosen index
   */
  public void setX_Ind(Index val) {
    m_XInd = val;
  }

  /**
   * Get the index object for choosing the y attribute
   * @return		chosen index
   */
  public Index getY_Ind() {
    return m_YInd;
  }

  /**
   * Set the index for the y attribute
   * @param val		chosen y index
   */
  public void setY_Ind(Index val) {
    m_YInd = val;
  }

  /**
   * Set the regular expression to use when determining the x attribute
   * @param val		regular expression for choosing x attribute
   */
  public void setX_IndexReg(BaseRegExp val) {
    m_XIndexReg = val;
  }

  /**
   * Gets the regular expression used when determining the x attribute
   * @return		regular expression for choosing x attribute
   */
  public BaseRegExp getX_IndexReg() {
    return m_XIndexReg;
  }

  /**
   * Set the regular expression to use when determining the y attribute
   * @param val		regular expression for choosing y attribute
   */
  public void setY_IndexReg(BaseRegExp val) {
    m_YIndexReg = val;
  }

  /**
   * Gets the regular expression used when determining the y attribute
   * @return		regular expression for choosing y attribute
   */
  public BaseRegExp getY_IndexReg() {
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
    return m_Array;
  }

  /**
   * Get the paintlet used initially to plot the data
   * @return		Paintlet used
   */
  public AbstractScatterPlotPaintlet getPaintlet() {
    return m_Val;
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
	    for(int i = 0; i< m_Array.length; i++) {
	      m_Array[i].getPaintlet().setCalculated(false);
	    }
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
	    for(int i = 0; i< m_Array.length; i++) {
	      m_Array[i].getPaintlet().setCalculated(false);
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
    m_Val.setX_Index(m_XIndex);
    m_Val.setY_Index(m_YIndex);
    m_Val.setData(m_Data);
    update();
  }

  /**
   * Updates the overlays, calculates each
   */
  public void prepareUpdate() {
    for(int i = 0; i< m_Array.length; i++) {
      if(m_Array[i].getPaintlet() != null) {
	m_Array[i].getPaintlet().parameters(m_Data, m_XIndex, m_YIndex);
	if(m_Array[i].getPaintlet().getCalculated() == false)
	  m_Array[i].getPaintlet().calculate();
      }
    }
  }
}