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
 * WekaFileReader.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.AArffLoader;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.SimpleArffLoader;
import weka.core.converters.URLSourcedLoader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads any file format that Weka's converters can handle and returns the full dataset or single weka.core.Instance objects. This actor takes the file or URL to read as input. In case of URLs, the associated loader must implement weka.core.converters.URLSourcedLoader.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.net.URL<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaFileReader
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
 * <pre>-use-custom (property: useCustomLoader)
 * &nbsp;&nbsp;&nbsp;If set to true, then the custom loader will be used for loading the data.
 * </pre>
 * 
 * <pre>-loader &lt;weka.core.converters.AbstractFileLoader&gt; (property: customLoader)
 * &nbsp;&nbsp;&nbsp;The custom loader to use if enabled.
 * &nbsp;&nbsp;&nbsp;default: weka.core.converters.SafeArffLoader
 * </pre>
 * 
 * <pre>-output-type &lt;DATASET|HEADER|INCREMENTAL&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;Defines how the data is output, e.g., as complete dataset or row-by-row.
 * &nbsp;&nbsp;&nbsp;default: DATASET
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaFileReader
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 9097157984356638281L;

  /** the key for storing the current structure in the backup. */
  public final static String BACKUP_STRUCTURE = "structure";

  /** the key for storing the current source in the backup. */
  public final static String BACKUP_SOURCE = "source";

  /**
   * Defines how to output the data.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputType {
    /** the complete dataset. */
    DATASET,
    /** only the header. */
    HEADER,
    /** row by row. */
    INCREMENTAL
  }
  
  /** whether to use a custom converter. */
  protected boolean m_UseCustomLoader;

  /** the custom loader. */
  protected AbstractFileLoader m_CustomLoader;

  /** how to output the data. */
  protected OutputType m_OutputType;

  /** the structure. */
  protected Instances m_Structure;

  /** the actual loader for loading the data. */
  protected DataSource m_Source;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Reads any file format that Weka's converters can handle and returns "
      + "the full dataset or single weka.core.Instance objects. This actor "
      + "takes the file or URL to read as input. In case of URLs, the "
      + "associated loader must implement " + URLSourcedLoader.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-custom", "useCustomLoader",
	    false);

    m_OptionManager.add(
	    "loader", "customLoader",
	    new SimpleArffLoader());

    m_OptionManager.add(
	    "output-type", "outputType",
	    OutputType.DATASET);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = null;

    if (QuickInfoHelper.hasVariable(this, "useCustomLoader") || m_UseCustomLoader)
      result = QuickInfoHelper.toString(this, "loader", Shortening.shortenEnd(OptionUtils.getShortCommandLine(getCustomLoader()), 40));
    else
      result = "automatic";

    result += " (" + QuickInfoHelper.toString(this, "outputType", m_OutputType) + ")";
 
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class, java.net.URL.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    List<Class>	result;

    result = new ArrayList<Class>();
    result.add(String.class);
    result.add(File.class);

    if (getUseCustomLoader()) {
      if (getCustomLoader() instanceof URLSourcedLoader)
	result.add(URL.class);
    }
    else {
      // we have to wait till we get the URL and determine the loader then
      result.add(URL.class);
    }

    return result.toArray(new Class[result.size()]);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    if (m_OutputType == OutputType.INCREMENTAL)
      return new Class[]{Instance.class};
    else
      return new Class[]{Instances.class};
  }

  /**
   * Sets whether to use a custom loader or not.
   *
   * @param value	if true then the custom loader will be used
   */
  public void setUseCustomLoader(boolean value) {
    m_UseCustomLoader = value;
    reset();
  }

  /**
   * Returns whether a custom loader is used or not.
   *
   * @return		true if a custom loader is used
   */
  public boolean getUseCustomLoader() {
    return m_UseCustomLoader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCustomLoaderTipText() {
    return "If set to true, then the custom loader will be used for loading the data.";
  }

  /**
   * Sets the custom loader to use.
   *
   * @param value	the custom loader
   */
  public void setCustomLoader(AbstractFileLoader value) {
    m_CustomLoader = value;
    reset();
  }

  /**
   * Returns the custom loader in use.
   *
   * @return		the custom loader
   */
  public AbstractFileLoader getCustomLoader() {
    return m_CustomLoader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customLoaderTipText() {
    return "The custom loader to use if enabled.";
  }

  /**
   * Sets how to output the data.
   *
   * @param value	the output type
   */
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns how to output the data.
   *
   * @return		the output type
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "Defines how the data is output, e.g., as complete dataset or row-by-row.";
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_STRUCTURE);
    pruneBackup(BACKUP_SOURCE);
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

    if (m_Structure != null)
      result.put(BACKUP_STRUCTURE, m_Structure);
    if (m_Source != null)
      result.put(BACKUP_SOURCE, m_Source);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_STRUCTURE)) {
      m_Structure = (Instances) state.get(BACKUP_STRUCTURE);
      state.remove(BACKUP_STRUCTURE);
    }
    if (state.containsKey(BACKUP_SOURCE)) {
      m_Source = (DataSource) state.get(BACKUP_SOURCE);
      state.remove(BACKUP_SOURCE);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Structure = null;
    m_Source    = null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    AbstractFileLoader	loader;
    Object		obj;
    File		file;
    URL			url;
    boolean		isArff;
    String[]		exts;
    String		ext;

    result = null;

    try {
      obj    = m_InputToken.getPayload();
      exts   = FileUtils.getExtensions(obj.toString().toLowerCase());
      if ((exts != null) && (exts.length > 0)) {
        ext    = exts[0];
        isArff = (ext.equals("arff") || ext.equals("arff.gz"));
      }
      else {
        ext    = "";
        isArff = false;
      }
      file   = null;
      url    = null;
      if (obj instanceof File)
	file = new File(((File) obj).getAbsolutePath());
      else if (obj instanceof URL)
	url = (URL) obj;
      else
	file = new File((new PlaceholderFile((String) obj)).getAbsolutePath());

      m_Source = null;
      if (m_UseCustomLoader) {
	loader = m_CustomLoader;
	if (url != null)
	  ((URLSourcedLoader) loader).setURL(url.toString());
	else
	  loader.setFile(file);
	m_Source = new DataSource(loader);
      }
      else {
        if (ext.isEmpty()) {
          result = "File has no extension to be used by file type detection, but no custom loader defined!";
        }
        else {
          if (isArff) {
            if (url != null) {
              loader = new AArffLoader();
              ((URLSourcedLoader) loader).setURL(url.toString());
            }
            else {
              loader = new SimpleArffLoader();
              loader.setFile(file);
            }
            m_Source = new DataSource(loader);
          }
          else {
            if (url != null)
              m_Source = new DataSource(url.toString());
            else
              m_Source = new DataSource(file.getAbsolutePath());
          }
        }
      }

      // obtain structure
      if (m_Source != null)
	m_Structure = m_Source.getStructure();
    }
    catch (Exception e) {
      result = handleException("Failed to load data from: " + m_InputToken.getPayload(), e);
    }

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

    switch (m_OutputType) {
      case DATASET:
	try {
	  result = new Token(m_Source.getDataSet());
	}
	catch (Exception e) {
	  result = null;
	  handleException("Failed to get dataset:", e);
	}
	m_Structure = null;
	m_Source    = null;
	break;
	
      case HEADER:
	result      = new Token(m_Structure);
	m_Structure = null;
	m_Source    = null;
	break;
	
      case INCREMENTAL:
	result = new Token(m_Source.nextElement(m_Structure));
	if (!m_Source.hasMoreElements(m_Structure)) {
	  m_Structure = null;
	  m_Source    = null;
	}
	break;
	
      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
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
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, ((Token) cont).getPayload().getClass()));
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    switch (m_OutputType) {
      case INCREMENTAL:
	return ((m_Structure != null) && (m_Source.hasMoreElements(m_Structure)));
      default:
	return (m_Structure != null);
    }
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Structure = null;
    m_Source    = null;

    super.wrapUp();
  }
}
