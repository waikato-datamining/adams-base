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
 * ImageFileReader.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.MetaDataFileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.input.JAIImageReader;
import adams.data.report.Report;
import adams.flow.core.Token;

import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads any file format that the specified image reader supports.<br>
 * If meta-data is associated with the image, then this can be loaded as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageReader
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
 * <pre>-reader &lt;adams.data.io.input.AbstractImageReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The image reader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.JAIImageReader
 * </pre>
 *
 * <pre>-load-meta-data &lt;boolean&gt; (property: loadMetaData)
 * &nbsp;&nbsp;&nbsp;If enabled, loading of meta-data is attempted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-meta-data-location &lt;SAME_NAME|STARTING_WITH&gt; (property: metaDataLocation)
 * &nbsp;&nbsp;&nbsp;The location of the meta-data.
 * &nbsp;&nbsp;&nbsp;default: SAME_NAME
 * </pre>
 *
 * <pre>-meta-data-reader &lt;adams.data.io.input.AbstractReportReader&gt; (property: metaDataReader)
 * &nbsp;&nbsp;&nbsp;The reader to use for the meta-data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultSimpleReportReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageFileReader
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7466006970025235243L;

  /** the image reader to use. */
  protected AbstractImageReader m_Reader;

  /** whether to load the meta-data as well (if present). */
  protected boolean m_LoadMetaData;

  /** how to locate the meta-data. */
  protected MetaDataFileUtils.MetaDataLocation m_MetaDataLocation;

  /** for reading the meta-data. */
  protected AbstractReportReader m_MetaDataReader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads any file format that the specified image reader supports.\n"
      + "If meta-data is associated with the image, then this can be loaded as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new JAIImageReader());

    m_OptionManager.add(
      "load-meta-data", "loadMetaData",
      false);

    m_OptionManager.add(
      "meta-data-location", "metaDataLocation",
      MetaDataFileUtils.MetaDataLocation.SAME_NAME);

    m_OptionManager.add(
      "meta-data-reader", "metaDataReader",
      new DefaultSimpleReportReader());
  }

  /**
   * Sets the reader to use.
   *
   * @param value 	the reader
   */
  public void setReader(AbstractImageReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use.
   *
   * @return 		the reader
   */
  public AbstractImageReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The image reader to use.";
  }

  /**
   * Sets whether to load available meta-data.
   *
   * @param value 	true if to load meta-data
   */
  public void setLoadMetaData(boolean value) {
    m_LoadMetaData = value;
    reset();
  }

  /**
   * Returns whether to load available meta-data.
   *
   * @return 		true if to load meta-data
   */
  public boolean getLoadMetaData() {
    return m_LoadMetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loadMetaDataTipText() {
    return "If enabled, loading of meta-data is attempted.";
  }

  /**
   * Sets where to find the meta-data.
   *
   * @param value 	the location
   */
  public void setMetaDataLocation(MetaDataFileUtils.MetaDataLocation value) {
    m_MetaDataLocation = value;
    reset();
  }

  /**
   * Returns where to find the meta-data.
   *
   * @return 		the location
   */
  public MetaDataFileUtils.MetaDataLocation getMetaDataLocation() {
    return m_MetaDataLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataLocationTipText() {
    return "The location of the meta-data.";
  }

  /**
   * Sets the reader to use for the meta-data.
   *
   * @param value 	the reader
   */
  public void setMetaDataReader(AbstractReportReader value) {
    m_MetaDataReader = value;
    reset();
  }

  /**
   * Returns the reader to use for the meta-data.
   *
   * @return 		the reader
   */
  public AbstractReportReader getMetaDataReader() {
    return m_MetaDataReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataReaderTipText() {
    return "The reader to use for the meta-data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.image.AbstractImageContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "reader", m_Reader);
    if (m_LoadMetaData) {
      result += QuickInfoHelper.toString(this, "metaDataLocation", m_MetaDataLocation, ", location: ");
      result += QuickInfoHelper.toString(this, "metaDataReader", m_MetaDataReader, ", meta-data: ");
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!m_Reader.isAvailable())
        result = "Reader '" + m_Reader.getClass().getName() + "' is not available - check setup!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    PlaceholderFile		file;
    AbstractImageContainer	cont;
    PlaceholderFile[]		metaFiles;
    Report			metaData;
    List	 		reports;
    Report			report;

    result = null;

    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else
      file = new PlaceholderFile((File) m_InputToken.getPayload());

    cont = null;
    try {
      cont = m_Reader.read(file);
      if (cont != null)
        m_OutputToken = new Token(cont);
      else
        result = "Failed to read image: " + file;
    }
    catch (Exception e) {
      result = handleException("Failed to read image: " + file, e);
    }

    // meta-data?
    if (m_LoadMetaData && (result == null)) {
      metaFiles = MetaDataFileUtils.find(this, file, m_MetaDataLocation, m_MetaDataReader.getDefaultFormatExtension(), m_MetaDataReader.getFormatExtensions());
      metaData  = null;
      for (PlaceholderFile metaFile: metaFiles) {
        m_MetaDataReader.setInput(metaFile);
        reports = m_MetaDataReader.read();
        for (Object obj: reports) {
          report = (Report) obj;
          if (metaData == null)
            metaData = report;
          else
            metaData.mergeWith(report);
	}
      }
      if ((cont != null) && (metaData != null))
        cont.getReport().mergeWith(metaData);
    }

    return result;
  }
}
