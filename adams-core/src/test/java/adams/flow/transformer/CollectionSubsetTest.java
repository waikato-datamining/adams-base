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
 * CollectionSubsetTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Range;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.AnyToString;
import adams.data.conversion.UnknownToUnknown;
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
import adams.flow.source.ForLoop;
import adams.flow.source.NewCollection;
import adams.flow.source.Start;
import adams.flow.source.StorageValue;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for CollectionSubset actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class CollectionSubsetTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CollectionSubsetTest(String name) {
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
    return new TestSuite(CollectionSubsetTest.class);
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

      // Flow.init
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("init"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.init.NewCollection
      NewCollection newcollection = new NewCollection();
      actors2.add(newcollection);

      // Flow.init.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("list"));
      actors2.add(setstoragevalue);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.add
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("add"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.add.ForLoop
      ForLoop forloop = new ForLoop();
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopUpper");
      forloop.setLoopUpper((Integer) argOption.valueOf("20"));
      actors3.add(forloop);

      // Flow.add.StorageCollectionInsert
      StorageCollectionInsert storagecollectioninsert = new StorageCollectionInsert();
      argOption = (AbstractArgumentOption) storagecollectioninsert.getOptionManager().findByProperty("storageName");
      storagecollectioninsert.setStorageName((StorageName) argOption.valueOf("list"));
      storagecollectioninsert.setAfter(true);

      actors3.add(storagecollectioninsert);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.subset
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("subset"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.subset.StorageValue
      StorageValue storagevalue = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue.getOptionManager().findByProperty("storageName");
      storagevalue.setStorageName((StorageName) argOption.valueOf("list"));
      UnknownToUnknown unknowntounknown = new UnknownToUnknown();
      storagevalue.setConversion(unknowntounknown);

      actors4.add(storagevalue);

      // Flow.subset.CollectionSubset
      CollectionSubset collectionsubset = new CollectionSubset();
      argOption = (AbstractArgumentOption) collectionsubset.getOptionManager().findByProperty("elements");
      collectionsubset.setElements((Range) argOption.valueOf("5-15"));
      actors4.add(collectionsubset);

      // Flow.subset.Convert
      Convert convert = new Convert();
      AnyToString anytostring = new AnyToString();
      convert.setConversion(anytostring);

      actors4.add(convert);

      // Flow.subset.DumpFile
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

