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
 * ClassifierBasedSimpleCatSwarmOptimization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso;

import adams.core.io.PlaceholderDirectory;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Ancestor for classifier-based CSO optimization schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ClassifierBasedCatSwarmOptimization
  extends CatSwarmOptimization {

  /** the key for the relation name in the generated properties file. */
  public final static String PROPS_RELATION = "relation";

  /**
   * Sets the data to use for cross-validation.
   *
   * @param value	the dataset
   */
  public void setInstances(Instances value);

  /**
   * Returns the currently set dataset for cross-validation.
   *
   * @return		the dataset
   */
  public Instances getInstances();

  /**
   * Sets the number of folds to use in cross-validation.
   *
   * @param value	the number of folds
   */
  public void setFolds(int value);

  /**
   * Returns the number of folds to use in cross-validation.
   *
   * @return		the number of folds
   */
  public int getFolds();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText();

  /**
   * Sets the seed value to use for cross-validation.
   *
   * @param value	the seed to use
   */
  public void setCrossValidationSeed(int value);

  /**
   * Returns the current seed value for cross-validation.
   *
   * @return		the seed value
   */
  public int getCrossValidationSeed();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String crossValidationSeedTipText();

  /**
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(Classifier value);

  /**
   * Returns the currently set classifier.
   *
   * @return		the classifier
   */
  public Classifier getClassifier();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText();

  /**
   * Sets the measure used for evaluating the fitness.
   *
   * @param value	the fitness measure
   */
  public void setMeasure(Measure value);

  /**
   * Returns the current measure for evaluating the fitness.
   *
   * @return		the measure
   */
  public Measure getMeasure();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String measureTipText();

  /**
   * Sets the directory for the generated ARFF files.
   *
   * @param value	the directory
   */
  public void setOutputDirectory(PlaceholderDirectory value);

  /**
   * Returns the currently set directory for the generated ARFF files.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getOutputDirectory();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirectoryTipText();

  /**
   * Sets the type of output to generate.
   *
   * @param value	the type
   */
  public void setOutputType(OutputType value);

  /**
   * Returns the type of output to generate.
   *
   * @return		the type
   */
  public OutputType getOutputType();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText();

  /**
   * Sets the type of prefix to use for the output.
   *
   * @param value	the type
   */
  public void setOutputPrefixType(OutputPrefixType value);

  /**
   * Returns the type of prefix to use for the output.
   *
   * @return		the type
   */
  public OutputPrefixType getOutputPrefixType();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputPrefixTypeTipText();

  /**
   * Sets the prefix to use in case of {@link OutputPrefixType#SUPPLIED}.
   *
   * @param value	the prefix
   */
  public void setSuppliedPrefix(String value);

  /**
   * Returns the prefix to use in case of {@link OutputPrefixType#SUPPLIED}.
   *
   * @return		the number of folds
   */
  public String getSuppliedPrefix();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppliedPrefixTipText();

  /**
   * Method to get the best classifier from the swarm.
   *
   * @return 		the best classifier
   */
  public Classifier getBestSetup();
}
