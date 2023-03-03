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
 * AbstractReportBasedAnnotator.java
 * Copyright (C) 2021-2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.data.report.AnnotationHelper;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;

import java.util.Map;

/**
 * Ancestor for annotators that use reports to store the annotations in.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractReportBasedAnnotator
  extends AbstractAnnotator
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = 3915632192147746710L;

  /** the prefix for the objects. */
  protected String m_Prefix;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      getDefaultPrefix());
  }

  /**
   * Returns the default prefix to use for the objects.
   *
   * @return		the default
   */
  protected abstract String getDefaultPrefix();

  /**
   * Sets the prefix to use for the objects.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the objects.
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
    return "The prefix to use for the fields in the report.";
  }

  /**
   * Returns all the values stored in the report under this index.
   *
   * @param report	the report to look up the index in
   * @param index	the index to retrieve the values for
   * @return		the values
   */
  protected Map<String,Object> valuesForIndex(Report report, int index) {
    return AnnotationHelper.valuesForIndex(report, m_Prefix, index);
  }

  /**
   * Removes the specified index from the report.
   *
   * @return		true if successfully removed
   */
  protected boolean removeIndex(Report report, int index) {
    return AnnotationHelper.removeIndex(report, m_Prefix, index);
  }

  /**
   * Determines the last index used with the given prefix.
   */
  protected int findLastIndex(Report report) {
    return AnnotationHelper.findLastIndex(report, m_Prefix);
  }
}
