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
 * FieldCacheUpdateEvent.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

import adams.gui.selection.AbstractFieldCacheItem;

/**
 * Event that gets sent when a field cache gets updated.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FieldCacheUpdateEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 8769821211266240856L;

  /**
   * Constructs the event.
   *
   * @param source	The cache item that triggered the event.
   */
  public FieldCacheUpdateEvent(AbstractFieldCacheItem source) {
    super(source);
  }

  /**
   * Returns the cache item that triggered the event.
   *
   * @return		the trigger
   */
  public AbstractFieldCacheItem getFieldCacheItem() {
    return (AbstractFieldCacheItem) getSource();
  }
}