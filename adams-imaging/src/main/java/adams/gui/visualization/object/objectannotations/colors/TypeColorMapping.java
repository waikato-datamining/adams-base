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
 * TypeColorMapping.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.colors;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseColor;
import adams.core.base.BaseString;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;
import java.util.HashMap;

/**
 * Uses the colors defined for each type.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TypeColorMapping
    extends AbstractAnnotationColors {

  private static final long serialVersionUID = -1331416212214116733L;

  /** the key in the meta-data that contains the type. */
  protected String m_MetaDataKey;

  /** the types. */
  protected BaseString[] m_Types;

  /** the colors. */
  protected BaseColor[] m_Colors;

  /** the fallback color for labels that have no color defined. */
  protected Color m_FallbackColor;

  /** the color to use if no type present. */
  protected Color m_NoTypeColor;

  /** the type/color mapping. */
  protected transient HashMap<String,Color> m_Mapping;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the colors defined for each type.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"meta-data-key", "metaDataKey",
	"type");

    m_OptionManager.add(
	"type", "types",
	new BaseString[0]);

    m_OptionManager.add(
	"color", "colors",
	new BaseColor[0]);

    m_OptionManager.add(
	"fallback-color", "fallbackColor",
	Color.YELLOW);

    m_OptionManager.add(
	"no-type-color", "noTypeColor",
	Color.WHITE);
  }

  /**
   * Sets the key in the meta-data of the object that contains the type.
   *
   * @param value 	the key
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    reset();
  }

  /**
   * Returns the key in the meta-data of the object that contains the type.
   *
   * @return 		the key
   */
  public String getMetaDataKey() {
    return m_MetaDataKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeyTipText() {
    return "The key in the meta-data of the object that contains the type.";
  }

  /**
   * Sets the types/labels.
   *
   * @param value	the types
   */
  public void setTypes(BaseString[] value) {
    m_Types  = value;
    m_Colors = (BaseColor[]) Utils.adjustArray(m_Colors, m_Types.length, new BaseColor());
    reset();
  }

  /**
   * Returns the types/labels.
   *
   * @return		the types
   */
  public BaseString[] getTypes() {
    return m_Types;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typesTipText() {
    return "The types/labels with predefined colors.";
  }

  /**
   * Sets the colors associated with the types/labels.
   *
   * @param value	the colors
   */
  public void setColors(BaseColor[] value) {
    m_Colors = value;
    m_Types  = (BaseString[]) Utils.adjustArray(m_Types, m_Colors.length, new BaseString());
    reset();
  }

  /**
   * Returns the colors associated with the types/labels.
   *
   * @return		the colors
   */
  public BaseColor[] getColors() {
    return m_Colors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorsTipText() {
    return "The colors associated with the types/labels.";
  }

  /**
   * Sets the color to use when a label has no color associated.
   *
   * @param value 	the color
   */
  public void setFallbackColor(Color value) {
    m_FallbackColor = value;
    reset();
  }

  /**
   * Returns the color to use when a label has no color associated.
   *
   * @return 		the color
   */
  public Color getFallbackColor() {
    return m_FallbackColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fallbackColorTipText() {
    return "The color to use when a label has no color defined.";
  }

  /**
   * Sets the color to use when no type information available.
   *
   * @param value 	the color
   */
  public void setNoTypeColor(Color value) {
    m_NoTypeColor = value;
    reset();
  }

  /**
   * Returns the color to use when no type information available.
   *
   * @return 		the color
   */
  public Color getNoTypeColor() {
    return m_NoTypeColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noTypeColorTipText() {
    return "The color to use when no type information available.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "metaDataKey", m_MetaDataKey, "key: ");

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
    int		i;

    m_Mapping = new HashMap<>();

    for (i = 0; i < m_Types.length; i++)
      m_Mapping.put(m_Types[i].getValue(), m_Colors[i].toColorValue());
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

    result = m_NoTypeColor;

    if (object.getMetaData().containsKey(m_MetaDataKey)) {
      type = "" + object.getMetaData().get(m_MetaDataKey);
      if (!m_Mapping.containsKey(type))
	m_Mapping.put(type, m_FallbackColor);
      result = m_Mapping.get(type);
    }

    return result;
  }
}
