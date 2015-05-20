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
 * SimplePruner.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import org.openstreetmap.gui.jmapviewer.JMapViewerTree;

/**
 <!-- globalinfo-start -->
 * Prunes according to a simple upper limit of objects, removing the older ones.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-max-markers &lt;int&gt; (property: maxMarkers)
 * &nbsp;&nbsp;&nbsp;The maximum number of markers to keep; -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-max-rectangles &lt;int&gt; (property: maxRectangles)
 * &nbsp;&nbsp;&nbsp;The maximum number of rectangles to keep; -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-max-polygons &lt;int&gt; (property: maxPolygons)
 * &nbsp;&nbsp;&nbsp;The maximum number of polygons to keep; -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimplePruner
  extends AbstractMapObjectPruner {

  /** for serialization. */
  private static final long serialVersionUID = -135743438219473331L;

  /** the maximum for markers. */
  protected int m_MaxMarkers;

  /** the maximum for rectangles. */
  protected int m_MaxRectangles;

  /** the maximum for polygons. */
  protected int m_MaxPolygons;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Prunes according to a simple upper limit of objects, removing the older ones.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "max-markers", "maxMarkers",
	    -1, -1, null);

    m_OptionManager.add(
	    "max-rectangles", "maxRectangles",
	    -1, -1, null);

    m_OptionManager.add(
	    "max-polygons", "maxPolygons",
	    -1, -1, null);
  }

  /**
   * Sets the maximum number of markers to keep.
   *
   * @param value	the maximum; -1 for unlimited
   */
  public void setMaxMarkers(int value) {
    if (value >= -1) {
      m_MaxMarkers = value;
      reset();
    }
    else {
      getLogger().warning("Maximum for Markers must be at least -1, provided: " + value);
    }
  }

  /**
   * Returns the maximum number of markers to keep.
   *
   * @return		the maximum; -1 for unlimited
   */
  public int getMaxMarkers() {
    return m_MaxMarkers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxMarkersTipText() {
    return "The maximum number of markers to keep; -1 for unlimited.";
  }

  /**
   * Sets the maximum number of rectangles to keep.
   *
   * @param value	the maximum; -1 for unlimited
   */
  public void setMaxRectangles(int value) {
    if (value >= -1) {
      m_MaxRectangles = value;
      reset();
    }
    else {
      getLogger().warning("Maximum for Rectangles must be at least -1, provided: " + value);
    }
  }

  /**
   * Returns the maximum number of rectangles to keep.
   *
   * @return		the maximum; -1 for unlimited
   */
  public int getMaxRectangles() {
    return m_MaxRectangles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxRectanglesTipText() {
    return "The maximum number of rectangles to keep; -1 for unlimited.";
  }

  /**
   * Sets the maximum number of polygons to keep.
   *
   * @param value	the maximum; -1 for unlimited
   */
  public void setMaxPolygons(int value) {
    if (value >= -1) {
      m_MaxPolygons = value;
      reset();
    }
    else {
      getLogger().warning("Maximum for Polygons must be at least -1, provided: " + value);
    }
  }

  /**
   * Returns the maximum number of polygons to keep.
   *
   * @return		the maximum; -1 for unlimited
   */
  public int getMaxPolygons() {
    return m_MaxPolygons;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxPolygonsTipText() {
    return "The maximum number of polygons to keep; -1 for unlimited.";
  }

  /**
   * Prunes the map objects.
   * 
   * @param tree	the tree to prune
   */
  @Override
  protected void doPrune(JMapViewerTree tree) {
    int		pruned;
    
    if (m_MaxMarkers > -1) {
      pruned = 0;
      while (tree.getViewer().getMapMarkerList().size() > m_MaxMarkers) {
	tree.getViewer().getMapMarkerList().remove(0);
	pruned++;
      }
      if (isLoggingEnabled())
	getLogger().fine("Markers pruned: " + pruned);
    }
    
    if (m_MaxRectangles > -1) {
      pruned = 0;
      while (tree.getViewer().getMapRectangleList().size() > m_MaxRectangles) {
	tree.getViewer().getMapRectangleList().remove(0);
	pruned++;
      }
      if (isLoggingEnabled())
	getLogger().fine("Rectangles pruned: " + pruned);
    }
    
    if (m_MaxPolygons > -1) {
      pruned = 0;
      while (tree.getViewer().getMapPolygonList().size() > m_MaxPolygons) {
	tree.getViewer().getMapPolygonList().remove(0);
	pruned++;
      }
      if (isLoggingEnabled())
	getLogger().fine("Polygons pruned: " + pruned);
    }
  }
}
