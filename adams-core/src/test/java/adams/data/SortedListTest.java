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

import java.util.Arrays;
import java.util.Comparator;

import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.SortedList class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9527 $
 */
public class SortedListTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SortedListTest(String name) {
    super(name);
  }
  
  /**
   * Tests simple inserting.
   */
  public void testInserting() {
    SortedList<Integer> list = new SortedList<Integer>();
    list.add(10);
    list.add(3);
    list.add(50);
    Integer[] expected = new Integer[]{3, 10, 50};
    assertEquals("size differs", expected.length, list.size());
    for (int i = 0; i < list.size(); i++)
      assertEquals("item #" + i + " differs", expected[i], list.get(i));
  }
  
  /**
   * Tests the constructor that takes collection.
   */
  public void testCollectionConstructor() {
    Integer[] expected = new Integer[]{3, 10, 50};
    SortedList<Integer> list = new SortedList<Integer>(Arrays.asList(expected));
    assertEquals("size differs", expected.length, list.size());
    for (int i = 0; i < list.size(); i++)
      assertEquals("item #" + i + " differs", expected[i], list.get(i));
  }
  
  /**
   * Tests the addAll method.
   */
  public void testAddAll() {
    Integer[] expected = new Integer[]{3, 10, 50};
    SortedList<Integer> list = new SortedList<Integer>();
    list.addAll(Arrays.asList(expected));
    assertEquals("size differs", expected.length, list.size());
    for (int i = 0; i < list.size(); i++)
      assertEquals("item #" + i + " differs", expected[i], list.get(i));
  }
  
  /**
   * Tests the removeAll method.
   */
  public void testRemoveAll() {
    Integer[] expected = new Integer[]{3, 10, 50};
    SortedList<Integer> list = new SortedList<Integer>();
    list.addAll(Arrays.asList(expected));
    assertEquals("size differs", expected.length, list.size());
    list.removeAll(Arrays.asList(expected));
    assertEquals("size differs", 0, list.size());
  }
  
  /**
   * Tests a custom comparator.
   */
  public void testCustomComparator() {
    SortedList<Integer> list = new SortedList<Integer>(new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
        return -o1.compareTo(o2);
      }
    });
    list.add(10);
    list.add(3);
    list.add(50);
    Integer[] expected = new Integer[]{50, 10, 3};
    assertEquals("size differs", expected.length, list.size());
    for (int i = 0; i < list.size(); i++)
      assertEquals("item #" + i + " differs", expected[i], list.get(i));
  }
}
