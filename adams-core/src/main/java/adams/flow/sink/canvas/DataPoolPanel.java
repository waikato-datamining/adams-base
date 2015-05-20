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
 * DataPoolPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.canvas;

import java.awt.BorderLayout;
import java.awt.Graphics;

import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PlotPanel;

/**
 * Panel for painting on the content of a DataPool.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataPoolPanel
  extends PaintablePanel {

  /** for serialization. */
  private static final long serialVersionUID = 1131414446883475974L;

  /** the data pool. */
  protected DataPool m_Pool;

  /** the post-processor to use. */
  protected AbstractDataPoolPostProcessor m_PostProcessor;
  
  /** the plot panel. */
  protected PlotPanel m_Plot;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PostProcessor = new PassThrough();
    m_Pool          = new DataPool();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_Plot = new PlotPanel();
    m_Plot.addPaintListener(this);
    add(m_Plot, BorderLayout.CENTER);
  }
  
  /**
   * Returns the data pool.
   *  
   * @return		the data pool
   */
  public DataPool getPool() {
    return m_Pool;
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
   * 
   * @see		AbstractDataPoolPaintlet#prepareUpdate()
   */
  @Override
  protected void prepareUpdate() {
    for (Paintlet paintlet: m_Paintlets) {
      if (paintlet instanceof DataPoolPaintlet)
	((DataPoolPaintlet) paintlet).prepareUpdate();
    }
  }

  /**
   * Hook method, called after the update was performed.
   * <br><br>
   * Post-processes the data pool.
   */
  @Override
  protected void postUpdate() {
    if (m_PostProcessor != null)
      m_PostProcessor.postProcess(getPool());
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  @Override
  protected boolean canPaint(Graphics g) {
    return (m_Paintlets.size() > 0);
  }
  
  /**
   * The post processor to use on the data.
   * 
   * @param value	the post processor
   */
  public void setPostProcessor(AbstractDataPoolPostProcessor value) {
    m_PostProcessor = value;
    update();
  }
  
  /**
   * Returns the post processor in use.
   * 
   * @return		the post processor
   */
  public AbstractDataPoolPostProcessor getPostProcessor() {
    return m_PostProcessor;
  }
  
  /**
   * Adds the data point to the pool and initiates a replot.
   * 
   * @param 
   */
  public void addData(Object obj) {
    addData(obj, true);
  }
  
  /**
   * Adds the data point to the pool and initiates a replot if requested.
   * 
   * @param obj		the data point to add
   * @param redraw	whether to redraw the plot
   */
  public void addData(Object obj, boolean redraw) {
    getPool().add(obj);
    if (redraw)
      update();
  }
  
  /**
   * Clears the data pool and initiates a replot.
   */
  public void clear() {
    getPool().clear();
    update();
  }
}
