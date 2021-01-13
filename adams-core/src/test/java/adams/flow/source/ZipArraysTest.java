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
 * ZipArraysTest.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.AnyToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.ArrayProcess;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SetStorageValue;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for ZipArrays actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class ZipArraysTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ZipArraysTest(String name) {
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
    return new TestSuite(ZipArraysTest.class);
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

      // Flow.same type
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("same type"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.same type.1st array
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("1st array"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.same type.1st array.ForLoop
      ForLoop forloop = new ForLoop();
      forloop.setOutputArray(true);

      actors3.add(forloop);

      // Flow.same type.1st array.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("array1"));
      actors3.add(setstoragevalue);
      trigger.setActors(actors3.toArray(new Actor[0]));

      actors2.add(trigger);

      // Flow.same type.2nd array
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("2nd array"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.same type.2nd array.ForLoop
      ForLoop forloop2 = new ForLoop();
      argOption = (AbstractArgumentOption) forloop2.getOptionManager().findByProperty("loopLower");
      forloop2.setLoopLower((Integer) argOption.valueOf("11"));
      argOption = (AbstractArgumentOption) forloop2.getOptionManager().findByProperty("loopUpper");
      forloop2.setLoopUpper((Integer) argOption.valueOf("20"));
      forloop2.setOutputArray(true);

      actors4.add(forloop2);

      // Flow.same type.2nd array.SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("array2"));
      actors4.add(setstoragevalue2);
      trigger2.setActors(actors4.toArray(new Actor[0]));

      actors2.add(trigger2);

      // Flow.same type.zip
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("zip"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.same type.zip.ZipArrays
      ZipArrays ziparrays = new ZipArrays();
      argOption = (AbstractArgumentOption) ziparrays.getOptionManager().findByProperty("storageNames");
      List<StorageName> storagenames = new ArrayList<>();
      storagenames.add((StorageName) argOption.valueOf("array1"));
      storagenames.add((StorageName) argOption.valueOf("array2"));
      ziparrays.setStorageNames(storagenames.toArray(new StorageName[0]));
      actors5.add(ziparrays);

      // Flow.same type.zip.ArrayToSequence
      ArrayToSequence arraytosequence = new ArrayToSequence();
      actors5.add(arraytosequence);

      // Flow.same type.zip.Convert
      Convert convert = new Convert();
      AnyToString anytostring = new AnyToString();
      convert.setConversion(anytostring);

      actors5.add(convert);

      // Flow.same type.zip.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

      actors5.add(dumpfile);
      trigger3.setActors(actors5.toArray(new Actor[0]));

      actors2.add(trigger3);
      tee.setActors(actors2.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.mixed types
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("mixed types"));
      List<Actor> actors6 = new ArrayList<>();

      // Flow.mixed types.1st array
      Trigger trigger4 = new Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
      trigger4.setName((String) argOption.valueOf("1st array"));
      List<Actor> actors7 = new ArrayList<>();

      // Flow.mixed types.1st array.ForLoop
      ForLoop forloop3 = new ForLoop();
      forloop3.setOutputArray(true);

      actors7.add(forloop3);

      // Flow.mixed types.1st array.SetStorageValue
      SetStorageValue setstoragevalue3 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue3.getOptionManager().findByProperty("storageName");
      setstoragevalue3.setStorageName((StorageName) argOption.valueOf("array1"));
      actors7.add(setstoragevalue3);
      trigger4.setActors(actors7.toArray(new Actor[0]));

      actors6.add(trigger4);

      // Flow.mixed types.2nd array
      Trigger trigger5 = new Trigger();
      argOption = (AbstractArgumentOption) trigger5.getOptionManager().findByProperty("name");
      trigger5.setName((String) argOption.valueOf("2nd array"));
      List<Actor> actors8 = new ArrayList<>();

      // Flow.mixed types.2nd array.ForLoop
      ForLoop forloop4 = new ForLoop();
      argOption = (AbstractArgumentOption) forloop4.getOptionManager().findByProperty("loopLower");
      forloop4.setLoopLower((Integer) argOption.valueOf("11"));
      argOption = (AbstractArgumentOption) forloop4.getOptionManager().findByProperty("loopUpper");
      forloop4.setLoopUpper((Integer) argOption.valueOf("20"));
      forloop4.setOutputArray(true);

      actors8.add(forloop4);

      // Flow.mixed types.2nd array.ArrayProcess
      ArrayProcess arrayprocess = new ArrayProcess();
      List<Actor> actors9 = new ArrayList<>();

      // Flow.mixed types.2nd array.ArrayProcess.Convert
      Convert convert2 = new Convert();
      AnyToString anytostring2 = new AnyToString();
      convert2.setConversion(anytostring2);

      actors9.add(convert2);
      arrayprocess.setActors(actors9.toArray(new Actor[0]));

      actors8.add(arrayprocess);

      // Flow.mixed types.2nd array.SetStorageValue
      SetStorageValue setstoragevalue4 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue4.getOptionManager().findByProperty("storageName");
      setstoragevalue4.setStorageName((StorageName) argOption.valueOf("array2"));
      actors8.add(setstoragevalue4);
      trigger5.setActors(actors8.toArray(new Actor[0]));

      actors6.add(trigger5);

      // Flow.mixed types.zip
      Trigger trigger6 = new Trigger();
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("name");
      trigger6.setName((String) argOption.valueOf("zip"));
      List<Actor> actors10 = new ArrayList<>();

      // Flow.mixed types.zip.ZipArrays
      ZipArrays ziparrays2 = new ZipArrays();
      argOption = (AbstractArgumentOption) ziparrays2.getOptionManager().findByProperty("storageNames");
      List<StorageName> storagenames2 = new ArrayList<>();
      storagenames2.add((StorageName) argOption.valueOf("array1"));
      storagenames2.add((StorageName) argOption.valueOf("array2"));
      ziparrays2.setStorageNames(storagenames2.toArray(new StorageName[0]));
      actors10.add(ziparrays2);

      // Flow.mixed types.zip.ArrayToSequence
      ArrayToSequence arraytosequence2 = new ArrayToSequence();
      actors10.add(arraytosequence2);

      // Flow.mixed types.zip.Convert
      Convert convert3 = new Convert();
      AnyToString anytostring3 = new AnyToString();
      convert3.setConversion(anytostring3);

      actors10.add(convert3);

      // Flow.mixed types.zip.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile2.setAppend(true);

      actors10.add(dumpfile2);
      trigger6.setActors(actors10.toArray(new Actor[0]));

      actors6.add(trigger6);
      tee2.setActors(actors6.toArray(new Actor[0]));

      actors.add(tee2);
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

