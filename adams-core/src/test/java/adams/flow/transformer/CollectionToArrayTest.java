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
 * CollectionToArrayTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

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
import adams.flow.source.NewCollection;
import adams.flow.source.StringConstants;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for CollectionToArray actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class CollectionToArrayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CollectionToArrayTest(String name) {
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
    return new TestSuite(CollectionToArrayTest.class);
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

      // Flow.NewCollection
      NewCollection newcollection = new NewCollection();
      actors.add(newcollection);

      // Flow.add item to storage
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("add item to storage"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.add item to storage.StringConstants
      StringConstants stringconstants = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("1"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      StringToString stringtostring = new StringToString();
      stringconstants.setConversion(stringtostring);

      actors2.add(stringconstants);

      // Flow.add item to storage.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("item"));
      actors2.add(setstoragevalue);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.CollectionInsert
      CollectionInsert collectioninsert = new CollectionInsert();
      argOption = (AbstractArgumentOption) collectioninsert.getOptionManager().findByProperty("objectStorage");
      collectioninsert.setObjectStorage((StorageName) argOption.valueOf("item"));
      collectioninsert.setAfter(true);

      actors.add(collectioninsert);

      // Flow.add item to storage (2)
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("add item to storage (2)"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.add item to storage (2).StringConstants
      StringConstants stringconstants2 = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      List<BaseString> strings2 = new ArrayList<>();
      strings2.add((BaseString) argOption.valueOf("2"));
      stringconstants2.setStrings(strings2.toArray(new BaseString[0]));
      StringToString stringtostring2 = new StringToString();
      stringconstants2.setConversion(stringtostring2);

      actors3.add(stringconstants2);

      // Flow.add item to storage (2).SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("item"));
      actors3.add(setstoragevalue2);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.CollectionInsert (2)
      CollectionInsert collectioninsert2 = new CollectionInsert();
      argOption = (AbstractArgumentOption) collectioninsert2.getOptionManager().findByProperty("name");
      collectioninsert2.setName((String) argOption.valueOf("CollectionInsert (2)"));
      argOption = (AbstractArgumentOption) collectioninsert2.getOptionManager().findByProperty("objectStorage");
      collectioninsert2.setObjectStorage((StorageName) argOption.valueOf("item"));
      collectioninsert2.setAfter(true);

      actors.add(collectioninsert2);

      // Flow.add item to storage (3)
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("add item to storage (3)"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.add item to storage (3).StringConstants
      StringConstants stringconstants3 = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants3.getOptionManager().findByProperty("strings");
      List<BaseString> strings3 = new ArrayList<>();
      strings3.add((BaseString) argOption.valueOf("3"));
      stringconstants3.setStrings(strings3.toArray(new BaseString[0]));
      StringToString stringtostring3 = new StringToString();
      stringconstants3.setConversion(stringtostring3);

      actors4.add(stringconstants3);

      // Flow.add item to storage (3).SetStorageValue
      SetStorageValue setstoragevalue3 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue3.getOptionManager().findByProperty("storageName");
      setstoragevalue3.setStorageName((StorageName) argOption.valueOf("item"));
      actors4.add(setstoragevalue3);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger3);

      // Flow.CollectionInsert (3)
      CollectionInsert collectioninsert3 = new CollectionInsert();
      argOption = (AbstractArgumentOption) collectioninsert3.getOptionManager().findByProperty("name");
      collectioninsert3.setName((String) argOption.valueOf("CollectionInsert (3)"));
      argOption = (AbstractArgumentOption) collectioninsert3.getOptionManager().findByProperty("objectStorage");
      collectioninsert3.setObjectStorage((StorageName) argOption.valueOf("item"));
      collectioninsert3.setAfter(true);

      actors.add(collectioninsert3);

      // Flow.CollectionToArray
      CollectionToArray collectiontoarray = new CollectionToArray();
      actors.add(collectiontoarray);

      // Flow.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors.add(stringjoin);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors.add(dumpfile);
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

