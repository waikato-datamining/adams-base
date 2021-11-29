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
 * AbstractRangeBasedSelectionProcessor.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor.processor;

import adams.data.weka.WekaAttributeRange;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Ancestor for processors that work on a range of attributes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRangeBasedSelectionProcessor
  extends AbstractSelectionProcessor {

  private static final long serialVersionUID = -598983861360058698L;

  protected static String RANGE = "range";

  public static final WekaAttributeRange DEFAULT_RANGE = new WekaAttributeRange(WekaAttributeRange.ALL);

  /** the range of attributes to work on. */
  protected WekaAttributeRange m_Range;

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, rangeTipText(), DEFAULT_RANGE, RANGE);
    WekaOptionUtils.add(result, super.listOptions());

    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setRange((WekaAttributeRange) WekaOptionUtils.parse(options, RANGE, DEFAULT_RANGE));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, RANGE, getRange());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Sets the attribute range to work on.
   *
   * @param value	the range
   */
  public void setRange(WekaAttributeRange value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the attribute range to work on.
   *
   * @return		the range
   */
  public WekaAttributeRange getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The range attributes to work on; " + m_Range.getExample();
  }
}
