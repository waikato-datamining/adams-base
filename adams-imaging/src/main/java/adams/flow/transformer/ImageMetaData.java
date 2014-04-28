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
 * ImageMetaData.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.io.File;

import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.ImageMetadata;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Returns any EXIF or IPTC and basic image information as a spreadsheet.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
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
 * &nbsp;&nbsp;&nbsp;default: ImageMetaData
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMetaData
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8005075286840278197L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns any EXIF or IPTC and basic image information as a spreadsheet.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
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
   * Adds data to the spreadsheet.
   * 
   * @param sheet	the spreadsheet to add the data to
   * @param key		the key column
   * @param value	the value column
   */
  protected void addRow(SpreadSheet sheet, String key, String value) {
    Row		row;
    
    row = sheet.addRow();
    row.addCell("K").setContent(key);
    row.addCell("V").setContent(Utils.unquote(value));
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    SpreadSheet				sheet;
    Row					row;
    File				file;
    IImageMetadata			meta;
    String[]				parts;
    String				key;
    String				value;
    org.apache.sanselan.ImageInfo	info;
    String				infoStr;
    String[]				lines;
    
    result = null;
    
    if (m_InputToken.getPayload() instanceof String)
      file = new File(new PlaceholderFile((String) m_InputToken.getPayload()).getAbsolutePath());
    else
      file = new File(((File) m_InputToken.getPayload()).getAbsolutePath());
    
    try {
      sheet = new SpreadSheet();
      // header
      row = sheet.getHeaderRow();
      row.addCell("K").setContent("Key");
      row.addCell("V").setContent("Value");
      // meta-data
      meta = Sanselan.getMetadata(file);
      if (meta != null) {
	for (Object item: meta.getItems()) {
	  key   = null;
	  value = null;
	  if (item instanceof ImageMetadata.Item) {
	    key   = ((ImageMetadata.Item) item).getKeyword();
	    value = ((ImageMetadata.Item) item).getText();
	  }
	  else {
	    parts = item.toString().split(": ");
	    if (parts.length == 2) {
	      key   = parts[0];
	      value = parts[1];
	    }
	    else {
	      if (isLoggingEnabled())
		getLogger().info("Failed to parse: " + item);
	    }
	  }
	  if (key != null)
	    addRow(sheet, key, value);
	}
      }
      // image info
      info = Sanselan.getImageInfo(file);
      if (info != null) {
	infoStr = info.toString();
	lines = infoStr.split(System.lineSeparator());
	for (String line: lines) {
	  parts = line.split(": ");
	  if (parts.length == 2) {
	    key   = parts[0];
	    value = parts[1];
	    addRow(sheet, key, value);
	  }
	}
      }
      
      m_OutputToken = new Token(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to read meta-data from: " + file, e);
    }
    
    return result;
  }
}
