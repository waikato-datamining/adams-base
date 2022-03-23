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
 * AbstractAnnotationColors.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.colors;

import adams.core.MessageCollection;
import adams.core.option.AbstractOptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;

/**
 * Ancestor for annotation colorizers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAnnotationColors
    extends AbstractOptionHandler
    implements AnnotationColors {

  private static final long serialVersionUID = 2859885916282772394L;

  /** whether the colorization is enabled. */
  protected boolean m_Enabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"enabled", "enabled",
	true);
  }

  /**
   * Sets whether the data cleaner is enabled.
   *
   * @param value 	true if enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the data cleaner is enabled.
   *
   * @return 		true if enabled
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "Whether the colorizer is enabled.";
  }
  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  protected String generateQuickInfo() {
    return null;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (getEnabled() || getOptionManager().hasVariableForProperty("enabled"))
      return generateQuickInfo();
    else
      return null;
  }

  /**
   * Hook method for checks.
   *
   * @param objects	the annotations to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(LocatedObjects objects) {
    if (objects == null)
      return "No annotations provided!";
    return null;
  }

  /**
   * Initializes the colors with the annotations.
   *
   * @param objects	the annotations to use for initialization
   * @param errors	for collecting errors
   */
  protected abstract void doInitColors(LocatedObjects objects, MessageCollection errors);

  /**
   * Initializes the colors with the annotations.
   *
   * @param objects	the annotations to use for initialization
   * @param errors	for collecting errors
   */
  public void initColors(LocatedObjects objects, MessageCollection errors) {
    String 	msg;

    if (!getEnabled())
      return;

    msg = check(objects);
    if (msg != null) {
      errors.add(msg);
      return;
    }

    doInitColors(objects, errors);
  }

  /**
   * Returns the color for the object.
   *
   * @param object	the annotation to get the color for
   */
  protected abstract Color doGetColor(LocatedObject object);

  /**
   * Returns the color for the object.
   *
   * @param object	the annotation to get the color for
   */
  public Color getColor(LocatedObject object) {
    if (getEnabled())
      return doGetColor(object);
    else
      return null;
  }
}
