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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7344 $
 */
public class SpreadSheetRowBuffer
  extends AbstractTransformer
  implements ProvenanceSupporter {

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
    
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "checkHeader", m_CheckHeader, "check header"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "clearBuffer", m_ClearBuffer, "clear"));
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
	if (m_ClearBuffer)
	  m_Buffer = null;
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

    updateProvenance(result);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
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
