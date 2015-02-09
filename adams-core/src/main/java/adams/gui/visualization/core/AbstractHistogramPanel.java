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
 * AbstractHistogramPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import adams.flow.sink.sequenceplotter.SequencePlotContainer;
import adams.flow.sink.sequenceplotter.SequencePlotContainerManager;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.BarPaintlet;

import java.awt.Dimension;
import java.awt.GridLayout;

/**
 * Ancestor for panels that generate and display histograms.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the data type to generate the histogram for
 */
public abstract class AbstractHistogramPanel<T>
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -8621818594275641231L;

  /** the layout. */
  protected GridLayout m_Layout;
  
  /** the current data. */
  protected T m_Data;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_Layout = new GridLayout(0, 1);
    setLayout(m_Layout);
  }
  
  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }
  
  /**
   * Creates a new plot panel.
   * 
   * @param name	the name for the panel
   * @return		the panel
   */
  protected SequencePlotterPanel newPanel(String name) {
    SequencePlotterPanel	result;
    FancyTickGenerator		tick;
    BarPaintlet			paintlet;

    result = new SequencePlotterPanel(name);
    result.setSidePanelVisible(false);
    paintlet = new BarPaintlet();
    paintlet.setWidth(3);
    result.setPaintlet(paintlet);
    tick = new FancyTickGenerator();
    tick.setNumTicks(10);
    result.getPlot().getAxis(Axis.LEFT).setTickGenerator(tick);
    result.getPlot().getAxis(Axis.LEFT).setNthValueToShow(3);
    result.getPlot().getAxis(Axis.LEFT).setNumberFormat("0");
    result.getPlot().getAxis(Axis.LEFT).setAxisName("");
    tick = new FancyTickGenerator();
    tick.setNumTicks(20);
    result.getPlot().getAxis(Axis.BOTTOM).setTickGenerator(tick);
    result.getPlot().getAxis(Axis.BOTTOM).setNthValueToShow(5);
    result.getPlot().getAxis(Axis.BOTTOM).setNumberFormat("0");
    result.getPlot().getAxis(Axis.BOTTOM).setAxisName("");
    result.getPlot().getAxis(Axis.BOTTOM).setTopMargin(0.01);
    result.getPlot().getAxis(Axis.BOTTOM).setBottomMargin(0.01);
    result.setPreferredSize(new Dimension(600, 200));
    
    return result;
  }

  /**
   * Generates the sequence(s) from the data.
   *
   * @return		the generated sequence(s)
   */
  protected abstract SequencePlotSequence[] createSequences();

  /**
   * Updates the histograms.
   */
  protected void update() {
    int 				i;
    SequencePlotterPanel 		panel;
    SequencePlotSequence[] 		seq;
    SequencePlotContainer 		cont;
    SequencePlotContainerManager 	manager;

    removeAll();

    if (m_Data == null) {
      m_Layout.setRows(0);
      return;
    }

    seq = createSequences();
    m_Layout.setRows(seq.length);
    for (i = 0; i < seq.length; i++) {
      panel = newPanel(seq[i].getID());
      manager = (SequencePlotContainerManager) panel.getContainerManager();
      cont = manager.newContainer(seq[i]);
      manager.add(cont);
      add(panel);
    }
  }

  /**
   * Sets the data to generate the histogram(s) for.
   * 
   * @param value	the data
   */
  public void setData(T value) {
    m_Data = value;
    update();
  }
  
  /**
   * Returns the currently set data.
   * 
   * @return		the data, null if none set
   */
  public T getData() {
    return m_Data;
  }
}
