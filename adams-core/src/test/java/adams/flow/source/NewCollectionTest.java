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
 * NewCollectionTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.VariableName;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.UnknownToUnknown;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ObjectRetriever.RetrievalType;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.transformer.CollectionInsert;
import adams.flow.transformer.CollectionToSequence;
import adams.flow.transformer.SetStorageValue;
import adams.flow.transformer.SetVariable;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for NewCollection actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class NewCollectionTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public NewCollectionTest(String name) {
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
    return new TestSuite(NewCollectionTest.class);
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

      // Flow.create list
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("create list"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.create list.NewCollection
      NewCollection newcollection = new NewCollection();
      actors2.add(newcollection);

      // Flow.create list.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("list"));
      actors2.add(setstoragevalue);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.fill in storage / update list
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("fill in storage / update list"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.fill in storage / update list.ForLoop
      ForLoop forloop = new ForLoop();
      actors3.add(forloop);

      // Flow.fill in storage / update list.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("name"));
      actors3.add(setvariable);

      // Flow.fill in storage / update list.SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      argOption.setVariable("@{name}");
      actors3.add(setstoragevalue2);

      // Flow.fill in storage / update list.add to list
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("add to list"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.fill in storage / update list.add to list.StorageValue
      StorageValue storagevalue = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue.getOptionManager().findByProperty("storageName");
      storagevalue.setStorageName((StorageName) argOption.valueOf("list"));
      UnknownToUnknown unknowntounknown = new UnknownToUnknown();
      storagevalue.setConversion(unknowntounknown);

      actors4.add(storagevalue);

      // Flow.fill in storage / update list.add to list.CollectionInsert
      CollectionInsert collectioninsert = new CollectionInsert();
      argOption = (AbstractArgumentOption) collectioninsert.getOptionManager().findByProperty("retrievalType");
      collectioninsert.setRetrievalType((RetrievalType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) collectioninsert.getOptionManager().findByProperty("objectStorage");
      argOption.setVariable("@{name}");
      collectioninsert.setAfter(true);

      actors4.add(collectioninsert);

      // Flow.fill in storage / update list.add to list.SetStorageValue
      SetStorageValue setstoragevalue3 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue3.getOptionManager().findByProperty("storageName");
      setstoragevalue3.setStorageName((StorageName) argOption.valueOf("list"));
      actors4.add(setstoragevalue3);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors3.add(trigger3);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.output
      Trigger trigger4 = new Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
      trigger4.setName((String) argOption.valueOf("output"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.output.StorageValue
      StorageValue storagevalue2 = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue2.getOptionManager().findByProperty("storageName");
      storagevalue2.setStorageName((StorageName) argOption.valueOf("list"));
      UnknownToUnknown unknowntounknown2 = new UnknownToUnknown();
      storagevalue2.setConversion(unknowntounknown2);

      actors5.add(storagevalue2);

      // Flow.output.CollectionToSequence
      CollectionToSequence collectiontosequence = new CollectionToSequence();
      actors5.add(collectiontosequence);

      // Flow.output.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

      actors5.add(dumpfile);
      trigger4.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger4);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

