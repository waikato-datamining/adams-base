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
 * LargeObjectDbWriter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.data.conversion.Conversion;
import adams.data.conversion.UnknownToUnknown;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.LargeObjectType;
import adams.db.SQLF;
import adams.db.SQLStatement;
import adams.db.SQLUtils;
import adams.flow.core.ActorUtils;
import adams.flow.core.Unknown;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;

/**
 <!-- globalinfo-start -->
 * Allows storing large objects in a SQL database.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LargeObjectDbWriter
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-blob-conversion &lt;adams.data.conversion.Conversion&gt; (property: blobConversion)
 * &nbsp;&nbsp;&nbsp;The conversion to apply to turn BLOB objects into byte arrays.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.UnknownToUnknown
 * </pre>
 *
 * <pre>-type &lt;BLOB|CLOB&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of large object to store.
 * &nbsp;&nbsp;&nbsp;default: BLOB
 * </pre>
 *
 * <pre>-query &lt;adams.db.SQLStatement&gt; (property: query)
 * &nbsp;&nbsp;&nbsp;The query to execute for storing the large objects; objects get attached
 * &nbsp;&nbsp;&nbsp;to the first column of the UPDATE&#47;INSERT statement.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LargeObjectDbWriter
    extends AbstractDbSink {

  private static final long serialVersionUID = -2679737280146743263L;

  /** the SQL statement to execute. */
  protected SQLStatement m_Query;

  /** the type of object to read. */
  protected LargeObjectType m_Type;

  /** the conversion to use blobs. */
  protected Conversion m_BlobConversion;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows storing large objects in a SQL database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"blob-conversion", "blobConversion",
	new UnknownToUnknown());

    m_OptionManager.add(
	"type", "type",
	LargeObjectType.BLOB);

    m_OptionManager.add(
	"query", "query",
	new SQLStatement());
  }

  /**
   * Sets the conversion to apply to BLOB byte arrays.
   *
   * @param value	the conversion
   */
  public void setBlobConversion(Conversion value) {
    m_BlobConversion = value;
    reset();
  }

  /**
   * Returns the conversion to apply to BLOB byte arrays.
   *
   * @return		the conversion
   */
  public Conversion getBlobConversion() {
    return m_BlobConversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String blobConversionTipText() {
    return "The conversion to apply to turn BLOB objects into byte arrays.";
  }

  /**
   * Sets the type to use.
   *
   * @param value	the type
   */
  public void setType(LargeObjectType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type to use.
   *
   * @return		the type
   */
  public LargeObjectType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of large object to store.";
  }

  /**
   * Sets the query to use.
   *
   * @param value	the query
   */
  public void setQuery(SQLStatement value) {
    m_Query = value;
    reset();
  }

  /**
   * Returns the query to use.
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
    return "The prepared statement to execute for storing the large objects; objects get attached to the first column of the UPDATE/INSERT statement (denoted by ?); variables get expanded automatically.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "blobConversion", m_BlobConversion, "blob conversion: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");
    result += QuickInfoHelper.toString(this, "query", m_Query, ", query: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    switch (m_Type) {
      case BLOB:
	return new Class[]{Unknown.class};
      case CLOB:
	return new Class[]{String.class};
      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
    }
  }

  /**
   * Returns the default database connection.
   *
   * @return the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return the database connection to use
   */
  @Override
  protected AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
	this, adams.flow.standalone.DatabaseConnectionProvider.class, getDefaultDatabaseConnection());
  }

  /**
   * Performs the actual database query.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String		result;
    String		query;
    PreparedStatement	stmt;
    byte[]		bytes;
    String		msg;
    Object		output;
    Blob		blob;
    Clob		clob;

    result = null;

    query = getVariables().expand(m_Query.getValue());
    stmt  = null;
    blob  = null;
    clob  = null;
    try {
      stmt = SQLF.getSingleton(m_DatabaseConnection).prepareStatement(query);
      switch (m_Type) {
	case BLOB:
	  try {
	    m_BlobConversion.setInput(m_InputToken.getPayload());
	    msg = m_BlobConversion.convert();
	    if (msg != null)
	      return "Failed to convert input data to byte array using: " + OptionUtils.getCommandLine(m_BlobConversion) + "\n" + msg;
	    output = m_BlobConversion.getOutput();
	    if (!(output instanceof byte[]))
	      return "Blob conversion did not generate a byte array: " + OptionUtils.getCommandLine(m_BlobConversion);
	    bytes = (byte[]) output;
	  }
	  catch (Exception e) {
	    return handleException("Failed to convert input data to byte array using: " + OptionUtils.getCommandLine(m_BlobConversion), e);
	  }
	  blob = m_DatabaseConnection.getConnection(false).createBlob();
	  blob.setBytes(1, bytes);
	  stmt.setBlob(1, blob);
	  break;
	case CLOB:
	  clob = m_DatabaseConnection.getConnection(false).createClob();
	  clob.setString(1, (String) m_InputToken.getPayload());
	  stmt.setClob(1, clob);
	  break;
	default:
	  return "Unhandled type: " + m_Type;
      }
      stmt.execute();
    }
    catch (Exception e) {
      result = handleException("Failed to store large object (" + m_Type + ") in DB using: " + query, e);
    }
    finally {
      SQLUtils.free(blob);
      SQLUtils.free(clob);
      SQLUtils.close(stmt);
    }

    return result;
  }
}
