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

/**
 * WekaFilter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.spreadsheet.filter;

import adams.core.option.OptionUtils;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;

/**
 * Applies a Weka filter to the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaFilter
  extends AbstractTrainableSpreadSheetFilter {

  private static final long serialVersionUID = 7531941908565712012L;

  /** the filter to use. */
  protected Filter m_Filter;

  /** the actual filter in use. */
  protected Filter m_ActualFilter;

  /** the threshold for number of labels before an attribute gets switched
   * to {@link Attribute#STRING}. */
  protected int m_MaxLabels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified Weka filter to the spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new weka.filters.AllFilter());

    m_OptionManager.add(
      "max-labels", "maxLabels",
      25, -1, null);
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(Filter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public Filter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use for filtering the spreadsheet.";
  }

  /**
   * Sets the maximum number of labels a nominal attribute can have.
   *
   * @param value 	the maximum
   */
  public void setMaxLabels(int value) {
    if (getOptionManager().isValid("maxLabels", value)) {
      m_MaxLabels = value;
      reset();
    }
  }

  /**
   * Returns the name of the global actor in use.
   *
   * @return 		the global name
   */
  public int getMaxLabels() {
    return m_MaxLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxLabelsTipText() {
    return
	"The maximum number of labels that a NOMINAL attribute can have "
	+ "before it is switched to a STRING attribute; use -1 to enforce STRING attributes.";
  }

  /**
   * Converts the spreadsheet into an Instances object.
   *
   * @param data	the spreadsheet to convert
   * @return		the Instances
   */
  protected Instances toInstances(SpreadSheet data) {
    Instances			result;
    SpreadSheetToWekaInstances	conv;
    String			msg;

    conv = new SpreadSheetToWekaInstances();
    conv.setMaxLabels(m_MaxLabels);
    conv.setInput(data);
    msg = conv.convert();
    if (msg != null)
      throw new IllegalStateException(msg);
    result = (Instances) conv.getOutput();
    conv.cleanUp();

    return result;
  }

  /**
   * Converts the Instances into a SpreadSheet object.
   *
   * @param data	the Instances to convert
   * @return		the SpreadSheet
   */
  protected SpreadSheet toSpreadSheet(Instances data) {
    SpreadSheet			result;
    WekaInstancesToSpreadSheet	conv;
    String			msg;

    conv = new WekaInstancesToSpreadSheet();
    conv.setInput(data);
    msg = conv.convert();
    if (msg != null)
      throw new IllegalStateException(msg);
    result = (SpreadSheet) conv.getOutput();
    conv.cleanUp();

    return result;
  }

  /**
   * Performs the actual retraining on the spreadsheet.
   *
   * @param data	the spreadsheet to train with and filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  @Override
  protected SpreadSheet doTrain(SpreadSheet data) throws Exception {
    Instances		inst;
    Instances		filtered;

    m_ActualFilter = (Filter) OptionUtils.shallowCopy(m_Filter);
    inst           = toInstances(data);
    m_ActualFilter.setInputFormat(inst);
    filtered = Filter.useFilter(inst, m_ActualFilter);

    return toSpreadSheet(filtered);
  }

  /**
   * Performs the actual filtering of the spreadsheet.
   *
   * @param data	the spreadsheet to filter
   * @return		the filtered spreadsheet
   * @throws Exception	if filtering fails
   */
  @Override
  protected SpreadSheet doFilter(SpreadSheet data) throws Exception {
    Instances		inst;
    Instances		filtered;

    inst = toInstances(data);
    filtered = Filter.useFilter(inst, m_ActualFilter);

    return toSpreadSheet(filtered);
  }
}
