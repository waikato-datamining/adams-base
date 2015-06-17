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
 * AbstractListTables.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.db.SQL;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for sources that list tables from a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractListTables
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8462709950859959951L;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /** the database connection. */
  protected adams.db.AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists tables of the current database connection.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> options;

    result = QuickInfoHelper.toString(this, "regExp", m_RegExp, (m_Invert ? "! " : ""));

    // further options
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", getOutputArray(), "array"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the table names in an array rather than one by one.";
  }

  /**
   * Sets the regular expression to match the table names against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the table names against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the table names.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected abstract adams.db.AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Configures the database connection if necessary.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpDatabaseConnection() {
    String	result;

    result = null;

    if (m_DatabaseConnection == null) {
      m_DatabaseConnection = getDatabaseConnection();
      if (m_DatabaseConnection == null)
	result = "No database connection available!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    ResultSet		rs;
    List<String>	tables;

    result = setUpDatabaseConnection();
    tables = new ArrayList<>();

    if (result == null) {
      rs = null;
      try {
	rs = m_DatabaseConnection.getConnection(false).getMetaData().getTables(null, null, "%", null);
	while (rs.next())
	  tables.add(rs.getString(3));
      }
      catch (Exception e) {
	result = handleException("Failed to obtain list of tables!", e);
      }
      finally {
	SQL.closeAll(rs);
      }
    }

    if (result == null) {
      if (m_RegExp.isMatchAll()) {
	if (!m_Invert)
	  m_Queue.addAll(tables);
      }
      else {
	for (String name : tables) {
	  if (m_Invert && !m_RegExp.isMatch(name))
	    m_Queue.add(name);
	  else if (!m_Invert && m_RegExp.isMatch(name))
	    m_Queue.add(name);
	}
      }

      Collections.sort(m_Queue);
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_DatabaseConnection = null;

    super.wrapUp();
  }
}
