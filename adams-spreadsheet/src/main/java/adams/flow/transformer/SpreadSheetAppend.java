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
 * SpreadSheetAppend.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.flow.control.StorageName;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Appends the incoming spreadsheet to one in storage.<br>
 * If there is none in storage yet, the incoming spreadsheet will simply get stored in storage.<br>
 * The spreadsheets need not have the same structure, but it is assumed that column names are unique within a spreadsheet.<br>
 * The combined spreadsheet is then forwarded.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetAppend
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
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the stored spreadsheet to append the incoming one.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetAppend
  extends AbstractInPlaceSpreadSheetTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -253714973019682939L;

  /** the name of the stored value. */
  protected StorageName m_StorageName;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Appends the incoming spreadsheet to one in storage.\n"
	+ "If there is none in storage yet, the incoming spreadsheet will "
	+ "simply get stored in storage.\n"
	+ "The spreadsheets need not have the same structure, but it is "
	+ "assumed that column names are unique within a spreadsheet.\n"
	+ "The combined spreadsheet is then forwarded.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName);
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");
    
    return result;
  }

  /**
   * Sets the name of the stored value.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored value.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the stored spreadsheet to append the incoming one.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;
    SpreadSheet		stored;
    int			i;
    int			n;
    Row			headerSheet;
    Row			headerStored;
    Row			row;
    Row			newRow;
    String		key;

    result = null;

    synchronized(getStorageHandler().getStorage()) {
      stored = null;
      if (getStorageHandler().getStorage().has(m_StorageName))
	stored = (SpreadSheet) getStorageHandler().getStorage().get(m_StorageName);
      if (isLoggingEnabled())
	getLogger().info("Spreadsheet '" + m_StorageName + "' available from storage: " + (stored != null));

      sheet = (SpreadSheet) m_InputToken.getPayload();
      if (stored == null) {
	stored       = sheet.newInstance();
	headerStored = stored.getHeaderRow();
	headerSheet  = sheet.getHeaderRow();
	// header
	for (i = 0; i < headerSheet.getCellCount(); i++)
	  headerStored.addCell("" + headerStored.getCellCount()).assign(headerSheet.getCell(i));
	// data
	for (n = 0; n < sheet.getRowCount(); n++) {
	  row    = sheet.getRow(n);
	  newRow = stored.addRow();
	  for (i = 0; i < headerSheet.getCellCount(); i++) {
	    key = headerSheet.getCellKey(i);
	    if (row.getCell(key) != null)
	      newRow.addCell(headerStored.getCellKey(i)).assign(row.getCell(key));
	  }
	}
	getStorageHandler().getStorage().put(m_StorageName, stored);
	m_OutputToken = new Token(sheet);
	if (isLoggingEnabled())
	  getLogger().info("Spreadsheet added to storage: " + m_StorageName);
      }
      else {
	stored = SpreadSheetHelper.append(stored, sheet, m_NoCopy);
	getStorageHandler().getStorage().put(m_StorageName, stored);
	m_OutputToken = new Token(stored);
	if (isLoggingEnabled())
	  getLogger().info("Appended #" + sheet.getRowCount() + " rows to stored one: " + m_StorageName);
      }
    }

    return result;
  }
}
