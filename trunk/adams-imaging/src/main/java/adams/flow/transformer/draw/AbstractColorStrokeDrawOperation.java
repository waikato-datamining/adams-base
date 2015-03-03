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
 * AbstractColorStrokeDrawOperation.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;


/**
 * Ancestor for operations that use a colored, variable stroke.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColorStrokeDrawOperation
  extends AbstractColorDrawOperation 
  implements AntiAliasingDrawOperation {
  
  /** for serialization. */
  private static final long serialVersionUID = -1600303277007377749L;
  
  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "stroke-thickness", "strokeThickness",
	    1.0f, 0.01f, null);

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    true);
  }

  /**
   * Sets the stroke thickness to use.
   *
   * @param value	the thickness
   */
  public void setStrokeThickness(float value) {
    m_StrokeThickness = value;
    reset();
  }

  /**
   * Returns the current stroke thickness.
   *
   * @return		the thickness
   */
  public float getStrokeThickness() {
    return m_StrokeThickness;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String strokeThicknessTipText() {
    return "The thickness of the stroke.";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    reset();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing.";
  }
}
