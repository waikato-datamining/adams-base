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

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.flow.container.FileBasedDatasetContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a container from the selected elements of the array.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Selected
  extends AbstractFileBasedDatasetPreparation<String[][]> {

  private static final long serialVersionUID = 7027794624748574933L;

  /** the index for the training set. */
  protected Index m_Train;

  /** the index for the test set. */
  protected Index m_Test;

  /** the index for the validation set. */
  protected Index m_Validation;

  /** the index for the negative set. */
  protected Index m_Negative;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a container from the selected array indices.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "train", "train",
      new Index(""));

    m_OptionManager.add(
      "test", "test",
      new Index(""));

    m_OptionManager.add(
      "validation", "validation",
      new Index(""));

    m_OptionManager.add(
      "negative", "negative",
      new Index(""));
  }

  /**
   * Sets the index for the training set.
   *
   * @param value	the index
   */
  public void setTrain(Index value) {
    m_Train = value;
    reset();
  }

  /**
   * Returns the index for the training set.
   *
   * @return  		the index
   */
  public Index getTrain() {
    return m_Train;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trainTipText() {
    return "The index in the array for the training set.";
  }

  /**
   * Sets the index for the test set.
   *
   * @param value	the index
   */
  public void setTest(Index value) {
    m_Test = value;
    reset();
  }

  /**
   * Returns the index for the test set.
   *
   * @return  		the index
   */
  public Index getTest() {
    return m_Test;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testTipText() {
    return "The index in the array for the test set.";
  }

  /**
   * Sets the index for the validation set.
   *
   * @param value	the index
   */
  public void setValidation(Index value) {
    m_Validation = value;
    reset();
  }

  /**
   * Returns the index for the validation set.
   *
   * @return  		the index
   */
  public Index getValidation() {
    return m_Validation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String validationTipText() {
    return "The index in the array for the validation set.";
  }

  /**
   * Sets the index for the negative set.
   *
   * @param value	the index
   */
  public void setNegative(Index value) {
    m_Negative = value;
    reset();
  }

  /**
   * Returns the index for the negative set.
   *
   * @return  		the index
   */
  public Index getNegative() {
    return m_Negative;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String negativeTipText() {
    return "The index in the array for the negative set.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String 	result;

    result  = QuickInfoHelper.toString(this, "train", (m_Train.isEmpty() ? "-empty-" : m_Train.getIndex()), "train: ");
    result += QuickInfoHelper.toString(this, "test", (m_Test.isEmpty() ? "-empty-" : m_Test.getIndex()), ", test: ");
    result += QuickInfoHelper.toString(this, "validation", (m_Validation.isEmpty() ? "-empty-" : m_Validation.getIndex()), ", validation: ");
    result += QuickInfoHelper.toString(this, "negative", (m_Negative.isEmpty() ? "-empty-" : m_Negative.getIndex()), ", negative: ");

    return result;
  }

  /**
   * Returns the class that the preparation scheme accepts as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return String[][].class;
  }

  /**
   * Hook method for checking the data.
   *
   * @param data	the data to check
   * @return		null if succesful, otherwise error message
   */
  @Override
  protected String check(String[][] data) {
    String	result;
    int		count;

    result = super.check(data);

    count = 0;

    if (result == null) {
      if (!m_Train.isEmpty()) {
	m_Train.setMax(data.length);
	if (m_Train.getIntIndex() == -1)
	  result = "Train index not available from input data: " + m_Train.getIndex();
	else
	  count++;
      }
    }

    if (result == null) {
      if (!m_Test.isEmpty()) {
	m_Test.setMax(data.length);
	if (m_Test.getIntIndex() == -1)
	  result = "Test index not available from input data: " + m_Test.getIndex();
	else
	  count++;
      }
    }

    if (result == null) {
      if (!m_Validation.isEmpty()) {
	m_Validation.setMax(data.length);
	if (m_Validation.getIntIndex() == -1)
	  result = "Validation index not available from input data: " + m_Validation.getIndex();
	else
	  count++;
      }
    }

    if (result == null) {
      if (!m_Negative.isEmpty()) {
	m_Negative.setMax(data.length);
	if (m_Negative.getIntIndex() == -1)
	  result = "Negative index not available from input data: " + m_Negative.getIndex();
	else
	  count++;
      }
    }

    if (result == null) {
      if (count == 0)
        result = "No index selected for dataset!";
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
  protected List<FileBasedDatasetContainer> doPrepare(String[][] data) {
    List<FileBasedDatasetContainer>	result;
    FileBasedDatasetContainer   	cont;
    String[]				train;
    String[]				test;
    String[]				validation;
    String[]				negative;

    train = null;
    if (m_Train.getIntIndex() > -1)
      train = data[m_Train.getIntIndex()];

    test = null;
    if (m_Test.getIntIndex() > -1)
      test = data[m_Test.getIntIndex()];

    validation = null;
    if (m_Validation.getIntIndex() > -1)
      validation = data[m_Validation.getIntIndex()];

    negative = null;
    if (m_Negative.getIntIndex() > -1)
      negative = data[m_Negative.getIntIndex()];

    cont = new FileBasedDatasetContainer(
      (train      != null ? train.clone()      : null),
      (test       != null ? test.clone()       : null),
      (validation != null ? validation.clone() : null),
      (negative   != null ? negative.clone()   : null));

    result = new ArrayList<>();
    result.add(cont);

    return result;
  }
}
