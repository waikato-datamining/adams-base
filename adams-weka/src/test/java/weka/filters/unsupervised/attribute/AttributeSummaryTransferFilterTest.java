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
 * AttributeSummaryTransferFilterTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import adams.core.base.BaseInteger;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.TestInstances;
import weka.filters.AbstractAdamsFilterTest;
import weka.filters.Filter;
import weka.test.AdamsTestHelper;

/**
 * Tests for the AttributeSummaryTransferFilter class.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class AttributeSummaryTransferFilterTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name the name of the test
   */
  public AttributeSummaryTransferFilterTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_Instances = getFilteredClassifierData();
  }

  /**
   * returns data generated for the FilteredClassifier test.
   *
   * @return		the dataset for the FilteredClassifier
   * @throws Exception	if generation of data fails
   */
  @Override
  protected Instances getFilteredClassifierData() throws Exception {
    TestInstances testinst;

    testinst = new TestInstances();
    testinst.setNumNominal(0);
    testinst.setNumNumeric(20);
    testinst.setClassType(Attribute.NOMINAL);
    testinst.setNumInstances(50);

    return testinst.generate();
  }

  @Override
  public Filter getFilter() {
    AttributeSummaryTransferFilter filter = new AttributeSummaryTransferFilter();

    // Set the column finder
    adams.data.weka.columnfinder.Constant columnFinder = new adams.data.weka.columnfinder.Constant();
    BaseInteger[] columns = new BaseInteger[9];
    for (int i = 0; i < columns.length; i++) {
      columns[i] = new BaseInteger(i + 1);
    }
    columnFinder.setColumns(columns);
    filter.setColumnFinder(columnFinder);

    // Set the row finder
    adams.data.weka.rowfinder.Constant rowFinder = new adams.data.weka.rowfinder.Constant();
    BaseInteger[] rows = new BaseInteger[40];
    for (int i = 0; i < rows.length; i++) {
      rows[i] = new BaseInteger(i + 10);
    }
    rowFinder.setRows(rows);
    filter.setRowFinder(rowFinder);

    return filter;
  }

  /**
   * returns the configured FilteredClassifier. Since the base classifier is
   * determined heuristically, derived tests might need to adjust it.
   *
   * @return the configured FilteredClassifier
   */
  @Override
  protected FilteredClassifier getFilteredClassifier() {
    // Skip filtered-classifier test as filter doesn't conserve
    // class attribute setting
    return null;
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(AttributeSummaryTransferFilterTest.class);
  }

  /**
   * Runs the test from the commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    AdamsTestHelper.setRegressionRoot();
    TestRunner.run(suite());
  }
}
