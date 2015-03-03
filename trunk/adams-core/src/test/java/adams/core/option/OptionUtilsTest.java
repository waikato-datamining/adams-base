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

/**
 * OptionUtilsTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.logging.LoggingLevel;
import adams.data.baseline.SlidingWindow;
import adams.env.Environment;
import adams.test.AdamsTestCase;


/**
 * Test class for the OptionUtils class. Run from the command line with: <p/>
 * java adams.core.option.OptionUtilsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionUtilsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public OptionUtilsTest(String name) {
    super(name);
  }

  /**
   * Tests the checkRemainingOptions methods.
   */
  public void testCheckRemainingOptions() {
    String[] options = new String[]{};
    assertNull("Should return null", OptionUtils.checkRemainingOptions(options));

    options = new String[]{"-blah"};
    assertNotNull("Should return an error message", OptionUtils.checkRemainingOptions(options));

    options = new String[]{"", "\\", ""};
    assertNull("Should return null", OptionUtils.checkRemainingOptions(options));
  }

  /**
   * Tests the forCommandLine() method.
   *
   * @throws Exception 	if parsing fails
   */
  public void testForCommandLine() throws Exception {
    String cmdline = "adams.data.filter.BaselineCorrection -baseline \"adams.data.baseline.SlidingWindow -logging-level FINE\"";
    adams.data.filter.BaselineCorrection filter = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    filter.setBaselineCorrection(baseline);

    Object obj = OptionUtils.forCommandLine(adams.data.filter.AbstractFilter.class, cmdline);
    assertEquals("Class differs", filter.getClass(), obj.getClass());

    ArrayProducer prodFilter = new ArrayProducer();
    String[] optionsFilter = prodFilter.produce(filter);
    ArrayProducer prodObj = new ArrayProducer();
    String[] optionsObj = prodObj.produce((OptionHandler) obj);
    assertEquals("options array differs in length", optionsFilter.length, optionsObj.length);
    for (int i = 0; i < optionsFilter.length; i++)
      assertEquals("option #" + (i+1) + " differs", optionsFilter[i], optionsObj[i]);
    prodFilter.cleanUp();
    prodObj.cleanUp();
  }

  /**
   * Tests the forName() method.
   *
   * @throws Exception	if parsing fails
   */
  public void testForName() throws Exception {
    String classname = "adams.data.filter.BaselineCorrection";
    String[] options = new String[]{"-baseline", "adams.data.baseline.SlidingWindow -logging-level FINE"};
    adams.data.filter.BaselineCorrection filter = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    filter.setBaselineCorrection(baseline);

    Object obj = OptionUtils.forName(adams.data.filter.AbstractFilter.class, classname, options);
    assertEquals("Class differs", filter.getClass(), obj.getClass());

    ArrayProducer prodFilter = new ArrayProducer();
    String[] optionsFilter = prodFilter.produce(filter);
    ArrayProducer prodObj = new ArrayProducer();
    String[] optionsObj = prodObj.produce((OptionHandler) obj);
    assertEquals("options array differs in length", optionsFilter.length, optionsObj.length);
    for (int i = 0; i < optionsFilter.length; i++)
      assertEquals("option #" + (i+1) + " differs", optionsFilter[i], optionsObj[i]);
    prodFilter.cleanUp();
    prodObj.cleanUp();
  }

  /**
   * Tests the forString() method.
   *
   * @throws Exception 	if parsing fails
   */
  public void testForString() throws Exception {
    adams.data.filter.BaselineCorrection filter = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    filter.setBaselineCorrection(baseline);

    String str = "adams.data.filter.BaselineCorrection -baseline \"adams.data.baseline.SlidingWindow -logging-level FINE -baseline adams.data.baseline.PassThrough\"";
    assertEquals("objects differ (1)", filter, OptionUtils.forString(adams.data.filter.AbstractFilter.class, str));

    str =   "# some dummy comment\n"
          + "adams.data.filter.BaselineCorrection\n"
          + "\t-baseline\n"
          + "\t\tadams.data.baseline.SlidingWindow\n"
          + "\t\t\t-logging-level\n"
          + "\t\t\tFINE\n"
          + "\t\t\t-baseline\n"
          + "\t\t\t\tadams.data.baseline.PassThrough\n";
    assertEquals("objects differ (2)", filter, OptionUtils.forString(adams.data.filter.AbstractFilter.class, str));
  }

  /**
   * Tests the getCommandLine() method.
   */
  public void testGetCommandLine() {
    String cmdline = "adams.data.filter.BaselineCorrection -baseline \"adams.data.baseline.SlidingWindow -logging-level FINE -baseline adams.data.baseline.PassThrough\"";
    adams.data.filter.BaselineCorrection filter = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    filter.setBaselineCorrection(baseline);

    assertEquals("commandlines differ", cmdline, OptionUtils.getCommandLine(filter));
  }

  /**
   * Tests the helpRequested methods.
   */
  public void testHelpRequested() {
    String[] options = new String[]{};
    assertFalse("Shouldn't detect help", OptionUtils.helpRequested(options));
    assertFalse("Shouldn't detect help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-H"};
    assertFalse("Shouldn't detect help", OptionUtils.helpRequested(options));
    assertFalse("Shouldn't detect help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-Help"};
    assertFalse("Shouldn't detect help", OptionUtils.helpRequested(options));
    assertFalse("Shouldn't detect help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-h"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-help"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-blah", "hello", "-h"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-blah", "hello", "-help"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-blah", "hello", "-h", "-some", "3"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-blah", "hello", "-help", "-some", "3"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-h", "-some", "3"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));

    options = new String[]{"-help", "-some", "3"};
    assertTrue("Should have detected help", OptionUtils.helpRequested(options));
    assertTrue("Should have detected help", OptionUtils.helpRequested(new Vector<String>(Arrays.asList(options))));
  }

  /**
   * Tests the joinOptions method.
   */
  public void testJoinOptions() {
    String[] options = new String[]{};
    assertEquals("Should have length zero", 0, OptionUtils.joinOptions(options).length());

    options = new String[]{"-blah", "1"};
    assertEquals("wrong joined length", 7, OptionUtils.joinOptions(options).length());

    options = new String[]{"-blah", "", "-some", "1"};
    assertEquals("wrong joined length", 16, OptionUtils.joinOptions(options).length());
  }

  /**
   * Tests the splitOptions method.
   *
   * @throws Exception	in case splitting fails
   */
  public void testSplitOptions() throws Exception {
    String cmdline = "-blah";
    assertEquals("Wrong number of parsed elements", 1, OptionUtils.splitOptions(cmdline).length);

    cmdline = "-blah 1";
    assertEquals("Wrong number of parsed elements", 2, OptionUtils.splitOptions(cmdline).length);

    cmdline = "-blah               1        -some 2";
    assertEquals("Wrong number of parsed elements", 4, OptionUtils.splitOptions(cmdline).length);
  }

  /**
   * Tests the shallowCopy method.
   */
  public void testShallowCopy() {
    adams.data.filter.BaselineCorrection corr = new adams.data.filter.BaselineCorrection();
    corr.setBaselineCorrection(new SlidingWindow());
    OptionHandler copy = OptionUtils.shallowCopy(corr, false);
    assertNotNull("shallow copy shouldn't be null", copy);
    assertEquals("class differs", corr.getClass(), copy.getClass());

    ArrayProducer prodFilter = new ArrayProducer();
    String[] optionsFilter = prodFilter.produce(corr);
    ArrayProducer prodObj = new ArrayProducer();
    String[] optionsObj = prodObj.produce(copy);
    assertEquals("options array differs in length", optionsFilter.length, optionsObj.length);
    for (int i = 0; i < optionsFilter.length; i++)
      assertEquals("option #" + (i+1) + " differs", optionsFilter[i], optionsObj[i]);
    prodFilter.cleanUp();
    prodObj.cleanUp();
  }

  /**
   * Test the hasFlag methods (checks existence of flag).
   */
  public void testHasFlag() {
    String[] optArray = new String[]{"-B", "-I", "6", "-R", "42"};
    assertFalse(OptionUtils.hasFlag(optArray, "-C"));
    assertTrue(OptionUtils.hasFlag(optArray, "-B"));
    assertTrue(OptionUtils.hasFlag(optArray, "-I"));   // does not distinguish between flags and options with arguments

    Vector<String> optVector = new Vector<String>(Arrays.asList(optArray));
    assertFalse(OptionUtils.hasFlag(optVector, "-C"));
    assertTrue(OptionUtils.hasFlag(optVector, "-B"));
    assertTrue(OptionUtils.hasFlag(optVector, "-I"));   // does not distinguish between flags and options with arguments
  }

  /**
   * Test the getOption methods (for obtaining the argument of an option).
   */
  public void testGetOption() {
    String[] optArray = new String[]{"-B", "-I", "6", "-R", "42"};
    assertNull(OptionUtils.getOption(optArray, "-C"));
    assertEquals("-I", OptionUtils.getOption(optArray, "-B"));   // does not distinguish between flags and options with arguments
    assertEquals("6", OptionUtils.getOption(optArray, "-I"));

    Vector<String> optVector = new Vector<String>(Arrays.asList(optArray));
    assertNull(OptionUtils.getOption(optVector, "-C"));
    assertEquals("-I", OptionUtils.getOption(optVector, "-B"));   // does not distinguish between flags and options with arguments
    assertEquals("6", OptionUtils.getOption(optVector, "-I"));
  }

  /**
   * Tests the transferOptions method.
   */
  public void testTransferOptions() {
    adams.data.filter.BaselineCorrection source = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    source.setBaselineCorrection(baseline);
    adams.data.filter.BaselineCorrection dest = new adams.data.filter.BaselineCorrection();
    if (!OptionUtils.transferOptions(source, dest))
      fail("Failed to transfer options!");
    assertEquals("command-lines not the same", OptionUtils.getCommandLine(source), OptionUtils.getCommandLine(dest));
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(OptionUtilsTest.class);
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
