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
 * MultiMapOperationTest.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseAnnotation;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.AnyToString;
import adams.data.conversion.MapToKeyValuePairs;
import adams.data.conversion.ObjectToObject;
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
import adams.flow.source.NewMap;
import adams.flow.source.Start;
import adams.flow.source.StorageValuesArray;
import adams.flow.transformer.multimapoperation.CommonKeys;
import adams.flow.transformer.multimapoperation.Merge;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for MultiMapOperation actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class MultiMapOperationTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MultiMapOperationTest(String name) {
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
    return new TestSuite(MultiMapOperationTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("annotations");
      flow.setAnnotations((BaseAnnotation) argOption.valueOf("Shows how to perform operations involving multiple maps."));
      List<Actor> actors = new ArrayList<>();

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.new map 1
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("new map 1"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.new map 1.NewMap
      NewMap newmap = new NewMap();
      actors2.add(newmap);

      // Flow.new map 1.SetMapValue
      SetMapValue setmapvalue = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("key");
      setmapvalue.setKey((String) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("value");
      setmapvalue.setValue((String) argOption.valueOf("a"));
      ObjectToObject objecttoobject = new ObjectToObject();
      setmapvalue.setConversion(objecttoobject);

      actors2.add(setmapvalue);

      // Flow.new map 1.SetMapValue (2)
      SetMapValue setmapvalue2 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("name");
      setmapvalue2.setName((String) argOption.valueOf("SetMapValue (2)"));
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("key");
      setmapvalue2.setKey((String) argOption.valueOf("b"));
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("value");
      setmapvalue2.setValue((String) argOption.valueOf("b"));
      ObjectToObject objecttoobject2 = new ObjectToObject();
      setmapvalue2.setConversion(objecttoobject2);

      actors2.add(setmapvalue2);

      // Flow.new map 1.SetMapValue (3)
      SetMapValue setmapvalue3 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("name");
      setmapvalue3.setName((String) argOption.valueOf("SetMapValue (3)"));
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("key");
      setmapvalue3.setKey((String) argOption.valueOf("c"));
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("value");
      setmapvalue3.setValue((String) argOption.valueOf("c"));
      ObjectToObject objecttoobject3 = new ObjectToObject();
      setmapvalue3.setConversion(objecttoobject3);

      actors2.add(setmapvalue3);

      // Flow.new map 1.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("map1"));
      actors2.add(setstoragevalue);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.new map 2
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("new map 2"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.new map 2.NewMap
      NewMap newmap2 = new NewMap();
      actors3.add(newmap2);

      // Flow.new map 2.SetMapValue
      SetMapValue setmapvalue4 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue4.getOptionManager().findByProperty("key");
      setmapvalue4.setKey((String) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setmapvalue4.getOptionManager().findByProperty("value");
      setmapvalue4.setValue((String) argOption.valueOf("1"));
      ObjectToObject objecttoobject4 = new ObjectToObject();
      setmapvalue4.setConversion(objecttoobject4);

      actors3.add(setmapvalue4);

      // Flow.new map 2.SetMapValue (2)
      SetMapValue setmapvalue5 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue5.getOptionManager().findByProperty("name");
      setmapvalue5.setName((String) argOption.valueOf("SetMapValue (2)"));
      argOption = (AbstractArgumentOption) setmapvalue5.getOptionManager().findByProperty("key");
      setmapvalue5.setKey((String) argOption.valueOf("b"));
      argOption = (AbstractArgumentOption) setmapvalue5.getOptionManager().findByProperty("value");
      setmapvalue5.setValue((String) argOption.valueOf("2"));
      ObjectToObject objecttoobject5 = new ObjectToObject();
      setmapvalue5.setConversion(objecttoobject5);

      actors3.add(setmapvalue5);

      // Flow.new map 2.SetMapValue (3)
      SetMapValue setmapvalue6 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue6.getOptionManager().findByProperty("name");
      setmapvalue6.setName((String) argOption.valueOf("SetMapValue (3)"));
      argOption = (AbstractArgumentOption) setmapvalue6.getOptionManager().findByProperty("key");
      setmapvalue6.setKey((String) argOption.valueOf("d"));
      argOption = (AbstractArgumentOption) setmapvalue6.getOptionManager().findByProperty("value");
      setmapvalue6.setValue((String) argOption.valueOf("4"));
      ObjectToObject objecttoobject6 = new ObjectToObject();
      setmapvalue6.setConversion(objecttoobject6);

      actors3.add(setmapvalue6);

      // Flow.new map 2.SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("map2"));
      actors3.add(setstoragevalue2);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.merge (no overwrite)
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("merge (no overwrite)"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.merge (no overwrite).StorageValuesArray
      StorageValuesArray storagevaluesarray = new StorageValuesArray();
      argOption = (AbstractArgumentOption) storagevaluesarray.getOptionManager().findByProperty("storageNames");
      List<StorageName> storagenames = new ArrayList<>();
      storagenames.add((StorageName) argOption.valueOf("map1"));
      storagenames.add((StorageName) argOption.valueOf("map2"));
      storagevaluesarray.setStorageNames(storagenames.toArray(new StorageName[0]));
      UnknownToUnknown unknowntounknown = new UnknownToUnknown();
      storagevaluesarray.setConversion(unknowntounknown);

      actors4.add(storagevaluesarray);

      // Flow.merge (no overwrite).MultiMapOperation
      MultiMapOperation multimapoperation = new MultiMapOperation();
      Merge merge = new Merge();
      multimapoperation.setOperation(merge);

      actors4.add(multimapoperation);

      // Flow.merge (no overwrite).Convert
      Convert convert = new Convert();
      MapToKeyValuePairs maptokeyvaluepairs = new MapToKeyValuePairs();
      convert.setConversion(maptokeyvaluepairs);

      actors4.add(convert);

      // Flow.merge (no overwrite).ArrayToSequence
      ArrayToSequence arraytosequence = new ArrayToSequence();
      actors4.add(arraytosequence);

      // Flow.merge (no overwrite).Convert (2)
      Convert convert2 = new Convert();
      argOption = (AbstractArgumentOption) convert2.getOptionManager().findByProperty("name");
      convert2.setName((String) argOption.valueOf("Convert (2)"));
      AnyToString anytostring = new AnyToString();
      convert2.setConversion(anytostring);

      actors4.add(convert2);

      // Flow.merge (no overwrite).DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

      actors4.add(dumpfile);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger3);

      // Flow.merge (overwrite)
      Trigger trigger4 = new Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
      trigger4.setName((String) argOption.valueOf("merge (overwrite)"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.merge (overwrite).StorageValuesArray
      StorageValuesArray storagevaluesarray2 = new StorageValuesArray();
      argOption = (AbstractArgumentOption) storagevaluesarray2.getOptionManager().findByProperty("storageNames");
      List<StorageName> storagenames2 = new ArrayList<>();
      storagenames2.add((StorageName) argOption.valueOf("map1"));
      storagenames2.add((StorageName) argOption.valueOf("map2"));
      storagevaluesarray2.setStorageNames(storagenames2.toArray(new StorageName[0]));
      UnknownToUnknown unknowntounknown2 = new UnknownToUnknown();
      storagevaluesarray2.setConversion(unknowntounknown2);

      actors5.add(storagevaluesarray2);

      // Flow.merge (overwrite).MultiMapOperation
      MultiMapOperation multimapoperation2 = new MultiMapOperation();
      Merge merge2 = new Merge();
      merge2.setOverwrite(true);

      multimapoperation2.setOperation(merge2);

      actors5.add(multimapoperation2);

      // Flow.merge (overwrite).Convert
      Convert convert3 = new Convert();
      MapToKeyValuePairs maptokeyvaluepairs2 = new MapToKeyValuePairs();
      convert3.setConversion(maptokeyvaluepairs2);

      actors5.add(convert3);

      // Flow.merge (overwrite).ArrayToSequence
      ArrayToSequence arraytosequence2 = new ArrayToSequence();
      actors5.add(arraytosequence2);

      // Flow.merge (overwrite).Convert (2)
      Convert convert4 = new Convert();
      argOption = (AbstractArgumentOption) convert4.getOptionManager().findByProperty("name");
      convert4.setName((String) argOption.valueOf("Convert (2)"));
      AnyToString anytostring2 = new AnyToString();
      convert4.setConversion(anytostring2);

      actors5.add(convert4);

      // Flow.merge (overwrite).DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile2.setAppend(true);

      actors5.add(dumpfile2);
      trigger4.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger4);

      // Flow.common keys
      Trigger trigger5 = new Trigger();
      argOption = (AbstractArgumentOption) trigger5.getOptionManager().findByProperty("name");
      trigger5.setName((String) argOption.valueOf("common keys"));
      List<Actor> actors6 = new ArrayList<>();

      // Flow.common keys.StorageValuesArray
      StorageValuesArray storagevaluesarray3 = new StorageValuesArray();
      argOption = (AbstractArgumentOption) storagevaluesarray3.getOptionManager().findByProperty("storageNames");
      List<StorageName> storagenames3 = new ArrayList<>();
      storagenames3.add((StorageName) argOption.valueOf("map1"));
      storagenames3.add((StorageName) argOption.valueOf("map2"));
      storagevaluesarray3.setStorageNames(storagenames3.toArray(new StorageName[0]));
      UnknownToUnknown unknowntounknown3 = new UnknownToUnknown();
      storagevaluesarray3.setConversion(unknowntounknown3);

      actors6.add(storagevaluesarray3);

      // Flow.common keys.MultiMapOperation
      MultiMapOperation multimapoperation3 = new MultiMapOperation();
      CommonKeys commonkeys = new CommonKeys();
      multimapoperation3.setOperation(commonkeys);

      actors6.add(multimapoperation3);

      // Flow.common keys.ArrayToSequence
      ArrayToSequence arraytosequence3 = new ArrayToSequence();
      actors6.add(arraytosequence3);

      // Flow.common keys.Convert
      Convert convert5 = new Convert();
      AnyToString anytostring3 = new AnyToString();
      convert5.setConversion(anytostring3);

      actors6.add(convert5);

      // Flow.common keys.DumpFile
      DumpFile dumpfile3 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile3.getOptionManager().findByProperty("outputFile");
      dumpfile3.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile3.setAppend(true);

      actors6.add(dumpfile3);
      trigger5.setActors(actors6.toArray(new Actor[0]));

      actors.add(trigger5);
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

