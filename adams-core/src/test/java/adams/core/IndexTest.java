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
 * IndexTest.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.Index class. Run from commandline with: <br><br>
 * java adams.core.IndexTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IndexTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IndexTest(String name) {
    super(name);
  }

  /**
   * Returns a new default Index instance.
   * 
   * @return		the new instance
   */
  protected Index newIndex() {
    return newIndex(null);
  }

  /**
   * Returns a new Index instance with no maximum set.
   * 
   * @param index	the index to use, null for default constructor
   * @return		the new instance
   */
  protected Index newIndex(String index) {
    return newIndex(index, -1);
  }

  /**
   * Returns a new Index instance.
   * 
   * @param index	the index to use, null for default constructor
   * @param max		the maximum to use
   * @return		the new instance
   */
  protected Index newIndex(String index, int max) {
    if (index == null)
      return new Index();
    else
      return new Index(index, max);
  }
  
  /**
   * Tests "hasIndex".
   */
  public void testHasIndex() {
    Index	index;

    index = newIndex();
    assertFalse(index.hasIndex());

    index = newIndex("");
    assertFalse(index.hasIndex());

    index = newIndex("1");
    assertTrue(index.hasIndex());

    index = newIndex("1", 10);
    assertTrue(index.hasIndex());

    index = newIndex("100", 10);
    assertTrue(index.hasIndex());
  }

  /**
   * Tests "first".
   */
  public void testFirst() {
    Index	index;

    index = newIndex(Index.FIRST, 10);
    assertEquals(0, index.getIntIndex());
  }

  /**
   * Tests "second".
   */
  public void testSecond() {
    Index	index;

    index = newIndex(Index.SECOND, 10);
    assertEquals(1, index.getIntIndex());
  }

  /**
   * Tests "third".
   */
  public void testThird() {
    Index	index;

    index = newIndex(Index.THIRD, 10);
    assertEquals(2, index.getIntIndex());
  }

  /**
   * Tests "first", not-lowercase.
   */
  public void testFirstMixed() {
    Index	index;

    index = newIndex("First", 10);
    assertEquals(0, index.getIntIndex());
  }

  /**
   * Tests "second", not-lowercase.
   */
  public void testSecondMixed() {
    Index	index;

    index = newIndex("Second", 10);
    assertEquals(1, index.getIntIndex());
  }

  /**
   * Tests "third", not-lowercase.
   */
  public void testThirdMixed() {
    Index	index;

    index = newIndex("Third", 10);
    assertEquals(2, index.getIntIndex());
  }

  /**
   * Tests "last_2".
   */
  public void testLastMinus2() {
    Index	index;

    index = newIndex(Index.LAST_2, 10);
    assertEquals(index.getMax() - 3, index.getIntIndex());
  }

  /**
   * Tests "last_1".
   */
  public void testLastMinus1() {
    Index	index;

    index = newIndex(Index.LAST_1, 10);
    assertEquals(index.getMax() - 2, index.getIntIndex());
  }

  /**
   * Tests "last".
   */
  public void testLast() {
    Index	index;

    index = newIndex(Index.LAST, 10);
    assertEquals(index.getMax() - 1, index.getIntIndex());
  }

  /**
   * Tests "last_2", not-lowercase.
   */
  public void testLastMinus2Mixed() {
    Index	index;

    index = newIndex("Last_2", 10);
    assertEquals(index.getMax() - 3, index.getIntIndex());
  }

  /**
   * Tests "last_1", not-lowercase.
   */
  public void testLastMinus1Mixed() {
    Index	index;

    index = newIndex("Last_1", 10);
    assertEquals(index.getMax() - 2, index.getIntIndex());
  }

  /**
   * Tests "last", not-lowercase.
   */
  public void testLastMixed() {
    Index	index;

    index = newIndex("Last", 10);
    assertEquals(index.getMax() - 1, index.getIntIndex());
  }

  /**
   * Tests a number.
   */
  public void testNumber() {
    Index	index;

    index = newIndex("3", 10);
    assertEquals(2, index.getIntIndex());

    index = newIndex("#3", 10);
    assertEquals(2, index.getIntIndex());
  }

  /**
   * Tests an invalid index.
   */
  public void testInvalidIndex() {
    Index	index;

    index = newIndex("blah", 10);
    assertEquals(-1, index.getIntIndex());
  }

  /**
   * Tests an no maximum.
   */
  public void testNoMax() {
    Index	index;

    index = newIndex(Index.FIRST);
    assertEquals(-1, index.getIntIndex());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(IndexTest.class);
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
