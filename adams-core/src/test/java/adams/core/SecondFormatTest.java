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
 * SecondFormatTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.SecondFormat class. Run from commandline with: <br><br>
 * java adams.core.SecondFormatTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SecondFormatTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SecondFormatTest(String name) {
    super(name);
  }

  /**
   * Performs a serializable test on the given class.
   *
   * @param cls		ignored
   */
  protected void performSerializableTest(Class cls) {
    assertNotNull("Serialization failed", Utils.deepCopy(new SecondFormat("BK")));
  }

  /**
   * Tests an empty format.
   */
  public void testEmptyFormat() {
    SecondFormat b = new SecondFormat("");
    assertNull("format is not null", b.getFormat());
  }

  /**
   * Tests an invalid format.
   */
  public void testInvalidFormat() {
    SecondFormat b = new SecondFormat("hello world");
    assertNull("format is not null", b.getFormat());
  }

  /**
   * Tests seconds.
   */
  public void testSeconds() {
    SecondFormat b = new SecondFormat("ts");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1.024));
    assertEquals("output differs", "2", b.format(2.048));
    assertEquals("output differs", "2", b.format(2.200));

    b = new SecondFormat("tS");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1s", b.format(1.024));
    assertEquals("output differs", "2s", b.format(2.048));
    assertEquals("output differs", "2s", b.format(2.200));

    b = new SecondFormat("t.1S");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0s", b.format(1.024));
    assertEquals("output differs", "2.0s", b.format(2.048));
    assertEquals("output differs", "2.2s", b.format(2.200));

    b = new SecondFormat("t.2S");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.02s", b.format(1.024));
    assertEquals("output differs", "2.04s", b.format(2.048));
    assertEquals("output differs", "2.20s", b.format(2.200));
  }

  /**
   * Tests minutes.
   */
  public void testMinutes() {
    SecondFormat b = new SecondFormat("tm");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(60));
    assertEquals("output differs", "2", b.format(120));
    assertEquals("output differs", "2", b.format(140));

    b = new SecondFormat("tM");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1m", b.format(60));
    assertEquals("output differs", "2m", b.format(120));
    assertEquals("output differs", "2m", b.format(140));

    b = new SecondFormat("t.1M");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0m", b.format(60));
    assertEquals("output differs", "2.0m", b.format(120));
    assertEquals("output differs", "2.3m", b.format(140));

    b = new SecondFormat("t.2M");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.00m", b.format(60));
    assertEquals("output differs", "2.00m", b.format(120));
    assertEquals("output differs", "2.33m", b.format(140));
  }

  /**
   * Tests hours.
   */
  public void testHours() {
    SecondFormat b = new SecondFormat("th");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(60*60));
    assertEquals("output differs", "2", b.format(120*60));
    assertEquals("output differs", "2", b.format(140*60));

    b = new SecondFormat("tH");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1h", b.format(60*60));
    assertEquals("output differs", "2h", b.format(120*60));
    assertEquals("output differs", "2h", b.format(140*60));

    b = new SecondFormat("t.1H");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0h", b.format(60*60));
    assertEquals("output differs", "2.0h", b.format(120*60));
    assertEquals("output differs", "2.3h", b.format(140*60));

    b = new SecondFormat("t.2H");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.00h", b.format(60*60));
    assertEquals("output differs", "2.00h", b.format(120*60));
    assertEquals("output differs", "2.33h", b.format(140*60));
  }

  /**
   * Tests days.
   */
  public void testDays() {
    SecondFormat b = new SecondFormat("td");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(60*60*24));
    assertEquals("output differs", "2", b.format(120*60*24));
    assertEquals("output differs", "2", b.format(140*60*24));

    b = new SecondFormat("tD");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1d", b.format(60*60*24));
    assertEquals("output differs", "2d", b.format(120*60*24));
    assertEquals("output differs", "2d", b.format(140*60*24));

    b = new SecondFormat("t.1D");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0d", b.format(60*60*24));
    assertEquals("output differs", "2.0d", b.format(120*60*24));
    assertEquals("output differs", "2.3d", b.format(140*60*24));

    b = new SecondFormat("t.2D");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.00d", b.format(60*60*24));
    assertEquals("output differs", "2.00d", b.format(120*60*24));
    assertEquals("output differs", "2.33d", b.format(140*60*24));
  }

  /**
   * Tests weeks.
   */
  public void testWeeks() {
    SecondFormat b = new SecondFormat("tw");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(60*60*24*7));
    assertEquals("output differs", "2", b.format(120*60*24*7));
    assertEquals("output differs", "2", b.format(140*60*24*7));

    b = new SecondFormat("tW");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1w", b.format(60*60*24*7));
    assertEquals("output differs", "2w", b.format(120*60*24*7));
    assertEquals("output differs", "2w", b.format(140*60*24*7));

    b = new SecondFormat("t.1W");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0w", b.format(60*60*24*7));
    assertEquals("output differs", "2.0w", b.format(120*60*24*7));
    assertEquals("output differs", "2.3w", b.format(140*60*24*7));

    b = new SecondFormat("t.2W");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.00w", b.format(60*60*24*7));
    assertEquals("output differs", "2.00w", b.format(120*60*24*7));
    assertEquals("output differs", "2.33w", b.format(140*60*24*7));
  }

  /**
   * Tests mixed representation.
   */
  public void testMixed() {
    assertEquals("output differs", "1w 1d 14h 43m 20s", SecondFormat.toMixed(61*61*25*8));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SecondFormatTest.class);
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
