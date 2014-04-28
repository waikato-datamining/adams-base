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
 * ByteFormatTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.ByteFormat;
import adams.core.Utils;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.ByteFormat class. Run from commandline with: <p/>
 * java adams.core.ByteFormatTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByteFormatTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ByteFormatTest(String name) {
    super(name);
  }

  /**
   * Performs a serializable test on the given class.
   *
   * @param cls		ignored
   */
  protected void performSerializableTest(Class cls) {
    assertNotNull("Serialization failed", Utils.deepCopy(new ByteFormat("BK")));
  }

  /**
   * Tests an empty format.
   */
  public void testEmptyFormat() {
    ByteFormat b = new ByteFormat("");
    assertNull("format is not null", b.getFormat());
  }

  /**
   * Tests an invalid format.
   */
  public void testInvalidFormat() {
    ByteFormat b = new ByteFormat("hello world");
    assertNull("format is not null", b.getFormat());
  }

  /**
   * Tests kilo bytes.
   */
  public void testKiloBytes() {
    ByteFormat b = new ByteFormat("bk");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1024));
    assertEquals("output differs", "2", b.format(2048));
    assertEquals("output differs", "2", b.format(2200));

    b = new ByteFormat("bK");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1KB", b.format(1024));
    assertEquals("output differs", "2KB", b.format(2048));
    assertEquals("output differs", "2KB", b.format(2200));

    b = new ByteFormat("b.1K");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0KB", b.format(1024));
    assertEquals("output differs", "2.0KB", b.format(2048));
    assertEquals("output differs", "2.1KB", b.format(2200));

    b = new ByteFormat("b.2K");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.00KB", b.format(1024));
    assertEquals("output differs", "2.00KB", b.format(2048));
    assertEquals("output differs", "2.14KB", b.format(2200));
  }

  /**
   * Tests mega bytes.
   */
  public void testMegaBytes() {
    ByteFormat b = new ByteFormat("bm");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1048576));
    assertEquals("output differs", "2", b.format(2097152));
    assertEquals("output differs", "2", b.format(2252800));

    b = new ByteFormat("bM");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1MB", b.format(1048576));
    assertEquals("output differs", "2MB", b.format(2097152));
    assertEquals("output differs", "2MB", b.format(2252800));

    b = new ByteFormat("b.1M");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0MB", b.format(1048576));
    assertEquals("output differs", "2.0MB", b.format(2097152));
    assertEquals("output differs", "2.1MB", b.format(2252800));

    b = new ByteFormat("b.2M");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.00MB", b.format(1048576));
    assertEquals("output differs", "2.00MB", b.format(2097152));
    assertEquals("output differs", "2.14MB", b.format(2252800));
  }

  /**
   * Tests giga bytes.
   */
  public void testGigaBytes() {
    ByteFormat b = new ByteFormat("bg");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1073741824.0));
    assertEquals("output differs", "2", b.format(2147483648.0));
    assertEquals("output differs", "2", b.format(2306867200.0));

    b = new ByteFormat("bG");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1GB", b.format(1073741824.0));
    assertEquals("output differs", "2GB", b.format(2147483648.0));
    assertEquals("output differs", "2GB", b.format(2306867200.0));

    b = new ByteFormat("b.1G");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0GB", b.format(1073741824.0));
    assertEquals("output differs", "2.0GB", b.format(2147483648.0));
    assertEquals("output differs", "2.1GB", b.format(2306867200.0));

    b = new ByteFormat("b.2G");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.00GB", b.format(1073741824.0));
    assertEquals("output differs", "2.00GB", b.format(2147483648.0));
    assertEquals("output differs", "2.14GB", b.format(2306867200.0));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ByteFormatTest.class);
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
