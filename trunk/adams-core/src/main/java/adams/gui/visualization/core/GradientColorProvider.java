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

/**
 * GradientColorProvider.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Uses a color gradient generator to generate the colors.
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
 * <pre>-generator &lt;adams.gui.visualization.core.AbstractColorGradientGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for generating the gradient.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.BiColorGenerator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GradientColorProvider
  extends AbstractColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6416066461328053283L;
  
  /** the gradient generator to use. */
  protected AbstractColorGradientGenerator m_Generator;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a color gradient generator to generate the colors.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_AllowDarkening = false;
    m_CheckTooDark   = false;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"generator", "generator",
	new BiColorGenerator());
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator to use
   */
  public void setGenerator(AbstractColorGradientGenerator value) {
    m_Generator = value;
    reset();
    resetColors();
  }

  /**
   * Returns the generator in use.
   *
   * @return		the generator in use
   */
  public AbstractColorGradientGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for generating the gradient.";
  }
  
  /**
   * Resets the colors.
   */
  @Override
  public synchronized void resetColors() {
    m_DefaultColors.clear();
    m_DefaultColors.addAll(Arrays.asList(m_Generator.generate()));
    
    super.resetColors();
  }
}
