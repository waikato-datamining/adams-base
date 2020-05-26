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
 * MicroSecondFormatTest.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.core.classmanager.ClassManager;
import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.MicroSecondFormat class. Run from commandline with: <br><br>
 * java adams.core.MicroSecondFormatTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MicroSecondFormatTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MicroSecondFormatTest(String name) {
    super(name);
  }

  /**
   * Performs a serializable test on the given class.
   *
   * @param cls		ignored
   */
  protected void performSerializableTest(Class cls) {
    assertNotNull("Serialization failed", ClassManager.getSingleton().deepCopy(new MicroSecondFormat("BK")));
  }

  /**
   * Tests an empty format.
   */
  public void testEmptyFormat() {
    MicroSecondFormat b = new MicroSecondFormat("");
    assertNull("format is not null", b.getFormat());
  }

  /**
   * Tests an invalid format.
   */
  public void testInvalidFormat() {
    MicroSecondFormat b = new MicroSecondFormat("hello world");
    assertNull("format is not null", b.getFormat());
  }

  /**
   * Tests milliseconds.
   */
  public void testMilliSeconds() {
    MicroSecondFormat b = new MicroSecondFormat("tl");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1024));
    assertEquals("output differs", "2", b.format(2048));
    assertEquals("output differs", "2", b.format(2200));

    b = new MicroSecondFormat("tL");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1ms", b.format(1024));
    assertEquals("output differs", "2ms", b.format(2048));
    assertEquals("output differs", "2ms", b.format(2200));

    b = new MicroSecondFormat("t.1L");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0ms", b.format(1024));
    assertEquals("output differs", "2.0ms", b.format(2048));
    assertEquals("output differs", "2.2ms", b.format(2200));

    b = new MicroSecondFormat("t.2L");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.02ms", b.format(1024));
    assertEquals("output differs", "2.04ms", b.format(2048));
    assertEquals("output differs", "2.20ms", b.format(2200));
  }

  /**
   * Tests seconds.
   */
  public void testSeconds() {
    MicroSecondFormat b = new MicroSecondFormat("ts");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1024L*1000));
    assertEquals("output differs", "2", b.format(2048L*1000));
    assertEquals("output differs", "2", b.format(2200L*1000));

    b = new MicroSecondFormat("tS");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1s", b.format(1024L*1000));
    assertEquals("output differs", "2s", b.format(2048L*1000));
    assertEquals("output differs", "2s", b.format(2200L*1000));

    b = new MicroSecondFormat("t.1S");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0s", b.format(1024L*1000));
    assertEquals("output differs", "2.0s", b.format(2048L*1000));
    assertEquals("output differs", "2.2s", b.format(2200L*1000));

    b = new MicroSecondFormat("t.2S");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.02s", b.format(1024L*1000));
    assertEquals("output differs", "2.04s", b.format(2048L*1000));
    assertEquals("output differs", "2.20s", b.format(2200L*1000));
  }

  /**
   * Tests minutes.
   */
  public void testMinutes() {
    MicroSecondFormat b = new MicroSecondFormat("tm");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1024L*1000*60));
    assertEquals("output differs", "2", b.format(2048L*1000*60));
    assertEquals("output differs", "2", b.format(2200L*1000*60));

    b = new MicroSecondFormat("tM");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1m", b.format(1024L*1000*60));
    assertEquals("output differs", "2m", b.format(2048L*1000*60));
    assertEquals("output differs", "2m", b.format(2200L*1000*60));

    b = new MicroSecondFormat("t.1M");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0m", b.format(1024L*1000*60));
    assertEquals("output differs", "2.0m", b.format(2048L*1000*60));
    assertEquals("output differs", "2.2m", b.format(2200L*1000*60));

    b = new MicroSecondFormat("t.2M");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.02m", b.format(1024L*1000*60));
    assertEquals("output differs", "2.04m", b.format(2048L*1000*60));
    assertEquals("output differs", "2.20m", b.format(2200L*1000*60));
  }

  /**
   * Tests hours.
   */
  public void testHours() {
    MicroSecondFormat b = new MicroSecondFormat("th");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1024L*1000*60*60));
    assertEquals("output differs", "2", b.format(2048L*1000*60*60));
    assertEquals("output differs", "2", b.format(2200L*1000*60*60));

    b = new MicroSecondFormat("tH");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1h", b.format(1024L*1000*60*60));
    assertEquals("output differs", "2h", b.format(2048L*1000*60*60));
    assertEquals("output differs", "2h", b.format(2200L*1000*60*60));

    b = new MicroSecondFormat("t.1H");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0h", b.format(1024L*1000*60*60));
    assertEquals("output differs", "2.0h", b.format(2048L*1000*60*60));
    assertEquals("output differs", "2.2h", b.format(2200L*1000*60*60));

    b = new MicroSecondFormat("t.2H");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.02h", b.format(1024L*1000*60*60));
    assertEquals("output differs", "2.04h", b.format(2048L*1000*60*60));
    assertEquals("output differs", "2.20h", b.format(2200L*1000*60*60));
  }

  /**
   * Tests days.
   */
  public void testDays() {
    MicroSecondFormat b = new MicroSecondFormat("td");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1024L*1000*60*60*24));
    assertEquals("output differs", "2", b.format(2048L*1000*60*60*24));
    assertEquals("output differs", "2", b.format(2200L*1000*60*60*24));

    b = new MicroSecondFormat("tD");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1d", b.format(1024L*1000*60*60*24));
    assertEquals("output differs", "2d", b.format(2048L*1000*60*60*24));
    assertEquals("output differs", "2d", b.format(2200L*1000*60*60*24));

    b = new MicroSecondFormat("t.1D");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0d", b.format(1024L*1000*60*60*24));
    assertEquals("output differs", "2.0d", b.format(2048L*1000*60*60*24));
    assertEquals("output differs", "2.2d", b.format(2200L*1000*60*60*24));

    b = new MicroSecondFormat("t.2D");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.02d", b.format(1024L*1000*60*60*24));
    assertEquals("output differs", "2.04d", b.format(2048L*1000*60*60*24));
    assertEquals("output differs", "2.20d", b.format(2200L*1000*60*60*24));
  }

  /**
   * Tests weeks.
   */
  public void testWeeks() {
    MicroSecondFormat b = new MicroSecondFormat("tw");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1", b.format(1024L*1000*60*60*24*7));
    assertEquals("output differs", "2", b.format(2048L*1000*60*60*24*7));
    assertEquals("output differs", "2", b.format(2200L*1000*60*60*24*7));

    b = new MicroSecondFormat("tW");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1w", b.format(1024L*1000*60*60*24*7));
    assertEquals("output differs", "2w", b.format(2048L*1000*60*60*24*7));
    assertEquals("output differs", "2w", b.format(2200L*1000*60*60*24*7));

    b = new MicroSecondFormat("t.1W");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.0w", b.format(1024L*1000*60*60*24*7));
    assertEquals("output differs", "2.0w", b.format(2048L*1000*60*60*24*7));
    assertEquals("output differs", "2.2w", b.format(2200L*1000*60*60*24*7));

    b = new MicroSecondFormat("t.2W");
    assertNotNull("format is null", b.getFormat());
    assertEquals("output differs", "1.02w", b.format(1024L*1000*60*60*24*7));
    assertEquals("output differs", "2.04w", b.format(2048L*1000*60*60*24*7));
    assertEquals("output differs", "2.20w", b.format(2200L*1000*60*60*24*7));
  }

  /**
   * Tests mixed representation.
   */
  public void testMixed() {
    assertEquals("output differs", "1w 1d 15h 8m 9s 144ms 200µs", MicroSecondFormat.toMixed(1001L*1001*61*61*25*8));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MicroSecondFormatTest.class);
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
