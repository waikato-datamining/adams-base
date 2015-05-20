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
 * WekaDatabaseReader.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.logging.Level;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.DatabaseLoader;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.PlaceholderFile;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionParameterHandler;
import adams.db.SQLStatement;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Executes a query and returns the data either in batch or incremental mode.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaDatabaseReader
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The JDBC URL of the database to query.
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The user for connecting to the database.
 * </pre>
 *
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password for the database user.
 * </pre>
 *
 * <pre>-query &lt;java.lang.String&gt; (property: query)
 * &nbsp;&nbsp;&nbsp;The SQL query to execute.
 * &nbsp;&nbsp;&nbsp;default: select * from result0
 * </pre>
 *
 * <pre>-keys &lt;java.lang.String&gt; (property: keys)
 * &nbsp;&nbsp;&nbsp;The keys to use for identifying a single row (comma-separated list of column
 * &nbsp;&nbsp;&nbsp;names).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-incremental (property: incremental)
 * &nbsp;&nbsp;&nbsp;If set to true, then single Instance objects are output, otherwise just
 * &nbsp;&nbsp;&nbsp;one Instances object.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaDatabaseReader
  extends AbstractSource
  implements ProvenanceSupporter, DatabaseConnectionParameterHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1255964542481136089L;

  /** the database URL to query. */
  protected String m_URL;

  /** the database user to use for connecting. */
  protected String m_User;

  /** the password for the user used for connecting. */
  protected BasePassword m_Password;

  /** the query to execute. */
  protected SQLStatement m_Query;

  /** the keys that uniquely identify a single row. */
  protected String m_Keys;

  /** whether to output the data row-by-row. */
  protected boolean m_Incremental;

  /** whether to output data in sparse format. */
  protected boolean m_SparseFormat;

  /** a custom properties file to use instead of default one. */
  protected PlaceholderFile m_CustomPropsFile;

  /** the structure. */
  protected Instances m_Structure;

  /** the actual loader for loading the data. */
  protected DatabaseLoader m_Source;

  /** the next instance to output. */
  protected Instance m_NextInstance;

  /** the full data. */
  protected Instances m_Data;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes a query and returns the data either in batch or incremental mode.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "url", "URL",
	    DatabaseConnection.getSingleton().getDefaultURL(),
	    false);

    m_OptionManager.add(
	    "user", "user",
	    DatabaseConnection.getSingleton().getDefaultUser(),
	    false);

    m_OptionManager.add(
	    "password", "password",
	    DatabaseConnection.getSingleton().getDefaultPassword(),
	    false);

    m_OptionManager.add(
	    "query", "query",
	    new SQLStatement("select * from result0"));

    m_OptionManager.add(
	    "keys", "keys",
	    "");

    m_OptionManager.add(
	    "incremental", "incremental",
	    false);

    m_OptionManager.add(
	    "sparse-format", "sparseFormat",
	    false);

    m_OptionManager.add(
	    "custom-props", "customPropsFile",
	    new PlaceholderFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    value = QuickInfoHelper.toString(this, "user", m_User);
    if (value == null)
      value = "";
    else
      value += "@";
    result = value;
    result += QuickInfoHelper.toString(this, "URL", m_URL);
    result += QuickInfoHelper.toString(this, "query", m_Query, " using ");
    value   = QuickInfoHelper.toString(this, "customPropsFile", (m_CustomPropsFile.isDirectory() ? null : m_CustomPropsFile));
    if (value != null)
      result += " (custom props: " + value + ")";

    return result;
  }

  /**
   * Sets the database URL to query.
   *
   * @param value	the JDBC URL
   */
  public void setURL(String value) {
    m_URL = value;
    reset();
  }

  /**
   * Returns the query to execute.
   *
   * @return		the query
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The JDBC URL of the database to query.";
  }

  /**
   * Sets the database user.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the database user.
   *
   * @return		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The user for connecting to the database.";
  }

  /**
   * Sets the database password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the database password.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password for the database user.";
  }

  /**
   * Sets the query to execute.
   *
   * @param value	the query
   */
  public void setQuery(SQLStatement value) {
    m_Query = value;
    reset();
  }

  /**
   * Returns the query to execute.
   *
   * @return		the query
   */
  public SQLStatement getQuery() {
    return m_Query;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String queryTipText() {
    return "The SQL query to execute.";
  }

  /**
   * Sets the keys to use for identifying a single row (comma-separated list).
   *
   * @param value	the keys
   */
  public void setKeys(String value) {
    m_Keys = value;
    reset();
  }

  /**
   * Returns the keys to use for identifying a single row (comma-separated list).
   *
   * @return		the keys
   */
  public String getKeys() {
    return m_Keys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keysTipText() {
    return
        "The keys to use for identifying a single row (comma-separated list "
      + "of column names).";
  }

  /**
   * Sets whether to output single Instance objects or just one Instances object.
   *
   * @param value	if true then a single Instance objects are output
   */
  public void setIncremental(boolean value) {
    m_Incremental = value;
    reset();
  }

  /**
   * Returns whether to output single Instance objects or just one Instances
   * object.
   *
   * @return		true if single Instance objects are output
   */
  public boolean getIncremental() {
    return m_Incremental;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String incrementalTipText() {
    return
        "If set to true, then single Instance objects are output, otherwise "
      + "just one Instances object.";
  }

  /**
   * Sets whether to output the data in sparse format.
   *
   * @param value	if true then sparse format is used
   */
  public void setSparseFormat(boolean value) {
    m_SparseFormat = value;
    reset();
  }

  /**
   * Returns whether data is output in sparse format.
   *
   * @return		true if sparse format is used
   */
  public boolean getSparseFormat() {
    return m_SparseFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sparseFormatTipText() {
    return "If set to true, the data will be output in sparse format.";
  }

  /**
   * Sets the custom properties file to use for initializing the database
   * setup instead of WEKA's default one.
   *
   * @param value	the custom props file, ignored if a directory
   */
  public void setCustomPropsFile(PlaceholderFile value) {
    m_CustomPropsFile = value;
    reset();
  }

  /**
   * Returns the custom properties file to use for initializing the database
   * setup instead of WEKA's default one.
   *
   * @return		the custom props file, ignored if a directory
   */
  public PlaceholderFile getCustomPropsFile() {
    return m_CustomPropsFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customPropsFileTipText() {
    return
        "Custom properties file to override the default database settings, "
      + "eg, for accessing a different type of database; ignored if pointing "
      + "to a directory.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instance.class, weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instance.class, Instances.class};
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Structure    = null;
    m_Source       = null;
    m_NextInstance = null;
    m_Data         = null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String		query;

    result = null;

    query = m_Query.getValue();
    query = getVariables().expand(query);
    try {
      m_Source = new DatabaseLoader();
      m_Source.setCustomPropsFile(m_CustomPropsFile.getAbsoluteFile());
      m_Source.setUrl(m_URL);
      m_Source.setUser(m_User);
      m_Source.setPassword(m_Password.getValue());
      m_Source.setKeys(m_Keys);
      m_Source.setQuery(query);
      m_Source.setSparseData(m_SparseFormat);
      if (m_Incremental)
	m_Structure = m_Source.getStructure();
      else
	m_Data = m_Source.getDataSet();
    }
    catch (Exception e) {
      result = handleException("Failed to read from database: " + m_URL, e);
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    if (m_Incremental) {
      try {
	if (m_NextInstance != null)
	  result = new Token(m_NextInstance);
	else
	  result = new Token(m_Source.getNextInstance(m_Structure));
	m_NextInstance = null;
      }
      catch (Exception e) {
	result = null;
	getLogger().log(Level.SEVERE, "Failed to read next instance", e);
      }
      if (result == null) {
	m_Structure = null;
	m_Source    = null;
      }
    }
    else {
      result   = new Token(m_Data);
      m_Source = null;
      m_Data   = null;
    }

    if (result != null)
      updateProvenance(result);

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    boolean	result;

    result = false;

    if (m_Incremental) {
      if ((m_Source != null) && (m_NextInstance == null)) {
	try {
	  m_NextInstance = m_Source.getNextInstance(m_Structure);
	}
	catch (Exception e) {
	  m_NextInstance = null;
	  getLogger().log(Level.SEVERE, "Failed to obtain next instance", e);
	}
      }
      result = (m_NextInstance != null);
    }
    else {
      result = (m_Data != null);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, ((Token) cont).getPayload().getClass()));
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    m_Structure    = null;
    m_Source       = null;
    m_NextInstance = null;
    m_Data         = null;
  }
}
