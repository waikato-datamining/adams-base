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
 * TesseractConfiguration.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.TesseractHelper;
import adams.core.io.PlaceholderFile;
import adams.core.management.OS;
import adams.flow.core.TesseractLanguage;
import adams.flow.core.TesseractPageSegmentation;

/**
 <!-- globalinfo-start -->
 * Setup parameters for tesseract.<br/>
 * For more information see:<br/>
 * http:&#47;&#47;code.google.com&#47;p&#47;tesseract-ocr&#47;
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TesseractConfiguration
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
 * <pre>-executable &lt;adams.core.io.PlaceholderFile&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The tesseract executable to use.
 * &nbsp;&nbsp;&nbsp;default: &#47;usr&#47;bin&#47;tesseract
 * </pre>
 * 
 * <pre>-config-file &lt;adams.core.io.PlaceholderFile&gt; (property: configFile)
 * &nbsp;&nbsp;&nbsp;The (optional) config file for tesseract; ignored if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TesseractConfiguration
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the executable to use. */
  protected PlaceholderFile m_Executable;

  /** the (optional) config file to use. */
  protected PlaceholderFile m_ConfigFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Setup parameters for tesseract.\n"
	+ "For more information see:\n"
	+ "http://code.google.com/p/tesseract-ocr/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "executable", "executable",
	    getDefaultExecutable());

    m_OptionManager.add(
	    "config-file", "configFile",
	    new PlaceholderFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = QuickInfoHelper.toString(this, "executable", m_Executable, "exe: ");
    result += QuickInfoHelper.toString(this, "config", m_ConfigFile, ", config: ");

    return result;
  }

  /**
   * Returns the default executable to use.
   * 
   * @return		the exectuable
   */
  protected PlaceholderFile getDefaultExecutable() {
    if (TesseractHelper.getSingleton().getExecutable().length() > 0)
      return new PlaceholderFile(TesseractHelper.getSingleton().getExecutable());
    
    if (OS.isWindows())
      return new PlaceholderFile("tesseract.exe");
    else
      return new PlaceholderFile("/usr/bin/tesseract");
  }
  
  /**
   * Sets the tesseract executable to use.
   *
   * @param value	the executable
   */
  public void setExecutable(PlaceholderFile value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the tesseract executable in use.
   *
   * @return		the executable
   */
  public PlaceholderFile getExecutable() {
    return m_Executable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executableTipText() {
    return "The tesseract executable to use.";
  }

  /**
   * Sets the config file, ignored if pointing to directory.
   *
   * @param value	the config file
   */
  public void setConfigFile(PlaceholderFile value) {
    m_ConfigFile = value;
    reset();
  }

  /**
   * Returns the config file, ignored if pointing to directory.
   *
   * @return		the config file
   */
  public PlaceholderFile getConfigFile() {
    return m_ConfigFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String configFileTipText() {
    return "The (optional) config file for tesseract; ignored if pointing to a directory.";
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
      if (!m_Executable.exists())
	result = "Executable '" + m_Executable + "' does not exist!";
      else if (m_Executable.isDirectory())
	result = "Executable '" + m_Executable + "' points to directory!";
      else if (!m_ConfigFile.exists())
	result = "Config file '" + m_ConfigFile + "' does not exist!";
    }
    
    return result;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    return null;
  }
  
  /**
   * Assembles the tesseract command for the given input/output.
   * 
   * @param input	the input file to process
   * @param outputbase	the output base to use
   * @return		the command
   */
  public String[] getCommand(String input, String outputbase, TesseractLanguage lang, TesseractPageSegmentation seg) {
    return TesseractHelper.getSingleton().getCommand(
	m_Executable.getAbsolutePath(), 
	input, 
	outputbase, 
	lang, 
	seg, 
	m_ConfigFile);
  }
}
