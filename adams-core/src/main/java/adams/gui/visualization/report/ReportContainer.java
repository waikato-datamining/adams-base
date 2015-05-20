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
 * ReportContainer.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.report;

import adams.core.Constants;
import adams.data.id.DatabaseIDHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.gui.core.BaseTable;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.ContainerWithComponent;
import adams.gui.visualization.container.DatabaseContainer;
import adams.gui.visualization.container.NamedContainer;

/**
 * A container for Report objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportContainer
  extends AbstractContainer
  implements /* VisibilityContainer, */ NamedContainer,
             ContainerWithComponent<BaseTable>, DatabaseContainer {

  /** for serialization. */
  private static final long serialVersionUID = -7972340961639418611L;

  /** whether the instance is visible. */
  protected boolean m_Visible;

  /** the ID to use for display. */
  protected String m_ID;

  /** the associated table. */
  protected BaseTable m_Table;

  /**
   * Initializes the container.
   *
   * @param manager	the manager this container belongs to
   * @param report	the report of this container
   */
  public ReportContainer(ReportContainerManager manager, Report report) {
    super(manager, report);
  }

  /**
   * Initializes the container.
   *
   * @param manager	the manager this container belongs to
   * @param handler	the report handler of this container
   */
  public ReportContainer(ReportContainerManager manager, ReportHandler handler) {
    this(manager, handler.getReport());
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    super.initialize();

    m_Table   = null;
    m_Visible = true;
    if (getReport() != null)
      m_ID = "" + getReport().getDatabaseID();
    else
      m_ID = "" + Constants.NO_ID;
  }

  /**
   * For invalidating cached data.
   * <br><br>
   * Resets the cached Table.
   *
   * @see		#m_Table
   */
  protected void invalidate() {
    super.invalidate();

    m_Table = null;
  }

  /**
   * Returns the payload as Report object.
   *
   * @return		the report
   */
  public Report getReport() {
    return (Report) getPayload();
  }

  /**
   * Sets the instance's visibility.
   *
   * @param value	if true then the instance will be visible
   */
  public void setVisible(boolean value) {
    m_Visible = value;

    if ((!m_Updating) && (getManager() != null))
      getManager().notifyDataChangeListeners(
          new DataChangeEvent(getManager(), Type.VISIBILITY, getManager().indexOf(this)));
  }

  /**
   * Returns whether the instance is visible.
   *
   * @return		true if the instance is visible
   */
  public boolean isVisible() {
    return m_Visible;
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
   * to make it unique among the chromatograms in the container manager).
   *
   * @return		a short ID for the GUI
   */
  public String getDisplayID() {
    return getID();
  }

  /**
   * Checks whether a database ID is available.
   *
   * @return		true if a database ID is available
   */
  public boolean hasDatabaseID() {
    return (getPayload() instanceof DatabaseIDHandler);
  }

  /**
   * Returns the database ID of the payload, if available.
   *
   * @return		the DB ID, {@link Constants#NO_ID} if not available
   */
  public int getDatabaseID() {
    if (getPayload() instanceof DatabaseIDHandler)
      return ((DatabaseIDHandler) getPayload()).getDatabaseID();
    else
      return Constants.NO_ID;
  }

  /**
   * Checks whether a component is stored.
   *
   * @return		true if a component is available
   */
  public boolean hasComponent() {
    return (m_Table != null);
  }

  /**
   * Sets the component to associate.
   *
   * @param value	the component
   */
  public void setComponent(BaseTable value) {
    m_Table = value;
  }

  /**
   * Returns the stored component.
   *
   * @return		the component, null if none available
   */
  public BaseTable getComponent() {
    return m_Table;
  }
}
