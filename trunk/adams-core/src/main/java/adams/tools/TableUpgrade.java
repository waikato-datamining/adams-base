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
 * TableUpgrade.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.tools;

import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.upgrade.AbstractTableUpgrade;
import adams.db.upgrade.PassThrough;

/**
 <!-- globalinfo-start -->
 * Tool for running a specific table upgrade.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-upgrade &lt;adams.db.upgrade.AbstractTableUpgrade [options]&gt; (property: tableUpgrade)
 *         The table upgrade scheme to use.
 *         default: adams.db.upgrade.PassThrough
 * </pre>
 *
 * Default options for adams.db.upgrade.PassThrough (-upgrade/tableUpgrade):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TableUpgrade
  extends AbstractDatabaseTool {

  /** for serialization. */
  private static final long serialVersionUID = -1179186237647446679L;

  /** the table upgrade to run. */
  protected AbstractTableUpgrade m_TableUpgrade;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Tool for running a specific table upgrade.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "upgrade", "tableUpgrade",
	    new PassThrough());
  }

  /**
   * Returns the default database connection.
   *
   * @return		the database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the table upgrade to use.
   *
   * @param value	the table upgrade to use
   */
  public void setTableUpgrade(AbstractTableUpgrade value) {
    m_TableUpgrade = value;
  }

  /**
   * Returns the table upgrade to use.
   *
   * @return		the table upgrade to use
   */
  public AbstractTableUpgrade getTableUpgrade() {
    return m_TableUpgrade;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tableUpgradeTipText() {
    return "The table upgrade scheme to use.";
  }

  /**
   * Attempt to run the table upgrade scheme.
   */
  @Override
  protected void doRun() {
    StringBuilder	info;

    m_TableUpgrade.setDatabaseConnection(getDatabaseConnection());
    info = m_TableUpgrade.upgrade();
    getLogger().info(info.toString());
  }
}
