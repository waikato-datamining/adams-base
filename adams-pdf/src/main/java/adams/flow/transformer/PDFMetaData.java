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
 * PDFMetaData.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SparseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Actor for extracting the meta-data from a PDF.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * &nbsp;&nbsp;&nbsp;default: PDFMetaData
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
 * <pre>-output-type &lt;SPREADSHEET|MAP&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;How to output the meta-data.
 * &nbsp;&nbsp;&nbsp;default: SPREADSHEET
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
    author = "Apache",
    license = License.APACHE2,
    note = "Original class: org.apache.pdfbox.examples.pdmodel.ExtractMetadata"
)
public class PDFMetaData
    extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5712406930007899590L;

  /**
   * How to output the meta-data
   */
  public enum OutputType {
    SPREADSHEET,
    MAP,
  }

  /** how to output the meta-data. */
  protected OutputType m_OutputType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Actor for extracting the meta-data from a PDF.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"output-type", "outputType",
	OutputType.SPREADSHEET);
  }

  /**
   * Sets how to output the meta-data.
   *
   * @param value	the type
   */
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns how to output the meta-data.
   *
   * @return 		the type
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "How to output the meta-data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputType", m_OutputType, "output: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the input
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the output
   */
  public Class[] generates() {
    switch (m_OutputType) {
      case SPREADSHEET:
	return new Class[]{SpreadSheet.class};
      case MAP:
	return new Class[]{Map.class};
      default:
	throw new IllegalStateException("Unhandled output type: "  + m_OutputType);
    }
  }

  /**
   * Adds the cell content to the spreadsheet.
   *
   * @param row		the row to add this to
   * @param header	the column name
   * @param content	the content for the cell
   */
  protected void addCell(Row row, String header, Calendar content) {
    row.getOwner().getHeaderRow().addCell(header).setContent(header);
    if (content != null)
      row.addCell(header).setContent(content.getTime());
  }

  /**
   * Adds the cell content to the spreadsheet.
   *
   * @param row		the row to add this to
   * @param header	the column name
   * @param content	the content for the cell
   */
  protected void addCell(Row row, String header, String content) {
    row.getOwner().getHeaderRow().addCell(header).setContent(header);
    if (content != null)
      row.addCell(header).setContent(content);
  }

  /**
   * Adds the value to the map if not null.
   * 
   * @param map		the map to add to
   * @param key		the key for the value
   * @param value	the value to add
   */
  public void addMapValue(Map<String,Object> map, String key, Object value) {
    if (value != null) {
      if (value instanceof Calendar)
        map.put(key, ((Calendar) value).getTime());
      else
	map.put(key, value);
    }
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    File			file;
    SpreadSheet			sheet;
    Map<String,Object>		map;
    PDDocument 			document;
    PDDocumentInformation	info;
    Row				row;
    Set<String>			keys;

    result = null;

    // get file
    if (m_InputToken.getPayload() instanceof File)
      file = (File) m_InputToken.getPayload();
    else
      file = new PlaceholderFile((String) m_InputToken.getPayload());

    try {
      document = PDDocument.load(file.getAbsoluteFile());
      info     = document.getDocumentInformation();
      keys     = info.getMetadataKeys();

      switch (m_OutputType) {
	case SPREADSHEET:
	  sheet = new DefaultSpreadSheet();
	  sheet.setDataRowClass(SparseDataRow.class);
	  sheet.setName("Meta-Data: " + file.getAbsolutePath());
	  row = sheet.addRow();
	  addCell(row, "Title",             info.getTitle());
	  addCell(row, "Subject",           info.getSubject());
	  addCell(row, "Author",            info.getAuthor());
	  addCell(row, "Keywords",          info.getKeywords());
	  addCell(row, "Producer",          info.getProducer());
	  addCell(row, "Creation Date",     info.getCreationDate());
	  addCell(row, "Modification Date", info.getModificationDate());
	  addCell(row, "Creator",           info.getCreator());
	  addCell(row, "Trapped",           info.getTrapped());
	  for (String key: keys)
	    addCell(row, "Meta-" + key, info.getCustomMetadataValue(key));
	  m_OutputToken = new Token(sheet);
	  break;

	case MAP:
	  map = new HashMap<>();
	  addMapValue(map, "Title",             info.getTitle());
	  addMapValue(map, "Subject",           info.getSubject());
	  addMapValue(map, "Author",            info.getAuthor());
	  addMapValue(map, "Keywords",          info.getKeywords());
	  addMapValue(map, "Producer",          info.getProducer());
	  addMapValue(map, "Creation Date",     info.getCreationDate());
	  addMapValue(map, "Modification Date", info.getModificationDate());
	  addMapValue(map, "Creator",           info.getCreator());
	  addMapValue(map, "Trapped",           info.getTrapped());
	  for (String key: keys)
	    addMapValue(map, "Meta-" + key, info.getCustomMetadataValue(key));
	  m_OutputToken = new Token(map);
	  break;

	default:
	  throw new IllegalStateException("Unhandled output type: " + m_OutputType);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to extract meta-data: ", e);
    }

    return result;
  }
}
