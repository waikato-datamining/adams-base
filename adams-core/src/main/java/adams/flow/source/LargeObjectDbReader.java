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
 * LargeObjectDbReader.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Allows reading of large objects from a SQL database.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: LargeObjectDbReader
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the large objects get output as array rather than one-by-one
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-query &lt;adams.db.SQLStatement&gt; (property: query)
 * &nbsp;&nbsp;&nbsp;The query to execute for retrieving the large objects; only the first column
 * &nbsp;&nbsp;&nbsp;of the returned result set will be retrieved.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-type &lt;BLOB|CLOB&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of large object to retrieve.
 * &nbsp;&nbsp;&nbsp;default: BLOB
 * </pre>
 *
 * <pre>-blob-conversion &lt;adams.data.conversion.Conversion&gt; (property: blobConversion)
 * &nbsp;&nbsp;&nbsp;The conversion to apply to BLOB byte arrays.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.UnknownToUnknown
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LargeObjectDbReader
    extends AbstractDbArrayProvider {

  private static final long serialVersionUID = 3879955415607623916L;

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
    return "Allows reading of large objects from a SQL database.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"query", "query",
	new SQLStatement());

    m_OptionManager.add(
	"type", "type",
	LargeObjectType.BLOB);

    m_OptionManager.add(
	"blob-conversion", "blobConversion",
	new UnknownToUnknown());
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
    return "The query to execute for retrieving the large objects; only the first column of the returned result set will be retrieved; variables get expanded automatically.";
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
    return "The type of large object to retrieve.";
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
    return "The conversion to apply to BLOB byte arrays.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case BLOB:
	return m_BlobConversion.generates();
      case CLOB:
	return String.class;
      default:
	throw new IllegalStateException("Unhandled type: " + m_Type);
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the large objects get output as array rather than one-by-one";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "query", m_Query, "query: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");
    result += QuickInfoHelper.toString(this, "blobConversion", m_BlobConversion, ", blob conversion: ");
    result += QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"), ", ");

    return result;
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
    ResultSet 		rs;
    List<Blob>		blobs;
    Blob		blob;
    List<Clob> 		clobs;
    Clob		clob;
    byte[]		bytes;
    String		msg;

    result = null;
    m_Queue.clear();

    query = getVariables().expand(m_Query.getValue());
    rs    = null;
    blobs = new ArrayList<>();
    clobs = new ArrayList<>();
    try {
      rs = SQLF.getSingleton(m_DatabaseConnection).getResultSet(query);
      while (rs.next()) {
	switch (m_Type) {
	  case BLOB:
	    blob = rs.getBlob(1);
	    blobs.add(blob);
	    bytes = blob.getBytes(1, (int) blob.length());
	    SQLUtils.free(blob);
	    blobs.remove(0);
	    try {
	      m_BlobConversion.setInput(bytes);
	      msg = m_BlobConversion.convert();
	      if (msg == null)
		m_Queue.add(m_BlobConversion.getOutput());
	    }
	    catch (Exception e) {
	      return handleException("Failed to convert BLOB bytes using: " + OptionUtils.getCommandLine(m_BlobConversion), e);
	    }
	    break;
	  case CLOB:
	    clob = rs.getClob(1);
	    clobs.add(clob);
	    m_Queue.add(clob.getSubString(1, (int) clob.length()));
	    SQLUtils.free(clob);
	    clobs.remove(0);
	    break;
	  default:
	    return "Unhandled type: " + m_Type;
	}
      }
    }
    catch (Exception e) {
      result = handleException("Failed to execute query: " + query, e);
    }
    finally {
      while (blobs.size() > 0)
        SQLUtils.free(blobs.remove(0));
      while (clobs.size() > 0)
	SQLUtils.free(clobs.remove(0));
      SQLUtils.closeAll(rs);
    }


    return result;
  }
}
