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
 * AbstractDatabaseCheck.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.db.AbstractDatabaseConnection;

/**
 * Ancestor for standalone actors that check project-specific database
 * connections.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDatabaseCheck
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1726172998200420556L;

  /** the regular expression to use for matching the JDBC URL. */
  protected BaseRegExp m_RegExp;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regExp", m_RegExp);
  }

  /**
   * Sets the regular expression to use.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression in use.
   *
   * @return 		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to match the JDBC URL against";
  }

  /**
   * Determines the database connection in use.
   *
   * @return		the database connection
   */
  protected abstract AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    String			url;
    AbstractDatabaseConnection	conn;

    result = null;

    conn = getDatabaseConnection();
    if (!conn.isConnected()) {
      result = "No connection to database!";
    }
    else {
      url = conn.getURL();
      if (!m_RegExp.isMatch(url))
	result = "Database URL '" + url + "' does not match regular expression '" + m_RegExp + "'!";
    }

    return result;
  }
}
