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
 * AbstractWEKAFitnessFunction.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.optimise.genetic.fitnessfunctions;

import java.io.FileReader;
import java.util.logging.Level;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.UnassignedClassException;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.opt.optimise.AbstractFitnessFunction;

/**
 * Perform attribute selection using WEKA classification.
 * @author dale
 *
 */
public abstract class AbstractWEKAFitnessFunction extends AbstractFitnessFunction {

  /**suid.   */

  /**
   *
   */
  private static final long serialVersionUID = 8593164242546231576L;

  /** the data to use for cross-validation. */
  protected Instances m_Instances=null;

  /** the filename of the data to use for cross-validation. */
  protected PlaceholderFile m_Dataset;

  /** the classifier to use if no serialized model is given. */
  protected Classifier m_Classifier;

  /** the directory to store the generated ARFF files in. */
  protected PlaceholderDirectory m_OutputDirectory;

  /** the number of folds for cross-validation. */
  protected int m_Folds;

  /** the cross-validation seed. */
  protected int m_CrossValidationSeed;

  /** the class index. */
  protected String m_ClassIndex;

  /** the measure to use for evaluating the fitness. */
  protected Measure m_Measure;

  /** initialised? */
  protected boolean m_init=false;

  /**
   * The measure to use for evaluating.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Measure {

    /** evaluation via: Correlation coefficient. */
    CC(false, false, true),
    /** evaluation via: Root mean squared error. */
    RMSE(true, true, true),
    /** evaluation via: Root relative squared error. */
    RRSE(true, true, true),
    /** evaluation via: Mean absolute error. */
    MAE(true, true, true),
    /** evaluation via: Relative absolute error. */
    RAE(true, true, true),
    /** evaluation via: Accuracy. */
    ACC(false, true, false);


    /** whether the measure is multiplied by -1 or not. Only used in sorting. */
    private boolean m_Negative;

    /** whether a nominal class is allowed. */
    private boolean m_Nominal;

    /** whether a numeric class is allowed. */
    private boolean m_Numeric;

    /**
     * initializes the measure with the given flags.
     *
     * @param negative	whether measures gets multiplied with -1
     * @param nominal	whether used for nominal classes
     * @param numeric	whether used for numeric classes
     */
    private Measure(boolean negative, boolean nominal, boolean numeric) {
      m_Negative = negative;
      m_Nominal  = nominal;
      m_Numeric  = numeric;
    }

    /**
     * Adjusts the measure value for sorting: either multiplies it with -1 or 1.
     *
     * @param measure	the raw measure
     * @return		the adjusted measure
     */
    public double adjust(double measure) {
      if (m_Negative)
	return -measure;
      else
	return measure;
    }

    /**
     * Checks whether the data can be used with this measure.
     *
     * @param data	the data to check
     * @return		true if the measure can be obtain for this kind of data
     */
    public boolean isValid(Instances data) {
      if (data.classIndex() == -1)
	throw new UnassignedClassException("No class attribute set!");

      if (data.classAttribute().isNominal())
	return m_Nominal;
      else if (data.classAttribute().isNumeric())
	return m_Numeric;
      else
	throw new IllegalStateException(
	    "Class attribute '" + data.classAttribute().type() + "' not handled!");
    }
  }
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "folds", "folds",
	    10);

    m_OptionManager.add(
	    "cv-seed", "crossValidationSeed",
	    55);

    m_OptionManager.add(
	    "classifier", "classifier",
	    new weka.classifiers.rules.ZeroR());

    m_OptionManager.add(
	    "output-dir", "outputDirectory",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "dataset", "dataset",
	    new PlaceholderFile("./data.arff"));

    m_OptionManager.add(
	    "class", "classIndex",
            "last");

    m_OptionManager.add(
	    "measure", "measure",
	    Measure.RMSE);

  }

  /**
   * Sets the number of folds to use in cross-validation.
   *
   * @param value	the number of folds
   */
  public void setFolds(int value) {
    m_Folds = value;
    reset();
  }

  /**
   * Returns the number of folds to use in cross-validation.
   *
   * @return		the number of folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in cross-validation.";
  }

  /**
   * Sets the seed value to use for cross-validation.
   *
   * @param value	the seed to use
   */
  public void setCrossValidationSeed(int value) {
    m_CrossValidationSeed = value;
    reset();
  }

  /**
   * Returns the current seed value for cross-validation.
   *
   * @return		the seed value
   */
  public int getCrossValidationSeed() {
    return m_CrossValidationSeed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String crossValidationSeedTipText() {
    return "The seed value for cross-validation.";
  }

  /**
   * Sets the data to use for cross-validation.
   *
   * @param value	the dataset
   */
  public void setInstances(Instances value) {
    m_Instances = value;
    reset();
  }

  /**
   * Returns the currently set dataset for cross-validation.
   *
   * @return		the dataset
   */
  public Instances getInstances() {
    return m_Instances;
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
    return "The dataset to use for cross-validation.";
  }


  /**
   * Sets the classifier to use (if no serialized model is used).
   *
   * @param value	the classifier
   */
  public void setClassifier(Classifier value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the currently set classifier.
   *
   * @return		the classifier
   */
  public Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The classifier to use if no serialized is supplied.";
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
   * Sets the measure used for evaluating the fitness.
   *
   * @param value	the fitness measure
   */
  public void setMeasure(Measure value) {
    m_Measure = value;
    reset();
  }

  /**
   * Returns the current measure for evaluating the fitness.
   *
   * @return		the measure
   */
  public Measure getMeasure() {
    return m_Measure;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String measureTipText() {
    return "The measure used for evaluating the fitness.";
  }

  @Override
  public String globalInfo() {
    // TODO Auto-generated method stub
    return "WEKA Fitness";
  }

  protected synchronized void init() {
    int classIndex = 0;
    if (!m_init) {
      FileReader	reader;
      try {
	reader      = new FileReader(m_Dataset.getAbsolutePath());
	m_Instances = new Instances(reader);
	reader.close();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to read instances: " + m_Dataset, e);
	throw new IllegalStateException("Error loading dataset '" + m_Dataset + "': " + e);
      }

      // class index
      if (m_ClassIndex.equals("first"))
	classIndex = 0;
      else if (m_ClassIndex.equals("last"))
	classIndex = m_Instances.numAttributes() - 1;
      else
	classIndex = Integer.parseInt(m_ClassIndex);
      m_Instances.setClassIndex(classIndex);

      m_init=true;
    }
  }


}
