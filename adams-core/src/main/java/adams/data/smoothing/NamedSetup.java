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
 * NamedSetup.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import adams.data.container.DataContainer;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * Applies a filter that is referenced via its global setup name (see 'NamedSetups').
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-setup &lt;java.lang.String&gt; (property: setup)
 * &nbsp;&nbsp;&nbsp;The name of the setup to use.
 * &nbsp;&nbsp;&nbsp;default: name_of_setup
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to pass through the smoother
 */
public class NamedSetup<T extends DataContainer>
  extends AbstractDatabaseConnectionSmoother<T> {

  /** for serialization. */
  private static final long serialVersionUID = 1821796377411070232L;

  /** the name of the setup to load. */
  protected adams.core.NamedSetup m_Setup;

  /** the actual scheme. */
  protected AbstractSmoother<T> m_ActualScheme;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies a filter that is referenced via its global setup name (see 'NamedSetups').";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "setup", "setup",
	    new adams.core.NamedSetup());
  }

  /**
   * Resets the filter.
   */
  @Override
  public void reset() {
    super.reset();

    m_ActualScheme = null;
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the setup name.
   *
   * @param value	the name
   */
  public void setSetup(adams.core.NamedSetup value) {
    m_Setup = value;
    if (!m_Setup.isDummy() && !m_Setup.exists())
      getLogger().severe("Warning: named setup '" + m_Setup + "' unknown!");
    reset();
  }

  /**
   * Returns the setup name.
   *
   * @return		the name
   */
  public adams.core.NamedSetup getSetup() {
    return m_Setup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String setupTipText() {
    return "The name of the setup to use.";
  }

  /**
   * Returns the named setup.
   *
   * @return		the actual scheme to use
   */
  protected AbstractSmoother<T> getActualScheme() {
    if (m_ActualScheme == null) {
      m_ActualScheme = (AbstractSmoother<T>) m_Setup.getSetup();
      if (m_ActualScheme == null)
	throw new IllegalStateException(
	    "Failed to instantiate named setup '" + m_Setup + "'!");
      if (m_ActualScheme instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) m_ActualScheme).setDatabaseConnection(getDatabaseConnection());
    }

    return m_ActualScheme;
  }

  /**
   * Applies the named setup to the data.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  @Override
  protected T processData(T data) {
    return (T) getActualScheme().smooth(data);
  }
}
