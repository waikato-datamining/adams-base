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
 * ExecutionLog.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Generates a trace file with all activity logged, uses stdout when the log file is pointing to a directory.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-stage &lt;PreInput|PostInput|PreExecute|PostExecute|PreOutput|PostOutput&gt; [-stage ...] (property: stages)
 * &nbsp;&nbsp;&nbsp;The execution stages to log.
 * &nbsp;&nbsp;&nbsp;default: PRE_EXECUTE
 * </pre>
 *
 * <pre>-log-file &lt;adams.core.io.PlaceholderFile&gt; (property: logFile)
 * &nbsp;&nbsp;&nbsp;The log file to write to; uses stdout if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-output-tokens &lt;boolean&gt; (property: outputTokens)
 * &nbsp;&nbsp;&nbsp;If enabled, a string representation of input tokens are output as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ExecutionLog
  extends AbstractFlowExecutionListener {

  /** for serialization. */
  private static final long serialVersionUID = 3877868695922876920L;

  /** the stages to output. */
  protected ExecutionStage[] m_Stages;

  /** the file to write to. */
  protected PlaceholderFile m_LogFile;

  /** whether to output any tokens as well. */
  protected boolean m_OutputTokens;

  /** the file writer. */
  protected BufferedWriter m_Writer;

  /** the date formatter to use. */
  protected transient DateFormat m_DateFormat;

  /** the actual stages. */
  protected transient Set<ExecutionStage> m_StagesSet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a trace file with all activity logged, uses stdout when the log file is pointing to a directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stage", "stages",
      new ExecutionStage[]{ExecutionStage.PRE_EXECUTE});

    m_OptionManager.add(
      "log-file", "logFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "output-tokens", "outputTokens",
      false);
  }

  /**
   * Sets the stages to log.
   *
   * @param value	the stages
   */
  public void setStages(ExecutionStage[] value) {
    m_Stages = value;
    reset();
  }

  /**
   * Returns the stages to log.
   *
   * @return		the stages
   */
  public ExecutionStage[] getStages() {
    return m_Stages;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stagesTipText() {
    return "The execution stages to log.";
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
   * Returns the log file.
   *
   * @return		the file
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
    return "The log file to write to; uses stdout if pointing to a directory.";
  }

  /**
   * Sets whether to output the string representation of input tokens as well.
   *
   * @param value	true if to output
   */
  public void setOutputTokens(boolean value) {
    m_OutputTokens = value;
    reset();
  }

  /**
   * Returns whether to output the string representation of input tokens as well.
   *
   * @return		true if output
   */
  public boolean getOutputTokens() {
    return m_OutputTokens;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTokensTipText() {
    return "If enabled, a string representation of input tokens are output as well.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Writer    = null;
    m_StagesSet = null;
  }

  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public void startListening() {
    super.startListening();

    m_StagesSet = new HashSet<>(Arrays.asList(m_Stages));

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
	if (m_OutputTokens) {
	  m_Writer.write("\t");
	  m_Writer.write("Token");
	}
	m_Writer.newLine();
	m_Writer.flush();
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
   * @param token	the token, can be null
   */
  protected void add(String origin, Actor actor, String msg, Token token) {
    if (m_Writer == null) {
      System.out.print(m_DateFormat.format(new Date()));
      System.out.print("\t");
      System.out.print(actor.getFullName());
      System.out.print("\t");
      System.out.print(origin);
      System.out.print("\t");
      System.out.print(msg);
      if (m_OutputTokens) {
	System.out.print("\t");
	if (token != null)
	  System.out.print("" + token.getPayload());
      }
      System.out.println();
    }
    else {
      try {
	m_Writer.write(m_DateFormat.format(new Date()));
	m_Writer.write("\t");
	m_Writer.write(actor.getFullName());
	m_Writer.write("\t");
	m_Writer.write(origin);
	m_Writer.write("\t");
	m_Writer.write(msg);
	if (m_OutputTokens) {
	  m_Writer.write("\t");
	  if (token != null)
	    m_Writer.write("" + token.getPayload());
	}
	m_Writer.newLine();
	m_Writer.flush();
      }
      catch (Exception e) {
	// ignored
      }
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
    if ((m_StagesSet != null) && m_StagesSet.contains(ExecutionStage.PRE_INPUT))
      add("preInput", actor, "#" + token.hashCode(), token);
  }

  /**
   * Gets called after the actor received the token.
   *
   * @param actor	the actor that received the token
   */
  @Override
  public void postInput(Actor actor) {
    if ((m_StagesSet != null) && m_StagesSet.contains(ExecutionStage.POST_INPUT))
      add("postInput", actor, "", null);
  }

  /**
   * Gets called before the actor gets executed.
   *
   * @param actor	the actor that will get executed
   */
  @Override
  public void preExecute(Actor actor) {
    if ((m_StagesSet != null) && m_StagesSet.contains(ExecutionStage.PRE_EXECUTE))
      add("preExecute", actor, "", null);
  }

  /**
   * Gets called after the actor was executed.
   *
   * @param actor	the actor that was executed
   */
  @Override
  public void postExecute(Actor actor) {
    if ((m_StagesSet != null) && m_StagesSet.contains(ExecutionStage.POST_EXECUTE))
      add("postExecute", actor, "", null);
  }

  /**
   * Gets called before a token gets obtained from the actor.
   *
   * @param actor	the actor the token gets obtained from
   */
  @Override
  public void preOutput(Actor actor) {
    if ((m_StagesSet != null) && m_StagesSet.contains(ExecutionStage.PRE_OUTPUT))
      add("preOutput", actor, "", null);
  }

  /**
   * Gets called after a token was acquired from the actor.
   *
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  @Override
  public void postOutput(Actor actor, Token token) {
    if ((m_StagesSet != null) && m_StagesSet.contains(ExecutionStage.POST_OUTPUT))
      add("postOutput", actor, "#" + token.hashCode(), token);
  }

  /**
   * Gets called when the flow execution ends.
   */
  @Override
  public void finishListening() {
    super.finishListening();

    m_DateFormat = null;
    m_StagesSet  = null;

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
