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
 * SortedListTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import java.io.Serializable;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.SortedList class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9527 $
 */
public class SortedListTest
  extends AdamsTestCase {

  /** the list to use. */
  protected SortedList m_List;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SortedListTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. This implementation creates
   * the default list object.
   *
   * @throws Exception 	if set up fails
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_List = new SortedList();
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception 	if something goes wrong
   */
  @Override
  protected void tearDown() throws Exception {
    m_List.clear();
    m_List = null;

    super.tearDown();
  }

  /**
   * Performs the actual test.
   *
   * @param insert	the data to insert in the list
   * @param expected	the expected output
   */
  protected void performTest(Comparable[] insert, Comparable[] expected) {
    assertEquals("input and output arrays length differ", expected.length, insert.length);

    // add elements
    for (Comparable c: insert)
      m_List.add((Serializable) c);
    assertEquals("number of elements in list are different from expected", expected.length, m_List.size());

    // check elements
    for (int i = 0; i < insert.length; i++)
      assertEquals("element #" + (i + 1) + " differs", expected[i], m_List.get(i));
  }

  /**
   * Tests the sorting when inserting integers that are already sorted.
   */
  public void testSortedIntegers() {
    performTest(new Integer[]{1, 2, 3}, new Integer[]{1, 2, 3});
  }

  /**
   * Tests the sorting when inserting integers that are not yet sorted.
   */
  public void testUnsortedIntegers() {
    performTest(new Integer[]{2, 4, 3, 1}, new Integer[]{1, 2, 3, 4});
  }

  /**
   * Tests the sorting when inserting duplicate integers that are not yet sorted.
   */
  public void testUnsortedDuplicateIntegers() {
    performTest(new Integer[]{2, 4, 2, 3, 1, 4}, new Integer[]{1, 2, 2, 3, 4, 4});
  }

  /**
   * Tests accessing the first string element.
   */
  public void testFirstInteger() {
    m_List.addAll(Arrays.asList(new Integer[]{2, 4, 2, 3, 1, 4}));
    assertEquals("first element differs", 1, m_List.get(0));
  }

  /**
   * Tests accessing the last string element.
   */
  public void testLastInteger() {
    m_List.addAll(Arrays.asList(new Integer[]{2, 4, 2, 3, 1, 4}));
    assertEquals("last element differs", 4, m_List.get(m_List.size()- 1));
  }

  /**
   * Tests the sorting when inserting doubles that are already sorted.
   */
  public void testSortedDoubles() {
    performTest(new Double[]{1.0, 2.0, 3.0}, new Double[]{1.0, 2.0, 3.0});
  }

  /**
   * Tests the sorting when inserting doubles that are not yet sorted.
   */
  public void testUnsortedDoubles() {
    performTest(new Double[]{2.0, 4.0, 3.0, 1.0}, new Double[]{1.0, 2.0, 3.0, 4.0});
  }

  /**
   * Tests the sorting when inserting duplicate doubles that are not yet sorted.
   */
  public void testUnsortedDuplicateDoubles() {
    performTest(new Double[]{2.0, 4.0, 2.0, 3.0, 1.0, 4.0}, new Double[]{1.0, 2.0, 2.0, 3.0, 4.0, 4.0});
  }

  /**
   * Tests accessing the first string element.
   */
  public void testFirstDouble() {
    m_List.addAll(Arrays.asList(new Double[]{2.0, 4.0, 2.0, 3.0, 1.0, 4.0}));
    assertEquals("first element differs", 1.0, m_List.get(0));
  }

  /**
   * Tests accessing the last string element.
   */
  public void testLastDouble() {
    m_List.addAll(Arrays.asList(new Double[]{2.0, 4.0, 2.0, 3.0, 1.0, 4.0}));
    assertEquals("last element differs", 4.0, m_List.get(m_List.size()- 1));
  }

  /**
   * Tests the sorting when inserting strings that are already sorted.
   */
  public void testSortedStrings() {
    performTest(new String[]{"1", "2", "3"}, new String[]{"1", "2", "3"});
  }

  /**
   * Tests the sorting when inserting strings that are not yet sorted.
   */
  public void testUnsortedStrings() {
    performTest(new String[]{"2", "4", "3", "1"}, new String[]{"1", "2", "3", "4"});
  }

  /**
   * Tests the sorting when inserting duplicate strings that are not yet sorted.
   */
  public void testUnsortedDuplicateStrings() {
    performTest(new String[]{"2", "4", "2", "3", "1", "4"}, new String[]{"1", "2", "2", "3", "4", "4"});
  }

  /**
   * Tests accessing the first string element.
   */
  public void testFirstString() {
    m_List.addAll(Arrays.asList(new String[]{"2", "4", "2", "3", "1", "4"}));
    assertEquals("first element differs", "1", m_List.get(0));
  }

  /**
   * Tests accessing the last string element.
   */
  public void testLastString() {
    m_List.addAll(Arrays.asList(new String[]{"2", "4", "2", "3", "1", "4"}));
    assertEquals("last element differs", "4", m_List.get(m_List.size()- 1));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SortedListTest.class);
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
