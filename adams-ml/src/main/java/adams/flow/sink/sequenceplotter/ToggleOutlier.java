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
 * ToggleOutlier.java
 * Copyright (C) 2015-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.flow.control.RemoveOutliers;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.CrossHitDetector;
import adams.gui.visualization.sequence.XYSequenceContainer;

import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Toggles the outlier state of data points.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ToggleOutlier
  extends AbstractMouseClickAction {

  private static final long serialVersionUID = -214148459426250712L;

  /** the hit detector to use. */
  protected AbstractXYSequencePointHitDetector m_HitDetector;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Toggles the outlier state of the data point(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "hit-detector", "hitDetector",
      new CrossHitDetector());
  }

  /**
   * Sets the hit detector to use.
   *
   * @param value 	the hit detector
   */
  public void setHitDetector(AbstractXYSequencePointHitDetector value) {
    m_HitDetector = value;
    reset();
  }

  /**
   * Returns the hit detector to use.
   *
   * @return 		the hit detector
   */
  public AbstractXYSequencePointHitDetector getHitDetector() {
    return m_HitDetector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hitDetectorTipText() {
    return "The hit detector to use.";
  }

  /**
   * Sets or toggles the outlier state of the specified points.
   *
   * @param panel 	the panel the points belong to
   * @param hits 	the points to process
   */
  protected void toggleHits(SequencePlotterPanel panel, List<XYSequencePoint> hits) {
    SequencePlotPoint		point;

    for (XYSequencePoint hit : hits) {
      if (hit instanceof SequencePlotPoint) {
	point = (SequencePlotPoint) hit;
	if (point.hasMetaData()) {
	  if (point.getMetaData().containsKey(RemoveOutliers.KEY_OUTLIER))
	    point.getMetaData().put(
	      RemoveOutliers.KEY_OUTLIER,
	      !((Boolean) point.getMetaData().get(RemoveOutliers.KEY_OUTLIER)));
	  else
	    point.getMetaData().put(
	      RemoveOutliers.KEY_OUTLIER,
	      true);
	}
	else {
	  point.setMetaData(new HashMap<>());
	  point.getMetaData().put(
	    RemoveOutliers.KEY_OUTLIER,
	    true);
	}
      }
    }
    panel.update();
  }

  /**
   * Displays the points that were surrounded by the polygon.
   *
   * @param panel	the associated panel
   */
  protected void togglePolygonPoints(SequencePlotterPanel panel) {
    int[]			x;
    int[]			y;
    int				i;
    Polygon 			poly;
    List<XYSequencePoint> 	hits;
    int				posX;
    int				posY;
    AxisPanel 			axisX;
    AxisPanel			axisY;
    XYSequenceContainer 	cont;
    XYSequence 			seq;

    if (panel.getSelection().size() < 3) {
      panel.clearSelection();
      return;
    }

    axisX = panel.getPlot().getAxis(Axis.BOTTOM);
    axisY = panel.getPlot().getAxis(Axis.LEFT);

    // create polygon
    x = new int[panel.getSelection().size()];
    y = new int[panel.getSelection().size()];
    for (i = 0; i < panel.getSelection().size(); i++) {
      x[i] = axisX.valueToPos(panel.getSelection().get(i).getX());
      y[i] = axisY.valueToPos(panel.getSelection().get(i).getY());
    }
    poly = new Polygon(x, y, x.length);

    // iterate data
    hits  = new ArrayList<>();
    for (i = 0; i < panel.getSequenceManager().count(); i++) {
      cont = (XYSequenceContainer) panel.getSequenceManager().get(i);
      seq  = cont.getData();
      for (XYSequencePoint point: seq.toList()) {
	posX = axisX.valueToPos(point.getX());
	posY = axisY.valueToPos(point.getY());
	if (poly.contains(posX, posY))
	  hits.add(point);
      }
    }

    // clear points
    panel.clearSelection();
    panel.update();

    // display data
    toggleHits(panel, hits);
  }

  /**
   * Gets triggered if the user clicks on the canvas.
   *
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  public void mouseClickOccurred(SequencePlotterPanel panel, MouseEvent e) {
    List<XYSequencePoint>	located;

    if (MouseUtils.isLeftClick(e) && KeyUtils.isNoneDown(e.getModifiersEx())) {
      e.consume();
      if (m_HitDetector.getOwner() != panel.getDataPaintlet())
	m_HitDetector.setOwner(panel.getDataPaintlet());
      located = m_HitDetector.locate(e);
      if (located != null)
	toggleHits(panel, located);
    }
    else if (MouseUtils.isRightClick(e) && KeyUtils.isShiftDown(e.getModifiersEx())) {
      e.consume();
      togglePolygonPoints(panel);
    }
  }
}
