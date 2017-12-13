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
 * AbstractXYSequencePointHitDetector.java
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.plot.AbstractDistanceBasedHitDetector;
import adams.gui.visualization.core.plot.ContainerHitDetector;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for XY sequence point hit detectors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractXYSequencePointHitDetector
  extends AbstractDistanceBasedHitDetector<List<XYSequencePoint>, String>
  implements ContainerHitDetector<List<XYSequencePoint>, String, XYSequenceContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 8048373104725687691L;

  /** the owner of this detector. */
  protected XYSequencePaintlet m_Owner;

  /**
   * Initializes the hit detector (constructor only for GOE) with no owner.
   */
  public AbstractXYSequencePointHitDetector() {
    this(null);
  }

  /**
   * Initializes the hit detector.
   *
   * @param owner	the paintlet that uses this detector
   */
  public AbstractXYSequencePointHitDetector(XYSequencePaintlet owner) {
    super();
    setOwner(owner);
  }

  /**
   * Sets the owner.
   *
   * @param value	the owning panel
   */
  public void setOwner(XYSequencePaintlet value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owning paintlet
   */
  public XYSequencePaintlet getOwner() {
    return m_Owner;
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  @Override
  protected abstract List<XYSequencePoint> isHit(MouseEvent e);

  /**
   * Performs the action when a hit is detected.
   *
   * @param e		the MouseEvent (for coordinates)
   * @param hit		the object that got determined by the hit
   * @return		the generated appendix for the tiptext
   */
  @Override
  protected String processHit(MouseEvent e, List<XYSequencePoint> hit) {
    String			result;
    int				i;
    XYSequence			sp;
    XYSequenceContainer 	cont;

    result = " (";
    for (i = 0; i < hit.size(); i++) {
      if (i > 0)
	result += ", ";
      sp  = (XYSequence) hit.get(i).getParent();
      cont = m_Owner.getSequencePanel().getContainerManager().newContainer(sp);
      result += cont.getDisplayID();
    }
    result += ")";

    return result;
  }

  /**
   * Detects hits and associates them with the containers.
   *
   * @param e		the mouse event to analyze for a hit
   * @return		optional result of processing the event
   */
  public XYSequenceContainer[] containers(MouseEvent e) {
    List<XYSequenceContainer>	result;
    List<XYSequencePoint> 	hit;
    XYSequenceContainerManager	manager;
    int				index;

    result  = new ArrayList<>();
    hit     = isHit(e);
    if (hit == null)
      return new XYSequenceContainer[0];

    manager = m_Owner.getSequencePanel().getContainerManager();
    for (XYSequencePoint point: hit) {
      index = manager.indexOf(point.getParent().getID());
      if (index > -1)
        result.add(manager.get(index));
    }

    return result.toArray(new XYSequenceContainer[result.size()]);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Owner = null;

    super.cleanUp();
  }
}
