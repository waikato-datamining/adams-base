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
 * ConfigurableEnumerationTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.ConfigurableEnumeration.FixedSource;
import adams.core.ConfigurableEnumeration.PropertiesSource;
import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the {@link ConfigurableEnumeration} class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConfigurableEnumerationTest
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public ConfigurableEnumerationTest(String name) {
    super(name);
  }

  /**
   * Tests the fixed source.
   */
  public void testFixedSource() {
    String[] labels = new String[]{"A","B","C","D"};
    ConfigurableEnumeration enm = new ConfigurableEnumeration(new FixedSource(labels));
    assertEquals("number of items differ", labels.length, enm.values().length);
    for (int i = 0; i < labels.length; i++) {
      assertEquals("item differs", labels[i], enm.values()[i].getLabel());
      assertNotNull("ID null", enm.values()[i].getID());
      assertNotNull("display null", enm.values()[i].getDisplay());
    }
  }

  /**
   * Tests the properties source.
   */
  public void testPropertiesSource() {
    Properties props = new Properties();
    props.setProperty(PropertiesSource.KEY_ITEMS, "a,b,c,d");
    props.setProperty("a" + PropertiesSource.SUFFIX_LABEL, "A");
    props.setProperty("a" + PropertiesSource.SUFFIX_ID, "IDA");
    props.setProperty("a" + PropertiesSource.SUFFIX_DISPLAY, "This is label A");
    props.setProperty("b" + PropertiesSource.SUFFIX_LABEL, "B");
    props.setProperty("c" + PropertiesSource.SUFFIX_LABEL, "C");
    props.setProperty("d" + PropertiesSource.SUFFIX_LABEL, "D");
    ConfigurableEnumeration enm = new ConfigurableEnumeration(new PropertiesSource(props));
    assertEquals("number of items differ", 4, enm.values().length);
    assertEquals("items differ", enm.values()[0], enm.parse("A"));
    assertEquals("items differ", enm.values()[0], enm.parse("IDA"));
    assertEquals("items differ", enm.values()[1], enm.parse("B"));
    assertEquals("items differ", enm.values()[2], enm.parse("C"));
    assertEquals("items differ", enm.values()[3], enm.parse("D"));
  }

  /**
   * Tests the parsing of strings.
   */
  public void testParsing() {
    String[] labels = new String[]{"A","B","C","D"};
    ConfigurableEnumeration enm = new ConfigurableEnumeration(new FixedSource(labels));
    String s = "A";
    assertNotNull("failed to locate: " + s, enm.parse(s));
    s = "B";
    assertNotNull("failed to locate: " + s, enm.parse(s));
    s = "C";
    assertNotNull("failed to locate: " + s, enm.parse(s));
    s = "D";
    assertNotNull("failed to locate: " + s, enm.parse(s));
    s = "E";
    assertNull("located: " + s, enm.parse(s));
  }

  /**
   * Performs a serializable test on the given class.
   *
   * @param cls		the class to test
   */
  protected void performSerializableTest(Class cls) {
    String[] labels = new String[]{"A","B","C","D"};
    ConfigurableEnumeration enm = new ConfigurableEnumeration(new FixedSource(labels));
    assertNotNull("Serialization failed", Utils.deepCopy(enm));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ConfigurableEnumerationTest.class);
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
