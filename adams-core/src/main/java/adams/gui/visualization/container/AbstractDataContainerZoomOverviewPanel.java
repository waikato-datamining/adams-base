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

/**
 * AbstractDataContainerZoomOverviewPanel.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import adams.core.Properties;
import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.gui.core.ColorHelper;
import adams.gui.event.PlotPanelPanningEvent;
import adams.gui.event.PlotPanelPanningListener;
import adams.gui.event.PlotPanelZoomEvent;
import adams.gui.event.PlotPanelZoomListener;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PaintletWithMarkers;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.SimpleTickGenerator;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Panel that shows the zoom in the data container panel as overlay.
 * <br><br>
 * Requires the following keys in the data container panel's props file:
 * <ul>
 *   <li>ZoomOverview.HighlightColor</li>
 *   <li>ZoomOverview.Visible</li>
 *   <li>ZoomOverview.Height</li>
 * </ul>
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <P> the type of DataContainerPanel to use
 * @param <C> the type of container paintlet to use
 * @param <Z> the type of zoom overview paintlet to use
 * @param <T> the type of data container to use
 * @param <M> the type of container manager to use
 */
public abstract class AbstractDataContainerZoomOverviewPanel<P extends DataContainerPanel, C extends Paintlet, Z extends AbstractDataContainerZoomOverviewPaintlet, T extends DataContainer, M extends AbstractContainerManager>
  extends DataContainerPanel<T, M> 
  implements PlotPanelZoomListener, PlotPanelPanningListener {

  /** for serialization. */
  private static final long serialVersionUID = -5141649373267221710L;

  /** the corresponding total ion count panel. */
  protected P m_ContainerPanel;

  /** paintlet for drawing the data. */
  protected C m_ContainerPaintlet;

  /** the zoom highlight paintlet. */
  protected Z m_ZoomOverviewPaintlet;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ContainerPanel = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_ContainerPaintlet = null;
    
    m_ZoomOverviewPaintlet = newZoomPaintlet();
    m_ZoomOverviewPaintlet.setPanel(this);

    m_PlotPanel.setZoomingEnabled(false);
    m_PlotPanel.setPanningEnabled(false);
    
    setVisible(false);
  }

  /**
   * Creates a new zoom paintlet.
   * 
   * @return		the paintlet
   */
  protected abstract Z newZoomPaintlet();
  
  /**
   * Returns the paintlet used for painting the containers.
   * 
   * @return		the paintlet
   */
  @Override
  public C getContainerPaintlet() {
    return m_ContainerPaintlet;
  }
  
  /**
   * Returns the paintlet used for painting the highlight.
   * 
   * @return		the paintlet
   */
  public AbstractDataContainerZoomOverviewPaintlet getZoomOverviewPaintlet() {
    return m_ZoomOverviewPaintlet;
  }
  
  /**
   * Sets the data container panel to use.
   *
   * @param value	the panel to use
   */    
  public void setDataContainerPanel(P value) {
    Properties		props;
    AxisPanel		panel;
    SimpleTickGenerator	tick;
   
    if (m_ContainerPanel != null) {
      m_ContainerPanel.getPlot().removeZoomListener(this);
      m_ContainerPanel.getPlot().removePanningListener(this);
    }
    
    m_ContainerPanel = value;
    if (m_ContainerPanel != null) {
      props = m_ContainerPanel.getProperties();
      m_ContainerPaintlet = (C) m_ContainerPanel.getContainerPaintlet().shallowCopy();
      if (m_ContainerPaintlet instanceof PaintletWithMarkers)
        ((PaintletWithMarkers) m_ContainerPaintlet).setMarkersDisabled(true);
      m_ContainerPaintlet.setPanel(this);
      
      // update axes properties
      panel = m_PlotPanel.getAxis(Axis.LEFT);
      panel.assign(m_ContainerPanel.getPlot().getAxis(Axis.LEFT));
      panel.setAxisName("Zoom view");
      tick = new SimpleTickGenerator();
      tick.setNumTicks(5);
      panel.setTickGenerator(tick);
      panel.setNthValueToShow(0);
      
      panel = m_PlotPanel.getAxis(Axis.BOTTOM);
      panel.assign(m_ContainerPanel.getPlot().getAxis(Axis.BOTTOM));
      panel.setAxisName("");
      tick = new SimpleTickGenerator();
      panel.setTickGenerator(tick);
      panel.setNthValueToShow(0);
      panel.setAxisWidth(20);
      
      m_PlotPanel.updateCorners();
      
      m_ZoomOverviewPaintlet.setHighlightColor(props.getColor("ZoomOverview.HighlightColor", ColorHelper.valueOf("#FFDDFF")));
      setVisible(props.getBoolean("ZoomOverview.Visible", false));
      setPreferredSize(new Dimension(0, props.getInteger("ZoomOverview.Height", 100)));
      
      m_ContainerPanel.getPlot().addZoomListener(this);
      m_ContainerPanel.getPlot().addPanningListener(this);
    }
    update();
  }

  /**
   * Returns the current data container panel, can be null.
   *
   * @return		the panel in use
   */
  public P getDataContainerPanel() {
    return m_ContainerPanel;
  }

  /**
   * Returns the default database connection.
   *
   * @return		always null
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return null;
  }

  /**
   * Returns the container manager to use.
   *
   * @return		always null
   */
  @Override
  protected M newContainerManager() {
    return null;
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		always null
   */
  @Override
  public AbstractScriptingEngine getScriptingEngine() {
    return null;
  }

  /**
   * Returns the current container manager.
   *
   * @return		the manager
   */
  @Override
  public M getContainerManager() {
    if (m_ContainerPanel != null)
      return (M) m_ContainerPanel.getContainerManager();
    else
      return null;
  }

  /**
   * Prepares the update, i.e., calculations etc.
   */
  @Override
  protected void prepareUpdate() {
    if (getDataContainerPanel() == null)
      return;
      
    getPlot().getAxis(Axis.LEFT).setMinimum(getDataContainerPanel().getPlot().getAxis(Axis.LEFT).getMinimum());
    getPlot().getAxis(Axis.LEFT).setMaximum(getDataContainerPanel().getPlot().getAxis(Axis.LEFT).getMaximum());
    getPlot().getAxis(Axis.BOTTOM).setMinimum(getDataContainerPanel().getPlot().getAxis(Axis.BOTTOM).getMinimum());
    getPlot().getAxis(Axis.BOTTOM).setMaximum(getDataContainerPanel().getPlot().getAxis(Axis.BOTTOM).getMaximum());
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  @Override
  protected boolean canPaint(Graphics g) {
    return (getContainerManager() != null);
  }

  /**
   * Invoked when a {@link PlotPanel} got zoomed in/out.
   * 
   * @param e		the event
   */
  public void painted(PlotPanelZoomEvent e) {
    update();
  }

  /**
   * Invoked when a {@link PlotPanel} experiences panning (or a reset of panning).
   * 
   * @param e		the event
   */
  public void panningOccurred(PlotPanelPanningEvent e) {
    update();
  }
}
