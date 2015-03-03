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
 * ReportValueExistsTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.env.Environment;
import adams.flow.core.AbstractActor;

/**
 * Tests the 'ReportValueExists' boolean condition.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportValueExistsTest
  extends AbstractBooleanConditionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ReportValueExistsTest(String name) {
    super(name);
  }

  /**
   * Returns the owning actors to use in the regression test (one per regression setup).
   *
   * @return		the owners (not all conditions might need owners)
   */
  @Override
  protected AbstractActor[] getRegressionOwners() {
    return new AbstractActor[]{
	null,
	null
    };
  }

  /**
   * Returns the input data to use in the regression test (one per regression setup).
   *
   * @return		the input data
   */
  @Override
  protected Object[] getRegressionInputs() {
    Report[]	result;
    
    result    = new Report[2];
    result[0] = new Report();
    result[0].addField(new Field("key1", DataType.STRING));
    result[0].setStringValue("key1", "string");
    result[0].addField(new Field("key2", DataType.BOOLEAN));
    result[0].setBooleanValue("key2", true);
    result[0].addField(new Field("key3", DataType.BOOLEAN));
    result[0].setBooleanValue("key3", false);
    result[0].addField(new Field("key4", DataType.NUMERIC));
    result[0].setNumericValue("key4", 0.3);
    result[1] = result[0].getClone();

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBooleanCondition[] getRegressionSetups() {
    ReportValueExists[]	result;
    
    result    = new ReportValueExists[2];
    result[0] = new ReportValueExists();
    result[1] = new ReportValueExists();
    result[1].setField(new Field("key1", DataType.STRING));
    
    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ReportValueExistsTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
