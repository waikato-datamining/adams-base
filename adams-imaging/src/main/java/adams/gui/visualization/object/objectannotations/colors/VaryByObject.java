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
 * VaryByObject.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.colors;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.ColorProviderHandler;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.util.HashMap;

/**
 * Varies the color per object, useful when just a single type of object is annotated.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class VaryByObject
  extends AbstractAnnotationColors
  implements ColorProviderHandler {

  private static final long serialVersionUID = -1331416212214116733L;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the object/color mapping. */
  protected transient HashMap<LocatedObject,Color> m_ObjectColors;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Varies the color per object, useful when just a single type of object is annotated.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"color-provider", "colorProvider",
	new DefaultColorProvider());
  }

  /**
   * Sets the color provider to use for the objects.
   *
   * @param value 	the provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for the objects.
   *
   * @return 		the provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider to use for the objects.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "colorProvider", m_ColorProvider, "colors: ");

    return result;
  }

  /**
   * Initializes the colors with the annotations.
   *
   * @param objects the annotations to use for initialization
   * @param errors  for collecting errors
   */
  @Override
  protected void doInitColors(LocatedObjects objects, MessageCollection errors) {
    m_ColorProvider.resetColors();
    m_ObjectColors = new HashMap<>();

    for (LocatedObject object: objects)
      m_ObjectColors.put(object, m_ColorProvider.next());
  }

  /**
   * Returns the color for the object.
   *
   * @param object the annotation to get the color for
   */
  @Override
  protected Color doGetColor(LocatedObject object) {
    Color	result;
    String 	type;

    if (!m_ObjectColors.containsKey(object))
      m_ObjectColors.put(object, m_ColorProvider.next());

    result = m_ObjectColors.get(object);

    return result;
  }
}
