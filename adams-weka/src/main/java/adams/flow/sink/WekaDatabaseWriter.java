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
 * WekaDatabaseWriter.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.util.Hashtable;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.DatabaseSaver;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.PlaceholderFile;
import adams.db.DatabaseConnection;

/**
 <!-- globalinfo-start -->
 * Actor for saving a weka.core.Instances object in a database.<br/>
 * The relation name of the incoming dataset can be used to replace the current filename (path and extension are kept).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaDatabaseWriter
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
 * <pre>-table &lt;java.lang.String&gt; (property: tableName)
 * &nbsp;&nbsp;&nbsp;The name of the table to store the data in.
 * &nbsp;&nbsp;&nbsp;default: weka
 * </pre>
 *
 * <pre>-auto-key-generation (property: autoKeyGeneration)
 * &nbsp;&nbsp;&nbsp;If set to true, a primary key is automatically generated.
 * </pre>
 *
 * <pre>-use-relation-as-table (property: useRelationNameAsTable)
 * &nbsp;&nbsp;&nbsp;If set to true, the relation name is used as table name.
 * </pre>
 *
 * <pre>-custom-props &lt;adams.core.io.PlaceholderFile&gt; (property: customPropsFile)
 * &nbsp;&nbsp;&nbsp;Custom properties file to override the default database settings, eg, for
 * &nbsp;&nbsp;&nbsp;accessing a different type of database; ignored if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaDatabaseWriter
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = 7509908838736709270L;

  /** the key for storing the current incremental clusterer in the backup. */
  public final static String BACKUP_SAVER = "saver";

  /** the database URL to query. */
  protected String m_URL;

  /** the database user to use for connecting. */
  protected String m_User;

  /** the password for the user used for connecting. */
  protected BasePassword m_Password;

  /** protected the name of the table to store the data in. */
  protected String m_TableName;

  /** whether to automatically generate a primary key. */
  protected boolean m_AutoKeyGeneration;

  /** whether to use the relation as table name. */
  protected boolean m_UseRelationNameAsTable;

  /** a custom properties file to use instead of default one. */
  protected PlaceholderFile m_CustomPropsFile;

  /** the database saver. */
  protected DatabaseSaver m_Saver;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor for saving a weka.core.Instances object in a database.\n"
      + "The relation name of the incoming dataset can be used to replace the "
      + "current filename (path and extension are kept).";
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
	    "table", "tableName",
	    "weka");

    m_OptionManager.add(
	    "auto-key-generation", "autoKeyGeneration",
	    false);

    m_OptionManager.add(
	    "use-relation-as-table", "useRelationNameAsTable",
	    false);

    m_OptionManager.add(
	    "custom-props", "customPropsFile",
	    new PlaceholderFile("."));
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Saver = null;
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
    result += QuickInfoHelper.toString(this, "URL", m_URL, "@");
    value = QuickInfoHelper.toString(this, "customPropsFile", (m_CustomPropsFile.isDirectory() ? null : " (custom props: " + m_CustomPropsFile + ")"));
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_SAVER);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Saver != null)
      result.put(BACKUP_SAVER, m_Saver);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_SAVER)) {
      m_Saver = (weka.core.converters.DatabaseSaver) state.get(BACKUP_SAVER);
      state.remove(BACKUP_SAVER);
    }

    super.restoreState(state);
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
   * Sets the table name to store the data in.
   *
   * @param value	the table name
   */
  public void setTableName(String value) {
    m_TableName = value;
    reset();
  }

  /**
   * Returns the table name to store the data in.
   *
   * @return		the table name
   */
  public String getTableName() {
    return m_TableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tableNameTipText() {
    return "The name of the table to store the data in.";
  }

  /**
   * Sets whether to automatically generate a primary key.
   *
   * @param value	if true then a primary key is generated
   */
  public void setAutoKeyGeneration(boolean value) {
    m_AutoKeyGeneration = value;
    reset();
  }

  /**
   * Returns whether a primary key is automatically generated.
   *
   * @return		true if a primary key is generated
   */
  public boolean getAutoKeyGeneration() {
    return m_AutoKeyGeneration;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String autoKeyGenerationTipText() {
    return "If set to true, a primary key is automatically generated.";
  }

  /**
   * Sets whether to output single Instance objects or just one Instances object.
   *
   * @param value	if true then a single Instance objects are output
   */
  public void setUseRelationNameAsTable(boolean value) {
    m_UseRelationNameAsTable = value;
    reset();
  }

  /**
   * Returns whether to output single Instance objects or just one Instances
   * object.
   *
   * @return		true if single Instance objects are output
   */
  public boolean getUseRelationNameAsTable() {
    return m_UseRelationNameAsTable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRelationNameAsTableTipText() {
    return "If set to true, the relation name is used as table name.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, Instance.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Instances		data;
    Instance		inst;

    result = null;

    if (m_InputToken.getPayload() instanceof Instance) {
      inst = (Instance) m_InputToken.getPayload();
      data = inst.dataset();
    }
    else {
      data = (Instances) m_InputToken.getPayload();
      inst = null;
    }

    try {
      if (m_Saver == null) {
	m_Saver = new DatabaseSaver();
	m_Saver.setUrl(m_URL);
	m_Saver.setUser(m_User);
	m_Saver.setPassword(m_Password.getValue());
	m_Saver.setTableName(m_TableName);
	m_Saver.setRelationForTableName(m_UseRelationNameAsTable);
	m_Saver.setAutoKeyGeneration(m_AutoKeyGeneration);
	if (!m_CustomPropsFile.isDirectory())
	  m_Saver.setCustomPropsFile(m_CustomPropsFile.getAbsoluteFile());
      }
      if (inst == null) {
	m_Saver.setInstances(data);
	m_Saver.writeBatch();
      }
      else {
	m_Saver.writeIncremental(inst);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to write to database: " + m_URL, e);
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Saver = null;

    super.wrapUp();
  }
}
