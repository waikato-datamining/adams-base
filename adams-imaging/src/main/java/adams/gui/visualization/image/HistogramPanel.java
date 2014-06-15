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
 * HistogramPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import adams.data.image.BufferedImageHelper;
import adams.flow.sink.sequenceplotter.SequencePlotContainer;
import adams.flow.sink.sequenceplotter.SequencePlotContainerManager;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.core.BasePanel;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.StickPaintlet;

/**
 * Generates and displays histogram(s) from an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistogramPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -8621818594275641231L;

  /** the layout. */
  protected GridLayout m_Layout;
  
  /** the current image. */
  protected BufferedImage m_Image;
  
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

    result = new SequencePlotterPanel(name);
    result.setSidePanelVisible(false);
    result.setPaintlet(new StickPaintlet());
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
   * Updates the histograms.
   */
  protected void update() {
    boolean				gray;
    int[][]				histogram;
    int					i;
    int					n;
    SequencePlotterPanel		panel;
    SequencePlotSequence		seq;
    SequencePlotContainer		cont;
    SequencePlotContainerManager	manager;
    String				name;
    
    removeAll();
    
    if (m_Image == null) {
      m_Layout.setRows(0);
      return;
    }
    
    gray = (m_Image.getType() == BufferedImage.TYPE_BYTE_GRAY)
	|| (m_Image.getType() == BufferedImage.TYPE_BYTE_BINARY);
    
    if (gray)
      m_Layout.setRows(1);
    else
      m_Layout.setRows(3);
    
    histogram = BufferedImageHelper.histogram(m_Image, gray);
    for (i = 0; i < histogram.length; i++) {
      switch (i) {
	case 0:
	  if (gray)
	    name = "Gray";
	  else
	    name = "Red";
	  break;
	case 1:
	  name = "Green";
	  break;
	case 2:
	  name = "Blue";
	  break;
	default:
	  name = null;
      }
      if (name == null)
	continue;
      
      seq = new SequencePlotSequence();
      seq.setID(name);
      for (n = 0; n < histogram[i].length; n++)
	seq.add(new SequencePlotPoint(n, histogram[i][n]));
      panel   = newPanel(name);
      manager = (SequencePlotContainerManager) panel.getContainerManager();
      cont    = manager.newContainer(seq);
      manager.add(cont);
      
      add(panel);
    }
  }
  
  /**
   * Sets the image to generate the histogram(s) for.
   * 
   * @param value	the image
   */
  public void setImage(BufferedImage value) {
    m_Image = value;
    update();
  }
  
  /**
   * Returns the currently set image.
   * 
   * @return		the image, null if none set
   */
  public BufferedImage getImage() {
    return m_Image;
  }
}
