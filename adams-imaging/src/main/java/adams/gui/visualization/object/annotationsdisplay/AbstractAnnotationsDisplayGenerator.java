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
 * AbstractAnnotationsDisplayGenerator.java
 * Copyright (C) 2020-2026 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotationsdisplay;

import adams.core.option.AbstractOptionHandler;
import adams.data.objectfilter.ObjectFilter;
import adams.data.objectfilter.PassThrough;
import adams.gui.visualization.image.ReportObjectOverlay;

/**
 * Ancestor for classes that create AbstractAnnotationsPanel implementations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAnnotationsDisplayGenerator
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 4349554101379518737L;

  /** the default prefix. */
  public final static String PREFIX_DEFAULT = ReportObjectOverlay.PREFIX_DEFAULT;

  /** the prefix to use. */
  protected String m_Prefix;

  /** the object filter to apply to the report/objects. */
  protected ObjectFilter m_ObjectFilter;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      PREFIX_DEFAULT);

    m_OptionManager.add(
      "object-filter", "objectFilter",
      new PassThrough());
  }

  /**
   * Sets the prefix to use for the objects in the report.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects in the report.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix of fields in the report to identify as object location, eg 'Object.'.";
  }

  /**
   * Sets the object filter to use for filtering the objects in the report.
   *
   * @param value 	the filter
   */
  public void setObjectFilter(ObjectFilter value) {
    m_ObjectFilter = value;
    reset();
  }

  /**
   * Returns the object filter to use for filtering the objects in the report.
   *
   * @return 		the filter
   */
  public ObjectFilter getObjectFilter() {
    return m_ObjectFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectFilterTipText() {
    return "The optional object filter to apply to the report/objects before displaying them.";
  }

  /**
   * Generates the panel.
   *
   * @return		the panel
   */
  public abstract AbstractAnnotationsDisplayPanel generate();
}
