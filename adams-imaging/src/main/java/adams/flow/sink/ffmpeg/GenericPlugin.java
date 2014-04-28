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
 * GenericPlugin.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.ffmpeg;

/**
 * A generic plugin.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenericPlugin
  extends AbstractFFmpegPlugin {
  
  /** for serialization. */
  private static final long serialVersionUID = 5346192832432715551L;
  
  /** input options to use for ffmpeg. */
  protected String m_InputOptions;
  
  /** output options to use for ffmpeg. */
  protected String m_OutputOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generic plugin simply returns the provided input and output options.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "input-options", "inputOptions",
	    "");

    m_OptionManager.add(
	    "output-options", "outputOptions",
	    "");
  }

  /**
   * Sets the input options to use.
   *
   * @param value	the options
   */
  public void setInputOptions(String value) {
    m_InputOptions = value;
    reset();
  }

  /**
   * Returns the input options to use.
   *
   * @return		the options
   */
  public String getInputOptions() {
    return m_InputOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputOptionsTipText() {
    return "The output options for ffmpeg to use.";
  }

  /**
   * Sets the output options to use.
   *
   * @param value	the options
   */
  public void setOutputOptions(String value) {
    m_OutputOptions = value;
    reset();
  }

  /**
   * Returns the  output options to use.
   *
   * @return		the options
   */
  public String getOutputOptions() {
    return m_OutputOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputOptionsTipText() {
    return "The output options for ffmpeg to use.";
  }
  
  /**
   * Assembles the input command-line options.
   * 
   * @return		the command-line
   */
  @Override
  protected String assembleInputOptions() {
    return getInputOptions();
  }
  
  /**
   * Assembles the output command-line options.
   * 
   * @return		the command-line
   */
  @Override
  protected String assembleOutputOptions() {
    return getOutputOptions();
  }
}
