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
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.AbstractAdamsClassifierTest;
import weka.classifiers.Classifier;
import weka.classifiers.meta.multisearch.Performance;
import weka.classifiers.trees.J48;
import weka.core.SelectedTag;
import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.MathParameter;

/**
 * Tests MultiSearch. Run from the command line with:<br><br>
 * java weka.classifiers.meta.MultiSearchTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiSearchTest
  extends AbstractAdamsClassifierTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MultiSearchTest(String name) {
    super(name);
  }

  /**
   * Creates a default MultiSearch.
   *
   * @return		the configured classifier
   */
  @Override
  public Classifier getClassifier() {
    MultiSearch		result;
    MathParameter	param;

    result = new MultiSearch();

    result.setEvaluation(new SelectedTag(Performance.EVALUATION_ACC, MultiSearch.TAGS_EVALUATION));
    result.setClassifier(new J48());

    param = new MathParameter();
    param.setBase(10.0);
    param.setMin(0.1);
    param.setMax(0.5);
    param.setStep(0.1);
    param.setExpression("I");
    param.setProperty("classifier.confidenceFactor");
    result.setSearchParameters(new AbstractParameter[]{
	param
    });

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MultiSearchTest.class);
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
