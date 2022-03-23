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
 * FixedColor.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.colors;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;

/**
 * Uses a single, fixed color.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FixedColor
  extends AbstractAnnotationColors {

  private static final long serialVersionUID = -1331416212214116733L;

  /** the fixed color to use. */
  protected Color m_Color;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a single, fixed color.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"color", "color",
	Color.RED);
  }

  /**
   * Sets the color to use.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    return QuickInfoHelper.toString(this, "color", m_Color, "color: ");
  }

  /**
   * Initializes the colors with the annotations.
   *
   * @param objects the annotations to use for initialization
   * @param errors  for collecting errors
   */
  @Override
  protected void doInitColors(LocatedObjects objects, MessageCollection errors) {
  }

  /**
   * Returns the color for the object.
   *
   * @param object the annotation to get the color for
   */
  @Override
  protected Color doGetColor(LocatedObject object) {
    return m_Color;
  }
}
