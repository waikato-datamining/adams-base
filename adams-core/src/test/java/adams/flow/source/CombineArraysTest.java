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
 * CombineArraysTest.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.transformer.SetStorageValue;
import adams.flow.transformer.StringJoin;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for CombineArrays actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class CombineArraysTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CombineArraysTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CombineArraysTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.first
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("first"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.first.StringConstants
      StringConstants stringconstants = new StringConstants();
      stringconstants.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("1"));
      strings.add((BaseString) argOption.valueOf("2"));
      strings.add((BaseString) argOption.valueOf("3"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      StringToString stringtostring = new StringToString();
      stringconstants.setConversion(stringtostring);

      actors2.add(stringconstants);

      // Flow.first.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("a1"));
      actors2.add(setstoragevalue);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.second
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("second"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.second.StringConstants
      StringConstants stringconstants2 = new StringConstants();
      stringconstants2.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      List<BaseString> strings2 = new ArrayList<>();
      strings2.add((BaseString) argOption.valueOf("4"));
      strings2.add((BaseString) argOption.valueOf("5"));
      strings2.add((BaseString) argOption.valueOf("6"));
      stringconstants2.setStrings(strings2.toArray(new BaseString[0]));
      StringToString stringtostring2 = new StringToString();
      stringconstants2.setConversion(stringtostring2);

      actors3.add(stringconstants2);

      // Flow.second.SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("a2"));
      actors3.add(setstoragevalue2);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.combine
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("combine"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.combine.CombineArrays
      CombineArrays combinearrays = new CombineArrays();
      argOption = (AbstractArgumentOption) combinearrays.getOptionManager().findByProperty("storageNames");
      List<StorageName> storagenames = new ArrayList<>();
      storagenames.add((StorageName) argOption.valueOf("a1"));
      storagenames.add((StorageName) argOption.valueOf("a2"));
      combinearrays.setStorageNames(storagenames.toArray(new StorageName[0]));
      actors4.add(combinearrays);

      // Flow.combine.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors4.add(stringjoin);

      // Flow.combine.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors4.add(dumpfile);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger3);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

