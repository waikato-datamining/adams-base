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
 * ItemsRemovedEvent.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

/**
 * Event that gets sent when items are to be deleted.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveItemsEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 9211592165507512950L;

  /** the indices of the items that were removed. */
  protected int[] m_Indices;

  /**
   * Initializes the object.
   *
   * @param src		the source that triggered the event
   * @param indices	the indices of the items to remove
   */
  public RemoveItemsEvent(Object src, int[] indices) {
    super(src);

    if (indices != null)
      m_Indices = indices.clone();
    else
      m_Indices = null;
  }

  /**
   * Checks whether there are indices stored for this event.
   *
   * @return		true if indices of the items are available
   */
  public boolean hasIndices() {
    return (m_Indices != null);
  }

  /**
   * Returns the stored indices.
   *
   * @return		the indices, can be null if none stored
   */
  public int[] getIndices() {
    return m_Indices;
  }
}
