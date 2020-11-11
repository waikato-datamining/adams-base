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
 * ColorPerLabel.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence.metadatacolor;

import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.ColorProviderWithNameSupport;
import adams.gui.visualization.core.DefaultColorProvider;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Maintains an internal mapping between labels obtained from the meta-data (using the specified key) and colors obtained from the color provider.<br>
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ColorPerLabel
  extends AbstractMetaDataColor<XYSequencePoint> {

  private static final long serialVersionUID = -7097430935066387456L;

  /** the color provider to use. */
  protected ColorProvider m_ColorProvider;

  /** the meta-data key to use for obtaining the label. */
  protected String m_MetaDataKey;

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
      "Maintains an internal mapping between labels obtained from the meta-data "
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
   * Returns the next color for the label.
   *
   * @param label	the label to get the next color for
   * @return		the color
   */
  protected Color getNextColor(String label) {
    if (m_ColorProvider instanceof ColorProviderWithNameSupport)
      return ((ColorProviderWithNameSupport) m_ColorProvider).next(label);
    else
      return m_ColorProvider.next();
  }

  /**
   * Determines the label for the point.
   *
   * @param point	the point to get the label for
   * @return		the label, null if not available
   */
  protected String determineLabel(XYSequencePoint point) {
    if (point.getMetaData().containsKey(m_MetaDataKey))
      return point.getMetaData().get(m_MetaDataKey).toString();
    else
      return null;
  }

  /**
   * Initializes the meta-data color scheme.
   *
   * @param points	the points to initialize with
   */
  @Override
  public void initialize(List<XYSequencePoint> points) {
    Set<String> 	unique;
    List<String>	labels;
    String		label;

    m_ColorMap.clear();
    m_ColorProvider.resetColors();

    unique = new HashSet<>();
    for (XYSequencePoint point: points) {
      label = determineLabel(point);
      if (label != null)
	unique.add(label);
    }

    labels = new ArrayList<>(unique);
    Collections.sort(labels);
    for (String l : labels)
      m_ColorMap.put(l, getNextColor(l));
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

    if (m_MetaDataKey.isEmpty())
      return defColor;

    if (!point.hasMetaData())
      return defColor;

    if (!point.getMetaData().containsKey(m_MetaDataKey))
      return defColor;

    label = determineLabel(point);
    if (!m_ColorMap.containsKey(label))
      m_ColorMap.put(label, getNextColor(label));

    return m_ColorMap.get(label);
  }
}
