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
 * SpreadSheetFileReader.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.ChunkedSpreadSheetReader;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.MultiSheetSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads a spreadsheet file and forwards the content.
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetFileReader
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
 * <pre>-output-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the sheets get output as array rather than one-by-one.
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader for storing the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetFileReader
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -1585651878523228177L;

  /** the reader to use. */
  protected SpreadSheetReader m_Reader;

  /**
   * Default constructor.
   */
  public SpreadSheetFileReader() {
    super();
  }

  /**
   * Constructor for setting the reader.
   */
  public SpreadSheetFileReader(SpreadSheetReader reader) {
    super();
    setReader(reader);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads a spreadsheet file and forwards the content.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    new CsvSpreadSheetReader());
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
    
    result = QuickInfoHelper.toString(this, "reader", m_Reader, "reader: ");
    value  = QuickInfoHelper.toString(this, "outputArray", m_OutputArray, (m_OutputArray ? "as array" : "one-by-one"), ", ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    if ((m_Reader != null) && (m_Reader.getSpreadSheetType() != null))
      return m_Reader.getSpreadSheetType().getClass();
    else
      return SpreadSheet.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the sheets get output as array rather than one-by-one.";
  }

  /**
   * Sets the reader to use.
   *
   * @param value	the reader to use
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader in use.
   *
   * @return		the reader in use
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader for storing the spreadsheet.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class, Reader.class, InputStream.class};
  }

  /**
   * Updates the name of the sheet if no name present and a filename available.
   *
   * @param sheet	the sheet to update
   * @param file	the (optional) file name
   * @param suffix	the (optional) suffix to append
   */
  protected void updateName(SpreadSheet sheet, File file, String suffix) {
    String	name;

    if (!sheet.hasName()) {
      if (file != null)
	name = FileUtils.replaceExtension(file, "").getName();
      else
	name = "input";
      sheet.setName(name + (suffix == null ? "" : suffix));
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File		file;
    Reader		reader;
    InputStream		stream;
    SpreadSheet		sheet;
    List<SpreadSheet>	sheets;
    boolean		added;
    int                 i;

    result = null;

    file   = null;
    reader = null;
    stream = null;
    if (m_InputToken.hasPayload(File.class))
      file = m_InputToken.getPayload(File.class);
    else if (m_InputToken.hasPayload(String.class))
      file = new PlaceholderFile(m_InputToken.getPayload(String.class));
    else if (m_InputToken.hasPayload(Reader.class))
      reader = m_InputToken.getPayload(Reader.class);
    else if (m_InputToken.hasPayload(InputStream.class))
      stream = m_InputToken.getPayload(InputStream.class);

    added  = false;
    sheets = null;
    sheet  = null;
    if (m_Reader instanceof MultiSheetSpreadSheetReader) {
      if (file != null)
	sheets = ((MultiSheetSpreadSheetReader) m_Reader).readRange(file);
      else if (reader != null)
	sheets = ((MultiSheetSpreadSheetReader) m_Reader).readRange(reader);
      else if (stream != null)
	sheets = ((MultiSheetSpreadSheetReader) m_Reader).readRange(stream);
      if (sheets != null) {
        i = 0;
        for (SpreadSheet sh: sheets) {
          i++;
          updateName(sh, file, "-" + i);
        }
	m_Queue.addAll(sheets);
	added = true;
      }
    }
    else if (m_Reader instanceof ChunkedSpreadSheetReader) {
      if (file != null)
	sheet = m_Reader.read(file);
      else if (reader != null)
	sheet = m_Reader.read(reader);
      else if (stream != null)
	sheet = m_Reader.read(stream);
      if (sheet != null) {
	updateName(sheet, file, null);
	if (m_OutputArray)
	  m_OutputToken = new Token(new SpreadSheet[]{sheet});
	else
	  m_OutputToken = new Token(sheet);
	added = true;
      }
    }
    else {
      if (file != null)
	sheet = m_Reader.read(file);
      else if (reader != null)
	sheet = m_Reader.read(reader);
      else if (stream != null)
	sheet = m_Reader.read(stream);
      if (sheet != null) {
	updateName(sheet, file, null);
	m_Queue.add(sheet);
	added = true;
      }
    }

    if (isStopped()) {
      m_Queue.clear();
      m_OutputToken = null;
    }
    else {
      if (!added) {
	if (m_Reader.hasLastError())
	  result = "Error reading spreadsheet: " + file + "\n" + m_Reader.getLastError();
	else
	  result = "Error reading spreadsheet: " + file;
      }
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
    boolean	result;
    
    if (m_Reader instanceof ChunkedSpreadSheetReader)
      result = (m_OutputToken != null) | ((ChunkedSpreadSheetReader) m_Reader).hasMoreChunks();
    else
      result = super.hasPendingOutput();
    
    return result;
  }
  
  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = null;

    if (m_Reader instanceof ChunkedSpreadSheetReader) {
      if (m_OutputToken != null) {
	result        = m_OutputToken;
	m_OutputToken = null;
      }
      else {
	if (((ChunkedSpreadSheetReader) m_Reader).hasMoreChunks()) {
	  if (m_OutputArray)
	    result = new Token(new SpreadSheet[]{((ChunkedSpreadSheetReader) m_Reader).nextChunk()});
	  else
	    result = new Token(((ChunkedSpreadSheetReader) m_Reader).nextChunk());
	}
      }
    }
    else {
      result = super.output();
    }
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Reader != null)
      m_Reader.stopExecution();
    super.stopExecution();
  }
}
