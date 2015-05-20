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
 * AbstractActorProcessorTest.java
 * Copyright (C) 2011-2013 University of Waikato
 */

package adams.flow.processor;

import java.io.File;

import adams.core.io.FileUtils;
import adams.core.option.ArrayProducer;
import adams.core.option.NestedProducer;
import adams.core.option.OptionProducer;
import adams.flow.control.Flow;
import adams.flow.control.Flow.ErrorHandling;
import adams.flow.core.AbstractActor;
import adams.test.AbstractDatabaseTestCase;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Abstract Test class for actor processors.
 * <br><br>
 * It is possible to dump the input and output flow used in "testProcessing()"
 * using the following properties.
 * <ul>
 *   <li>"adams.test.flowprocessor.dump.inputfile" allows you to specify the file in which
 *   to save the input flow. Example: <br>
 *   <code>-Dadams.test.flowprocessor.dump.inputfile=/some/where/flow-in.txt</code>
 *   </li>
 *   <li>"adams.test.flowprocessor.dump.outputfile" allows you to specify the file in which
 *   to save the output flow. Example: <br>
 *   <code>-Dadams.test.flowprocessor.dump.outputfile=/some/where/flow-out.txt</code>
 *   </li>
 *   <li>"adams.test.flowprocessor.dump.append" allows to append all the flows that are
 *   run in the test sequence in the same file. Example: <br>
 *   <code>-Dadams.test.flowprocessor.dump.append=true</code>
 *   </li>
 *   <li>"adams.test.flowprocessor.dump.format" allows to specify the format (classname
 *   of option producer) of the dump. Example: <br>
 *   <code>-Dadams.test.flowprocessor.dump.format=adams.core.option.NestedProducer</code>
 *   </li>
 * </ul>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractActorProcessorTestCase
  extends AbstractDatabaseTestCase {

  /** the system property for specifying the dump file for the input flow. */
  public final static String PROPERTY_DUMPFILE_INPUT = "adams.test.flowprocessor.dump.inputfile";

  /** the system property for specifying the dump file for the output flow. */
  public final static String PROPERTY_DUMPFILE_OUTPUT = "adams.test.flowprocessor.dump.outputfile";

  /** the system property for specifying the to append the flows to the dumpfile. */
  public final static String PROPERTY_APPEND = "adams.test.flowprocessor.dump.append";

  /** the system property for specifying the to format of the dumped flows. */
  public final static String PROPERTY_FORMAT = "adams.test.flowprocessor.dump.format";

  /** Set to true to print out extra info during testing. */
  protected static boolean VERBOSE = false;

  /** The actor to be used. */
  protected AbstractActor m_Actor;

  /**
   * Constructs the <code>AbstractActorProcessorTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractActorProcessorTestCase(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. This implementation creates
   * the default actor.
   *
   * @throws Exception if an error occurs reading the example instances.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_Actor = getActor();
    if (m_Actor instanceof Flow)
      ((Flow) m_Actor).setErrorHandling(ErrorHandling.ACTORS_ALWAYS_STOP_ON_ERROR);
  }

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
	System.out.println(dumpActor(m_Actor, new NestedProducer()));
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
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public abstract AbstractActor getActor();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract AbstractActorProcessor[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[]{0,1,2,3};
  }

  /**
   * Performs the regression test.
   */
  public void testRegression() {
    int				i;
    AbstractActorProcessor[]	setups;
    AbstractActor		input;
    AbstractActor		inputBak;
    AbstractActor		output;
    File[]			files;
    String			filenameIn;
    String			filenameOut;
    boolean			append;
    String			regression;
    ModifyingProcessor		modifying;

    if (m_NoRegressionTest)
      return;

    append     = Boolean.getBoolean(PROPERTY_APPEND);
    filenameIn = System.getProperty(PROPERTY_DUMPFILE_INPUT);
    if ((filenameIn != null) && (filenameIn.length() == 0) && filenameIn.startsWith("$"))
      filenameIn = null;
    filenameOut = System.getProperty(PROPERTY_DUMPFILE_OUTPUT);
    if ((filenameOut != null) && (filenameOut.length() == 0) && filenameOut.startsWith("$"))
      filenameOut = null;

    setups = getRegressionSetups();
    files  = new File[setups.length];
    for (i = 0; i < setups.length; i++) {
      try {
	input = m_Actor.shallowCopy();
	if (filenameIn != null)
	  dumpActor(input, filenameIn, append);
	inputBak = input.shallowCopy(false);
	setups[i].process(input);
	output = input;
	assertEquals("Modified input actor", inputBak, input);
	if (setups[i] instanceof ModifyingProcessor) {
	  modifying = (ModifyingProcessor) setups[i];
	  if (modifying.isModified())
	    output = modifying.getModifiedActor();
	}
	if (filenameOut != null)
	  dumpActor(output, filenameOut, append);
	files[i] = new TmpFile("processed-" + (i + 1));
	FileUtils.writeToFile(
	    files[i].getAbsolutePath(),
	    dumpActor(output, new NestedProducer()),
	    false);
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }

    regression = m_Regression.compare(files, getRegressionIgnoredLineIndices());
    assertNull("Output differs:\n" + regression, regression);
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
   * @param producer	the option producer to use
   * @return		the actor in the specified format dump
   */
  protected String dumpActor(AbstractActor actor, OptionProducer producer) {
    String	result;

    producer.produce(actor);
    result = producer.toString();
    producer.cleanUp();

    return result;
  }

  /**
   * Returns the actor dumped in the specified format.
   *
   * @param actor	the actor to dump
   * @param filename	the file to dump the actor in
   * @param append	whether to append
   * @return		true if successfully dumped
   * @see		#PROPERTY_FORMAT
   */
  protected boolean dumpActor(AbstractActor actor, String filename, boolean append) {
    boolean		result;
    String		content;
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
      producer = new ArrayProducer();

    content = dumpActor(actor, producer);

    result = FileUtils.writeToFile(filename, content, append);
    if (result)
      System.out.println(
	  getClass().getName() + ": flow " + (append ? "appended" : "saved") + " to " + filename);
    else
      System.out.println(
	  getClass().getName() + ": failed to " + (append ? "append" : "save") + " flow to " + filename);

    return result;
  }
}
