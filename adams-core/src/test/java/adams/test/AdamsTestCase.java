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
 * AdamsTestCase.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.test;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import adams.core.ClassLocator;
import adams.core.DateUtils;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.management.CharsetHelper;
import adams.core.management.LocaleHelper;
import adams.core.management.OS;
import adams.core.management.ProcessUtils;
import adams.core.option.OptionHandler;
import adams.data.statistics.StatUtils;
import adams.gui.scripting.ScriptingEngine;

/**
 * Ancestor for all test cases.
 * <br><br>
 * The environment class can be set as follows: <br>
 *   <code>-Dadams.env.class=adams.env.Environment</code>
 * <br><br>
 * Any regression test can be skipped as follows: <br>
 *   <code>-Dadams.test.noregression=true</code>
 * <br><br>
 * Any quickinfo regression test can be skipped as follows: <br>
 *   <code>-Dadams.test.quickinfo.noregression=true</code>
 * <br><br>
 * Headless environment can be indicated as follows: <br>
 *   <code>-Dadams.test.headless=true</code>
 * <br><br>
 * Individual tests can be skipped as follows (comma-separated lost): <br>
 *   <code>-Dadams.test.skip=adams.some.where.Class1Test,adams.some.where.else.Class2Test</code>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdamsTestCase
  extends TestCase {

  /** property indicating whether tests should be run in headless mode. */
  public final static String PROPERTY_HEADLESS = "adams.test.headless";

  /** property indicating whether regression tests should not be executed. */
  public final static String PROPERTY_NOREGRESSION = "adams.test.noregression";

  /** property indicating whether quickinfo regression tests should not be executed. */
  public final static String PROPERTY_NOQUICKINFOREGRESSION = "adams.test.quickinfo.noregression";

  /** property defining the environment class to use (see pom.xml files/surefire plugin). */
  public final static String PROPERTY_ENV_CLASS = "adams.env.class";

  /** property listing all test classes that should not get executed. */
  public final static String PROPERTY_SKIP = "adams.test.skip";

  /** whether to execute any regression test. */
  protected boolean m_NoRegressionTest;

  /** whether to execute the quickinfo regression test. */
  protected boolean m_NoQuickInfoRegressionTest;
  
  /** the helper class for regression. */
  protected Regression m_Regression;

  /** the helper class for quick info regression. */
  protected Regression m_QuickInfoRegression;

  /** the test class to use. */
  protected AbstractTestHelper m_TestHelper;

  /** whether to run tests in headless mode. */
  protected boolean m_Headless;
  
  /** the classnames of tests to skip. */
  protected HashSet<String> m_SkipTests;

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AdamsTestCase(String name) {
    super(name);
  }

  /**
   * Tries to load the class based on the test class's name.
   *
   * @return		the class that is being tested or null if none could
   * 			be determined
   */
  protected Class getTestedClass() {
    Class	result;

    result = null;

    if (getClass().getName().endsWith("Test")) {
      try {
	result = Class.forName(getClass().getName().replaceAll("Test$", ""));
      }
      catch (Exception e) {
	result = null;
      }
    }

    return result;
  }

  /**
   * Returns the platforms this test class is for.
   * 
   * @return		the platforms
   */
  protected HashSet<Platform> getPlatforms() {
    return new HashSet<Platform>(Arrays.asList(new Platform[]{Platform.ALL}));
  }
  
  /**
   * Returns whether the test can be executed in a headless environment. If not
   * then the test gets skipped.
   * 
   * @return		true if OK to run in headless mode
   */
  protected boolean canHandleHeadless() {
    return true;
  }
  
  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    String	clsname;
    Class	cls;
    String	skipped;
    String[]	parts;

    super.setUp();

    // SSL handling
    System.setProperty("jsse.enableSNIExtension", "false");
    
    // set up the correct environment
    clsname = System.getProperty(PROPERTY_ENV_CLASS);
    if (clsname != null)
      adams.env.Environment.setEnvironmentClass(Class.forName(clsname));
    if (adams.env.Environment.getEnvironmentClass() == null)
      throw new IllegalStateException("No environment class set!");

    // any tests that are skipped?
    m_SkipTests = new HashSet<String>();
    skipped     = System.getProperty(PROPERTY_SKIP);
    if ((skipped != null) && !skipped.trim().isEmpty()) {
      parts = skipped.trim().replace(" ", "").split(",");
      for (String part: parts) {
	if (!part.trim().isEmpty())
	  m_SkipTests.add(part);
      }
    }
    
    // set up timezone/locale
    LocaleHelper.getSingleton().setDefault(LocaleHelper.LOCALE_EN_US);
    LocaleHelper.getSingleton().setLocale(LocaleHelper.LOCALE_EN_US);
    DateUtils.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
    DateUtils.setLocale(new Locale("en", "NZ"));
    CharsetHelper.getSingleton().setCharset("UTF-8");
    
    cls = getTestedClass();
    if (cls != null) {
      m_Regression          = new Regression(cls);
      m_QuickInfoRegression = new Regression(cls);
      m_QuickInfoRegression.setReferenceFile(Regression.createReferenceFile(cls, null, ".quickinfo"));
    }

    m_TestHelper                = newTestHelper();
    m_Headless                  = Boolean.getBoolean(PROPERTY_HEADLESS);
    m_NoRegressionTest          = Boolean.getBoolean(PROPERTY_NOREGRESSION);
    m_NoQuickInfoRegressionTest = Boolean.getBoolean(PROPERTY_NOQUICKINFOREGRESSION);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(Object[] expected, Object[] actual) {
    assertEqualsArrays(null, expected, actual);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param msg		the message to output, null to ignore
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(String msg, Object[] expected, Object[] actual) {
    if (msg == null)
      msg = "";
    if (!msg.isEmpty())
      msg += ": ";
    assertEquals(msg + "Array length differs", expected.length, actual.length);
    for (int i = 0; i < expected.length; i++)
      assertEquals(msg + "Array element #" + (i+1) + " differs", expected[i], actual[i]);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(byte[] expected, byte[] actual) {
    assertEqualsArrays(null, expected, actual);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param msg		the message to output, null to ignore
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(String msg, byte[] expected, byte[] actual) {
    assertEqualsArrays(msg, StatUtils.toNumberArray(expected), StatUtils.toNumberArray(actual));
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(short[] expected, short[] actual) {
    assertEqualsArrays(null, expected, actual);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param msg		the message to output, null to ignore
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(String msg, short[] expected, short[] actual) {
    assertEqualsArrays(msg, StatUtils.toNumberArray(expected), StatUtils.toNumberArray(actual));
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(int[] expected, int[] actual) {
    assertEqualsArrays(null, expected, actual);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param msg		the message to output, null to ignore
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(String msg, int[] expected, int[] actual) {
    assertEqualsArrays(msg, StatUtils.toNumberArray(expected), StatUtils.toNumberArray(actual));
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(long[] expected, long[] actual) {
    assertEqualsArrays(null, expected, actual);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param msg		the message to output, null to ignore
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(String msg, long[] expected, long[] actual) {
    assertEqualsArrays(msg, StatUtils.toNumberArray(expected), StatUtils.toNumberArray(actual));
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(float[] expected, float[] actual) {
    assertEqualsArrays(null, expected, actual);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param msg		the message to output, null to ignore
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(String msg, float[] expected, float[] actual) {
    assertEqualsArrays(msg, StatUtils.toNumberArray(expected), StatUtils.toNumberArray(actual));
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(double[] expected, double[] actual) {
    assertEqualsArrays(null, expected, actual);
  }

  /**
   * Compares arrays and fails if they differ.
   * 
   * @param msg		the message to output, null to ignore
   * @param expected	the expected array
   * @param actual	the actual array
   */
  public void assertEqualsArrays(String msg, double[] expected, double[] actual) {
    assertEqualsArrays(msg, StatUtils.toNumberArray(expected), StatUtils.toNumberArray(actual));
  }
  
  /**
   * Override to run the test and assert its state. Checks whether the test
   * or test-method is platform-specific.
   * 
   * @throws Throwable if any exception is thrown
   */
  @Override
  protected void runTest() throws Throwable {
    String		msg;
    HashSet<Platform>	platforms;
    
    platforms = getPlatforms();
    msg       = null;
    if (!platforms.contains(Platform.ALL)) {
      if ((msg == null) && OS.isMac() && !platforms.contains(Platform.MAC))
	msg = "Cannot run test on Mac";
      if ((msg == null) && OS.isWindows() && !platforms.contains(Platform.WINDOWS))
	msg = "Cannot run test on Windows";
      if ((msg == null) && (!OS.isWindows() && !OS.isMac()) && !platforms.contains(Platform.LINUX))
	msg = "Cannot run test on Linux";
    }

    if ((msg == null) && m_Headless && !canHandleHeadless())
      msg = "Cannot execute test in headless environment";
    
    if ((msg == null) && m_SkipTests.contains(getClass().getName()))
      msg = "Test excluded from being run (" + getClass().getName() + ")";
    
    if (msg == null)
      super.runTest();
    else
      System.out.println("Skipped: " + msg);
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_Regression = null;

    ScriptingEngine.stopAllEngines();

    super.tearDown();
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "");
  }

  /**
   * Tries to obtain an instance of the given class.
   *
   * @param cls		the class to obtain an instance from
   * @param intf	the required interface that the class must implement
   * @param fail	if true, errors/exceptions will result in a test fail
   */
  protected Object getInstance(Class cls, Class intf, boolean fail) {
    Object		result;
    Constructor		constr;

    if (!ClassLocator.hasInterface(intf, cls))
      return null;

    // default constructor?
    constr = null;
    try {
      constr = cls.getConstructor(new Class[0]);
    }
    catch (NoSuchMethodException e) {
      if (fail)
	fail("No default constructor, requires custom test method: " + cls.getName());
      return null;
    }

    // create instance
    result = null;
    try {
      result = constr.newInstance(new Object[0]);
    }
    catch (Exception e) {
      if (fail)
	fail("Failed to instantiate object using default constructor: " + cls.getName());
      return null;
    }

    return result;
  }

  /**
   * Performs a serializable test on the given class.
   *
   * @param cls		the class to test
   */
  protected void performSerializableTest(Class cls) {
    Object		obj;

    obj = getInstance(cls, Serializable.class, true);
    if (obj == null)
      return;
    
    assertNotNull("Serialization failed", Utils.deepCopy(obj));
  }

  /**
   * For classes (with default constructor) that are serializable, are tested
   * whether they are truly serializable.
   */
  public void testSerializable() {
    if (m_Regression != null)
      performSerializableTest(m_Regression.getRegressionClass());
  }

  /**
   * Performs a quickinfo test on the given class.
   *
   * @param cls		the class to test
   */
  protected void performQuickInfoTest(Class cls) {
    Object		obj;

    obj = getInstance(cls, QuickInfoSupporter.class, true);
    if (obj == null)
      return;

    // obtain quick info
    try {
      ((QuickInfoSupporter) obj).getQuickInfo();
    }
    catch (Exception e) {
      fail("QuickInfo test failed '" + cls.getName() + "': " + e);
    }
  }

  /**
   * For classes (with default constructor) that implement 
   * the {@link QuickInfoSupporter} interface.
   */
  public void testQuickInfo() {
    if (m_Regression != null)
      performQuickInfoTest(m_Regression.getRegressionClass());
  }

  /**
   * For classes (with default constructor) that implement 
   * the {@link QuickInfoSupporter} interface, creates a regression reference
   * file.
   */
  public void testQuickInfoRegression() {
    String		current;
    Object		obj;
    String		msg;
    
    if (m_NoRegressionTest || m_NoQuickInfoRegressionTest)
      return;
    
    if (m_QuickInfoRegression == null)
      return;
    
    // can we obtain an instance of the object?
    obj = getInstance(m_QuickInfoRegression.getRegressionClass(), QuickInfoSupporter.class, false);
    if (obj == null)
      return;
    
    current = null;
    try {
      current = ((QuickInfoSupporter) obj).getQuickInfo();
      if (current == null)
	current = "";
    }
    catch (Exception e) {
      fail("Failed to obtain quick info: " + e);
    }
    
    if ((msg = m_QuickInfoRegression.compare(current)) != null)
      fail("Quick info changed:\n" + msg);
  }

  /**
   * Performs setting the default options, in case the class implements the
   * OptionHandler interface.
   *
   * @param cls		the class to test
   */
  protected void performDefaultOptionsTest(Class cls) {
    Object		obj;
    OptionHandler	handler;
    Constructor		constr;

    if (!ClassLocator.hasInterface(OptionHandler.class, cls))
      return;

    // default constructor?
    constr = null;
    try {
      constr = cls.getConstructor(new Class[0]);
    }
    catch (NoSuchMethodException e) {
      fail("No default constructor, requires custom test method: " + cls.getName());
    }

    // create instance
    obj = null;
    try {
      obj = constr.newInstance(new Object[0]);
    }
    catch (Exception e) {
      fail("Failed to instantiate object using default constructor: " + cls.getName());
    }

    handler = (OptionHandler) obj;
    handler.getOptionManager().setThrowExceptions(true);
    try {
      handler.getOptionManager().setDefaults();
    }
    catch (Exception e) {
      fail("Setting default options failed: " + cls.getName());
    }
  }

  /**
   * For classes (with default constructor) that implement the OptionHandler
   * interface, are tested whether setting the default options works properly.
   */
  public void testDefaultOptions() {
    if (m_Regression != null)
      performDefaultOptionsTest(m_Regression.getRegressionClass());
  }

  /**
   * Runs the specified suite. Used for running the test from commandline.
   *
   * @param suite	the suite to run
   */
  public static void runTest(Test suite) {
    System.out.println("PID: " + ProcessUtils.getVirtualMachinePID());
    TestRunner.run(suite);
  }
}
