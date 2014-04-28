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
 * AbstractFFmpegPluginWithOptions.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.ffmpeg;

import java.util.ArrayList;
import java.util.Arrays;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.sink.FFmpeg;

/**
 * Ancestor for {@link FFmpeg} plugins that offers the user to specify
 * additional input/output options.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFFmpegPluginWithOptions
  extends AbstractFFmpegPlugin {

  /** for serialization. */
  private static final long serialVersionUID = -2555683041357914117L;
  
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
	    "additional-input-options", "additionalInputOptions",
	    "");

    m_OptionManager.add(
	    "additional-output-options", "additionalOutputOptions",
	    "");
  }

  /**
   * Returns a quick info about the plugin, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "additionalInputOptions", (m_AdditionalInputOptions.length() > 0 ? m_AdditionalInputOptions : ""));
    result += QuickInfoHelper.toString(this, "additionalOutputOptions", (m_AdditionalOutputOptions.length() > 0 ? m_AdditionalOutputOptions : ""));
    
    return result;
  }
  /**
   * Sets the additional input options to use.
   *
   * @param value	the options
   */
  public void setAdditionalInputOptions(String value) {
    m_AdditionalInputOptions = value;
    reset();
  }

  /**
   * Returns the additional input options to use.
   *
   * @return		the options
   */
  public String getAdditionalInputOptions() {
    return m_AdditionalInputOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalInputOptionsTipText() {
    return "The additional output options for ffmpeg to use.";
  }

  /**
   * Sets the additional output options to use.
   *
   * @param value	the options
   */
  public void setAdditionalOutputOptions(String value) {
    m_AdditionalOutputOptions = value;
    reset();
  }

  /**
   * Returns the additional output options to use.
   *
   * @return		the options
   */
  public String getAdditionalOutputOptions() {
    return m_AdditionalOutputOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalOutputOptionsTipText() {
    return "The additional output options for ffmpeg to use.";
  }
  
  /**
   * Assembles the actual input command-line options, not including the
   * additional options or the input file.
   * 
   * @return		the command-line
   */
  protected abstract String assembleActualInputOptions();
  
  /**
   * Assembles the input command-line options, actual and additional.
   * 
   * @return		the command-line
   */
  @Override
  protected String assembleInputOptions() {
    ArrayList<String>	result;
    
    result = new ArrayList<String>();
    try {
      result.addAll(Arrays.asList(OptionUtils.splitOptions(getAdditionalInputOptions())));
      result.addAll(Arrays.asList(OptionUtils.splitOptions(assembleActualInputOptions())));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to parse additional input options: " + getAdditionalInputOptions(), e);
    }
    
    return OptionUtils.joinOptions(result.toArray(new String[result.size()]));
  }
  
  /**
   * Assembles the actual output command-line options, not including the
   * additional options.
   * 
   * @return		the command-line
   */
  protected abstract String assembleActualOutputOptions();
  
  /**
   * Assembles the ouput command-line options.
   * 
   * @return		the command-line
   */
  @Override
  protected String assembleOutputOptions() {
    ArrayList<String>	result;
    
    result = new ArrayList<String>();
    try {
      result.addAll(Arrays.asList(OptionUtils.splitOptions(getAdditionalOutputOptions())));
      result.addAll(Arrays.asList(OptionUtils.splitOptions(assembleActualOutputOptions())));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to parse additional output options: " + getAdditionalOutputOptions(), e);
    }
    
    return OptionUtils.joinOptions(result.toArray(new String[result.size()]));
  }
}
