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
 * Macro.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagej.transformer;

import adams.core.Shortening;
import ij.IJ;
import ij.ImagePlus;

import java.util.Date;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.data.imagej.ImagePlusContainer;

/**
 <!-- globalinfo-start -->
 * Executes the macro commands to transform the image.<br>
 * Due to ImageJ's architecture, only a single ImageJ instance is available in a process at any given time. Interacting with ImageJ while running a macro will lead to unpredictable side-effects.
 * <br><br>
 <!-- globalinfo-end -->
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
 * <pre>-commands &lt;adams.core.base.BaseText&gt; (property: commands)
 * &nbsp;&nbsp;&nbsp;The macro commands to transform the image width.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Macro
  extends AbstractImageJTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3739155764347863440L;
  
  /** the macro commands. */
  protected BaseText m_Commands;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Executes the macro commands to transform the image.\n"
	+ "Due to ImageJ's architecture, only a single ImageJ instance is "
	+ "available in a process at any given time. Interacting with ImageJ "
	+ "while running a macro will lead to unpredictable side-effects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "commands", "commands",
	    new BaseText());
  }

  /**
   * Sets the width to resize to.
   *
   * @param value 	the width, -1 uses original width
   */
  public void setCommands(BaseText value) {
    m_Commands = value;
    reset();
  }

  /**
   * Returns the width to resize to.
   *
   * @return 		the width, -1 if original width is used
   */
  public BaseText getCommands() {
    return m_Commands;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commandsTipText() {
    return "The macro commands to transform the image width.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "commands", Shortening.shortenEnd(m_Commands.stringValue(), 40));
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected ImagePlusContainer[] doTransform(ImagePlusContainer img) {
    ImagePlusContainer[]	result;
    ImagePlus			im;
    ImagePlus			imNew;

    // nothing to do?
    if (m_Commands.getValue().trim().length() == 0)
      return new ImagePlusContainer[]{img};
    
    im = img.getImage();
    IJ.newImage(hashCode() + " - " + new Date(), "RGB", im.getWidth(), im.getHeight(), 1);
    imNew = IJ.getImage();
    imNew.setImage(im.getImage());
    
    IJ.runMacro(m_Commands.getValue());

    result    = new ImagePlusContainer[1];
    result[0] = (ImagePlusContainer) img.getHeader();
    result[0].setImage(IJ.getImage());
    
    // close the images that we know of
    if (IJ.getImage() != imNew)
      imNew.close();
    IJ.getImage().close();
    
    return result;
  }
}
