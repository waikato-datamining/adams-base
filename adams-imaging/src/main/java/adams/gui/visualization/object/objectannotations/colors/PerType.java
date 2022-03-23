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
 * PerType.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.colors;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.util.HashMap;

/**
 * Uses a color per object type.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PerType
    extends AbstractAnnotationColors {

  private static final long serialVersionUID = -1331416212214116733L;

  /** the key in the meta-data that contains the type. */
  protected String m_MetaDataKey;

  /** the regular expression for the types to draw. */
  protected BaseRegExp m_TypeRegExp;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the color to use if no type present. */
  protected Color m_NoTypeColor;

  /** predefined types. */
  protected BaseString[] m_PredefinedTypes;

  /** the type/color mapping. */
  protected transient HashMap<String,Color> m_TypeColors;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a color per object type.";
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
	"type-regexp", "typeRegExp",
	new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	"color-provider", "colorProvider",
	new DefaultColorProvider());

    m_OptionManager.add(
	"no-type-color", "noTypeColor",
	Color.YELLOW);

    m_OptionManager.add(
	"predefined-types", "predefinedTypes",
	new BaseString[0]);
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
   * Sets the regular expression that the types must match in order to get
   * drawn.
   *
   * @param value 	the expression
   */
  public void setTypeRegExp(BaseRegExp value) {
    m_TypeRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the types must match in order to get
   * drawn.
   *
   * @return 		the expression
   */
  public BaseRegExp getTypeRegExp() {
    return m_TypeRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeRegExpTipText() {
    return "The regular expression that the types must match in order to get drawn (eg only plotting a subset).";
  }

  /**
   * Sets the color provider to use for the types.
   *
   * @param value 	the provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for the types.
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
    return "The color provider to use for the various types.";
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
   * Sets the predefined types.
   *
   * @param value	the types
   */
  public void setPredefinedTypes(BaseString[] value) {
    m_PredefinedTypes = value;
    reset();
  }

  /**
   * Returns the predefined types.
   *
   * @return		the types
   */
  public BaseString[] getPredefinedTypes() {
    return m_PredefinedTypes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predefinedTypesTipText() {
    return "The predefined types to use for setting up the colors; avoids constants changing in color pallet.";
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
    m_ColorProvider.resetColors();
    m_TypeColors = new HashMap<>();

    for (BaseString type : m_PredefinedTypes)
      m_TypeColors.put(type.getValue(), m_ColorProvider.next());
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
      if (!m_TypeColors.containsKey(type))
        m_TypeColors.put(type, m_ColorProvider.next());
      result = m_TypeColors.get(type);
    }

    return result;
  }
}
