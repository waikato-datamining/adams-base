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
 * AbstractGeneticAlgorithmWithDataset.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.genetic;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import weka.core.Instances;

/**
 * Ancestor for genetic algorithms that use a dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticAlgorithmWithDataset
  extends AbstractGeneticAlgorithm {

  private static final long serialVersionUID = 2081325389083203901L;

  /**
   * Job class for algorithms with datasets.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static abstract class GeneticAlgorithmJobWithDataset
    extends GeneticAlgorithmJob {

    /** for serialization. */
    private static final long serialVersionUID = 8259167463381721274L;

    /**
     * Initializes the job.
     *
     * @param g		the algorithm object this job belongs to
     * @param num	the number of chromsomes
     * @param w		the initial weights
     */
    public GeneticAlgorithmJobWithDataset(AbstractGeneticAlgorithm g, int num, int[] w) {
      super(g, num, w);
    }

    /**
     * Returns the instances in use by the genetic algorithm.
     *
     * @return		the instances
     */
    protected Instances getInstances() {
      return ((AbstractGeneticAlgorithmWithDataset) m_genetic).getInstances();
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      String	result;

      result = super.preProcessCheck();

      if (result == null) {
        if (getInstances() == null)
          result = "No instances provided!";
      }

      return result;
    }
  }

  /** the filename of the data to use for cross-validation. */
  protected PlaceholderFile m_Dataset;

  /** the class index. */
  protected String m_ClassIndex;

  /** the directory to store the generated ARFF files in. */
  protected PlaceholderDirectory m_OutputDirectory;

  /** the data to use for cross-validation. */
  protected Instances m_Instances;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-dir", "outputDirectory",
      new PlaceholderDirectory("."));

    m_OptionManager.add(
      "dataset", "dataset",
      new PlaceholderFile("./data.arff"));

    m_OptionManager.add(
      "class", "classIndex",
      "last");
  }

  /**
   * Sets the filename of the dataset to use for cross-validation.
   *
   * @param value	the filename
   */
  public void setDataset(PlaceholderFile value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the currently set filename of the dataset for cross-validation.
   *
   * @return		the filename
   */
  public PlaceholderFile getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset to use.";
  }

  /**
   * Sets the class index.
   *
   * @param value	the class index
   */
  public void setClassIndex(String value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current class index.
   *
   * @return		the class index
   */
  public String getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The class index of the dataset ('first' and 'last' are accepted as well).";
  }

  /**
   * Sets the directory for the generated ARFF files.
   *
   * @param value	the directory
   */
  public void setOutputDirectory(PlaceholderDirectory value) {
    m_OutputDirectory = value;
    reset();
  }

  /**
   * Returns the currently set directory for the generated ARFF files.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getOutputDirectory() {
    return m_OutputDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirectoryTipText() {
    return "The directory for storing the generated ARFF files.";
  }

  /**
   * Sets the data to use for cross-validation.
   *
   * @param value	the dataset
   */
  public void setInstances(Instances value) {
    m_Instances = value;
  }

  /**
   * Returns the currently set dataset for cross-validation.
   *
   * @return		the dataset
   */
  public Instances getInstances() {
    return m_Instances;
  }

}
