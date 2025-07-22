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
 * AppendArchiveTest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.io.TarArchiveManager;
import adams.core.io.ZipArchiveManager;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.control.postflowexecution.Null;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.CloseArchive;
import adams.flow.source.CombineVariables;
import adams.flow.source.FileSupplier;
import adams.flow.source.NewArchive;
import adams.flow.source.Start;
import adams.flow.transformer.AppendArchive.DataType;
import adams.flow.transformer.appendarchive.BytesObjectAppender;
import adams.flow.transformer.appendarchive.SerializedObjectAppender;
import adams.flow.transformer.appendarchive.StringObjectAppender;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for AppendArchive actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class AppendArchiveTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AppendArchiveTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("3267694739_4981d2f944_z.jpg");
    m_TestHelper.copyResourceToTmp("adams_logo.jpg");
    m_TestHelper.copyResourceToTmp("bodyfat.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("3267694739_4981d2f944_z.jpg");
    m_TestHelper.deleteFileFromTmp("adams_logo.jpg");
    m_TestHelper.deleteFileFromTmp("bodyfat.arff");

    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(AppendArchiveTest.class);
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

      // Flow.zip
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("zip"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.zip.NewArchive
      NewArchive newarchive = new NewArchive();
      ZipArchiveManager ziparchivemanager = new ZipArchiveManager();
      newarchive.setManager(ziparchivemanager);

      argOption = (AbstractArgumentOption) newarchive.getOptionManager().findByProperty("output");
      newarchive.setOutput((PlaceholderFile) argOption.valueOf("${TMP}/incremental.zip"));
      actors2.add(newarchive);

      // Flow.zip.add binary file
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("add binary file"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.zip.add binary file.AppendArchive
      AppendArchive appendarchive = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive.getOptionManager().findByProperty("inputFile");
      appendarchive.setInputFile((PlaceholderFile) argOption.valueOf("${TMP}/3267694739_4981d2f944_z.jpg"));
      SerializedObjectAppender serializedobjectappender = new SerializedObjectAppender();
      appendarchive.setObjectAppender(serializedobjectappender);

      argOption = (AbstractArgumentOption) appendarchive.getOptionManager().findByProperty("entryName");
      appendarchive.setEntryName((String) argOption.valueOf("image.jpg"));
      actors3.add(appendarchive);
      tee.setActors(actors3.toArray(new Actor[0]));

      actors2.add(tee);

      // Flow.zip.add text file
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("add text file"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.zip.add text file.AppendArchive
      AppendArchive appendarchive2 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive2.getOptionManager().findByProperty("inputFile");
      appendarchive2.setInputFile((PlaceholderFile) argOption.valueOf("${TMP}/bodyfat.arff"));
      SerializedObjectAppender serializedobjectappender2 = new SerializedObjectAppender();
      appendarchive2.setObjectAppender(serializedobjectappender2);

      argOption = (AbstractArgumentOption) appendarchive2.getOptionManager().findByProperty("entryName");
      appendarchive2.setEntryName((String) argOption.valueOf("dataset.arff"));
      actors4.add(appendarchive2);
      tee2.setActors(actors4.toArray(new Actor[0]));

      actors2.add(tee2);

      // Flow.zip.add text object
      Tee tee3 = new Tee();
      argOption = (AbstractArgumentOption) tee3.getOptionManager().findByProperty("name");
      tee3.setName((String) argOption.valueOf("add text object"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.zip.add text object.add string to storage
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("add string to storage"));
      List<Actor> actors6 = new ArrayList<>();

      // Flow.zip.add text object.add string to storage.CombineVariables
      CombineVariables combinevariables = new CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables.getOptionManager().findByProperty("expression");
      combinevariables.setExpression((BaseText) argOption.valueOf("This is just a simple string to store in storage."));
      StringToString stringtostring = new StringToString();
      combinevariables.setConversion(stringtostring);

      actors6.add(combinevariables);

      // Flow.zip.add text object.add string to storage.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("text"));
      actors6.add(setstoragevalue);
      trigger2.setActors(actors6.toArray(new Actor[0]));

      actors5.add(trigger2);

      // Flow.zip.add text object.AppendArchive
      AppendArchive appendarchive3 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive3.getOptionManager().findByProperty("type");
      appendarchive3.setType((DataType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) appendarchive3.getOptionManager().findByProperty("storageName");
      appendarchive3.setStorageName((StorageName) argOption.valueOf("text"));
      StringObjectAppender stringobjectappender = new StringObjectAppender();
      appendarchive3.setObjectAppender(stringobjectappender);

      argOption = (AbstractArgumentOption) appendarchive3.getOptionManager().findByProperty("entryName");
      appendarchive3.setEntryName((String) argOption.valueOf("text.txt"));
      actors5.add(appendarchive3);
      tee3.setActors(actors5.toArray(new Actor[0]));

      actors2.add(tee3);

      // Flow.zip.add bytes object
      Tee tee4 = new Tee();
      argOption = (AbstractArgumentOption) tee4.getOptionManager().findByProperty("name");
      tee4.setName((String) argOption.valueOf("add bytes object"));
      List<Actor> actors7 = new ArrayList<>();

      // Flow.zip.add bytes object.load binary file as bytes
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("load binary file as bytes"));
      List<Actor> actors8 = new ArrayList<>();

      // Flow.zip.add bytes object.load binary file as bytes.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/adams_logo.jpg"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      filesupplier.setUseForwardSlashes(true);

      actors8.add(filesupplier);

      // Flow.zip.add bytes object.load binary file as bytes.BinaryFileReader
      BinaryFileReader binaryfilereader = new BinaryFileReader();
      binaryfilereader.setOutputArray(true);

      actors8.add(binaryfilereader);

      // Flow.zip.add bytes object.load binary file as bytes.SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("bytes"));
      actors8.add(setstoragevalue2);
      trigger3.setActors(actors8.toArray(new Actor[0]));

      actors7.add(trigger3);

      // Flow.zip.add bytes object.AppendArchive
      AppendArchive appendarchive4 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive4.getOptionManager().findByProperty("type");
      appendarchive4.setType((DataType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) appendarchive4.getOptionManager().findByProperty("storageName");
      appendarchive4.setStorageName((StorageName) argOption.valueOf("bytes"));
      BytesObjectAppender bytesobjectappender = new BytesObjectAppender();
      appendarchive4.setObjectAppender(bytesobjectappender);

      argOption = (AbstractArgumentOption) appendarchive4.getOptionManager().findByProperty("entryName");
      appendarchive4.setEntryName((String) argOption.valueOf("logo.jpg"));
      actors7.add(appendarchive4);
      tee4.setActors(actors7.toArray(new Actor[0]));

      actors2.add(tee4);

      // Flow.zip.add serialized object
      Tee tee5 = new Tee();
      argOption = (AbstractArgumentOption) tee5.getOptionManager().findByProperty("name");
      tee5.setName((String) argOption.valueOf("add serialized object"));
      List<Actor> actors9 = new ArrayList<>();

      // Flow.zip.add serialized object.AppendArchive
      AppendArchive appendarchive5 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive5.getOptionManager().findByProperty("type");
      appendarchive5.setType((DataType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) appendarchive5.getOptionManager().findByProperty("storageName");
      appendarchive5.setStorageName((StorageName) argOption.valueOf("text"));
      SerializedObjectAppender serializedobjectappender3 = new SerializedObjectAppender();
      appendarchive5.setObjectAppender(serializedobjectappender3);

      argOption = (AbstractArgumentOption) appendarchive5.getOptionManager().findByProperty("entryName");
      appendarchive5.setEntryName((String) argOption.valueOf("models/object.ser"));
      actors9.add(appendarchive5);
      tee5.setActors(actors9.toArray(new Actor[0]));

      actors2.add(tee5);

      // Flow.zip.CloseArchive
      CloseArchive closearchive = new CloseArchive();
      actors2.add(closearchive);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.tar
      Trigger trigger4 = new Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
      trigger4.setName((String) argOption.valueOf("tar"));
      List<Actor> actors10 = new ArrayList<>();

      // Flow.tar.NewArchive
      NewArchive newarchive2 = new NewArchive();
      TarArchiveManager tararchivemanager = new TarArchiveManager();
      newarchive2.setManager(tararchivemanager);

      argOption = (AbstractArgumentOption) newarchive2.getOptionManager().findByProperty("output");
      newarchive2.setOutput((PlaceholderFile) argOption.valueOf("${TMP}/incremental.tar.gz"));
      actors10.add(newarchive2);

      // Flow.tar.add binary file
      Tee tee6 = new Tee();
      argOption = (AbstractArgumentOption) tee6.getOptionManager().findByProperty("name");
      tee6.setName((String) argOption.valueOf("add binary file"));
      List<Actor> actors11 = new ArrayList<>();

      // Flow.tar.add binary file.AppendArchive
      AppendArchive appendarchive6 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive6.getOptionManager().findByProperty("inputFile");
      appendarchive6.setInputFile((PlaceholderFile) argOption.valueOf("${TMP}/3267694739_4981d2f944_z.jpg"));
      SerializedObjectAppender serializedobjectappender4 = new SerializedObjectAppender();
      appendarchive6.setObjectAppender(serializedobjectappender4);

      argOption = (AbstractArgumentOption) appendarchive6.getOptionManager().findByProperty("entryName");
      appendarchive6.setEntryName((String) argOption.valueOf("image.jpg"));
      actors11.add(appendarchive6);
      tee6.setActors(actors11.toArray(new Actor[0]));

      actors10.add(tee6);

      // Flow.tar.add text file
      Tee tee7 = new Tee();
      argOption = (AbstractArgumentOption) tee7.getOptionManager().findByProperty("name");
      tee7.setName((String) argOption.valueOf("add text file"));
      List<Actor> actors12 = new ArrayList<>();

      // Flow.tar.add text file.AppendArchive
      AppendArchive appendarchive7 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive7.getOptionManager().findByProperty("inputFile");
      appendarchive7.setInputFile((PlaceholderFile) argOption.valueOf("${TMP}/bodyfat.arff"));
      SerializedObjectAppender serializedobjectappender5 = new SerializedObjectAppender();
      appendarchive7.setObjectAppender(serializedobjectappender5);

      argOption = (AbstractArgumentOption) appendarchive7.getOptionManager().findByProperty("entryName");
      appendarchive7.setEntryName((String) argOption.valueOf("dataset.arff"));
      actors12.add(appendarchive7);
      tee7.setActors(actors12.toArray(new Actor[0]));

      actors10.add(tee7);

      // Flow.tar.add text object
      Tee tee8 = new Tee();
      argOption = (AbstractArgumentOption) tee8.getOptionManager().findByProperty("name");
      tee8.setName((String) argOption.valueOf("add text object"));
      List<Actor> actors13 = new ArrayList<>();

      // Flow.tar.add text object.add string to storage
      Trigger trigger5 = new Trigger();
      argOption = (AbstractArgumentOption) trigger5.getOptionManager().findByProperty("name");
      trigger5.setName((String) argOption.valueOf("add string to storage"));
      List<Actor> actors14 = new ArrayList<>();

      // Flow.tar.add text object.add string to storage.CombineVariables
      CombineVariables combinevariables2 = new CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables2.getOptionManager().findByProperty("expression");
      combinevariables2.setExpression((BaseText) argOption.valueOf("This is just a simple string to store in storage."));
      StringToString stringtostring2 = new StringToString();
      combinevariables2.setConversion(stringtostring2);

      actors14.add(combinevariables2);

      // Flow.tar.add text object.add string to storage.SetStorageValue
      SetStorageValue setstoragevalue3 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue3.getOptionManager().findByProperty("storageName");
      setstoragevalue3.setStorageName((StorageName) argOption.valueOf("text"));
      actors14.add(setstoragevalue3);
      trigger5.setActors(actors14.toArray(new Actor[0]));

      actors13.add(trigger5);

      // Flow.tar.add text object.AppendArchive
      AppendArchive appendarchive8 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive8.getOptionManager().findByProperty("type");
      appendarchive8.setType((DataType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) appendarchive8.getOptionManager().findByProperty("storageName");
      appendarchive8.setStorageName((StorageName) argOption.valueOf("text"));
      StringObjectAppender stringobjectappender2 = new StringObjectAppender();
      appendarchive8.setObjectAppender(stringobjectappender2);

      argOption = (AbstractArgumentOption) appendarchive8.getOptionManager().findByProperty("entryName");
      appendarchive8.setEntryName((String) argOption.valueOf("text.txt"));
      actors13.add(appendarchive8);
      tee8.setActors(actors13.toArray(new Actor[0]));

      actors10.add(tee8);

      // Flow.tar.add bytes object
      Tee tee9 = new Tee();
      argOption = (AbstractArgumentOption) tee9.getOptionManager().findByProperty("name");
      tee9.setName((String) argOption.valueOf("add bytes object"));
      List<Actor> actors15 = new ArrayList<>();

      // Flow.tar.add bytes object.load binary file as bytes
      Trigger trigger6 = new Trigger();
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("name");
      trigger6.setName((String) argOption.valueOf("load binary file as bytes"));
      List<Actor> actors16 = new ArrayList<>();

      // Flow.tar.add bytes object.load binary file as bytes.FileSupplier
      FileSupplier filesupplier2 = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files2 = new ArrayList<>();
      files2.add((PlaceholderFile) argOption.valueOf("${TMP}/adams_logo.jpg"));
      filesupplier2.setFiles(files2.toArray(new PlaceholderFile[0]));
      filesupplier2.setUseForwardSlashes(true);

      actors16.add(filesupplier2);

      // Flow.tar.add bytes object.load binary file as bytes.BinaryFileReader
      BinaryFileReader binaryfilereader2 = new BinaryFileReader();
      binaryfilereader2.setOutputArray(true);

      actors16.add(binaryfilereader2);

      // Flow.tar.add bytes object.load binary file as bytes.SetStorageValue
      SetStorageValue setstoragevalue4 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue4.getOptionManager().findByProperty("storageName");
      setstoragevalue4.setStorageName((StorageName) argOption.valueOf("bytes"));
      actors16.add(setstoragevalue4);
      trigger6.setActors(actors16.toArray(new Actor[0]));

      actors15.add(trigger6);

      // Flow.tar.add bytes object.AppendArchive
      AppendArchive appendarchive9 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive9.getOptionManager().findByProperty("type");
      appendarchive9.setType((DataType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) appendarchive9.getOptionManager().findByProperty("storageName");
      appendarchive9.setStorageName((StorageName) argOption.valueOf("bytes"));
      BytesObjectAppender bytesobjectappender2 = new BytesObjectAppender();
      appendarchive9.setObjectAppender(bytesobjectappender2);

      argOption = (AbstractArgumentOption) appendarchive9.getOptionManager().findByProperty("entryName");
      appendarchive9.setEntryName((String) argOption.valueOf("logo.jpg"));
      actors15.add(appendarchive9);
      tee9.setActors(actors15.toArray(new Actor[0]));

      actors10.add(tee9);

      // Flow.tar.add serialized object
      Tee tee10 = new Tee();
      argOption = (AbstractArgumentOption) tee10.getOptionManager().findByProperty("name");
      tee10.setName((String) argOption.valueOf("add serialized object"));
      List<Actor> actors17 = new ArrayList<>();

      // Flow.tar.add serialized object.AppendArchive
      AppendArchive appendarchive10 = new AppendArchive();
      argOption = (AbstractArgumentOption) appendarchive10.getOptionManager().findByProperty("type");
      appendarchive10.setType((DataType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) appendarchive10.getOptionManager().findByProperty("storageName");
      appendarchive10.setStorageName((StorageName) argOption.valueOf("text"));
      SerializedObjectAppender serializedobjectappender6 = new SerializedObjectAppender();
      appendarchive10.setObjectAppender(serializedobjectappender6);

      argOption = (AbstractArgumentOption) appendarchive10.getOptionManager().findByProperty("entryName");
      appendarchive10.setEntryName((String) argOption.valueOf("models/object.ser"));
      actors17.add(appendarchive10);
      tee10.setActors(actors17.toArray(new Actor[0]));

      actors10.add(tee10);

      // Flow.tar.CloseArchive
      CloseArchive closearchive2 = new CloseArchive();
      actors10.add(closearchive2);
      trigger4.setActors(actors10.toArray(new Actor[0]));

      actors.add(trigger4);
      flow.setActors(actors.toArray(new Actor[0]));

      Null null_ = new Null();
      flow.setExecuteOnError(null_);

      Null null_2 = new Null();
      flow.setExecuteOnFinish(null_2);

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

