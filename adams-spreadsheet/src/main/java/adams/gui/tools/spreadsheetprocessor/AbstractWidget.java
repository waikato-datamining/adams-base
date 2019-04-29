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
 * AbstractWidget.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor;

import adams.core.logging.CustomLoggingLevelObject;
import adams.gui.event.SpreadSheetProcessorEvent;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.tools.SpreadSheetProcessorPanel;

import java.awt.Component;

/**
 * Ancestor for widgets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWidget
  extends CustomLoggingLevelObject
  implements Comparable<AbstractWidget> {

  private static final long serialVersionUID = 5621070732995969891L;

  /** the owner. */
  protected SpreadSheetProcessorPanel m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(SpreadSheetProcessorPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public SpreadSheetProcessorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  public abstract Component getWidget();

  /**
   * Updates the widget.
   */
  public abstract void update();

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  public String toString() {
    return getName();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public abstract void assign(AbstractWidget other);

  /**
   * Notifies the owner.
   *
   * @param type	the type of event
   * @param message	the optional message, can be null
   */
  protected void notifyOwner(EventType type, String message) {
    if (m_Owner == null)
      return;
    m_Owner.processorStateChanged(new SpreadSheetProcessorEvent(m_Owner, type, message));
  }

  /**
   * Compares the two widgets using their name.
   *
   * @param o		the other widget
   * @return		the result of the name comparison
   */
  public int compareTo(AbstractWidget o) {
    return getName().compareTo(o.getName());
  }

  /**
   * Compares equality based on the widget name.
   *
   * @param obj		the other widget to compare with
   * @return		true if the same name
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractWidget) && (compareTo((AbstractWidget) obj) == 0);
  }
}
