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
 * JsonToSpreadSheetTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.minidev.json.JSONAware;
import net.minidev.json.parser.JSONParser;
import adams.env.Environment;

/**
 * Tests the JsonToSpreadSheet conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5999 $
 */
public class JsonToSpreadSheetTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public JsonToSpreadSheetTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    JSONAware[]	result;

    try {
      result = new JSONAware[2];
      result[0] = (JSONAware) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse("{\"age\":42,\"name\":\"first last\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"]}");
      result[1] = (JSONAware) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse("[\"e 1\", \"e 2\", \"e 3\", \"e 4\", \"e 5\", \"e 6\", \"e 7\", \"e 8\", \"e 9\", \"e 10\", \"e 11\"]");
    }
    catch (Exception e) {
      fail("Failed to parse JSON strings: " + e);
      result = new JSONAware[0];
    }

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    JsonToSpreadSheet[]	result;

    result    = new JsonToSpreadSheet[3];
    result[0] = new JsonToSpreadSheet();
    result[1] = new JsonToSpreadSheet();
    result[1].setPrefix("json-");
    result[2] = new JsonToSpreadSheet();
    result[2].setLeftPad(true);

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(JsonToSpreadSheetTest.class);
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
