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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.AbstractAdamsClassifierTest;
import weka.classifiers.CheckClassifier;
import weka.classifiers.Classifier;
import weka.core.SelectedTag;
import weka.filters.supervised.attribute.PLSFilter;

/**
 * Tests ClassificationViaPLS. Run from the command line with:<br><br>
 * java weka.classifiers.functions.ClassificationViaPLSTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassificationViaPLSTest
  extends AbstractAdamsClassifierTest {

  /** the number of PLS components to generate */
  public final static int NUM_COMPONENTS = 5;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ClassificationViaPLSTest(String name) {
    super(name);
  }

  /**
   * Creates a default ClassificationViaPLS.
   *
   * @return		the configured classifier
   */
  @Override
  public Classifier getClassifier() {
    ClassificationViaPLS	result;
    PLSFilter			filter;

    result = new ClassificationViaPLS();
    filter = ((PLSFilter) result.getFilter());
    filter.setReplaceMissing(true);
    filter.setPreprocessing(new SelectedTag(PLSFilter.PREPROCESSING_CENTER, PLSFilter.TAGS_PREPROCESSING));
    filter.setNumComponents(NUM_COMPONENTS);
    filter.setAlgorithm(new SelectedTag(PLSFilter.ALGORITHM_SIMPLS, PLSFilter.TAGS_ALGORITHM));

    return result;
  }
  
  /**
   * configures the CheckClassifier instance used throughout the tests
   * 
   * @return	the fully configured CheckClassifier instance used for testing
   */
  @Override
  protected CheckClassifier getTester() {
    CheckClassifier	result;
    
    result = super.getTester();
    result.setNumNominal(NUM_COMPONENTS * 2);
    result.setNumNumeric(NUM_COMPONENTS * 2);
    result.setNumString(NUM_COMPONENTS * 2);
    result.setNumDate(NUM_COMPONENTS * 2);
    result.setNumRelational(NUM_COMPONENTS * 2);
    
    return result;
  }

  /**
   * Ignores 100% missing.
   */
  @Override
  public void testMissingPredictors() {
    int           i;
    
    for (i = FIRST_CLASSTYPE; i <= LAST_CLASSTYPE; i++) {
      // does the classifier support this type of class at all?
      if (!canPredict(i))
        continue;
      
      // 20% missing
      checkMissingPredictors(i, 20, true);
    }
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(ClassificationViaPLSTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}
