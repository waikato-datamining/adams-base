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
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import java.awt.event.MouseEvent;
import java.util.Vector;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.plot.AbstractDistanceBasedHitDetector;

/**
 * Ancestor for XY sequence point hit detectors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractXYSequencePointHitDetector
  extends AbstractDistanceBasedHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = 8048373104725687691L;

  /** the owner of this detector. */
  protected XYSequencePanel m_Owner;

  /**
   * Initializes the hit detector.
   *
   * @param owner	the panel that uses this detector
   */
  public AbstractXYSequencePointHitDetector(XYSequencePanel owner) {
    super();
    setOwner(owner);
  }

  /**
   * Sets the owner.
   *
   * @param value	the owning panel
   */
  public void setOwner(XYSequencePanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owning panel
   */
  public XYSequencePanel getOwner() {
    return m_Owner;
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  protected abstract Object isHit(MouseEvent e);

  /**
   * Performs the action when a hit is detected.
   *
   * @param e		the MouseEvent (for coordinates)
   * @param hit		the object that got determined by the hit
   * @return		the generated appendix for the tiptext
   */
  protected Object processHit(MouseEvent e, Object hit) {
    String			result;
    Vector<XYSequencePoint>	hits;
    int				i;
    XYSequence			sp;
    XYSequenceContainer 	cont;

    hits = (Vector<XYSequencePoint>) hit;

    result = " (";
    for (i = 0; i < hits.size(); i++) {
      if (i > 0)
	result += ", ";
      sp  = (XYSequence) hits.get(i).getParent();
      cont = m_Owner.getContainerManager().newContainer(sp);
      result += cont.getDisplayID();
    }
    result += ")";

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Owner = null;

    super.cleanUp();
  }
}
