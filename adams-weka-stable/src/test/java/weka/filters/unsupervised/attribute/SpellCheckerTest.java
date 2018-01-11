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
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;
import weka.core.TestInstances;
import weka.core.Utils;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;

/**
 * Tests SpellChecker. Run from the command line with: <br><br>
 * java weka.filters.unsupervised.attribute.SpellCheckerTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpellCheckerTest
  extends AbstractAdamsFilterTest {

  public SpellCheckerTest(String name) {
    super(name);
  }

  /** Creates a default SpellChecker */
  public Filter getFilter() {
    return getFilter(1, "rgb", new String[]{"r", "g", "b"});
  }

  /** Creates a specialized SpellChecker */
  public Filter getFilter(int pos, String correct, String[] incorrect) {
    SpellChecker result = new SpellChecker();
    result.setAttributeIndex("" + (pos + 1));
    result.setCorrect(correct);
    try {
      result.setIncorrect(Utils.joinOptions(incorrect));
    }
    catch (Exception e) {
      fail(e.toString());
    }
    return result;
  }

  /**
   * returns data generated for the FilteredClassifier test
   *
   * @return		the dataset for the FilteredClassifier
   * @throws Exception	if generation of data fails
   */
  protected Instances getFilteredClassifierData() throws Exception {
    TestInstances	test;
    Instances		result;

    // NB: in order to make sure that the classifier can handle the data,
    //     we're using the classifier's capabilities to generate the data.
    test = TestInstances.forCapabilities(
  	m_FilteredClassifier.getClassifier().getCapabilities());
    test.setNumNominal(2);
    test.setClassIndex(TestInstances.CLASS_IS_LAST);

    result = test.generate();

    return result;
  }

  public static Test suite() {
    return new TestSuite(SpellCheckerTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }
}
