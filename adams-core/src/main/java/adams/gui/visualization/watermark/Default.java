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
 * Default.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.watermark;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.env.Environment;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.logging.Level;

/**
 * Applies the watermark defined in the global preferences.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Default
  extends AbstractWatermark {

  private static final long serialVersionUID = -3228039317393257050L;

  /** the file to store the default watermark setup in. */
  public final static String FILENAME = "DefaultWatermark.props";

  /** the default watermark. */
  protected transient Watermark m_Default;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the watermark defined in the global preferences.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    m_Default = null;
  }

  /**
   * Applies the watermark in the specified graphics context.
   *
   * @param g         the graphics context
   * @param dimension the dimension of the drawing area
   */
  @Override
  protected void doApplyWatermark(Graphics g, Dimension dimension) {
    Properties	props;

    if (m_Default == null) {
      props     = getProperties();
      m_Default = new Null();
      try {
	m_Default = props.getObject("Default", Watermark.class, new Null());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to load default watermark setup!", e);
      }
    }

    m_Default.applyWatermark(g, dimension);
  }

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  public static Properties getProperties() {
    Properties 	result;
    String	propsFile;

    result    = new Properties();
    propsFile = Environment.getInstance().createPropertiesFilename(FILENAME);
    if (FileUtils.fileExists(propsFile))
      result.load(propsFile);
    if (!result.hasKey("Default"))
      result.setObject("Default", new Null());

    return result;
  }
}
