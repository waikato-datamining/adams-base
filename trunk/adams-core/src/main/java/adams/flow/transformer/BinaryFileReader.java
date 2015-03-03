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
 * BinaryFileReader.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import gnu.trove.list.array.TByteArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.ArrayProvider;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Reads a binary file and forwards the content byte by byte or as byte array.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Byte<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: BinaryFileReader
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-start &lt;long&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The starting position in the file (1-based index).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-end &lt;long&gt; (property: end)
 * &nbsp;&nbsp;&nbsp;The last position in the file (incl; 1-based index; &lt;0 denotes end-of-file
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the file content as byte array rather than byte by byte.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BinaryFileReader
  extends AbstractTransformer
  implements ArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6525019504599945776L;

  /** the key for storing the current position the backup. */
  public final static String BACKUP_POSITION = "position";

  /** the key for storing the current stream the backup. */
  public final static String BACKUP_STREAM = "stream";

  /** the key for storing the current file the backup. */
  public final static String BACKUP_FILE = "file";

  /** the start position. */
  protected long m_Start;

  /** the end position. */
  protected long m_End;
  
  /** the current position. */
  protected long m_Position;
  
  /** the actual last position. */
  protected long m_ActualEnd;
  
  /** the input stream. */
  protected transient FileInputStream m_Stream;
  
  /** the current file to read from. */
  protected File m_CurrentFile;

  /** whether to output an array instead of single items. */
  protected boolean m_OutputArray;
  
  /** the collected data. */
  protected TByteArrayList m_Data;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Reads a binary file and forwards the content byte by byte or as byte array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "start", "start",
	    1L, 1L, null);

    m_OptionManager.add(
	    "end", "end",
	    -1L, -1L, null);

    m_OptionManager.add(
	    "output-array", "outputArray",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Position    = 0;
    m_Stream      = null;
    m_CurrentFile = null;
    m_Data        = null;
  }

  /**
   * Resets the actor.
   * Clears the output queue.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Position    = 0;
    m_Stream      = null;
    m_CurrentFile = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "start", m_Start, "from: ");
    result += QuickInfoHelper.toString(this, "start", (m_End > 0 ? m_End : "EOF"), ", to: ");
    result += QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "as array", ", ");
    
    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_POSITION);
    pruneBackup(BACKUP_STREAM);
    pruneBackup(BACKUP_FILE);
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

    if (m_Position > 0)
      result.put(BACKUP_POSITION, m_Position);
    if (m_Stream != null)
      result.put(BACKUP_STREAM, m_Stream);
    if (m_CurrentFile != null)
      result.put(BACKUP_FILE, m_CurrentFile);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_POSITION)) {
      m_Position = (Long) state.get(BACKUP_POSITION);
      state.remove(BACKUP_POSITION);
    }

    if (state.containsKey(BACKUP_STREAM)) {
      m_Stream = (FileInputStream) state.get(BACKUP_STREAM);
      state.remove(BACKUP_STREAM);
    }

    if (state.containsKey(BACKUP_FILE)) {
      m_CurrentFile = (File) state.get(BACKUP_FILE);
      state.remove(BACKUP_FILE);
    }

    super.restoreState(state);
  }

  /**
   * Sets the starting position.
   *
   * @param value	the position (1-based)
   */
  public void setStart(long value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns the starting position.
   *
   * @return		the position (1-based)
   */
  public long getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The starting position in the file (1-based index).";
  }

  /**
   * Sets the last position (incl).
   *
   * @param value	the position (1-based)
   */
  public void setEnd(long value) {
    m_End = value;
    reset();
  }

  /**
   * Returns the last position (incl.).
   *
   * @return		the position (1-based)
   */
  public long getEnd() {
    return m_End;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String endTipText() {
    return "The last position in the file (incl; 1-based index; <0 denotes end-of-file).";
  }

  /**
   * Sets whether to output the file as byte array or byte by byte.
   *
   * @param value	true if output is an array
   */
  public void setOutputArray(boolean value) {
    m_OutputArray = value;
    reset();
  }

  /**
   * Returns whether to output the file as byte array or byte by byte.
   *
   * @return		true if output is an array
   */
  public boolean getOutputArray() {
    return m_OutputArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputArrayTipText() {
    return "If enabled, outputs the file content as byte array rather than byte by byte.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Byte.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    if (m_OutputArray)
      return new Class[]{byte[].class};
    else
      return new Class[]{Byte.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		fileObj;
    File		file;
    int			c;

    result = null;

    fileObj = m_InputToken.getPayload();
    if (fileObj instanceof File)
      file = (File) fileObj;
    else
      file = new PlaceholderFile((String) fileObj);

    try {
      m_Stream = new FileInputStream(file.getAbsolutePath());
      if (m_End > 0)
	m_ActualEnd = Math.min(m_End, file.length());
      else
	m_ActualEnd = file.length();
      if (m_Start > m_ActualEnd) {
	result = "Start position is after end position: " + m_Start + " < " + m_ActualEnd;
      }
      else {
	m_CurrentFile = file;
	m_Position    = m_Start - 1;
	if (m_Position > 0)
	  m_Stream.skip(m_Position);
      }
      
      // read all data?
      if (m_OutputArray) {
	m_Data = new TByteArrayList();
	while (m_Position < m_ActualEnd) {
	  c = m_Stream.read();
	  if (c != -1) {
	    m_Data.add((byte) c);
	    m_Position++;
	  }
	  else {
	    m_Position = m_ActualEnd;
	    closeStream();
	  }
	  if (m_Stopped) {
	    m_Data = null;
	    break;
	  }
	}
      }
    }
    catch (Exception e) {
      result        = handleException("Failed to open file '" + file + "':", e);
      m_ActualEnd   = 0;
      m_CurrentFile = null;
    }

    if (m_Stopped)
      closeStream();

    return result;
  }

  /**
   * Closes the stream if possible.
   */
  protected void closeStream() {
    if (m_Stream == null)
      return;
    
    try {
      m_Stream.close();
    }
    catch (Exception e) {
      // ignored
    }
    m_CurrentFile = null;
  }
  
  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return ((m_CurrentFile != null) && (m_Position < m_ActualEnd)) || (m_Data != null);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    int		c;

    if (m_Data != null) {
      result = new Token(m_Data.toArray());
      m_Data = null;
    }
    else {
      try {
	c = m_Stream.read();
	if (c != -1) {
	  result = new Token((byte) c);
	  m_Position++;
	}
	else {
	  result = null;
	  m_Position = m_ActualEnd;
	  closeStream();
	}
      }
      catch (Exception e) {
	handleException("Failed to read from file '" + m_CurrentFile + "':", e);
	result = null;
      }
    }

    return result;
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    closeStream();
    
    super.wrapUp();
  }
}
