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

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.transformer.metadata.AbstractMetaDataExtractor;
import adams.flow.transformer.metadata.Sanselan;

/**
 <!-- globalinfo-start -->
 * Returns any EXIF or IPTC and basic image information as a spreadsheet.
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
 * &nbsp;&nbsp;&nbsp;default: ImageMetaData
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
 * <pre>-extractor &lt;adams.flow.transformer.metadata.AbstractMetaDataExtractor&gt; (property: extractor)
 * &nbsp;&nbsp;&nbsp;The meta-data extrator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.metadata.Sanselan
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

  /** the extractor to use. */
  protected AbstractMetaDataExtractor m_Extractor;
  
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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "extractor", "extractor",
	    new Sanselan());
  }

  /**
   * Sets the extractor to use.
   *
   * @param value	the extractor
   */
  public void setExtractor(AbstractMetaDataExtractor value) {
    m_Extractor = value;
    reset();
  }

  /**
   * Returns the extractor in use.
   *
   * @return		the extractor
   */
  public AbstractMetaDataExtractor getExtractor() {
    return m_Extractor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extractorTipText() {
    return "The meta-data extrator to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "extractor", m_Extractor, "extractor: ");
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    SpreadSheet	sheet;
    File	file;
    
    result = null;
    
    if (m_InputToken.getPayload() instanceof String)
      file = new File(new PlaceholderFile((String) m_InputToken.getPayload()).getAbsolutePath());
    else
      file = new File(((File) m_InputToken.getPayload()).getAbsolutePath());
    
    try {
      sheet         = m_Extractor.extract(file);
      m_OutputToken = new Token(sheet);
    }
    catch (Exception e) {
      result = handleException("Failed to read meta-data from: " + file, e);
    }
    
    return result;
  }
}
