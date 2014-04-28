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
 * LogEntry.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import adams.core.CloneHandler;
import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.net.InternetHelper;
import adams.data.id.DatabaseIDHandler;

/**
 * A simple log entry container that can be stored and retrieve in a database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEntry
  implements Serializable, DatabaseIDHandler, CloneHandler<LogEntry>, Comparable<LogEntry> {

  /** for serialization. */
  private static final long serialVersionUID = 565425043739971996L;

  /** the format of the timestamps. */
  public final static String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /** the name of the properties file. */
  public final static String FILENAME = "LogEntry.props";

  /** the key for the database ID. */
  public final static String KEY_DBID = "DB-ID";

  /** the key for the ID. */
  public final static String KEY_ID = "ID";

  /** the key for the computer name. */
  public final static String KEY_HOST = "Host";

  /** the key for the IP address. */
  public final static String KEY_IP = "IP";

  /** the key for errors. */
  public final static String KEY_ERRORS = "Errors";

  /** the status "New". */
  public final static String STATUS_NEW = "New";

  /** the status "Open". */
  public final static String STATUS_OPEN = "Open";

  /** the status "Resolved". */
  public final static String STATUS_RESOLVED = "Resolved";

  /** the database ID of the entry. */
  protected int m_DatabaseID;

  /** the computer name. */
  protected String m_Host;

  /** the IP address. */
  protected String m_IP;

  /** the type of the message. */
  protected String m_Type;

  /** the content of the message (props format). */
  protected String m_Message;

  /** the generation timestamp. */
  protected Date m_Generation;

  /** the source of the entry. */
  protected String m_Source;

  /** the status of the entry. */
  protected String m_Status;

  /** the formatter for the generation timestamp. */
  protected static DateFormat m_DateFormat;
  static {
    m_DateFormat = new DateFormat(TIMESTAMP_FORMAT);
  }

  /**
   * Initializes the entry.
   */
  public LogEntry() {
    super();

    m_DatabaseID = Constants.NO_ID;
    m_Host       = InternetHelper.getLocalHostName();
    m_IP         = InternetHelper.getLocalHostIP();
    m_Type       = "";
    m_Message    = "";
    m_Generation = new Date();
    m_Source     = "";
    m_Status     = "";
  }

  /**
   * Sets the database ID.
   *
   * @param value	the database ID
   */
  public void setDatabaseID(int value) {
    m_DatabaseID = value;
  }

  /**
   * Returns the database ID.
   *
   * @return		the database ID
   */
  public int getDatabaseID() {
    return m_DatabaseID;
  }

  /**
   * Sets the host name.
   *
   * @param value	the host name
   */
  public void setHost(String value) {
    m_Host = value;
  }

  /**
   * Returns the host name.
   *
   * @return		the host name, can be null
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Sets the IP address.
   *
   * @param value	the IP address
   */
  public void setIP(String value) {
    m_IP = value;
  }

  /**
   * Returns the IP address.
   *
   * @return		the IP address, can be null
   */
  public String getIP() {
    return m_IP;
  }

  /**
   * Sets the type.
   *
   * @param value	the type
   */
  public void setType(String value) {
    if (value == null)
      m_Type = "";
    else
      m_Type = value;
  }

  /**
   * Returns the type.
   *
   * @return		the type
   */
  public String getType() {
    return m_Type;
  }

  /**
   * Sets the message.
   *
   * @param value	the message
   */
  public void setMessage(String value) {
    if (value == null)
      m_Message = "";
    else
      m_Message = value.replaceAll("\"", "'");
  }

  /**
   * Sets the message as properties object.
   *
   * @param value	the message
   */
  public void setMessage(Properties value) {
    List<String>	lines;

    lines = new ArrayList<String>(Arrays.asList(value.toString().split("\n")));
    Utils.removeComments(lines, "#");
    m_Message = Utils.flatten(lines, "\n");
  }

  /**
   * Returns the message.
   *
   * @return		the message
   */
  public String getMessage() {
    return m_Message;
  }

  /**
   * Returns the message as properties object.
   *
   * @return		the message
   */
  public Properties getMessageAsProperties() {
    Properties		result;
    BufferedInputStream	stream;

    result = new Properties();

    stream = null;
    try {
      stream = new BufferedInputStream(new ByteArrayInputStream(m_Message.getBytes()));
      result.load(stream);
    }
    catch (Exception e) {
      System.err.println("Failed to turn message into properties:");
      e.printStackTrace();
    }
    finally {
      if (stream != null) {
	try {
	  stream.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Sets the generation timestamp.
   *
   * @param value	the generation timestamp
   */
  public void setGeneration(Date value) {
    if (value == null)
      m_Generation = new Date();
    else
      m_Generation = value;
  }

  /**
   * Returns the generation timestamp.
   *
   * @return		the generation timestamp
   */
  public Date getGeneration() {
    return m_Generation;
  }

  /**
   * Returns the generation timestamp as string.
   *
   * @return		the generation timestamp
   * @see		#TIMESTAMP_FORMAT
   */
  public String getGenerationAsString() {
    return m_DateFormat.format(m_Generation);
  }

  /**
   * Sets the source.
   *
   * @param value	the source
   */
  public void setSource(String value) {
    if (value == null)
      m_Source = "";
    else
      m_Source = value;
  }

  /**
   * Returns the source.
   *
   * @return		the source
   */
  public String getSource() {
    return m_Source;
  }

  /**
   * Sets the status.
   *
   * @param value	the status
   */
  public void setStatus(String value) {
    if (value == null)
      m_Status = "";
    else
      m_Status = value;
  }

  /**
   * Returns the status.
   *
   * @return		the status
   */
  public String getStatus() {
    return m_Status;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public LogEntry getClone() {
    LogEntry	result;

    result = new LogEntry();
    result.setDatabaseID(m_DatabaseID);
    result.setHost(new String(getHost()));
    result.setIP(new String(getIP()));
    result.setType(new String(m_Type));
    result.setMessage(new String(m_Message));
    result.setGeneration((Date) m_Generation.clone());
    result.setSource(new String(m_Source));
    result.setStatus(new String(m_Status));

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  public int compareTo(LogEntry o) {
    int		result;

    result = new Integer(getDatabaseID()).compareTo(new Integer(o.getDatabaseID()));
    if (result == 0)
      result = getGeneration().compareTo(o.getGeneration());

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LogEntry))
      return false;
    else
      return (compareTo((LogEntry) obj) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * "DBID + Generation" string.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return new String(m_DatabaseID + " " + m_Generation).hashCode();
  }

  /**
   * Returns a short string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder 	result;

    result = new StringBuilder();
    //result.append("DB-ID: " + m_DatabaseID + "\n");
    result.append("Host: " + m_Host + "\n");
    result.append("IP: " + m_IP + "\n");
    result.append("Type: " + m_Type + "\n");
    result.append("Generation: " + getGenerationAsString() + "\n");
    result.append("Source: " + m_Source + "\n");
    result.append("Status: " + m_Status + "\n");
    result.append("Message:\n" + m_Message);
    result.append("\n");

    return result.toString();
  }
}
