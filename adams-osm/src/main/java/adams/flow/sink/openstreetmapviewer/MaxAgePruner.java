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
 * MaxAgePruner.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.util.Date;

import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

import adams.core.base.BaseDateTime;
import adams.data.mapobject.TimestampSupporter;

/**
 <!-- globalinfo-start -->
 * Removes map objects that are older than a specific timespan compared to the newest map object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-max-age &lt;adams.core.base.BaseDateTime&gt; (property: maxAge)
 * &nbsp;&nbsp;&nbsp;The maximum age; eg 'START -7 DAY' with 'START' being the date of the newest 
 * &nbsp;&nbsp;&nbsp;mapobject.
 * &nbsp;&nbsp;&nbsp;default: START -7 DAY
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaxAgePruner
  extends AbstractMapObjectPruner {

  /** for serialization. */
  private static final long serialVersionUID = -135743438219473331L;

  /** the maximum age. */
  protected BaseDateTime m_MaxAge;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes map objects that are older than a specific timespan compared to the newest map object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "max-age", "maxAge",
	    new BaseDateTime(BaseDateTime.START + " -7 DAY"));
  }

  /**
   * Sets the maximum age date.
   *
   * @param value	the date
   */
  public void setMaxAge(BaseDateTime value) {
    m_MaxAge = value;
    reset();
  }

  /**
   * Returns the maximum age date.
   *
   * @return		the date
   */
  public BaseDateTime getMaxAge() {
    return m_MaxAge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxAgeTipText() {
    return "The maximum age; eg 'START -7 DAY' with 'START' being the date of the newest mapobject.";
  }

  /**
   * Prunes the map objects.
   * 
   * @param tree	the tree to prune
   */
  @Override
  protected void doPrune(JMapViewerTree tree) {
    Date	newest;
    Date 	cutoff;
    int		i;
    MapObject	mapobject;
    int		pruned;

    // find date of newest mapobject
    newest = new BaseDateTime(BaseDateTime.INF_PAST).dateValue();
    for (MapMarker item: tree.getViewer().getMapMarkerList()) {
      if (item instanceof TimestampSupporter) {
	if (((TimestampSupporter) item).getTimestamp().compareTo(newest) > 0)
	  newest = ((TimestampSupporter) item).getTimestamp();
      }
    }
    for (MapRectangle item: tree.getViewer().getMapRectangleList()) {
      if (item instanceof TimestampSupporter) {
	if (((TimestampSupporter) item).getTimestamp().compareTo(newest) > 0)
	  newest = ((TimestampSupporter) item).getTimestamp();
      }
    }
    for (MapPolygon item: tree.getViewer().getMapPolygonList()) {
      if (item instanceof TimestampSupporter) {
	if (((TimestampSupporter) item).getTimestamp().compareTo(newest) > 0)
	  newest = ((TimestampSupporter) item).getTimestamp();
      }
    }
    if (isLoggingEnabled())
      getLogger().fine("newest: " + newest);
    
    m_MaxAge.setStart(newest);
    cutoff = m_MaxAge.dateValue();
    if (isLoggingEnabled())
      getLogger().fine("cutoff: " + cutoff);
    
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
	  continue;
	}
      }
      i++;
    }
    if (isLoggingEnabled())
      getLogger().fine("Polygons pruned: " + pruned);
  }
}
