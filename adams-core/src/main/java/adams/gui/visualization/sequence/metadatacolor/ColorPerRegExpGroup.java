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
 * ColorPerRegExpGroup.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence.metadatacolor;

import adams.core.base.BaseRegExp;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.ColorProviderWithNameSupport;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Maintains an internal mapping between groups obtained from the meta-data via the regular expression extraction (using the specified key) and colors obtained from the color provider.<br>
 * Makes use of color providers that implement the adams.gui.visualization.core.ColorProviderWithNameSupport interface, supplying it with the label obtained from the meta-data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: colorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-meta-data-key &lt;java.lang.String&gt; (property: metaDataKey)
 * &nbsp;&nbsp;&nbsp;The key in the meta-data to obtain the label from.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to apply to the meta-data value.
 * &nbsp;&nbsp;&nbsp;default: (.*)
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-group &lt;java.lang.String&gt; (property: group)
 * &nbsp;&nbsp;&nbsp;The group from the regular expression to use for determining the color.
 * &nbsp;&nbsp;&nbsp;default: $1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ColorPerRegExpGroup
  extends AbstractMetaDataColor<XYSequencePoint> {

  private static final long serialVersionUID = -7097430935066387456L;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the meta-data key to use for obtaining the label. */
  protected String m_MetaDataKey;

  /** the regular expression to use. */
  protected BaseRegExp m_RegExp;

  /** the group to use for the coloring. */
  protected String m_Group;

  /** the mapping between labels and colors. */
  protected Map<String,Color> m_ColorMap;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Maintains an internal mapping between groups obtained from the meta-data via the regular expression extraction "
	+ "(using the specified key) and colors obtained from the color provider.\n"
	+ "Makes use of color providers that implement the " + ColorProviderWithNameSupport.class.getName() + " "
	+ "interface, supplying it with the label obtained from the meta-data.";
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

    m_OptionManager.add(
      "meta-data-key", "metaDataKey",
      "");

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp("(.*)"));

    m_OptionManager.add(
      "group", "group",
      "$1");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ColorMap = new HashMap<>();
  }

  /**
   * Resets the mapping.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ColorMap.clear();
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the provider
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
    return "The color provider to use.";
  }

  /**
   * Sets the key in the meta-data to obtain the label from.
   *
   * @param value	the key
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    reset();
  }

  /**
   * Returns the key in the meta-data to obtain the label from.
   *
   * @return		the key
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
    return "The key in the meta-data to obtain the label from.";
  }

  /**
   * Sets the regular expression to apply to the meta-data value.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the meta-data value.
   *
   * @return		the regular expressions
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to apply to the meta-data value.";
  }

  /**
   * Sets the group from the regular expression to use for determining the color.
   *
   * @param value	the group
   */
  public void setGroup(String value) {
    m_Group = value;
    reset();
  }

  /**
   * Returns the group from the regular expression to use for determining the color.
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupTipText() {
    return "The group from the regular expression to use for determining the color.";
  }

  /**
   * Extracts the color from the meta-data.
   *
   * @param point	the point to get the color from
   * @param defColor 	the default color to use
   * @return		the color
   */
  @Override
  public Color getColor(XYSequencePoint point, Color defColor) {
    String	label;
    String	group;

    if (m_MetaDataKey.isEmpty())
      return defColor;

    if (!point.hasMetaData())
      return defColor;

    if (!point.getMetaData().containsKey(m_MetaDataKey))
      return defColor;

    label = point.getMetaData().get(m_MetaDataKey).toString();
    group = label.replaceAll(m_RegExp.getValue(), m_Group);
    if (isLoggingEnabled())
      getLogger().info(label + " -> " + group);
    if (!m_ColorMap.containsKey(group)) {
      if (m_ColorProvider instanceof ColorProviderWithNameSupport)
	m_ColorMap.put(group, ((ColorProviderWithNameSupport) m_ColorProvider).next(group));
      else
	m_ColorMap.put(group, m_ColorProvider.next());
    }

    return m_ColorMap.get(group);
  }
}
