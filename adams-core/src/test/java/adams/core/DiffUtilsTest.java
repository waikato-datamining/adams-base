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
 * DiffUtilsTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.DiffUtils.Filler;
import adams.core.DiffUtils.SideBySideDiff;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.DiffUtils class. Run from commandline with: <br><br>
 * java adams.core.DiffUtilsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DiffUtilsTest
  extends AdamsTestCase {

  /** the indicator for "changed" (shortcut). */
  public final static char CHANGED = DiffUtils.INDICATOR_CHANGED;

  /** the indicator for "added" (shortcut). */
  public final static char ADDED = DiffUtils.INDICATOR_ADDED;

  /** the indicator for "deleted" (shortcut). */
  public final static char DELETED = DiffUtils.INDICATOR_DELETED;

  /** the indicator for "same" (shortcut). */
  public final static char SAME = DiffUtils.INDICATOR_SAME;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DiffUtilsTest(String name) {
    super(name);
  }

  /**
   * Performs a test of the unified(List,List) method.
   * 
   * @param s1		the first string array (original)
   * @param s2		the second string array (modified)
   * @param expected	the expected output
   */
  protected void performUnifiedTest(String[] s1, String[] s2, String expected) {
    ArrayList<String> list1 = new ArrayList(Arrays.asList(s1));
    ArrayList<String> list2 = new ArrayList(Arrays.asList(s2));
    assertEquals(expected, DiffUtils.unified(list1, list2));
  }
  
  /**
   * Tests the unified(List,List) method.
   */
  public void testUnified() {
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"1", "2", "3"}, "");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"1", "2", "4"}, "3c3\n< 3\n---\n> 4\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"1", "2", "2a", "3"}, "2a3\n> 2a\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"1", "2", "2a", "2b", "3"}, "2a3,4\n> 2a\n> 2b\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"1", "3"}, "2d1\n< 2\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"1", "2"}, "3d2\n< 3\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"2", "3"}, "1d0\n< 1\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"3"}, "1,2d0\n< 1\n< 2\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"3", "4"}, "1,2d0\n< 1\n< 2\n3a2\n> 4\n");
    performUnifiedTest(new String[]{"1", "2", "3"}, new String[]{"3a", "4"}, "1,3c1,2\n< 1\n< 2\n< 3\n---\n> 3a\n> 4\n");
  }

  /**
   * Performs a test of the unified(List,List) method.
   * 
   * @param s1		the first string array (original)
   * @param s2		the second string array (modified)
   * @param orig	the expected output for the original list
   * @param mod		the expected output for the modified list
   * @pa4am ind		the expected output for the indicator list
   */
  protected void performSideBySideTest(String[] s1, String[] s2, List orig, List mod, List ind) {
    ArrayList<String> list1 = new ArrayList(Arrays.asList(s1));
    ArrayList<String> list2 = new ArrayList(Arrays.asList(s2));
    SideBySideDiff diff = DiffUtils.sideBySide(list1, list2);
    assertEquals(orig, diff.getLeft());
    assertEquals(mod,  diff.getRight());
    assertEquals(ind,  diff.getIndicator());
  }
  
  /**
   * Tests the sideBySide(List,List) method.
   */
  public void testSideBySide() {
    Filler fill = new Filler();
    
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"1", "2", "3"},
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{SAME, SAME, SAME})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"1", "2", "4"}, 
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "4"})),
	new ArrayList(Arrays.asList(new Object[]{SAME, SAME, CHANGED})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"1", "2", "2a", "3"},
	new ArrayList(Arrays.asList(new Object[]{"1", "2", fill, "3"})),
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "2a", "3"})),
	new ArrayList(Arrays.asList(new Object[]{SAME, SAME, ADDED, SAME})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"1", "2", "2a", "2b", "3"},
	new ArrayList(Arrays.asList(new Object[]{"1", "2", fill, fill, "3"})),
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "2a", "2b", "3"})),
	new ArrayList(Arrays.asList(new Object[]{SAME, SAME, ADDED, ADDED, SAME})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"1", "3"}, 
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{"1", fill, "3"})),
	new ArrayList(Arrays.asList(new Object[]{SAME, DELETED, SAME})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"1", "2"}, 
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{"1", "2", fill})),
	new ArrayList(Arrays.asList(new Object[]{SAME, SAME, DELETED})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"2", "3"}, 
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{fill, "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{DELETED, SAME, SAME})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"3"},
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{fill, fill, "3"})),
	new ArrayList(Arrays.asList(new Object[]{DELETED, DELETED, SAME})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"3", "4"}, 
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3", fill})),
	new ArrayList(Arrays.asList(new Object[]{fill, fill, "3", "4"})),
	new ArrayList(Arrays.asList(new Object[]{DELETED, DELETED, SAME, ADDED})));
    performSideBySideTest(
	new String[]{"1", "2", "3"}, 
	new String[]{"3a", "4"}, 
	new ArrayList(Arrays.asList(new Object[]{"1", "2", "3"})),
	new ArrayList(Arrays.asList(new Object[]{"3a", "4", fill})),
	new ArrayList(Arrays.asList(new Object[]{CHANGED, CHANGED, CHANGED})));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DiffUtilsTest.class);
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
