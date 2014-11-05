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
 * AdaptiveBlur.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick.im;

import org.im4java.core.IMOperation;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Adaptive blur (option -adaptive-blur)
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-radius &lt;double&gt; (property: radius)
 * &nbsp;&nbsp;&nbsp;The radius of the Gaussian in pixels.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-sigma &lt;double&gt; (property: sigma)
 * &nbsp;&nbsp;&nbsp;The sigma of the Gaussian in pixels.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdaptiveBlur
  extends AbstractIMSimpleOperation {

  /** for serialization. */
  private static final long serialVersionUID = 3529048936510645338L;

  /** the radius of the gaussian (in pixels). */
  protected double m_Radius;

  /** the sigma (in pixels). */
  protected double m_Sigma;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adaptive blur (option -adaptive-blur)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "radius", "radius",
	    1.0, 0.0, null);

    m_OptionManager.add(
	    "sigma", "sigma",
	    0.0, 0.0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "radius", m_Radius, "radius: ");
    result += QuickInfoHelper.toString(this, "sigma", m_Sigma, ", sigma: ");
    
    return result;
  }

  /**
   * Sets the radius in pixels.
   *
   * @param value	the radius
   */
  public void setRadius(double value) {
    if (value > 0) {
      m_Radius = value;
      reset();
    }
    else {
      getLogger().warning("Radius must be > 0, provided: " + value);
    }
  }

  /**
   * Returns the radius in pixels.
   *
   * @return		the radius
   */
  public double getRadius() {
    return m_Radius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String radiusTipText() {
    return "The radius of the Gaussian in pixels.";
  }

  /**
   * Sets the sigma in pixels.
   *
   * @param value	the sigma
   */
  public void setSigma(double value) {
    if (value >= 0) {
      m_Sigma = value;
      reset();
    }
    else {
      getLogger().warning("Sigma must be >= 0, provided: " + value);
    }
  }

  /**
   * Returns the sigma in pixels.
   *
   * @return		the sigma
   */
  public double getSigma() {
    return m_Sigma;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sigmaTipText() {
    return "The sigma of the Gaussian in pixels.";
  }

  /**
   * Hook method for performing checks before applying the operation.
   * 
   * @param input	the input file
   * @param output	the output file
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(PlaceholderFile input, PlaceholderFile output) {
    if ((m_Sigma > 0.0) && (m_Radius == 0.0))
      return "A radius must be supplied if sigma is >0.";
    else
      return super.check(input, output);
  }
  
  /**
   * Adds the operation.
   * 
   * @param op		the operation object to update
   */
  @Override
  public void addOperation(IMOperation op) {
    if (m_Sigma == 0.0)
      op.adaptiveBlur(m_Radius);
    else
      op.adaptiveBlur(m_Radius, m_Sigma);
  }
}
