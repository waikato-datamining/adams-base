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
 * TranslucentColorProvider.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;


/**
 <!-- globalinfo-start -->
 * Allows the user to add translucency to the colors generated by the base color provider.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-alpha &lt;int&gt; (property: alpha)
 * &nbsp;&nbsp;&nbsp;The alpha value to use (0=translucent, 255=opaque).
 * &nbsp;&nbsp;&nbsp;default: 255
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 * 
 * <pre>-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: provider)
 * &nbsp;&nbsp;&nbsp;The base color provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class TranslucentColorProvider
  extends AbstractColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /** the alpha parameter. */
  protected int m_Alpha;
  
  /** the base color provider. */
  protected ColorProvider m_Provider;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to add translucency to the colors generated by the base color provider.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"alpha", "alpha",
	255, 0, 255);

    m_OptionManager.add(
	"provider", "provider",
	new DefaultColorProvider());
  }

  /**
   * Sets the colors to use.
   *
   * @param value	the colors to use
   */
  public void setAlpha(int value) {
    if ((value >= 0) && (value <= 255)) {
      m_Alpha = value;
      reset();
      resetColors();
    }
    else {
      getLogger().warning("Alpha must satisfy 0 <= x <= 255, provided: " + value);
    }
  }

  /**
   * Returns the colors in use.
   *
   * @return		the colors in use
   */
  public int getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alphaTipText() {
    return "The alpha value to use (0=translucent, 255=opaque).";
  }

  /**
   * Sets the base color provider to use.
   *
   * @param value	the colors to use
   */
  public void setProvider(ColorProvider value) {
    m_Provider = value;
    reset();
    resetColors();
  }

  /**
   * Returns the base color provider.
   *
   * @return		the provider to use
   */
  public ColorProvider getProvider() {
    return m_Provider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String providerTipText() {
    return "The base color provider to use.";
  }
  
  /**
   * Resets the colors.
   */
  @Override
  public synchronized void resetColors() {
    if (m_Provider != null)
      m_Provider.resetColors();
  }
  
  /**
   * "Recycles" the specified colors, i.e., makes it available for future use.
   *
   * @param c		the color to re-use
   */
  @Override
  public synchronized void recycle(Color c) {
    if (m_Provider != null)
      m_Provider.recycle(c);
  }

  /**
   * "Excludes" the specified colors, i.e., makes it unavailable for future use.
   *
   * @param c		the color to exclude
   */
  @Override
  public synchronized void exclude(Color c) {
    if (m_Provider != null)
      m_Provider.exclude(c);
  }
  
  /**
   * Returns the next color.
   *
   * @return		the next color
   */
  @Override
  public synchronized Color next() {
    Color	result;

    if (m_Provider == null)
      return Color.BLACK;
    
    result = m_Provider.next();
    if (m_Alpha != 255)
      result = new Color(result.getRed(), result.getGreen(), result.getBlue(), m_Alpha);
    
    return result;
  }
}
