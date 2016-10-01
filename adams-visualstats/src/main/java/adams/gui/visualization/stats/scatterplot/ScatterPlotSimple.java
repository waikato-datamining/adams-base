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
 * ScatterPlotSimple.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.Axis;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class for displaying a single scatterplotpanel only, used in the matrix.
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterPlotSimple
extends AbstractScatterPlot{

  /** for serialization */
  private static final long serialVersionUID = -6414605668492610814L;

  /**Arraylist of overlays applied currently */
  protected ArrayList<AbstractScatterPlotOverlay> m_overs;

  public void prepareUpdate() {
    if(m_Data != null){
      //set the data for the scatterplot
      m_Plot.setData(m_Data);
      m_Plot.setX(m_XIntIndex);
      m_Plot.setY(m_YIntIndex);
      //set the data for the paintlet
      if(m_Paintlet != null) {
	m_Paintlet.setRepaintOnChange(false);
	m_Paintlet.setXIndex(m_XIntIndex);
	m_Paintlet.setYIndex(m_YIntIndex);
        m_Paintlet.setColorIndex(-1);
	m_Paintlet.setData(m_Data);
	m_Paintlet.setRepaintOnChange(true);
      }
    }
  }

  protected void initGUI() {
    super.initGUI();
    setLayout(new BorderLayout());
    m_Plot = new ScatterPlotPanel();
    m_Plot.addPaintListener(this);
    m_Plot.setAxisVisibility(Axis.BOTTOM, Visibility.HIDDEN);
    m_Plot.setAxisVisibility(Axis.LEFT, Visibility.HIDDEN);
    add(m_Plot, BorderLayout.CENTER);
  }

  /**
   * removes existing overlays
   * @param val			hash set of the overlays to delete
   */
  protected void removeOverlays(HashSet<String> val) {
    if(m_overs != null) {
      for(int i = 0; i< m_overs.size(); i++) {
	String thisStr = m_overs.get(i).toCommandLine();
	Iterator<String> it = val.iterator();
	while(it.hasNext()) {
	  String compare = it.next();
	  if(thisStr.equals(compare)) {
	    removePaintlet(m_overs.get(i).getPaintlet());
	    m_overs.remove(i);
	  }
	}
      }
    }
  }

  protected void removeAllOverlays() {
    if(m_overs != null) {
      for(int i = 0; i < m_overs.size(); i++) {
	removePaintlet(m_overs.get(i).getPaintlet());
      }
      m_overs = new ArrayList<>();
    }
  }

  /**
   * Add overlay to the list of overlays
   * @param val			overlay to add
   */
  protected void addOverlay(AbstractScatterPlotOverlay val) {
    if(m_overs == null)
      m_overs = new ArrayList<>();
    m_overs.add(val);
  }
}