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
 * Exec.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.management.LoggingObjectOutputPrinter;
import adams.core.management.OutputProcessStream;
import adams.core.option.OptionUtils;
import adams.data.conversion.AnyToString;
import adams.data.conversion.ConversionToString;

/**
 <!-- globalinfo-start -->
 * Pipes the incoming data, after converting it using the provided conversion scheme, into the started process.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
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
 * &nbsp;&nbsp;&nbsp;default: Exec
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
 * <pre>-cmd &lt;java.lang.String&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The external command to pipe the data into.
 * &nbsp;&nbsp;&nbsp;default: mysql test
 * </pre>
 * 
 * <pre>-conversion &lt;adams.data.conversion.ConversionToString&gt; (property: conversion)
 * &nbsp;&nbsp;&nbsp;The conversion scheme to apply to the input tokens.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.AnyToString
 * </pre>
 * 
 * <pre>-delimiter &lt;java.lang.String&gt; (property: delimiter)
 * &nbsp;&nbsp;&nbsp;The delimiter string to forward to the process after each token; uses backquoted 
 * &nbsp;&nbsp;&nbsp;strings, ie you can use \n for new line and \r for carriage return.
 * &nbsp;&nbsp;&nbsp;default: \\n
 * </pre>
 * 
 * <pre>-finished-signal &lt;java.lang.String&gt; (property: finishedSignal)
 * &nbsp;&nbsp;&nbsp;The string to signal the process that the processing has finished; gets 
 * &nbsp;&nbsp;&nbsp;ignored if empty string; you can use \\uXXXX for unicode characters (with 
 * &nbsp;&nbsp;&nbsp;XXXX being a hexadecimal number), eg \\u001a for EOF.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Exec
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -5040421332565191432L;

  /** the key for storing the process in the backup. */
  public final static String BACKUP_PROCESS = "process";

  /** the key for storing the writer in the backup. */
  public final static String BACKUP_WRITER = "writer";

  /** the key for storing the stdout output processor. */
  public final static String BACKUP_OUTPUTPROCESSOR_STDOUT = "output processor stdout";

  /** the key for storing the stderr output processor. */
  public final static String BACKUP_OUTPUTPROCESSOR_STDERR = "output processor stderr";
  
  /** the command to run. */
  protected String m_Command;

  /** the conversion scheme to turn the input into strings. */
  protected ConversionToString m_Conversion;

  /** the string to forward to the process after each token. */
  protected String m_Delimiter;
  
  /** the string to signal the process that flow has finished. */
  protected String m_FinishedSignal;
  
  /** the process to pipe the data in. */
  protected Process m_Process;
  
  /** for writing to stdin of the process. */
  protected BufferedWriter m_Writer;

  /** for outputting stdout of the process. */
  protected OutputProcessStream m_Stdout;

  /** for outputting stderr of the process. */
  protected OutputProcessStream m_Stderr;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Pipes the incoming data, after converting it using the provided "
	+ "conversion scheme, into the started process.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cmd", "command",
	    "mysql test");

    m_OptionManager.add(
	    "conversion", "conversion",
	    new AnyToString());

    m_OptionManager.add(
	    "delimiter", "delimiter",
	    "\\n");

    m_OptionManager.add(
	    "finished-signal", "finishedSignal",
	    "");
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

    result  = QuickInfoHelper.toString(this, "command", m_Command, "Command: ");
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", Conversion: ");
    result += QuickInfoHelper.toString(this, "delimiter", Utils.backQuoteChars(m_Delimiter), ", Delimiter: ");
    value = QuickInfoHelper.toString(this, "finishedSignal", Utils.backQuoteChars(m_FinishedSignal).replace("\\\\u", "\\u"), ", finished Signal: ");
    if (value != null)
      result += value;
    
    return result;
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

    if (m_Process != null)
      result.put(BACKUP_PROCESS, m_Process);
    
    if (m_Writer != null)
      result.put(BACKUP_WRITER, m_Writer);
    
    if (m_Stdout != null)
      result.put(BACKUP_OUTPUTPROCESSOR_STDOUT, m_Stdout);
    
    if (m_Stderr != null)
      result.put(BACKUP_OUTPUTPROCESSOR_STDERR, m_Stderr);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_PROCESS)) {
      m_Process = (Process) state.get(BACKUP_PROCESS);
      state.remove(BACKUP_PROCESS);
    }

    if (state.containsKey(BACKUP_WRITER)) {
      m_Writer = (BufferedWriter) state.get(BACKUP_WRITER);
      state.remove(BACKUP_WRITER);
    }

    if (state.containsKey(BACKUP_OUTPUTPROCESSOR_STDOUT)) {
      m_Stdout = (OutputProcessStream) state.get(BACKUP_OUTPUTPROCESSOR_STDOUT);
      state.remove(BACKUP_OUTPUTPROCESSOR_STDOUT);
    }

    if (state.containsKey(BACKUP_OUTPUTPROCESSOR_STDERR)) {
      m_Stderr = (OutputProcessStream) state.get(BACKUP_OUTPUTPROCESSOR_STDERR);
      state.remove(BACKUP_OUTPUTPROCESSOR_STDERR);
    }

    super.restoreState(state);
  }

  /**
   * Removes entries from the backup.
   *
   * @see		#reset()
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    
    // TODO clean up?
    pruneBackup(BACKUP_PROCESS);
    pruneBackup(BACKUP_WRITER);
    pruneBackup(BACKUP_OUTPUTPROCESSOR_STDOUT);
    pruneBackup(BACKUP_OUTPUTPROCESSOR_STDERR);
  }

  /**
   * Sets the command to run.
   *
   * @param value	the command
   */
  public void setCommand(String value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to run.
   *
   * @return 		the command
   */
  public String getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The external command to pipe the data into.";
  }

  /**
   * Sets the conversion to apply to the input.
   *
   * @param value	the conversion
   */
  public void setConversion(ConversionToString value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion to apply to the input.
   *
   * @return 		the conversion
   */
  public ConversionToString getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The conversion scheme to apply to the input tokens.";
  }

  /**
   * Sets the delimiter to write to forward to the process after each token.
   * Uses a backquoted string.
   *
   * @param value	the delimiter
   */
  public void setDelimiter(String value) {
    m_Delimiter = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the delimiter to write to the process after each token.
   * Backquoted string.
   *
   * @return 		the delimiter
   */
  public String getDelimiter() {
    return Utils.backQuoteChars(m_Delimiter);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String delimiterTipText() {
    return 
	"The delimiter string to forward to the process after each token; "
	+ "uses backquoted strings, ie you can use \\n for new line and \\r "
	+ "for carriage return.";
  }

  /**
   * Sets the string to send to the process when the flow finishes.
   * Uses a backquoted string.
   *
   * @param value	the signal string, ignored if empty string
   */
  public void setFinishedSignal(String value) {
    m_FinishedSignal = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the string to signal the process the end of flow execution.
   * Backquoted string.
   *
   * @return 		the signal string, ignored if empty string
   */
  public String getFinishedSignal() {
    return Utils.backQuoteChars(m_FinishedSignal).replace("\\\\u", "\\u");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String finishedSignalTipText() {
    return 
	"The string to signal the process that the processing has finished; "
	+ "gets ignored if empty string; "
	+ "you can use \\uXXXX for unicode characters (with XXXX being a "
	+ "hexadecimal number), eg \\u001a for EOF.";
  }
  
  /**
   * Returns the "end of execution" signal string to send to the process.
   * Processes any unicode characters.
   * 
   * @return		the string
   */
  protected String getActualFinishedSignal() {
    StringBuilder	result;
    int			i;
    int			curr;
    int			next;
    
    result = new StringBuilder();
    
    for (i = 0; i < m_FinishedSignal.length(); i++) {
      curr = m_FinishedSignal.charAt(i);
      next = -1;
      if (i < m_FinishedSignal.length() - 1)
	next = m_FinishedSignal.charAt(i+1);
      if (next != -1) {
	if ((curr == '\\') && (next == 'u') && (i < m_FinishedSignal.length() - 5)) {
	  result.append(Character.toChars(Integer.parseInt(m_FinishedSignal.substring(i + 2, i + 6), 16)));
	  i += 5;
	}
	else {
	  result.append((char) curr);
	}
      }
    }
    
    return result.toString();
  }
  
  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{m_Conversion.accepts()};
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String[]	cmd;
    String	msg;
    
    result = null;

    if (m_Process == null) {
      try {
	cmd       = OptionUtils.splitOptions(m_Command);
	m_Process = Runtime.getRuntime().exec(cmd);
	m_Writer  = new BufferedWriter(new OutputStreamWriter(m_Process.getOutputStream()));
	m_Stdout  = new OutputProcessStream(m_Process, LoggingObjectOutputPrinter.class, true);
	m_Stderr  = new OutputProcessStream(m_Process, LoggingObjectOutputPrinter.class, false);
	((LoggingObjectOutputPrinter) m_Stdout.getPrinter()).setOwner(this);
	((LoggingObjectOutputPrinter) m_Stderr.getPrinter()).setOwner(this);
	new Thread(m_Stdout).start();
	new Thread(m_Stderr).start();
      }
      catch (Exception e) {
	m_Writer = null;
	handleException("Failed to execute command: " + m_Command, e);
      }
    }
    
    if (m_Writer != null) {
      m_Conversion.setInput(m_InputToken.getPayload());
      msg = m_Conversion.convert();
      if (msg == null) {
	try {
	  m_Writer.write((String) m_Conversion.getOutput());
	  m_Writer.write(m_Delimiter);
	  m_Writer.flush();
	}
	catch (Exception e) {
	  handleException("Failed to pipe data:", e);
	}
      }
      else {
	result = "Failed to convert token: " + msg;
      }
    }
    
    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    String	finished;
    
    if (m_Stdout != null) {
      m_Stdout.stop();
      m_Stdout = null;
    }

    if (m_Stderr != null) {
      m_Stderr.stop();
      m_Stderr = null;
    }
    
    if (m_Writer != null) {
      finished = getActualFinishedSignal();
      try {
	if (finished.length() > 0) {
	  m_Writer.write(finished);
	  m_Writer.flush();
	}
	m_Writer.close();
      }
      catch (Exception e) {
	handleException("Failed to close pipe:", e);
      }
      m_Writer = null;
    }

    super.wrapUp();
  }
}
