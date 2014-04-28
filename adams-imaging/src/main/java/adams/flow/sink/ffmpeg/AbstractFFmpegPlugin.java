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
 * AbstractFFmpegPlugin.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.ffmpeg;

import java.util.ArrayList;
import java.util.Arrays;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.management.ProcessUtils;
import adams.core.management.ProcessUtils.ProcessResult;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.sink.FFmpeg;

/**
 * Ancestor for {@link FFmpeg} plugins.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFFmpegPlugin
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2555683041357914117L;

  /** the owning actor. */
  protected FFmpeg m_Owner;
  
  /** the input file. */
  protected PlaceholderFile m_Input;
  
  /** the bitrate in kbits to use. */
  protected int m_BitRate;
  
  /** the frames per second to use. */
  protected int m_FramesPerSecond;
  
  /** additional input options to use for ffmpeg. */
  protected String m_AdditionalInputOptions;
  
  /** additional output options to use for ffmpeg. */
  protected String m_AdditionalOutputOptions;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "bit-rate", "bitRate",
	    64, 1, null);

    m_OptionManager.add(
	    "fps", "framesPerSecond",
	    25, 1, null);
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Owner = null;
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Input = null;
  }
  
  /**
   * Sets the owner.
   * 
   * @param owner	the owner
   */
  public void setOwner(FFmpeg owner) {
    m_Owner = owner;
  }
  
  /**
   * Sets the owner.
   * 
   * @reutrn		the owner
   */
  public FFmpeg getOwner() {
    return m_Owner;
  }

  /**
   * Returns a quick info about the plugin, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "framesPerSecond", m_FramesPerSecond, "-r ");
    result += QuickInfoHelper.toString(this, "bitRate", m_BitRate, "-b ");
    
    return result;
  }

  /**
   * Sets the frames per second to use.
   *
   * @param value	the fps
   */
  public void setFramesPerSecond(int value) {
    m_FramesPerSecond = value;
    reset();
  }

  /**
   * Returns the frames per second to use.
   *
   * @return		the options
   */
  public int getFramesPerSecond() {
    return m_FramesPerSecond;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String framesPerSecondTipText() {
    return "The frames per second (in Hz).";
  }

  /**
   * Sets the bit rate in kbits to use.
   *
   * @param value	the bit rate
   */
  public void setBitRate(int value) {
    m_BitRate = value;
    reset();
  }

  /**
   * Returns the bit rate in kbits to use.
   *
   * @return		the bit rate
   */
  public int getBitRate() {
    return m_BitRate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bitRateTipText() {
    return "The bit rate (in kbits) to use.";
  }

  /**
   * Sets the input file.
   * 
   * @param value	the input file
   */
  public void setInput(PlaceholderFile value) {
    m_Input = value;
  }
  
  /**
   * Returns the input file.
   * 
   * @return		the input file, null if none set
   */
  public PlaceholderFile getInput() {
    return m_Input;
  }

  /**
   * Returns the ffmpeg executable.
   * 
   * @return		the executable, null if not available or no owner set
   */
  protected PlaceholderFile getExecutable() {
    if (m_Owner == null)
      return null;
    else
      return m_Owner.getExecutable();
  }
  
  /**
   * Checks the configuration.
   * <p/>
   * Default implementation only checks whether owner is set.
   * 
   * @see #getOwner()
   * 
   * @return		null if setup ok, otherwise error message
   */
  public String setUp() {
    String	result;
    
    result = null;
    
    if (m_Owner == null)
      result = "No owner set!";
    
    return result;
  }

  /**
   * Outputs the stacktrace along with the message on stderr and returns a 
   * combination of both of them as string.
   * 
   * @param msg		the message for the exception
   * @param t		the exception
   * @return		the full error message (message + stacktrace)
   */
  protected String handleException(String msg, Throwable t) {
    return Utils.handleException(this, msg, t);
  }
  
  /**
   * Assembles the input command-line options.
   * 
   * @return		the command-line
   */
  protected abstract String assembleInputOptions();
  
  /**
   * Assembles the ouput command-line options.
   * 
   * @return		the command-line
   */
  protected abstract String assembleOutputOptions();
  
  /**
   * Performs the ffmpeg execution.
   * 
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String		result;
    ArrayList<String>	options;
    String		cmd;
    ProcessResult	proc;
    
    result = null;
    
    if (m_Input == null)
      result = "No input file set!";
    
    if (result == null) {
      options = new ArrayList<String>();
      try {
	// executable
	options.add(getExecutable().getAbsolutePath());
	// fps
	options.add("-r");
	options.add("" + m_FramesPerSecond);
	// bitrate
	options.add("-b");
	options.add("" + m_BitRate);
	// always overwrite output files
	options.add("-y");
	// input options
	cmd = assembleInputOptions();
	if (cmd.length() > 0)
	  options.addAll(Arrays.asList(OptionUtils.splitOptions(cmd)));
	options.add("-i");
	options.add(m_Input.getAbsolutePath());
	// output options
	cmd = assembleOutputOptions();
	if (cmd.length() > 0)
	  options.addAll(Arrays.asList(OptionUtils.splitOptions(cmd)));
	options.add(getOwner().getOutputFile().getAbsolutePath());
	// execute command
	if (isLoggingEnabled())
	  getLogger().info("Command-line: " + Utils.flatten(options, " "));
	proc = ProcessUtils.execute(options.toArray(new String[options.size()]));
	if (!proc.hasSucceeded())
	  result = proc.toErrorOutput();
      }
      catch (Exception e) {
	result = handleException("Failed to execute commandline:\n" + Utils.flatten(options, "\n"), e);
      }
    }
    
    m_Input = null;
    
    return result;
  }
}
