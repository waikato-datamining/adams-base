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
 * Command.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagej.transformer;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import adams.data.imagej.ImagePlusContainer;

/**
 <!-- globalinfo-start -->
 * A transformer that allows the execution of ImageJ commands.<br/>
 * Note(s):<br/>
 * - the options for the command can be obtained when recording macros<br/>
 *   in ImageJ
 * <p/>
 <!-- globalinfo-end -->
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
 * <pre>-command &lt;java.lang.String&gt; (property: command)
 * &nbsp;&nbsp;&nbsp;The ImageJ command to execute.
 * &nbsp;&nbsp;&nbsp;default: Add Specified Noise...
 * </pre>
 *
 * <pre>-command-options &lt;java.lang.String&gt; (property: commandOptions)
 * &nbsp;&nbsp;&nbsp;The options for the command (not all commands take options).
 * &nbsp;&nbsp;&nbsp;default: standard=25
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Command
  extends AbstractImageJTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2700141722155551567L;

  /** the command to run. */
  protected String m_Command;

  /** the options for the command. */
  protected String m_CommandOptions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "A transformer that allows the execution of ImageJ commands.\n"
      + "Note(s):\n"
      + "- the options for the command can be obtained when recording macros\n"
      + "  in ImageJ";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "command", "command",
	    "Add Specified Noise...");

    m_OptionManager.add(
	    "command-options", "commandOptions",
	    "standard=25");
  }

  /**
   * Sets the command to execute.
   *
   * @param value 	the command
   */
  public void setCommand(String value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to execute.
   *
   * @return 		the command
   */
  public String getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandTipText() {
    return "The ImageJ command to execute.";
  }

  /**
   * Sets the options for the command.
   *
   * @param value 	the options
   */
  public void setCommandOptions(String value) {
    m_CommandOptions = value;
    reset();
  }

  /**
   * Returns the options for the command.
   *
   * @return 		the options
   */
  public String getCommandOptions() {
    return m_CommandOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandOptionsTipText() {
    return "The options for the command (not all commands take options).";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  protected ImagePlusContainer[] doTransform(ImagePlusContainer img) {
    ImagePlusContainer[]	result;
    ImagePlus			imp;

    imp = img.getImage();
    IJ.run(imp, m_Command, m_CommandOptions);
    result = new ImagePlusContainer[1];
    result[0] = (ImagePlusContainer) img.getHeader();
    result[0].setImage(imp);

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    WindowManager.closeAllWindows();

    super.cleanUp();
  }
}
