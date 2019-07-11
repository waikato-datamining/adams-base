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
 * TrainTestSplit.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.preparefilebaseddataset;

import adams.core.QuickInfoHelper;
import adams.data.binning.Binnable;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.operation.Wrapping.IndexedBinValueExtractor;
import adams.data.splitgenerator.generic.randomization.DefaultRandomization;
import adams.data.splitgenerator.generic.randomization.PassThrough;
import adams.data.splitgenerator.generic.randomsplit.RandomSplitGenerator;
import adams.data.splitgenerator.generic.randomsplit.SplitPair;
import adams.flow.container.FileBasedDatasetContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates a train/test split.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainTestSplit
  extends AbstractRandomizableFileBasedDatasetPreparation<String[]> {

  private static final long serialVersionUID = 7027794624748574933L;

  /** the percentage. */
  protected double m_Percentage;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a train/test split.";
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
    return "The percentage of the data to use for the training set.";
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
    return "If enabled, the data doesn't get randomized.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String 	result;

    result  = QuickInfoHelper.toString(this, "percentage", m_Percentage, "perc: ");
    result += QuickInfoHelper.toString(this, "preserveOrder", m_PreserveOrder, "preserve", ", ");

    return result;
  }

  /**
   * Returns the class that the preparation scheme accepts as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return String[].class;
  }

  /**
   * Hook method for checking the data.
   *
   * @param data	the data to check
   * @return		null if succesful, otherwise error message
   */
  @Override
  protected String check(String[] data) {
    String	result;

    result = super.check(data);

    if (result == null) {
      if (data.length < 2)
	result = "At least two files required, provided: " + data.length;
    }

    return result;
  }

  /**
   * Prepares the data.
   *
   * @param data	the data to use
   * @return		the generated container
   */
  @Override
  protected List<FileBasedDatasetContainer> doPrepare(String[] data) {
    List<FileBasedDatasetContainer>	result;
    FileBasedDatasetContainer   	cont;
    String[]				train;
    String[]				test;
    RandomSplitGenerator 		generator;
    DefaultRandomization		defRand;
    List<Binnable<String>>		binnable;
    SplitPair<Binnable<String>>		pair;

    generator = new RandomSplitGenerator();
    if (!m_PreserveOrder) {
      defRand = new DefaultRandomization();
      defRand.setSeed(m_Seed);
      defRand.setLoggingLevel(m_LoggingLevel);
      generator.setRandomization(defRand);
    }
    else {
      generator.setRandomization(new PassThrough());
    }

    try {
      binnable = Wrapping.wrap(Arrays.asList(data), new IndexedBinValueExtractor<>());
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to wrap file names in Binnable objects!");
    }

    pair  = generator.generate(binnable);
    train = Wrapping.unwrap(pair.getTrain().getData()).toArray(new String[0]);
    test  = Wrapping.unwrap(pair.getTest().getData()).toArray(new String[0]);

    cont = new FileBasedDatasetContainer(train, test);

    result = new ArrayList<>();
    result.add(cont);

    return result;
  }
}
