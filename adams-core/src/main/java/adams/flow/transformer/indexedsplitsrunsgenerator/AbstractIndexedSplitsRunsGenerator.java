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
 * AbstractIndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseKeyValuePair;
import adams.core.option.AbstractOptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.flow.core.Actor;
import adams.flow.core.Compatibility;

/**
 * Ancestor for schemes that generate indexed splits runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractIndexedSplitsRunsGenerator
  extends AbstractOptionHandler
  implements IndexedSplitsRunsGenerator {

  private static final long serialVersionUID = 6513142055574442720L;

  /** the meta-data to add. */
  protected BaseKeyValuePair[] m_MetaData;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "meta-data", "metaData",
      new BaseKeyValuePair[0]);
  }

  /**
   * Sets the meta-data to attach. Variables in 'value' parts get automatically expanded.
   *
   * @param value	the meta-data
   */
  public void setMetaData(BaseKeyValuePair[] value) {
    m_MetaData = value;
    reset();
  }

  /**
   * Returns the meta-data to attach. Variables in 'value' parts get automatically expanded.
   *
   * @return		the meta-data
   */
  public BaseKeyValuePair[] getMetaData() {
    return m_MetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataTipText() {
    return "The meta-data to attach; any variables in the 'value' parts get automatically expanded.";
  }

  /**
   * Sets the flow context.
   *
   * @param value the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "metaData", m_MetaData, "meta-data: ");
  }

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return		the classes
   */
  public abstract Class[] accepts();

  /**
   * Checks whether the data can be processed.
   *
   * @param data	the data to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Object data) {
    Compatibility	comp;

    if (data == null)
      return "No data provided!";

    if (m_FlowContext == null)
      return "No flow context set!";

    comp = new Compatibility();
    if (!comp.isCompatible(new Class[]{data.getClass()}, accepts()))
      return "Input of '" + Utils.classToString(data) + "' is not compatible with: " + Utils.classesToString(accepts());

    return null;
  }

  /**
   * Generates the indexed splits.
   *
   * @param data	the data to use for generating the splits
   * @param errors	for storing any errors occurring during processing
   * @return		the splits or null in case of error
   */
  protected abstract IndexedSplitsRuns doGenerate(Object data, MessageCollection errors);

  /**
   * For post-processing successfully generated splits.
   *
   * @param data	the input data
   * @param runs	the generated runs
   * @param errors	for storing errors
   * @return		the runs, null if failed to post-process
   */
  protected IndexedSplitsRuns postGenerate(Object data, IndexedSplitsRuns runs, MessageCollection errors) {
    if (m_MetaData.length > 0) {
      for (BaseKeyValuePair metaData: m_MetaData)
	runs.getMetaData().put(metaData.getPairKey(), getFlowContext().getVariables().expand(metaData.getPairValue()));
    }

    return runs;
  }

  /**
   * Generates the indexed splits.
   *
   * @param data	the data to use for generating the splits
   * @param errors	for storing any errors occurring during processing
   * @return		the splits or null in case of error
   */
  public IndexedSplitsRuns generate(Object data, MessageCollection errors) {
    IndexedSplitsRuns	result;
    String		msg;

    msg = check(data);
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    result = doGenerate(data, errors);
    result = postGenerate(data, result, errors);

    return result;
  }
}
