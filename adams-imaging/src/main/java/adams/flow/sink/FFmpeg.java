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
 * FFmpeg.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.File;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.core.management.OS;
import adams.flow.sink.ffmpeg.AbstractFFmpegPlugin;
import adams.flow.sink.ffmpeg.GenericPlugin;

/**
 <!-- globalinfo-start -->
 * Uses the specified plugin to perform a avconv/ffmpeg operation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * &nbsp;&nbsp;&nbsp;default: FFmpeg
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
 * &nbsp;&nbsp;&nbsp;The output file to generate.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-executable &lt;adams.core.io.PlaceholderFile&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The ffmpeg executable.
 * &nbsp;&nbsp;&nbsp;default: &#47;usr&#47;bin&#47;ffmpeg
 * </pre>
 * 
 * <pre>-plugin &lt;adams.flow.sink.ffmpeg.AbstractFFmpegPlugin&gt; (property: plugin)
 * &nbsp;&nbsp;&nbsp;The plugin to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.ffmpeg.GenericPlugin
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FFmpeg
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -7106585852803101639L;

  /** the ffmpeg executable. */
  protected PlaceholderFile m_Executable;
  
  /** the plugin to execute. */
  protected AbstractFFmpegPlugin m_Plugin;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified plugin to perform an avconv/ffmpeg operation.";
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
	    "plugin", "plugin",
	    new GenericPlugin());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "executable", m_Executable, ", executable: ");
    result += QuickInfoHelper.toString(this, "plugin", m_Plugin, ", plugin: ");

    return result;
  }
  
  /**
   * Returns the default executable.
   * 
   * @return		the default
   */
  protected PlaceholderFile getDefaultExecutable() {
    // TODO search PATH?
    if (OS.isWindows())
      return new PlaceholderFile("avconv.exe");
    else
      return new PlaceholderFile("/usr/bin/avconv");
  }

  /**
   * Sets the ffmpeg executable.
   *
   * @param value	the executable
   */
  public void setExecutable(PlaceholderFile value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the ffmpeg executable.
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
    return "The avconv/ffmpeg executable.";
  }

  /**
   * Sets the plugin to use.
   *
   * @param value	the plugin
   */
  public void setPlugin(AbstractFFmpegPlugin value) {
    m_Plugin = value;
    m_Plugin.setOwner(this);
    reset();
  }

  /**
   * Returns the plugin to use.
   *
   * @return		the plugin
   */
  public AbstractFFmpegPlugin getPlugin() {
    return m_Plugin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pluginTipText() {
    return "The plugin to use.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The output file to generate.";
  }
  
  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{File.class, String.class};
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
      if (m_Executable.isDirectory())
	result = "Executable points to a directory: " + m_Executable;
      else if (!m_Executable.exists())
	result = "Executable does not exist: " + m_Executable;
    }
    
    if (result == null)
      result = m_Plugin.setUp();
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (m_InputToken.getPayload() instanceof File)
      m_Plugin.setInput(new PlaceholderFile((File) m_InputToken.getPayload()));
    else
      m_Plugin.setInput(new PlaceholderFile((String) m_InputToken.getPayload()));
    
    return m_Plugin.execute();
  }
}
