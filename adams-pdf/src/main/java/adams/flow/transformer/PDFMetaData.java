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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.Calendar;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SparseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Actor for extracting a range of pages from a PDF file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: PDFMetaData
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The PDF file to output the extracted pages to.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-pages &lt;java.lang.String&gt; (property: pages)
 * &nbsp;&nbsp;&nbsp;The range of pages to extract; A range is a comma-separated list of single
 * &nbsp;&nbsp;&nbsp;1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts
 * &nbsp;&nbsp;&nbsp;the range '...'; the following placeholders can be used as well: first,
 * &nbsp;&nbsp;&nbsp;second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    File			file;
    SpreadSheet			sheet;
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

    sheet = new SpreadSheet();
    sheet.setDataRowClass(SparseDataRow.class);
    sheet.setName("Meta-Data: " + file.getAbsolutePath());

    try {
      row      = sheet.addRow();
      document = PDDocument.load(file.getAbsolutePath());
      info     = document.getDocumentInformation();

      addCell(row, "Title",             info.getTitle());
      addCell(row, "Subject",           info.getSubject());
      addCell(row, "Author",            info.getAuthor());
      addCell(row, "Keywords",          info.getKeywords());
      addCell(row, "Producer",          info.getProducer());
      addCell(row, "Creation Date",     info.getCreationDate());
      addCell(row, "Modification Date", info.getModificationDate());
      addCell(row, "Creator",           info.getCreator());
      addCell(row, "Trapped",           info.getTrapped());
      keys = info.getMetadataKeys();
      for (String key: keys)
	addCell(row, "Meta-" + key, info.getCustomMetadataValue(key));
    }
    catch (Exception e) {
      result = handleException("Failed to extract meta-data: ", e);
    }

    if (result == null)
      m_OutputToken = new Token(sheet);

    return result;
  }
}
