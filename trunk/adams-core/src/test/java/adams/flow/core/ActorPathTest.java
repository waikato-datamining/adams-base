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
 * ActorPathTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.core.ActorPath;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.flow.core.ActorPath class. Run from commandline with: <p/>
 * java adams.flow.core.ActorPathTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorPathTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ActorPathTest(String name) {
    super(name);
  }

  /**
   * Tests single path element.
   */
  public static void testSingleElement() {
    ActorPath path = new ActorPath("hello");
    assertEquals("Path length differs", 1, path.getPathCount());
    assertEquals("Path element differs", "hello", path.getPathComponent(0));
  }

  /**
   * Tests multiple path elements.
   */
  public static void testMultipleElements() {
    ActorPath path = new ActorPath("hello.world.out.there");
    assertEquals("Path length differs", 4, path.getPathCount());
    assertEquals("Path element 1 differs", "hello", path.getPathComponent(0));
    assertEquals("Path element 2 differs", "world", path.getPathComponent(1));
    assertEquals("Path element 3 differs", "out", path.getPathComponent(2));
    assertEquals("Path element 4 differs", "there", path.getPathComponent(3));
  }

  /**
   * Tests "blank" in element name.
   */
  public static void testBlankInElement() {
    ActorPath path = new ActorPath("hello world.out.there");
    assertEquals("Path length differs", 3, path.getPathCount());
    assertEquals("Path element 1 differs", "hello world", path.getPathComponent(0));
    assertEquals("Path element 2 differs", "out", path.getPathComponent(1));
    assertEquals("Path element 3 differs", "there", path.getPathComponent(2));
  }

  /**
   * Tests "dot" in element name.
   */
  public static void testDotInElement() {
    ActorPath path = new ActorPath("hello\\.world.out.there");
    assertEquals("Path length differs", 3, path.getPathCount());
    assertEquals("Path element 1 differs", "hello.world", path.getPathComponent(0));
    assertEquals("Path element 2 differs", "out", path.getPathComponent(1));
    assertEquals("Path element 3 differs", "there", path.getPathComponent(2));
  }

  /**
   * Tests parsing path from console output.
   * <p/>
   * Example: [Flow.ContainerValuePicker/700475288-DEBUG]
   */
  public static void testConsoleString() {
    ActorPath path = new ActorPath("[Flow.ContainerValuePicker/700475288-DEBUG]");
    assertEquals("Path length differs", 2, path.getPathCount());
    assertEquals("Path element 1 differs", "Flow", path.getPathComponent(0));
    assertEquals("Path element 2 differs", "ContainerValuePicker", path.getPathComponent(1));
  }

  /**
   * Tests "toString" method.
   */
  public static void testToString() {
    String s = "hello\\.world.out.there";
    ActorPath path = new ActorPath(s);
    assertEquals("Path differs", s, path.toString());

    s = "hello.world.out.there";
    path = new ActorPath(s);
    assertEquals("Path differs", s, path.toString());

    s = "hello";
    path = new ActorPath(s);
    assertEquals("Path differs", s, path.toString());

    s = "hel lo";
    path = new ActorPath(s);
    assertEquals("Path differs", s, path.toString());

    s = "hello world.out.there";
    path = new ActorPath(s);
    assertEquals("Path differs", s, path.toString());
  }

  /**
   * Tests "isDescendant" method.
   */
  public static void testDescendant() {
    ActorPath path1 = new ActorPath("a.b");

    ActorPath path2 = new ActorPath("a.b.c");
    assertEquals("isDescendant failed", true, path1.isDescendant(path2));

    path2 = new ActorPath("a");
    assertEquals("isDescendant failed", false, path1.isDescendant(path2));

    path2 = new ActorPath("a.b");
    assertEquals("isDescendant failed", true, path1.isDescendant(path2));
  }

  /**
   * Tests "getCommonAncestor" method.
   */
  public static void testCommonAncestor() {
    ActorPath path1 = new ActorPath("a.b");

    ActorPath path2 = new ActorPath("a.b.c");
    assertEquals("getCommonAncestor failed", "a.b", path1.getCommonAncestor(path2).toString());

    path2 = new ActorPath("a");
    assertEquals("getCommonAncestor failed", "a", path1.getCommonAncestor(path2).toString());

    path2 = new ActorPath("a.b");
    assertEquals("getCommonAncestor failed", "a.b", path1.getCommonAncestor(path2).toString());

    path2 = new ActorPath("b");
    assertEquals("getCommonAncestor failed", "", path1.getCommonAncestor(path2).toString());

    path2 = new ActorPath("b.c.d");
    assertEquals("getCommonAncestor failed", "", path1.getCommonAncestor(path2).toString());
  }

  /**
   * Tests "compareTo" method.
   */
  public static void testCompareTo() {
    ActorPath path1 = new ActorPath("a.b");

    ActorPath path2 = new ActorPath("a.b.c");
    assertEquals("compareTo failed (" + path1 + ", " + path2 + ")", true, (path1.compareTo(path2) < 0));

    path2 = new ActorPath("a.b");
    assertEquals("compareTo failed (" + path1 + ", " + path2 + ")", true, (path1.compareTo(path2) == 0));

    path2 = new ActorPath("a.c");
    assertEquals("compareTo failed (" + path1 + ", " + path2 + ")", true, (path1.compareTo(path2) < 0));

    path2 = new ActorPath("b.c");
    assertEquals("compareTo failed (" + path1 + ", " + path2 + ")", true, (path1.compareTo(path2) < 0));

    path2 = new ActorPath("a.a.c");
    assertEquals("compareTo failed (" + path1 + ", " + path2 + ")", true, (path1.compareTo(path2) > 0));
  }

  /**
   * Tests "equals" method.
   */
  public static void testEquals() {
    ActorPath path1 = new ActorPath("a.b");

    ActorPath path2 = new ActorPath("a.b.c");
    assertEquals("equals failed (" + path1 + ", " + path2 + ")", false, path1.equals(path2));

    path2 = new ActorPath("a.b");
    assertEquals("equals failed (" + path1 + ", " + path2 + ")", true, path1.equals(path2));

    assertEquals("equals failed (integer)", false, path1.equals(new Integer(3)));
  }

  /**
   * Tests "getFirstPathComponent" method.
   */
  public static void testFirstPathComponent() {
    ActorPath path = new ActorPath("a.b");
    assertEquals("getFirstPathComponent failed (" + path + ")", "a", path.getFirstPathComponent());

    path = new ActorPath("a");
    assertEquals("getFirstPathComponent failed (" + path + ")", "a", path.getFirstPathComponent());

    path = new ActorPath("");
    assertEquals("getFirstPathComponent failed (" + path + ")", null, path.getFirstPathComponent());
  }

  /**
   * Tests "getLastPathComponent" method.
   */
  public static void testLastPathComponent() {
    ActorPath path = new ActorPath("a.b");
    assertEquals("getLastPathComponent failed (" + path + ")", "b", path.getLastPathComponent());

    path = new ActorPath("a");
    assertEquals("getLastPathComponent failed (" + path + ")", "a", path.getLastPathComponent());

    path = new ActorPath("");
    assertEquals("getLastPathComponent failed (" + path + ")", null, path.getLastPathComponent());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ActorPathTest.class);
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
