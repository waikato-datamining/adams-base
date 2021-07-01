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
 * ManualSplitGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.base.BaseKeyValuePair;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses the manually defined split ranges to generate the splits.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ManualSplitGenerator
  extends AbstractIndexedSplitsRunsGenerator {

  private static final long serialVersionUID = -8978470236154443815L;

  /** the named ranges. */
  protected BaseKeyValuePair[] m_Splits;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the manually defined split ranges to generate the splits.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "split", "splits",
      new BaseKeyValuePair[0]);
  }

  /**
   * Sets the named split ranges.
   *
   * @param value	the split ranges
   */
  public void setSplits(BaseKeyValuePair[] value) {
    m_Splits = value;
    reset();
  }

  /**
   * Returns the named split ranges.
   *
   * @return		the split ranges
   */
  public BaseKeyValuePair[] getSplits() {
    return m_Splits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitsTipText() {
    return "The key represents the name of the split and the value the range of indices.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "splits", m_Splits, ", splits: ");

    return result;
  }

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object[].class};
  }

  /**
   * Checks whether the data can be processed.
   *
   * @param data	the data to check
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check(Object data) {
    String	result;

    result = super.check(data);

    if (result == null) {
      if (m_Splits.length == 0)
        result = "Not split ranges defined!";
    }

    return result;
  }

  /**
   * Generates the indexed splits.
   *
   * @param data   the data to use for generating the splits
   * @param errors for storing any errors occurring during processing
   * @return the splits or null in case of error
   */
  @Override
  protected IndexedSplitsRuns doGenerate(Object data, MessageCollection errors) {
    IndexedSplitsRuns	result;
    IndexedSplitsRun 	indexedSplitsRun;
    IndexedSplits	indexedSplits;
    IndexedSplit 	indexedSplit;
    SplitIndices	splitIndices;
    Object[]		array;
    Map<String,Range> 	ranges;
    Range		range;

    array = (Object[]) data;

    // init ranges
    ranges = new HashMap<>();
    for (BaseKeyValuePair split: m_Splits) {
      range = new Range(split.getPairValue());
      range.setMax(array.length);
      if (range.getIntIndices().length == 0)
        errors.add("Range '" + split.getPairValue() + "' did not generate any indices!");
      ranges.put(split.getPairValue(), range);
    }
    if (!errors.isEmpty())
      return null;

    // generate splits
    result = new IndexedSplitsRuns();
    indexedSplit = new IndexedSplit(0);
    indexedSplits = new IndexedSplits();
    indexedSplits.add(indexedSplit);
    indexedSplitsRun = new IndexedSplitsRun(0, indexedSplits);
    result.add(indexedSplitsRun);
    for (String name: ranges.keySet()) {
      splitIndices = new SplitIndices(name, ranges.get(name).getIntIndices());
      indexedSplit.add(splitIndices);
    }

    return result;
  }
}
