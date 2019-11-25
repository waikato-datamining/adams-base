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
 * TrainValidateTestSplit.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
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
import adams.data.splitgenerator.generic.splitter.DefaultSplitter;
import adams.flow.container.FileBasedDatasetContainer;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates a train/validate/test split.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainValidateTestSplit
  extends AbstractRandomizableFileBasedDatasetPreparation<String[]> {

  private static final long serialVersionUID = 7027794624748574933L;

  /** the train percentage. */
  protected double m_TrainPercentage;

  /** the validate percentage. */
  protected double m_ValidatePercentage;

  /** whether to preserve the order. */
  protected boolean m_PreserveOrder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a train/validate/test split.\n"
      + "After training and validation set have been split off, the remainder "
      + "is used for the test set.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "train-percentage", "trainPercentage",
      0.8, 0.0, 1.0);

    m_OptionManager.add(
      "validate-percentage", "validatePercentage",
      0.1, 0.0, 1.0);

    m_OptionManager.add(
      "preserve-order", "preserveOrder",
      false);
  }

  /**
   * Sets the split percentage for training.
   *
   * @param value	the percentage (0-1)
   */
  public void setTrainPercentage(double value) {
    if (getOptionManager().isValid("percentage", value)) {
      m_TrainPercentage = value;
      reset();
    }
  }

  /**
   * Returns the split percentage for training.
   *
   * @return		the percentage (0-1)
   */
  public double getTrainPercentage() {
    return m_TrainPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trainPercentageTipText() {
    return "The percentage of the data to use for the training set.";
  }

  /**
   * Sets the split percentage for validation.
   *
   * @param value	the percentage (0-1)
   */
  public void setValidatePercentage(double value) {
    if (getOptionManager().isValid("percentage", value)) {
      m_ValidatePercentage = value;
      reset();
    }
  }

  /**
   * Returns the split percentage for validation.
   *
   * @return		the percentage (0-1)
   */
  public double getValidatePercentage() {
    return m_ValidatePercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String validatePercentageTipText() {
    return "The percentage of the data to use for the validation set.";
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

    result  = QuickInfoHelper.toString(this, "trainPercentage", m_TrainPercentage, "train: ");
    result += QuickInfoHelper.toString(this, "validatePercentage", m_ValidatePercentage, ", val: ");
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
      if (data.length < 3)
	result = "At least three files required, provided: " + data.length;
    }

    if (m_TrainPercentage + m_ValidatePercentage >= 1.0) {
      result = "The sum of percentages for train and validate must be < 1.0: "
        + m_TrainPercentage + " (train) + " + m_ValidatePercentage + " (validate) = "
        + (m_TrainPercentage + m_ValidatePercentage);
    }

    return result;
  }

  /**
   * Splits the data using the specified percentage.
   *
   * @param data	the data to use
   * @return		the generated container
   */
  protected Struct2<String[],String[]> split(String[] data, double percentage) {
    String[]				train;
    String[]				test;
    DefaultSplitter			splitter;
    RandomSplitGenerator 		generator;
    DefaultRandomization		defRand;
    List<Binnable<String>>		binnable;
    SplitPair<Binnable<String>>		pair;

    generator = new RandomSplitGenerator();
    splitter  = new DefaultSplitter();
    splitter.setPercentage(percentage);
    generator.setSplitter(splitter);
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

    return new Struct2<>(train, test);
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
    String[]				remainder;
    String[]				validate;
    String[]				test;
    Struct2<String[],String[]>		split;

    split     = split(data, m_TrainPercentage);
    train     = split.value1;
    remainder = split.value2;
    split     = split(remainder, m_ValidatePercentage / (1.0 - m_TrainPercentage));
    validate  = split.value1;
    test      = split.value2;
    cont      = new FileBasedDatasetContainer(train, test, validate, null);

    result = new ArrayList<>();
    result.add(cont);

    return result;
  }
}
