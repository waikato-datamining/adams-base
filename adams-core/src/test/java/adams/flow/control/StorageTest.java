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
 * StorageTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.control.Storage;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.flow.control.Storage class. Run from commandline with: <p/>
 * java adams.flow.control.StorageTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StorageTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StorageTest(String name) {
    super(name);
  }

  /**
   * Tests the clear() method.
   */
  public void testClear() {
    Storage s = new Storage();
    s.put(new StorageName("blah"), 1);
    assertEquals("storage size > 0", 1, s.size());
    s.clear();
    assertEquals("storage size = 0", 0, s.size());
  }

  /**
   * Tests the put(), has() and get() methods (no cache).
   */
  public void testPutGetHasNoCache() {
    Storage s = new Storage();
    s.put(new StorageName("blah"), 1);
    assertEquals("storage size > 0", 1, s.size());
    assertTrue(s.has(new StorageName("blah")));
    assertFalse(s.has(new StorageName("blah2")));
    assertEquals("get", 1, s.get(new StorageName("blah")));
    assertNull("get", s.get(new StorageName("blah3")));
  }

  /**
   * Tests the put(), has() and get() methods (cache).
   */
  public void testPutGetHasCache() {
    Storage s = new Storage();
    s.addCache("1", 10);
    s.put("1", new StorageName("blah"), 1);
    assertEquals("cache size > 0", 1, s.size("1"));
    assertTrue(s.has("1", new StorageName("blah")));
    assertFalse(s.has("1", new StorageName("blah2")));
    assertEquals("get", 1, s.get("1", new StorageName("blah")));
    assertNull("get", s.get("1", new StorageName("blah3")));

    for (int i = 2; i < 8; i++)
      s.put("1", new StorageName("blah" + i), i);
    assertEquals("cache size", 7, s.size("1"));
    assertTrue(s.has("1", new StorageName("blah")));

    for (int i = 8; i < 15; i++)
      s.put("1", new StorageName("blah" + i), i);
    assertEquals("max cache size reaced", 10, s.size("1"));
    assertFalse(s.has("1", new StorageName("blah")));
  }

  /**
   * Tests the remove() method (no cache).
   */
  public void testRemoveNoCache() {
    Storage s = new Storage();
    s.put(new StorageName("blah"), 1);
    s.put(new StorageName("blah2"), 1);
    s.put(new StorageName("blah3"), 1);
    assertEquals("storage size", 3, s.size());
    s.remove(new StorageName("blah4"));
    assertEquals("removed non-existing item", 3, s.size());
    s.remove(new StorageName("blah2"));
    assertEquals("removed existing item", 2, s.size());
  }

  /**
   * Tests the getClone() method (cache).
   */
  public void testGetClone() {
    Storage s = new Storage();
    s.put(new StorageName("blah"), 1);
    s.put(new StorageName("blah2"), 1);
    s.put(new StorageName("blah3"), 1);
    assertEquals("storage size", 3, s.size());

    s.addCache("1", 10);
    s.put("1", new StorageName("blah"), 1);
    s.put("1", new StorageName("blah2"), 1);
    s.put("1", new StorageName("blah3"), 1);
    assertEquals("storage size", 3, s.size("1"));

    Storage sc = s.getClone();
    assertEquals("storage size", 3, sc.size());
    assertEquals("storage size", 3, sc.size("1"));

    Set<StorageName> keys;
    keys = s.keySet();
    for (StorageName key: keys) {
      assertTrue("key exists: " + key, sc.has(key));
      assertEquals("value the same: " + key, s.get(key), sc.get(key));
    }

    keys = s.keySet("1");
    for (StorageName key: keys) {
      assertTrue("cache key exists: " + key, sc.has("1", key));
      assertEquals("cache value the same: " + key, s.get("1", key), sc.get("1", key));
    }
  }

  /**
   * Tests the remove() method (cache).
   */
  public void testRemoveCache() {
    Storage s = new Storage();
    s.addCache("1", 10);
    s.put("1", new StorageName("blah"), 1);
    s.put("1", new StorageName("blah2"), 1);
    s.put("1", new StorageName("blah3"), 1);
    assertEquals("storage size", 3, s.size("1"));
    s.remove("1", new StorageName("blah4"));
    assertEquals("removed non-existing item", 3, s.size("1"));
    s.remove("1", new StorageName("blah2"));
    assertEquals("removed existing item", 2, s.size("1"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StorageTest.class);
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
