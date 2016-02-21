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
 * Counter.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.NamedCounter;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.standalone.CounterInit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Outputs the specified counter as spreadsheet, with two columns: 'Key' and 'Count'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
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
 * &nbsp;&nbsp;&nbsp;default: Counter
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the counter in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: counter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Counter
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -4888807180866059350L;

  /** the column of the spreadsheet containing the counter names. */
  public final static String COLUMN_KEY = "Key";

  /** the column of the spreadsheet containing the counts. */
  public final static String COLUMN_COUNT = "Count";
  
  /** the name of the lookup table in the internal storage. */
  protected StorageName m_StorageName;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Outputs the specified counter as spreadsheet, with two columns: '"
          + COLUMN_KEY + "' and '" + COLUMN_COUNT + "'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageName",
	    new StorageName("counter"));
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
  }

  /**
   * Sets the name of the counter in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the counter in the internal storage.
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
    return "The name of the counter in the internal storage.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    NamedCounter 	counter;
    SpreadSheet		sheet;
    List<String> 	names;
    Row			row;
    
    result = null;
    
    if (!getStorageHandler().getStorage().has(m_StorageName)) {
      result = "Counter '" + m_StorageName + "' not available! Not initialized with " + CounterInit.class.getName() + "?";
    }
    else {
      counter = (NamedCounter) getStorageHandler().getStorage().get(m_StorageName);
      sheet  = new DefaultSpreadSheet();
      sheet.setName(m_StorageName.getValue());
      sheet.getHeaderRow().addCell("k").setContent(COLUMN_KEY);
      sheet.getHeaderRow().addCell("c").setContent(COLUMN_COUNT);
      names = new ArrayList<>(counter.nameSet());
      Collections.sort(names);
      for (String name: names) {
	row = sheet.addRow();
	row.addCell("k").setContentAsString(name);
	row.addCell("c").setContent(counter.current(name));
      }
      m_OutputToken = new Token(sheet);
    }
    
    return result;
  }
}
