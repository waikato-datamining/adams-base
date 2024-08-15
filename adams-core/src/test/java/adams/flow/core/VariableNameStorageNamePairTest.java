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
 * VariableNameStorageNamePairTest.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.base.AbstractBaseObjectTestCase;
import adams.env.Environment;
import adams.flow.control.StorageName;
import adams.flow.control.VariableNameStorageNamePair;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.flow.control.VariableNameStorageNamePair class. Run from commandline with: <br><br>
 * java adams.flow.control.VariableNameStorageNamePairTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class VariableNameStorageNamePairTest
  extends AbstractBaseObjectTestCase<VariableNameStorageNamePair> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public VariableNameStorageNamePairTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected VariableNameStorageNamePair getDefault() {
    return new VariableNameStorageNamePair();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected VariableNameStorageNamePair getCustom(String s) {
    return new VariableNameStorageNamePair(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "vname=sname";
  }

  /**
   * Tests a simple key=value pair.
   */
  public void testSimple() {
    VariableNameStorageNamePair pair = new VariableNameStorageNamePair();
    pair.setValue("blah=something_else");
    assertEquals("blah", pair.variableNameValue());
    assertEquals("something_else", pair.storageNameValue().getValue());
  }

  /**
   * Tests empty value (not allowed).
   */
  public void testEmptyValue() {
    VariableNameStorageNamePair pair = new VariableNameStorageNamePair();
    pair.setValue("blah=");
    assertEquals("", pair.variableNameValue());
    assertEquals(StorageName.DEFAULT, pair.storageNameValue().getValue());
  }

  /**
   * Tests missing separator.
   */
  public void testNoSeparator() {
    VariableNameStorageNamePair pair = new VariableNameStorageNamePair();
    pair.setValue("blah");
    assertEquals("", pair.variableNameValue());
    assertEquals(StorageName.DEFAULT, pair.storageNameValue().getValue());
  }

  /**
   * Tests invalid storage name.
   */
  public void testInvalidStoragename() {
    VariableNameStorageNamePair pair = new VariableNameStorageNamePair();
    pair.setValue("blah=$ablue");
    assertEquals("", pair.variableNameValue());
    assertEquals(StorageName.DEFAULT, pair.storageNameValue().getValue());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(VariableNameStorageNamePairTest.class);
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
