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
 * Mat5FileAddArrayTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.Mat5ArrayDimensions;
import adams.core.base.Mat5ArrayElementIndex;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ObjectRetriever.RetrievalType;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.NewMat5File;
import adams.flow.source.NewMat5Matrix;
import adams.flow.source.Start;
import adams.flow.transformer.Mat5FileInfo.InfoType;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for Mat5FileAddArray actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class Mat5FileAddArrayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public Mat5FileAddArrayTest(String name) {
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
    return new TestSuite(Mat5FileAddArrayTest.class);
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

      // Flow.new matrix
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("new matrix"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.new matrix.NewMat5Matrix
      NewMat5Matrix newmat5matrix = new NewMat5Matrix();
      actors2.add(newmat5matrix);

      // Flow.new matrix.Mat5SetMatrixElement
      Mat5SetMatrixElement mat5setmatrixelement = new Mat5SetMatrixElement();
      argOption = (AbstractArgumentOption) mat5setmatrixelement.getOptionManager().findByProperty("index");
      mat5setmatrixelement.setIndex((Mat5ArrayElementIndex) argOption.valueOf("1;1"));
      argOption = (AbstractArgumentOption) mat5setmatrixelement.getOptionManager().findByProperty("value");
      mat5setmatrixelement.setValue((String) argOption.valueOf("11"));
      actors2.add(mat5setmatrixelement);

      // Flow.new matrix.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("matrix1"));
      actors2.add(setstoragevalue);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.new matrix (2)
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("new matrix (2)"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.new matrix (2).NewMat5Matrix
      NewMat5Matrix newmat5matrix2 = new NewMat5Matrix();
      argOption = (AbstractArgumentOption) newmat5matrix2.getOptionManager().findByProperty("dimensions");
      newmat5matrix2.setDimensions((Mat5ArrayDimensions) argOption.valueOf("2;2;2"));
      actors3.add(newmat5matrix2);

      // Flow.new matrix (2).Mat5SetMatrixElement
      Mat5SetMatrixElement mat5setmatrixelement2 = new Mat5SetMatrixElement();
      argOption = (AbstractArgumentOption) mat5setmatrixelement2.getOptionManager().findByProperty("index");
      mat5setmatrixelement2.setIndex((Mat5ArrayElementIndex) argOption.valueOf("2;2;2"));
      argOption = (AbstractArgumentOption) mat5setmatrixelement2.getOptionManager().findByProperty("value");
      mat5setmatrixelement2.setValue((String) argOption.valueOf("222"));
      actors3.add(mat5setmatrixelement2);

      // Flow.new matrix (2).SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("matrix2"));
      actors3.add(setstoragevalue2);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.create
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("create"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.create.NewMat5File
      NewMat5File newmat5file = new NewMat5File();
      actors4.add(newmat5file);

      // Flow.create.Mat5FileAddArray
      Mat5FileAddArray mat5fileaddarray = new Mat5FileAddArray();
      argOption = (AbstractArgumentOption) mat5fileaddarray.getOptionManager().findByProperty("retrievalType");
      mat5fileaddarray.setRetrievalType((RetrievalType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) mat5fileaddarray.getOptionManager().findByProperty("objectStorage");
      mat5fileaddarray.setObjectStorage((StorageName) argOption.valueOf("matrix1"));
      argOption = (AbstractArgumentOption) mat5fileaddarray.getOptionManager().findByProperty("entryName");
      mat5fileaddarray.setEntryName((String) argOption.valueOf("matrix1"));
      actors4.add(mat5fileaddarray);

      // Flow.create.Mat5FileAddArray (2)
      Mat5FileAddArray mat5fileaddarray2 = new Mat5FileAddArray();
      argOption = (AbstractArgumentOption) mat5fileaddarray2.getOptionManager().findByProperty("name");
      mat5fileaddarray2.setName((String) argOption.valueOf("Mat5FileAddArray (2)"));
      argOption = (AbstractArgumentOption) mat5fileaddarray2.getOptionManager().findByProperty("retrievalType");
      mat5fileaddarray2.setRetrievalType((RetrievalType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) mat5fileaddarray2.getOptionManager().findByProperty("objectStorage");
      mat5fileaddarray2.setObjectStorage((StorageName) argOption.valueOf("matrix2"));
      argOption = (AbstractArgumentOption) mat5fileaddarray2.getOptionManager().findByProperty("entryName");
      mat5fileaddarray2.setEntryName((String) argOption.valueOf("matrix2"));
      actors4.add(mat5fileaddarray2);

      // Flow.create.Mat5FileInfo
      Mat5FileInfo mat5fileinfo = new Mat5FileInfo();
      mat5fileinfo.setOutputArray(true);

      argOption = (AbstractArgumentOption) mat5fileinfo.getOptionManager().findByProperty("type");
      mat5fileinfo.setType((InfoType) argOption.valueOf("ENTRY_NAMES"));
      actors4.add(mat5fileinfo);

      // Flow.create.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors4.add(stringjoin);

      // Flow.create.DumpFile
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

