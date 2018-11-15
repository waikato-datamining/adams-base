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
import adams.core.Randomizable;
import adams.flow.container.FileBasedDatasetContainer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generates a train/test split.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TrainTestSplit
  extends AbstractFileBasedDatasetPreparation<String[]>
  implements Randomizable {

  private static final long serialVersionUID = 7027794624748574933L;

  /** the seed. */
  protected long m_Seed;

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
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "percentage", "percentage",
      0.66, 0.0, 1.0);

    m_OptionManager.add(
      "preserve-order", "preserveOrder",
      false);
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for randomizing the data.";
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
    Random				rand;
    TIntList				indices;
    String[]				tmp;
    int					i;
    int					train;

    if (!m_PreserveOrder) {
      rand = new Random(m_Seed);
      indices = new TIntArrayList();
      for (i = 0; i < data.length; i++)
        indices.add(i);
      indices.shuffle(rand);
      tmp = new String[data.length];
      for (i = 0; i < indices.size(); i++)
        tmp[i] = data[indices.get(i)];
      data = tmp;
    }

    train = (int) Math.round(data.length * m_Percentage);
    if (train == data.length)
      train--;
    if (isLoggingEnabled())
      getLogger().info("# instances for train: " + train);

    cont = new FileBasedDatasetContainer(
      Arrays.copyOfRange(data, 0, train),
      Arrays.copyOfRange(data, train, data.length),
      null,
      null);

    result = new ArrayList<>();
    result.add(cont);

    return result;
  }
}
