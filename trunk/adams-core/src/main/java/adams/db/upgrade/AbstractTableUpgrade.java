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
 * AbstractTableUpgrade.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.db.upgrade;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.event.DatabaseConnectionChangeEvent;

/**
 * Abstract superclass for all helper classes that upgrade tables in one way
 * or another.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTableUpgrade
  extends AbstractOptionHandler
  implements DatabaseConnectionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5174695729150073016L;

  /** the information generated during upgrading. */
  protected StringBuilder m_UpgradeInfo;

  /** indicates whether the upgrade has happened. */
  protected boolean m_Upgraded;

  /** database connection. */
  protected transient AbstractDatabaseConnection m_dbc;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_UpgradeInfo = new StringBuilder();
    m_dbc         = null;
  }

  /**
   * Resets the reader (but does not clear the input data!).
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  public void reset() {
    super.reset();

    m_UpgradeInfo = new StringBuilder();
    m_Upgraded    = false;
  }

  /**
   * Sets the database connection to use.
   *
   * @param value	the database connection
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_dbc = value;
  }

  /**
   * Returns the current database connection.
   *
   * @return		the database connection
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_dbc;
  }

  /**
   * Appends the string to the upgrade info plus a new line.
   *
   * @param s		the string to add
   */
  protected void addInfo(String s) {
    m_UpgradeInfo.append(s);
    m_UpgradeInfo.append("\n");
  }

  /**
   * A pre-upgrade hook.
   */
  protected void preUpgrade() {
    boolean 	connected;
    
    if (getDatabaseConnection() == null)
      throw new IllegalStateException("No database connection set!");
    
    if (!getDatabaseConnection().isConnected()) {
      connected = false;
      try {
	connected = getDatabaseConnection().connect();
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to connect to database!", e);
      }
      if (!connected)
	throw new IllegalStateException("Failed to connect to database!");
    }
  }

  /**
   * Performs the actual upgrade.
   */
  protected abstract void doUpgrade();

  /**
   * A post-upgrade hook.
   */
  protected void postUpgrade() {
    getDatabaseConnection().notifyChangeListeners(
	new DatabaseConnectionChangeEvent(
	    DatabaseConnection.getSingleton(),
	    DatabaseConnectionChangeEvent.EventType.GENERAL));
  }

  /**
   * Performs the upgrade if not yet performed and returns the information
   * that was generated during the upgrade (can be empty).
   *
   * @return		(potential) information generated during the upgrade
   */
  public StringBuilder upgrade() {
    if (!m_Upgraded) {
      addInfo("1. preUpgrade\n");
      getLogger().info("Entering 'preUpgrade'...");
      preUpgrade();

      addInfo("\n2. doUpgrade\n");
      getLogger().info("Entering 'doUpgrade'...");
      doUpgrade();

      addInfo("\n3. postUpgrade\n");
      getLogger().info("Entering 'postUpgrade'...");
      postUpgrade();

      getLogger().info("Finished.");
    }

    m_Upgraded = true;

    return m_UpgradeInfo;
  }

  /**
   * Returns a list with classnames of upgrade schemes.
   *
   * @return		the upgrade schemes
   */
  public static String[] getUpgraders() {
    return ClassLister.getSingleton().getClassnames(AbstractTableUpgrade.class);
  }

  /**
   * Instantiates the table upgrade scheme with the given options.
   *
   * @param classname	the classname of the table upgrade scheme to instantiate
   * @param options	the options for the table upgrade scheme
   * @return		the instantiated table upgrade scheme or null if an error occurred
   */
  public static AbstractTableUpgrade forName(String classname, String[] options) {
    AbstractTableUpgrade	result;

    try {
      result = (AbstractTableUpgrade) OptionUtils.forName(AbstractTableUpgrade.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the table upgrade scheme from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			table upgrade scheme to instantiate
   * @return		the instantiated table upgrade scheme or null if an error occurred
   */
  public static AbstractTableUpgrade forCommandLine(String cmdline) {
    return (AbstractTableUpgrade) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
