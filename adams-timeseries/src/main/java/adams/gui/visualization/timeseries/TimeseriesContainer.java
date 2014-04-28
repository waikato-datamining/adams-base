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
 * TimeseriesContainer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import java.awt.Color;

import adams.core.Constants;
import adams.data.timeseries.Timeseries;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.visualization.container.ColorContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.DatabaseContainer;
import adams.gui.visualization.container.NamedContainer;
import adams.gui.visualization.container.VisibilityContainer;

/**
 * A container class for a timeseries and additional information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesContainer
  extends AbstractContainer
  implements VisibilityContainer, NamedContainer, ColorContainer, DatabaseContainer {

  /** for serialization. */
  private static final long serialVersionUID = -2589474045543243525L;

  /** the ID to use for display. */
  protected String m_ID;

  /** whether the timeseries is visible. */
  protected boolean m_Visible;

  /** the associated color. */
  protected Color m_Color;

  /**
   * Initializes the container.
   *
   * @param manager	the owning manager
   * @param data	the timeseries to store (visible)
   */
  public TimeseriesContainer(TimeseriesContainerManager manager, Timeseries data) {
    super(manager, data);
  }

  /**
   * Initializes members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Visible = true;
    m_Color   = Color.WHITE;
  }

  /**
   * Sets the ID to use for display.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
  }

  /**
   * Returns the ID used for display.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns an ID to be used in the GUI (the DB ID with added single quotes
   * to make it unique among the timeseries in the container manager).
   *
   * @return		a short ID for the GUI
   */
  public String getDisplayID() {
    return getID();
  }

  /**
   * For post-processing the payload, just after it got set.
   * <p/>
   * Sets the ID.
   */
  @Override
  protected void postProcessPayload() {
    super.postProcessPayload();
    if (getData() != null)
      setID(getData().getID());
  }

  /**
   * Sets the timeseries.
   *
   * @param value	the timeseries
   */
  public void setData(Timeseries value) {
    setPayload(value);
  }

  /**
   * Returns the stored timeseries.
   *
   * @return		the timeseries
   */
  public Timeseries getData() {
    return (Timeseries) getPayload();
  }

  /**
   * Sets the timeseries visibility.
   *
   * @param value	if true then the timeseries will be visible
   */
  public void setVisible(boolean value) {
    m_Visible = value;

    if ((!m_Updating) && (getManager() != null))
      getManager().notifyDataChangeListeners(
          new DataChangeEvent(getManager(), Type.VISIBILITY, getManager().indexOf(this)));
  }

  /**
   * Returns whether the timeseries is visible.
   *
   * @return		true if the timeseries is visible
   */
  public boolean isVisible() {
    return m_Visible;
  }

  /**
   * Sets the color to use.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;

    if ((!m_Updating) && (getManager() != null))
      getManager().notifyDataChangeListeners(
          new DataChangeEvent(getManager(), Type.UPDATE, getManager().indexOf(this)));
  }

  /**
   * Returns the current color in use.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the database ID.
   *
   * @return		the database ID
   */
  public int getDatabaseID() {
    if (getData().hasReport())
      return getData().getDatabaseID();
    else
      return Constants.NO_ID;
  }

  /**
   * Updates itself with the values from given container (the manager is
   * excluded!). Derived classes need to override this method.
   *
   * @param c		the container to get the values from
   */
  @Override
  public void assign(AbstractContainer c) {
    super.assign(c);

    m_Updating = true;

    if (c instanceof TimeseriesContainer) {
      setID(((TimeseriesContainer) c).getID());
      setVisible(((TimeseriesContainer) c).isVisible());
      setColor(((TimeseriesContainer) c).getColor());
    }

    m_Updating = false;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  @Override
  public int compareTo(AbstractContainer o) {
    TimeseriesContainer	c;

    if (o == null)
      return 1;

    c = (TimeseriesContainer) o;

    return getData().compareToHeader(c.getData());
  }

  /**
   * Returns the hashcode of the ID string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return getID().hashCode();
  }

  /**
   * Returns a short string representation of the container.
   *
   * @return		a string representation
   */
  @Override
  public String toString() {
    return "DB ID: " + getData().getDatabaseID() + ", ID: " + getID() + ", Visible: " + isVisible() + ", Color: " + getColor();
  }
}