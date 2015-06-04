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
 * AbstractFlowTest.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, NZ
 */

package adams.flow;

import adams.core.io.FileUtils;
import adams.core.option.NestedProducer;
import adams.core.option.OptionProducer;
import adams.flow.control.Flow;
import adams.flow.control.Flow.ErrorHandling;
import adams.flow.core.AbstractActor;
import adams.gui.core.GUIHelper;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.Regression;
import adams.test.TestHelper;

import java.io.File;

/**
 * Abstract Test class for flow actors.
 * <br><br>
 * It is possible to dump the flow used in "testActor()" using the following
 * properties.
 * <ul>
 *   <li>"adams.test.flow.dump.file" allows you to specify the file in which
 *   to save the flow as command-line string. Example: <br>
 *   <code>-Dadams.test.flow.dump.file=/some/where/flow.txt</code>
 *   </li>
 *   <li>"adams.test.flow.dump.append" allows to append all the flows that are
 *   run in the test sequence in the same file. Example: <br>
 *   <code>-Dadams.test.flow.dump.append=true</code>
 *   </li>
 *   <li>"adams.test.flow.dump.format" allows to specify the format (classname
 *   of option producer) of the dump. Example: <br>
 *   <code>-Dadams.test.flow.dump.format=adams.core.option.NestedProducer</code>
 *   </li>
 * </ul>
 * The regression test can be skipped as follows: <br>
 *   <code>-Dadams.test.flow.noregression=true</code>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFlowTest
  extends AbstractDatabaseTestCase {

  /** the system property for specifying the dump file for the flow. */
  public final static String PROPERTY_DUMPFILE = "adams.test.flow.dump.file";

  /** the system property for specifying the to append the flows to the dumpfile. */
  public final static String PROPERTY_APPEND = "adams.test.flow.dump.append";

  /** the system property for specifying the to format of the dumped flows. */
  public final static String PROPERTY_FORMAT = "adams.test.flow.dump.format";

  /** property indicating whether regression tests should not be executed. */
  public final static String PROPERTY_NOFLOWREGRESSION = "adams.test.flow.noregression";

  /** Set to true to print out extra info during testing. */
  protected static boolean VERBOSE = false;

  /** The actor to be tested. */
  protected AbstractActor m_Actor;

  /** whether to execute the flow regression test. */
  protected boolean m_NoFlowRegressionTest;

  /**
   * Constructs the <code>AbstractFlowTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractFlowTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. This implementation creates
   * the default actor.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_Actor = getActor();
    if (m_Actor instanceof Flow)
      ((Flow) m_Actor).setErrorHandling(ErrorHandling.ACTORS_ALWAYS_STOP_ON_ERROR);

    m_NoFlowRegressionTest = Boolean.getBoolean(PROPERTY_NOFLOWREGRESSION);
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    if (!m_Actor.isStopped())
      m_Actor.wrapUp();

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public abstract AbstractActor getActor();

  /**
   * Dumps the actor, in case of an error.
   * 
   * @throws Throwable		any test failure
   */
  @Override
  public void runBare() throws Throwable {
    try {
      super.runBare();
    }
    catch (Throwable t) {
      if (m_Actor != null)
	System.out.println(dumpActor(m_Actor));
      throw t;
    }
    finally {
      if (m_Actor != null) {
	m_Actor.destroy();
	m_Actor = null;
      }
    }
  }
  
  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Performs the regression test based on the given file.
   *
   * @param file	the file for the regression test
   */
  protected void performRegressionTest(File file) {
    performRegressionTest(new File[]{file});
  }

  /**
   * Performs the regression test based on the given files.
   *
   * @param files	the files for the regression test
   */
  protected void performRegressionTest(File[] files) {
    performRegressionTest(m_Actor, files, m_Regression, getRegressionIgnoredLineIndices());
  }

  /**
   * Performs the regression test based on the given files.
   *
   * @param files	the files for the regression test
   */
  protected void performRegressionTest(AbstractActor actor, File[] files, Regression regr, int[] ignoredLines) {
    String	regression;

    if (m_NoRegressionTest || m_NoFlowRegressionTest)
      return;
    
    if (performActorExecution(actor) == null) {
      regression = regr.compare(files, ignoredLines);
      assertNull("Output differs:\n" + regression, regression);
    }
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/flow/data");
  }

  /**
   * Returns the actor dumped in the specified format.
   *
   * @param actor	the actor to dump
   * @return		the actor in the specified format dump
   * @see		#PROPERTY_FORMAT
   */
  protected String dumpActor(AbstractActor actor) {
    String		format;
    OptionProducer	producer;

    format   = System.getProperty(PROPERTY_FORMAT);
    producer = null;
    if ((format != null) && (format.length() >= 0)) {
      try {
	producer = (OptionProducer) Class.forName(format).newInstance();
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate option producer '" + format + "': " + e);
	producer = null;
      }
    }

    if (producer == null)
      producer = new NestedProducer();

    producer.produce(actor);

    return producer.toString();
  }

  /**
   * Tests multiple calls of setUp/wrapUp.
   *
   * @see		#getActor()
   */
  public void testMultiSetUpWrapUp() {
    String      result;

    result = m_Actor.setUp();
    assertNull("setUp() [1] not null: " + result, result);
    m_Actor.wrapUp();

    result = m_Actor.setUp();
    assertNull("setUp() [2] not null: " + result, result);
    m_Actor.wrapUp();

    result = m_Actor.setUp();
    assertNull("setUp() [3] not null: " + result, result);
    m_Actor.wrapUp();
  }

  /**
   * Tests the given actor.
   * 
   * @param actor	the actor to test
   * @return		null if OK
   */
  protected String performActorExecution(AbstractActor actor) {
    String	result;

    if (actor instanceof Flow)
      ((Flow) actor).setHeadless(m_Headless);
    
    result = actor.setUp();
    assertNull("setUp() not null: " + result, result);

    result = actor.execute();
    assertNull("execute() not null: " + result, result);

    result = actor.getStopMessage();
    assertNull("getStopMessage() not null: " + result, result);

    actor.wrapUp();
    
    return result;
  }
  
  /**
   * Tests an example actor setup.
   *
   * @see		#getActor()
   */
  public void testActor() {
    String	filename;
    boolean	append;
    boolean	written;

    // dump the test flow to a file?
    filename = System.getProperty(PROPERTY_DUMPFILE);
    if ((filename != null) && (filename.length() > 0) && !filename.startsWith("$")) {
      append  = Boolean.getBoolean(PROPERTY_APPEND);
      written = FileUtils.writeToFile(filename, dumpActor(m_Actor), append);
      if (written)
	System.out.println(
	    getClass().getName() + ": flow " + (append ? "appended" : "saved") + " to " + filename);
      else
	System.out.println(
	    getClass().getName() + ": failed to " + (append ? "append" : "save") + " flow to " + filename);
    }

    performActorExecution(m_Actor);
  }

  /**
   * Tests whether an image is available for the actor.
   */
  public void testImage() {
    assertNotNull("No icon found", GUIHelper.getIcon(getTestedClass()));
  }
}
