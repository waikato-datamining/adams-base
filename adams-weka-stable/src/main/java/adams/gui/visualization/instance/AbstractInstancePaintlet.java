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
 * InstanceLinePaintlet.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.container.AbstractDataContainerPaintlet;

/**
 * Ancestor for Instance paintlets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInstancePaintlet
  extends AbstractDataContainerPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -2971846774962333662L;

  /**
   * Returns the instance panel currently in use.
   *
   * @return		the panel in use
   */
  public InstancePanel getInstancePanel() {
    return (InstancePanel) m_Panel;
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }
}
