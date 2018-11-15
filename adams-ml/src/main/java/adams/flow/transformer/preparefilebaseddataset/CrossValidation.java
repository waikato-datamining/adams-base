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
 * CrossValidation.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.preparefilebaseddataset;

import adams.core.QuickInfoHelper;
import adams.flow.container.FileBasedDatasetContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates cross-validation folds.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CrossValidation
  extends AbstractRandomizableFileBasedDatasetPreparation<String[]> {

  private static final long serialVersionUID = 7027794624748574933L;

  /** the number of folds. */
  protected int m_NumFolds;

  /** the actual number of folds. */
  protected int m_ActualNumFolds;

  /** whether to randomize the data. */
  protected boolean m_Randomize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates cross-validation folds.\n"
      + "If number of folds is less than 2, leave-one-out is performed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-folds", "numFolds",
      10);

    m_OptionManager.add(
      "randomize", "randomize",
      true);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualNumFolds = -1;
  }

  /**
   * Sets the number of folds to use.
   *
   * @param value	the number of folds, less than 2 for LOO
   */
  public void setNumFolds(int value) {
    m_NumFolds = value;
    reset();
  }

  /**
   * Returns the number of folds.
   *
   * @return		the number of folds
   */
  public int getNumFolds() {
    return m_NumFolds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFoldsTipText() {
    return "The number of folds; use <2 for leave one out (LOO).";
  }

  /**
   * Returns the actual number of folds used (eg when using LOO).
   *
   * @return		the actual number of folds, -1 if not yet calculated
   */
  public int getActualNumFolds() {
    return m_ActualNumFolds;
  }

  /**
   * Sets whether to randomize the data.
   *
   * @param value	true if to randomize the data
   */
  public void setRandomize(boolean value) {
    m_Randomize = value;
    reset();
  }

  /**
   * Returns whether to randomize the data.
   *
   * @return		true if to randomize the data
   */
  public boolean getRandomize() {
    return m_Randomize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String randomizeTipText() {
    return "If enabled, the data is randomized first.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String 	result;

    result  = QuickInfoHelper.toString(this, "numFolds", m_NumFolds, "folds: ");
    result += QuickInfoHelper.toString(this, "randomize", m_Randomize, "randomize", ", ");

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
      if (m_NumFolds < 2) {
        if (data.length < 2)
          result = "At least two files required, provided: " + data.length;
      }
      else {
        if (data.length < m_NumFolds)
          result = "At least " + m_NumFolds + " files required, provided: " + data.length;
      }
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
    int					i;
    int					n;
    int					m;
    double				inc;
    String[]				train;
    String[]				test;
    int					testFrom;
    int					testTo;

    result = new ArrayList<>();

    if (m_NumFolds < 2)
      m_ActualNumFolds = data.length;
    else
      m_ActualNumFolds = m_NumFolds;

    if (m_Randomize)
      data = randomize(data);

    if (m_ActualNumFolds == data.length) {
      for (i = 0; i < data.length; i++) {
        // train
	train = new String[data.length - 1];
	m = 0;
	for (n = 0; n < data.length; n++) {
	  if (n != i)
	    continue;
	  train[m] = data[n];
	  m++;
	}

	// test
	test = new String[]{data[i]};

	result.add(new FileBasedDatasetContainer(train, test));
      }
    }
    else {
      for (i = 0; i < m_NumFolds; i++) {
        // test
        testFrom = (int) Math.round(i * (data.length * 1.0 / m_NumFolds));
        if (i == m_NumFolds - 1)
          testTo = data.length;
        else
	  testTo = (int) Math.round((i+1) * (data.length * 1.0 / m_NumFolds));
        test = new String[testTo - testFrom];
        for (n = testFrom; n < testTo; n++)
          test[n - testFrom] = data[n];

        // train
        train = new String[data.length - test.length];
        m = 0;
        for (n = 0; n < testFrom; n++) {
          train[m] = data[n];
          m++;
	}
        for (n = testTo; n < data.length; n++) {
          train[m] = data[n];
          m++;
	}

	result.add(new FileBasedDatasetContainer(train, test));
      }
    }

    return result;
  }
}
