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
 * WekaTextDirectoryReader.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Loads all text files in a directory and uses the subdirectory names as class labels. The content of the text files will be stored in a String attribute, the filename can be stored as well.<br/>
 * Uses the WEKA weka.core.converters.TextDirectoryLoader converter.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaTextDirectoryReader
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
 * <pre>-store-filename (property: storeFilename)
 * &nbsp;&nbsp;&nbsp;If enabled, the filename will be stored in extra attribute.
 * </pre>
 *
 * <pre>-char-set &lt;java.lang.String&gt; (property: charSet)
 * &nbsp;&nbsp;&nbsp;The character set to use when loading the text files.
 * &nbsp;&nbsp;&nbsp;default: UTF-8
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTextDirectoryReader
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 9097157984356638281L;

  /** whether to store the filename as extra attribute. */
  protected boolean m_StoreFilename;

  /** the character set. */
  protected String m_CharSet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        new TextDirectoryLoader().globalInfo() + "\n"
      + "Uses the WEKA " + TextDirectoryLoader.class.getName() + " converter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "store-filename", "storeFilename",
	    false);

    m_OptionManager.add(
	    "char-set", "charSet",
	    "UTF-8");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "charSet", m_CharSet);
    result += QuickInfoHelper.toString(this, "storeFilename", m_StoreFilename, "(add filename)", ", ");

    return result;
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
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Sets whether to store the filename in extra attribute.
   *
   * @param value	if true then filename gets stored as well
   */
  public void setStoreFilename(boolean value) {
    m_StoreFilename = value;
    reset();
  }

  /**
   * Returns whether the filename gets stored in extra attribute.
   *
   * @return		true if a filename gets stored
   */
  public boolean getStoreFilename() {
    return m_StoreFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storeFilenameTipText() {
    return "If enabled, the filename will be stored in extra attribute.";
  }

  /**
   * Sets the character set to use.
   *
   * @param value	the character set
   */
  public void setCharSet(String value) {
    m_CharSet = value;
    reset();
  }

  /**
   * Returns the character set in use.
   *
   * @return		the character set
   */
  public String getCharSet() {
    return m_CharSet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String charSetTipText() {
    return "The character set to use when loading the text files.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    TextDirectoryLoader	loader;
    Instances		data;
    File		file;

    result = null;

    try {
      if (m_InputToken.getPayload() instanceof File)
	file = (File) m_InputToken.getPayload();
      else
	file = new PlaceholderFile((String) m_InputToken.getPayload());

      if (file.isDirectory()) {
	loader = new TextDirectoryLoader();
	loader.setDirectory(file);
	loader.setOutputFilename(m_StoreFilename);
	loader.setCharSet(m_CharSet);
	data = loader.getDataSet();
	m_OutputToken = new Token(data);
	updateProvenance(m_OutputToken);
      }
      else {
	result = "Input is not a directory: " + file;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to load directory with text files: ", e);
    }

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
}
