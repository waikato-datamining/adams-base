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
 * WekaFileWriter.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.File;

import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSink;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;

/**
 <!-- globalinfo-start -->
 * Actor for saving a weka.core.Instances object as file.<br>
 * The relation name of the incoming dataset can be used to replace the current filename (path and extension are kept).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaFileWriter
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The filename of the dataset to write (the file extension determines the
 * &nbsp;&nbsp;&nbsp;file format).
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-use-relation (property: useRelationNameAsFilename)
 * &nbsp;&nbsp;&nbsp;If set to true, then the relation name replaces the name of the output file;
 * &nbsp;&nbsp;&nbsp; eg if the output file is '&#47;some&#47;where&#47;file.arff' and the relation is 'anneal'
 * &nbsp;&nbsp;&nbsp; then the resulting file name will be '&#47;some&#47;where&#47;anneal.arff'.
 * </pre>
 *
 * <pre>-use-custom (property: useCustomSaver)
 * &nbsp;&nbsp;&nbsp;If set to true, then the custom saver will be used for saving the data.
 * </pre>
 *
 * <pre>-saver &lt;weka.core.converters.AbstractFileSaver [options]&gt; (property: customSaver)
 * &nbsp;&nbsp;&nbsp;The custom saver to use if enabled.
 * &nbsp;&nbsp;&nbsp;default: weka.core.converters.ArffSaver
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaFileWriter
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 7509908838736709270L;

  /** whether to use the relation name as filename. */
  protected boolean m_UseRelationNameAsFilename;

  /** whether to use a custom converter. */
  protected boolean m_UseCustomSaver;

  /** the custom saver. */
  protected AbstractFileSaver m_CustomSaver;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor for saving a weka.core.Instances object as file.\n"
      + "The relation name of the incoming dataset can be used to replace the "
      + "current filename (path and extension are kept).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-relation", "useRelationNameAsFilename",
	    false);

    m_OptionManager.add(
	    "use-custom", "useCustomSaver",
	    false);

    m_OptionManager.add(
	    "saver", "customSaver",
	    new ArffSaver());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The filename of the dataset to write (the file extension determines the file format).";
  }

  /**
   * Sets whether to use the relation name as filename instead.
   *
   * @param value	if true then the relation name will be used
   */
  public void setUseRelationNameAsFilename(boolean value) {
    m_UseRelationNameAsFilename = value;
    reset();
  }

  /**
   * Returns whether the relation name is used as filename.
   *
   * @return		true if the relation name is used
   */
  public boolean getUseRelationNameAsFilename() {
    return m_UseRelationNameAsFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRelationNameAsFilenameTipText() {
    return
        "If set to true, then the relation name replaces the name of the output "
      + "file; eg if the output file is '/some/where/file.arff' and the "
      + "relation is 'anneal' then the resulting file name will be "
      + "'/some/where/anneal.arff'.";
  }

  /**
   * Sets whether to use a custom saver or not.
   *
   * @param value	if true then the custom saver will be used
   */
  public void setUseCustomSaver(boolean value) {
    m_UseCustomSaver = value;
    reset();
  }

  /**
   * Returns whether a custom saver is used or not.
   *
   * @return		true if a custom saver is used
   */
  public boolean getUseCustomSaver() {
    return m_UseCustomSaver;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomSaverTipText() {
    return "If set to true, then the custom saver will be used for saving the data.";
  }

  /**
   * Sets the custom saver to use.
   *
   * @param value	the custom saver
   */
  public void setCustomSaver(AbstractFileSaver value) {
    m_CustomSaver = value;
    reset();
  }

  /**
   * Returns the custom saver in use.
   *
   * @return		the custom saver
   */
  public AbstractFileSaver getCustomSaver() {
    return m_CustomSaver;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customSaverTipText() {
    return "The custom saver to use if enabled.";
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

    result = super.getQuickInfo();
    
    result += QuickInfoHelper.toString(this, "useRelationNameAsFilename", m_UseRelationNameAsFilename, "relation name as filename", ", ");
    
    if (QuickInfoHelper.hasVariable(this, "useCustomSaver") || m_UseCustomSaver) {
      value = QuickInfoHelper.toString(this, "customSaver", Utils.shorten(OptionUtils.getShortCommandLine(getCustomSaver()), 40), ", saver: ");
      if (value != null)
	result += value;
    }
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Hook for performing setup checks -- used in setUp() and preExecute().
   *
   * @param fromSetUp	whether the method has been called from within setUp()
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String performSetUpChecks(boolean fromSetUp) {
    String	result;

    result = super.performSetUpChecks(fromSetUp);

    if (result == null) {
      if (!m_UseCustomSaver && canPerformSetUpCheck(fromSetUp, "outputFile")) {
	if (ConverterUtils.getSaverForFile(m_OutputFile.getAbsolutePath()) == null)
	  result = "Cannot determine converter for file '" + m_OutputFile.getAbsolutePath() + "'!";

      }
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
    String		result;
    Instances		data;
    String		filename;
    File		file;
    DataSink		sink;

    result = null;

    data     = (Instances) m_InputToken.getPayload();
    filename = null;
    try {
      // determine filename
      filename = m_OutputFile.getAbsolutePath();
      if (m_UseRelationNameAsFilename) {
	file     = new File(filename);
	filename =   file.getParent()
	           + File.separator
	           + FileUtils.createFilename(data.relationName(), "_")
	           + file.getName().replaceAll(".*\\.", ".");
      }

      if (m_UseCustomSaver) {
	m_CustomSaver.setFile(new File(filename));
	sink = new DataSink(m_CustomSaver);
      }
      else {
	sink = new DataSink(filename);
      }

      // save file
      sink.write(data);
    }
    catch (Exception e) {
      result = handleException("Failed to save dataset to: " + filename, e);
    }

    return result;
  }
}
