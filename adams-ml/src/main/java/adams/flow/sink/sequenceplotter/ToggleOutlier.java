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

import adams.data.sequence.XYSequencePoint;
import adams.flow.control.RemoveOutliers;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.CrossHitDetector;

import java.awt.event.MouseEvent;
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
   * Gets triggered if the user clicks on the canvas.
   *
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  public void mouseClickOccurred(SequencePlotterPanel panel, MouseEvent e) {
    Object			located;
    List<XYSequencePoint> 	hits;
    SequencePlotPoint		point;

    if (MouseUtils.isLeftClick(e) && KeyUtils.isNoneDown(e.getModifiersEx())) {
      e.consume();
      if (m_HitDetector.getOwner() != panel.getDataPaintlet())
	m_HitDetector.setOwner(panel.getDataPaintlet());
      located = m_HitDetector.locate(e);
      if (located instanceof List) {
	hits = (List<XYSequencePoint>) located;
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
    }
  }
}
