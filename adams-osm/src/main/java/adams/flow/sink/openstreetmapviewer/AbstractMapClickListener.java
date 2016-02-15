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
 * AbstractMapClickListener.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.event.MapClickEvent;
import adams.gui.event.MapClickListener;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

import java.awt.event.MouseEvent;

/**
 * Ancestor for classes that listen to clicks on a {@link JMapViewer}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMapClickListener
  extends AbstractOptionHandler
  implements MapClickListener, ShallowCopySupporter<AbstractMapClickListener> {

  /** for serialization. */
  private static final long serialVersionUID = 4468210013390130296L;

  /** the database connection in use. */
  protected transient adams.db.AbstractDatabaseConnection m_DatabaseConnection;
  
  /**
   * Returns whether a database connection is required.
   * 
   * @return		true if connection required
   */
  public abstract boolean requiresDatabaseConnection();

  /**
   * Determines the database connection in the flow.
   * <br><br>
   * Derived classes can override this method if different database
   * connection objects need to be located.
   *
   * @param actor	the actor to use for looking up database connection
   * @return		the database connection to use
   */
  protected adams.db.AbstractDatabaseConnection getDatabaseConnection(Actor actor) {
    return ActorUtils.getDatabaseConnection(
	  actor,
	  adams.flow.standalone.DatabaseConnection.class,
	  adams.db.DatabaseConnection.getSingleton());
  }

  /**
   * Updates, if necessary, its database connection using the specified 
   * actor as starting point.
   * 
   * @param actor	the actor to use for lookin up database connection
   * @see		#getDatabaseConnection(Actor)
   */
  public void updateDatabaseConnection(Actor actor) {
    m_DatabaseConnection = getDatabaseConnection(actor);
  }

  /**
   * Performs the processing of the mouse click.
   * 
   * @param viewer	the associated viewer
   * @param e		the associated mouse event
   * @return		true if mouse event should get consumed
   */
  protected abstract boolean processClick(JMapViewer viewer, MouseEvent e);

  /**
   * Gets called when user clicks on a {@link JMapViewer}.
   * 
   * @param e		the event
   */
  public void mapClicked(MapClickEvent e) {
    if (processClick(e.getViewer(), e.getMouseEvent()))
      e.getMouseEvent().consume();
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  @Override
  public AbstractMapClickListener shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public AbstractMapClickListener shallowCopy(boolean expand) {
    return (AbstractMapClickListener) OptionUtils.shallowCopy(this, expand);
  }
}
