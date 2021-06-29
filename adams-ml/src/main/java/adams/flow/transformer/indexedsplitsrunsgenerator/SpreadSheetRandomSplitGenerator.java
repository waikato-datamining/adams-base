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
 * SpreadSheetRandomSplitGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.data.binning.Binnable;
import adams.data.binning.BinnableDataset;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.randomization.PassThrough;
import adams.data.splitgenerator.generic.randomsplit.RandomSplitGenerator;
import adams.data.splitgenerator.generic.randomsplit.SplitPair;
import adams.data.splitgenerator.generic.splitter.DefaultSplitter;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.SpreadSheet;

import java.util.List;

/**
 * Random split generator that works on spreadsheets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRandomSplitGenerator
  extends AbstractSpreadSheetIndexedSplitsRunsGenerator
  implements Randomizable {

  private static final long serialVersionUID = -845552507613381226L;

  /** the percentage. */
  protected double m_Percentage;

  /** the seed value. */
  protected long m_Seed;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Random split generator that works on spreadsheets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "percentage", "percentage",
      0.66, 0.0, 1.0);

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "preserve-order", "preserveOrder",
      false);
  }

  /**
   * Sets the split percentage.
   *
   * @param value	the percentage (0-1)
   */
  public void setPercentage(double value) {
    if (getOptionManager().isValid("percentage", value)) {
      m_Percentage = value;
      reset();
    }
  }

  /**
   * Returns the split percentage.
   *
   * @return		the percentage (0-1)
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return "The percentage to use for training (0-1).";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return		the seed
   */
  @Override
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String seedTipText() {
    return "The seed value for the random number generator.";
  }

  /**
   * Sets whether to preserve the order.
   *
   * @param value	true if to preserve order
   */
  public void setPreserveOrder(boolean value) {
    m_PreserveOrder = value;
    reset();
  }

  /**
   * Returns whether to preserve the order.
   *
   * @return		true if to preserve order
   */
  public boolean getPreserveOrder() {
    return m_PreserveOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preserveOrderTipText() {
    return "If enabled, the order in the data is preserved in the split.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "percentage", m_Percentage, "percentage: ");
    result += QuickInfoHelper.toString(this, "preserveOrder", (m_PreserveOrder ? "preserve order" : "randomize"), ", ");
    if (!m_PreserveOrder)
      result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");

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
    IndexedSplitsRuns			result;
    IndexedSplitsRun			indexedSplitsRun;
    IndexedSplits			indexedSplits;
    IndexedSplit			indexedSplit;
    SpreadSheet				sheet;
    RandomSplitGenerator 		generator;
    List<Binnable<DataRow>> 		binnedData;
    SplitPair<Binnable<DataRow>> 	splitPair;
    int[]				trainRows;
    int[]				testRows;

    sheet = (SpreadSheet) data;

    generator = new RandomSplitGenerator();
    if (m_PreserveOrder) {
      PassThrough rand = new PassThrough();
      generator.setRandomization(rand);
    }
    else {
      DefaultRandomization rand = new DefaultRandomization();
      rand.setSeed(m_Seed);
      generator.setRandomization(rand);
    }

    DefaultSplitter splitter = new DefaultSplitter();
    splitter.setPercentage(m_Percentage);
    generator.setSplitter(splitter);

    try {
      binnedData = BinnableDataset.toBinnableUsingIndex(sheet);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to create binnable spreadsheet!", e);
    }
    splitPair = generator.generate(binnedData);

    trainRows = splitPair.getTrain().getOriginalIndices().toArray();
    testRows  = splitPair.getTest().getOriginalIndices().toArray();

    indexedSplit = new IndexedSplit(0);
    indexedSplit.add(new SplitIndices("train", trainRows));
    indexedSplit.add(new SplitIndices("test", testRows));
    indexedSplits = new IndexedSplits();
    indexedSplits.add(indexedSplit);
    indexedSplitsRun = new IndexedSplitsRun(0, indexedSplits);
    result = new IndexedSplitsRuns();
    result.add(indexedSplitsRun);

    return result;
  }
}
