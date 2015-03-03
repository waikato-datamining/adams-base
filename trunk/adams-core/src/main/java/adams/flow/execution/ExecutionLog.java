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
 * ExecutionLog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.logging.Level;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates a trace file with all activity logged.
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
 * <pre>-log-file &lt;adams.core.io.PlaceholderFile&gt; (property: logFile)
 * &nbsp;&nbsp;&nbsp;The log file to write to; writing is disabled if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExecutionLog
  extends AbstractFlowExecutionListener {

  /** for serialization. */
  private static final long serialVersionUID = 3877868695922876920L;

  /** the file to write to. */
  protected PlaceholderFile m_LogFile;
  
  /** the file writer. */
  protected BufferedWriter m_Writer;
  
  /** the date formatter to use. */
  protected transient DateFormat m_DateFormat;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a trace file with all activity logged.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	    "log-file", "logFile",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the log file.
   *
   * @param value	the file
   */
  public void setLogFile(PlaceholderFile value) {
    m_LogFile = value;
    reset();
  }

  /**
   * Returns the current condition.
   *
   * @return		the condition
   */
  public PlaceholderFile getLogFile() {
    return m_LogFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logFileTipText() {
    return "The log file to write to; writing is disabled if pointing to a directory.";
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Writer = null;
  }
  
  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public void startListening() {
    super.startListening();
    
    m_DateFormat = DateUtils.getTimestampFormatterMsecs();
    
    if (!m_LogFile.isDirectory()) {
      try {
	m_Writer = new BufferedWriter(new FileWriter(m_LogFile.getAbsolutePath()));
	m_Writer.write("Date");
	m_Writer.write("\t");
	m_Writer.write("Actor");
	m_Writer.write("\t");
	m_Writer.write("Origin");
	m_Writer.write("\t");
	m_Writer.write("Message");
	m_Writer.newLine();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to open log file: " + m_LogFile, e);
	m_Writer = null;
      }
    }
  }
  
  /**
   * Writes a message to the log file.
   * 
   * @param origin	the origin, e.g., preInput
   * @param actor	the actor this message is for
   * @param msg		the actual message
   */
  protected void add(String origin, Actor actor, String msg) {
    if (m_Writer == null)
      return;
    
    try {
      m_Writer.write(m_DateFormat.format(new Date()));
      m_Writer.write("\t");
      m_Writer.write(actor.getFullName());
      m_Writer.write("\t");
      m_Writer.write(origin);
      m_Writer.write("\t");
      m_Writer.write(msg);
      m_Writer.newLine();
    }
    catch (Exception e) {
      // ignored
    }
  }
  
  /**
   * Gets called before the actor receives the token.
   * 
   * @param actor	the actor that will receive the token
   * @param token	the token that the actor will receive
   */
  @Override
  public void preInput(Actor actor, Token token) {
    add("preInput", actor, "#" + token.hashCode());
  }
  
  /**
   * Gets called after the actor received the token.
   * 
   * @param actor	the actor that received the token
   * @param token	the token that the actor received
   */
  @Override
  public void postInput(Actor actor) {
    add("postInput", actor, "");
  }
  
  /**
   * Gets called before the actor gets executed.
   * 
   * @param actor	the actor that will get executed
   */
  @Override
  public void preExecute(Actor actor) {
    add("preExecute", actor, "");
  }

  /**
   * Gets called after the actor was executed.
   * 
   * @param actor	the actor that was executed
   */
  @Override
  public void postExecute(Actor actor) {
    add("postExecute", actor, "");
  }
  
  /**
   * Gets called before a token gets obtained from the actor.
   * 
   * @param actor	the actor the token gets obtained from
   */
  @Override
  public void preOutput(Actor actor) {
    add("preOutput", actor, "");
  }
  
  /**
   * Gets called after a token was acquired from the actor.
   * 
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  @Override
  public void postOutput(Actor actor, Token token) {
    add("postOutput", actor, "#" + token.hashCode());
  }
  
  /**
   * Gets called when the flow execution ends.
   */
  @Override
  public void finishListening() {
    super.finishListening();
    
    m_DateFormat = null;
    
    if (m_Writer != null) {
      try {
	m_Writer.flush();
	m_Writer.close();
	m_Writer = null;
      }
      catch (Exception e) {
	// ignored
      }
    }
  }
}
