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
 * VariablesTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.Variables class. Run from commandline with: <p/>
 * java adams.core.VariablesTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariablesTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public VariablesTest(String name) {
    super(name);
  }

  /**
   * Tests the isValidName(String) method.
   */
  public void testIsValidName() {
    String s;
    s = "@{hello}";
    assertTrue(s, Variables.isValidName(s));
    s = "@{hello-world}";
    assertTrue(s, Variables.isValidName(s));
    s = "@{hello_world}";
    assertTrue(s, Variables.isValidName(s));
    s = "@{HELLO_world}";
    assertTrue(s, Variables.isValidName(s));
    s = "@{hello_world_YOU_2}";
    assertTrue(s, Variables.isValidName(s));
    s = "@{2_for_1}";
    assertTrue(s, Variables.isValidName(s));
    s = "@{!hello}";
    assertFalse(s, Variables.isValidName(s));
    s = "@{hello world}";
    assertFalse(s, Variables.isValidName(s));
    s = "@{hello:world}";
    assertTrue(s, Variables.isValidName(s));
  }

  /**
   * Tests the createValidName(String) method.
   */
  public void testCreateValidName() {
    String s;
    s = "@{hello}";
    assertEquals("hello", Variables.createValidName(s));
    s = "@{hello-world}";
    assertEquals("hello-world", Variables.createValidName(s));
    s = "@{hello_world}";
    assertEquals("hello_world", Variables.createValidName(s));
    s = "@{HELLO_world}";
    assertEquals("HELLO_world", Variables.createValidName(s));
    s = "@{hello_world_YOU_2}";
    assertEquals("hello_world_YOU_2", Variables.createValidName(s));
    s = "@{2_for_1}";
    assertEquals("2_for_1", Variables.createValidName(s));
    s = "@{!hello}";
    assertEquals("_hello", Variables.createValidName(s));
    s = "@{hello world}";
    assertEquals("hello_world", Variables.createValidName(s));
    s = "@{hello:world}";
    assertEquals("hello:world", Variables.createValidName(s));
  }

  /**
   * Tests the isPlaceholder(String) method.
   */
  public void testIsPlaceholder() {
    String s;
    s = "@{hello}";
    assertTrue(s, Variables.isPlaceholder(s));
    s = "@{hello-world}";
    assertTrue(s, Variables.isPlaceholder(s));
    s = "@{hello_world}";
    assertTrue(s, Variables.isPlaceholder(s));
    s = "@{hello";
    assertFalse(s, Variables.isPlaceholder(s));
    s = "@ {hello}";
    assertFalse(s, Variables.isPlaceholder(s));
    s = "{hello}";
    assertFalse(s, Variables.isPlaceholder(s));
    s = "hello}";
    assertFalse(s, Variables.isPlaceholder(s));
    s = "hello";
    assertFalse(s, Variables.isPlaceholder(s));
    s = "hello world";
    assertFalse(s, Variables.isPlaceholder(s));
  }

  /**
   * Tests the extractName(String) method.
   */
  public void testExtractName() {
    String s;
    s = "@{hello}";
    assertEquals(s, "hello", Variables.extractName(s));
    s = "hello";
    assertEquals(s, "hello", Variables.extractName(s));
    s = "@{hello";
    assertEquals(s, "@{hello", Variables.extractName(s));
    s = "@ {hello}";
    assertEquals(s, "@ {hello}", Variables.extractName(s));
    s = "@hello}";
    assertEquals(s, "@hello}", Variables.extractName(s));
    s = "hello}";
    assertEquals(s, "hello}", Variables.extractName(s));
  }

  /**
   * Tests the padName(String) method.
   */
  public void testPadName() {
    String s;
    s = "@{hello}";
    assertEquals(s, "@{hello}", Variables.padName(s));
    s = "hello";
    assertEquals(s, "@{hello}", Variables.padName(s));
  }

  /**
   * Tests the clear() method.
   */
  public void testClear() {
    Variables v = new Variables();
    v.set("hello", "world");
    assertEquals("size filled", 1, v.size());
    v.clear();
    assertEquals("size filled", 0, v.size());
  }

  /**
   * Tests the set(), has() and get() methods.
   */
  public void testSetGetHas() {
    Variables v = new Variables();

    assertFalse(v.has("hello"));
    v.set("hello", "world");
    assertEquals("size after set", 1, v.size());
    assertTrue(v.has("hello"));
    assertEquals("get after set", "world", v.get("hello"));

    v.set("hello", "world2");
    assertEquals("size after same set", 1, v.size());
    assertTrue(v.has("hello"));
    assertEquals("get after set", "world2", v.get("hello"));

    assertFalse(v.has("hello2"));
    v.set("hello2", "world3");
    assertEquals("size after another set", 2, v.size());
    assertTrue(v.has("hello2"));
    assertEquals("get after set", "world2", v.get("hello"));
    assertEquals("get after set", "world3", v.get("hello2"));
  }

  /**
   * Tests the remove(String) and remove(BaseRegExp) methods.
   */
  public void testRemove() {
    Variables v = new Variables();
    v.set("hello", "world");
    v.set("hello2", "world2");
    v.set("hello3", "world3");
    assertEquals("size after initial sets", 3, v.size());
    v.remove("blah");
    assertEquals("size after unsuccessful remove", 3, v.size());
    v.remove("hello");
    assertEquals("size after successful remove", 2, v.size());
    v.set("hello", "world");
    v.remove(new BaseRegExp("hello[0-9]"));
    assertEquals("size after successful remove/regexp", 1, v.size());
  }

  /**
   * Tests the expand(String) method.
   */
  public void testExpand() {
    Variables v = new Variables();
    v.set("hello", "world");
    v.set("hello2", "world2");
    v.set("hello3", "world3");
    String s;
    s = "hello";
    assertEquals(s, "hello", v.expand(s));
    s = "@{hello2}";
    assertEquals(s, "world2", v.expand(s));
    s = " @{hello2} / @{hello3}";
    assertEquals(s, " world2 / world3", v.expand(s));
    s = " @{hello2} / @{hello3} / @ {hello2} / {hello3} / @{hello / @hello3";
    assertEquals(s, " world2 / world3 / @ {hello2} / {hello3} / @{hello / @hello3", v.expand(s));
    s = "@{hello2}@{hello3}";
    assertEquals(s, "world2world3", v.expand(s));
  }

  /**
   * Tests the getClone() method.
   */
  public void testGetClone() {
    Variables v = new Variables();
    v.set("hello", "world");
    v.set("hello2", "world2");
    v.set("hello3", "world3");
    Variables vc = v.getClone();
    assertEquals("size after clone", v.size(), vc.size());

    Enumeration<String> names = v.names();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      assertTrue("cloned variable: " + name, vc.has(name));
      assertEquals("cloned variable: " + name, v.get(name), vc.get(name));
    }

    int size = v.size();
    v.clear();
    assertEquals("size after clear() of original (clone)", size, vc.size());
    assertEquals("size after clear() of original (original)", 0, v.size());
  }
  
  /**
   * Compares the expeced and extracted variable names.
   * 
   * @param expected	the expected variable names
   * @param current	the extract variable names
   * @see #testExtractNames()
   */
  protected void compareVariableNames(String[] expected, String[] current) {
    assertEquals("# of vars differ", expected.length, current.length);
    for (int i = 0; i < expected.length; i++)
      assertEquals("variable #" + i + " differs", expected[i], current[i]);
  }
  
  /**
   * Tests the {@link Variables#extractNames(String)} method. 
   */
  public void testExtractNames() {
    String expr = "hello world";
    String[] vars = new String[]{};
    compareVariableNames(vars, Variables.extractNames(expr));
    
    expr = "@{blah}";
    vars = new String[]{"blah"};
    compareVariableNames(vars, Variables.extractNames(expr));
    
    expr = " some @{blah} where";
    vars = new String[]{"blah"};
    compareVariableNames(vars, Variables.extractNames(expr));
    
    expr = "@{one} some @{blah} where @{another} ladeedah";
    vars = new String[]{"one", "blah", "another"};
    compareVariableNames(vars, Variables.extractNames(expr));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(VariablesTest.class);
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
