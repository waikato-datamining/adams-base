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
 * SpreadSheetRowBuffer.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.event.VariableChangeEvent;
import adams.flow.core.Token;
import adams.flow.core.VariableMonitor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Can act in two different ways:<br>
 * 1. Row -&gt; SpreadSheet<br>
 * Buffers adams.data.spreadsheet.Row objects and outputs a adams.data.spreadsheet.SpreadSheet object, whenever the interval condition has been met.<br>
 * 2. SpreadSheet -&gt; Row<br>
 * Outputs all the adams.data.spreadsheet.Row objects that the incoming adams.data.spreadsheet.SpreadSheet object contains.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.Row<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.Row[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetRowBuffer
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
 * <pre>-operation &lt;SPREADSHEET_TO_ROW|ROW_TO_SPREADSHEET&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The way the buffer operates.
 * &nbsp;&nbsp;&nbsp;default: ROW_TO_SPREADSHEET
 * </pre>
 *
 * <pre>-check &lt;boolean&gt; (property: checkHeader)
 * &nbsp;&nbsp;&nbsp;Whether to check the headers - if the headers change, the Row object gets
 * &nbsp;&nbsp;&nbsp;added to a new spreadsheet (in case of ROW_TO_SPREADSHEET).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval at which to output the SpreadSheet object (in case of ROW_TO_SPREADSHEET
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-clear-buffer &lt;boolean&gt; (property: clearBuffer)
 * &nbsp;&nbsp;&nbsp;Whether to clear the buffer once the dataset has been forwarded (in case
 * &nbsp;&nbsp;&nbsp;of ROW_TO_SPREADSHEET).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to monitor.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRowBuffer
    extends AbstractTransformer
    implements VariableMonitor {

  /** for serialization. */
  private static final long serialVersionUID = 6774529845778672623L;

  /** the key for storing the current buffer in the backup. */
  public final static String BACKUP_BUFFER = "buffer";

  /** the key for storing the current iterator in the backup. */
  public final static String BACKUP_ITERATOR = "iterator";

  /**
   * Defines how the buffer actor operates.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 7344 $
   */
  public enum Operation {
    SPREADSHEET_TO_ROW,
    ROW_TO_SPREADSHEET,
  }

  /** the currently buffered data. */
  protected SpreadSheet m_Buffer;

  /** the iterator for broadcasting Row objects. */
  protected Iterator<DataRow> m_Iterator;

  /** the way the buffer operates. */
  protected Operation m_Operation;

  /** whether to check the header. */
  protected boolean m_CheckHeader;

  /** the interval of when to output the SpreadSheet object. */
  protected int m_Interval;

  /** whether to clear the buffer once it has been forwarded. */
  protected boolean m_ClearBuffer;

  /** the variable to listen to. */
  protected VariableName m_VariableName;

  /** whether variable triggered clear of buffer. */
  protected boolean m_ClearBufferRequired;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Can act in two different ways:\n"
	    + "1. Row -> SpreadSheet\n"
	    + "Buffers " + Row.class.getName() + " objects and outputs a " + SpreadSheet.class.getName() + " "
	    + "object, whenever the interval condition has been met.\n"
	    + "2. SpreadSheet -> Row\n"
	    + "Outputs all the " + Row.class.getName() + " objects that the incoming "
	    + SpreadSheet.class.getName() + " object contains.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"operation", "operation",
	Operation.ROW_TO_SPREADSHEET);

    m_OptionManager.add(
	"check", "checkHeader",
	false);

    m_OptionManager.add(
	"interval", "interval",
	1, 1, null);

    m_OptionManager.add(
	"clear-buffer", "clearBuffer",
	false);

    m_OptionManager.add(
	"var-name", "variableName",
	new VariableName());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "operation", m_Operation);
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", interval: ");

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "checkHeader", m_CheckHeader, "check header"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "clearBuffer", m_ClearBuffer, "clear"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue()));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the way the buffer operates.
   *
   * @param value	the operation
   */
  public void setOperation(Operation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the way the buffer operates.
   *
   * @return 		the operation
   */
  public Operation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The way the buffer operates.";
  }

  /**
   * Sets whether to check the header or not.
   *
   * @param value	if true then the headers get checked
   */
  public void setCheckHeader(boolean value) {
    m_CheckHeader = value;
    reset();
  }

  /**
   * Returns whether the header gets checked or not.
   *
   * @return		true if the header gets checked
   */
  public boolean getCheckHeader() {
    return m_CheckHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkHeaderTipText() {
    return
	"Whether to check the headers - if the headers change, the Row "
	    + "object gets added to a new spreadsheet (in case of " + Operation.ROW_TO_SPREADSHEET + ").";
  }

  /**
   * Sets the interval for outputting the SpreadSheet objects.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the interval for outputting the SpreadSheet objects.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return
	"The interval at which to output the SpreadSheet object (in case of "
	    + Operation.ROW_TO_SPREADSHEET + ").";
  }

  /**
   * Sets whether to clear the buffer once the dataset has been forwarded.
   *
   * @param value	true if to clear buffer
   */
  public void setClearBuffer(boolean value) {
    m_ClearBuffer = value;
    reset();
  }

  /**
   * Returns whether to clear the buffer once the dataset has been forwarded.
   *
   * @return		true if to clear buffer
   */
  public boolean getClearBuffer() {
    return m_ClearBuffer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clearBufferTipText() {
    return
	"Whether to clear the buffer once the dataset has been forwarded "
	    + "(in case of " + Operation.ROW_TO_SPREADSHEET + ").";
  }

  /**
   * Sets the name of the variable to monitor.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to monitor.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The variable to monitor.";
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    super.variableChanged(e);
    if ((e.getType() == VariableChangeEvent.Type.MODIFIED) || (e.getType() == VariableChangeEvent.Type.ADDED)) {
      if (e.getName().equals(m_VariableName.getValue())) {
	m_ClearBufferRequired = true;
	if (isLoggingEnabled())
	  getLogger().info("Clearing of buffer required");
      }
    }
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the type of accepted data
   */
  public Class[] accepts() {
    if (m_Operation == Operation.ROW_TO_SPREADSHEET)
      return new Class[]{Row.class, Row[].class};
    else if (m_Operation == Operation.SPREADSHEET_TO_ROW)
      return new Class[]{SpreadSheet.class};
    else
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the type of generated data
   */
  public Class[] generates() {
    if (m_Operation == Operation.ROW_TO_SPREADSHEET)
      return new Class[]{SpreadSheet.class};
    else if (m_Operation == Operation.SPREADSHEET_TO_ROW)
      return new Class[]{Row.class};
    else
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_BUFFER);
    pruneBackup(BACKUP_ITERATOR);
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

    if (m_Buffer != null)
      result.put(BACKUP_BUFFER, m_Buffer);
    if (m_Iterator != null)
      result.put(BACKUP_ITERATOR, m_Iterator);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_BUFFER)) {
      m_Buffer = (SpreadSheet) state.get(BACKUP_BUFFER);
      state.remove(BACKUP_BUFFER);
    }
    if (state.containsKey(BACKUP_ITERATOR)) {
      m_Iterator = (Iterator<DataRow>) state.get(BACKUP_ITERATOR);
      state.remove(BACKUP_ITERATOR);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Buffer   = null;
    m_Iterator = null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Row[]	rows;
    Row		row;
    int		n;
    String	msg;

    result = null;

    // monitor variable triggered clear?
    if (m_ClearBufferRequired) {
      m_Buffer = null;
      m_ClearBufferRequired = false;
      if (isLoggingEnabled())
        getLogger().info("Buffer cleared (triggered by monitor variable)");
    }

    if (m_Operation == Operation.ROW_TO_SPREADSHEET) {
      if (m_InputToken.getPayload() instanceof Row) {
	rows = new Row[]{(Row) m_InputToken.getPayload()};
      }
      else {
	rows = (Row[]) m_InputToken.getPayload();
      }

      for (n = 0; n < rows.length; n++) {
	row = rows[n];

	if ((m_Buffer != null) && m_CheckHeader) {
	  if ((msg = m_Buffer.equalsHeader(row.getOwner())) != null) {
	    getLogger().info("Header changed, resetting buffer: " + msg);
	    m_Buffer = null;
	  }
	}

	// buffer row
	if (m_Buffer == null)
	  m_Buffer = row.getOwner().getHeader();
	m_Buffer.addRow().assign(row);
      }

      if (m_Buffer.getRowCount() % m_Interval == 0) {
	m_OutputToken = new Token(m_Buffer);
        if (m_ClearBuffer) {
          m_Buffer = null;
          if (isLoggingEnabled())
            getLogger().info("Buffer cleared (clearing interval reached)");
        }
      }
    }
    else if (m_Operation == Operation.SPREADSHEET_TO_ROW) {
      m_Buffer   = (SpreadSheet) m_InputToken.getPayload();
      m_Iterator = m_Buffer.rows().iterator();
    }
    else {
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    if (m_Operation == Operation.ROW_TO_SPREADSHEET)
      return super.hasPendingOutput();
    else if (m_Operation == Operation.SPREADSHEET_TO_ROW)
      return ((m_Iterator != null) && m_Iterator.hasNext());
    else
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (m_Operation == Operation.ROW_TO_SPREADSHEET) {
      result        = m_OutputToken;
      m_OutputToken = null;
    }
    else if (m_Operation == Operation.SPREADSHEET_TO_ROW) {
      result = new Token(m_Iterator.next());
    }
    else {
      throw new IllegalStateException("Unhandled operation: " + m_Operation);
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Iterator = null;
    m_Buffer   = null;

    super.wrapUp();
  }
}
