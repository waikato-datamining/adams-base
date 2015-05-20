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
 * TimestampPruner.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.util.Date;

import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.core.base.BaseDateTime;
import adams.data.mapobject.TimestampSupporter;

/**
 <!-- globalinfo-start -->
 * Removes map objects that are older than the specified cut-off date.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-cut-off &lt;adams.core.base.BaseDateTime&gt; (property: cutOff)
 * &nbsp;&nbsp;&nbsp;The cut-off date, ie any map object older than this gets removed.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimestampPruner
  extends AbstractMapObjectPruner {

  /** for serialization. */
  private static final long serialVersionUID = -135743438219473331L;

  /** the cutoff date. */
  protected BaseDateTime m_CutOff;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes map objects that are older than the specified cut-off date.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cut-off", "cutOff",
	    new BaseDateTime(BaseDateTime.INF_PAST));
  }

  /**
   * Sets the cut-off date.
   *
   * @param value	the date
   */
  public void setCutOff(BaseDateTime value) {
    m_CutOff = value;
    reset();
  }

  /**
   * Returns the cut-off date.
   *
   * @return		the date
   */
  public BaseDateTime getCutOff() {
    return m_CutOff;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cutOffTipText() {
    return "The cut-off date, ie any map object older than this gets removed.";
  }

  /**
   * Prunes the map objects.
   * 
   * @param tree	the tree to prune
   */
  @Override
  protected void doPrune(JMapViewerTree tree) {
    Date 	cutoff;
    int		i;
    MapObject	mapobject;
    int		pruned;
    
    cutoff = m_CutOff.dateValue();
    
    // markers
    i      = 0;
    pruned = 0;
    while (i < tree.getViewer().getMapMarkerList().size()) {
      mapobject = tree.getViewer().getMapMarkerList().get(i);
      if (mapobject instanceof TimestampSupporter) {
	if (((TimestampSupporter) mapobject).getTimestamp().compareTo(cutoff) < 0) {
	  tree.getViewer().getMapMarkerList().remove(i);
	  pruned++;
	  continue;
	}
      }
      i++;
    }
    if (isLoggingEnabled())
      getLogger().fine("Markers pruned: " + pruned);

    // rectangles
    i      = 0;
    pruned = 0;
    while (i < tree.getViewer().getMapRectangleList().size()) {
      mapobject = tree.getViewer().getMapRectangleList().get(i);
      if (mapobject instanceof TimestampSupporter) {
	if (((TimestampSupporter) mapobject).getTimestamp().compareTo(cutoff) < 0) {
	  tree.getViewer().getMapRectangleList().remove(i);
	  pruned++;
	  continue;
	}
      }
      i++;
    }
    if (isLoggingEnabled())
      getLogger().fine("Rectangles pruned: " + pruned);
    
    // polygons
    i      = 0;
    pruned = 0;
    while (i < tree.getViewer().getMapPolygonList().size()) {
      mapobject = tree.getViewer().getMapPolygonList().get(i);
      if (mapobject instanceof TimestampSupporter) {
	if (((TimestampSupporter) mapobject).getTimestamp().compareTo(cutoff) < 0) {
	  tree.getViewer().getMapPolygonList().remove(i);
	  pruned++;
	  continue;
	}
      }
      i++;
    }
    if (isLoggingEnabled())
      getLogger().fine("Polygons pruned: " + pruned);
  }
}
