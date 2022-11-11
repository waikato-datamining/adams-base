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
 * Mat5GetStructFieldTest.java
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
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.NewMat5Matrix;
import adams.flow.source.NewMat5Struct;
import adams.flow.source.Start;
import adams.flow.transformer.Mat5StructInfo.InfoType;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for Mat5GetStructField actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class Mat5GetStructFieldTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public Mat5GetStructFieldTest(String name) {
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
    return new TestSuite(Mat5GetStructFieldTest.class);
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
      mat5setmatrixelement.setIndex((Mat5ArrayElementIndex) argOption.valueOf("2;2"));
      argOption = (AbstractArgumentOption) mat5setmatrixelement.getOptionManager().findByProperty("value");
      mat5setmatrixelement.setValue((String) argOption.valueOf("2.2"));
      actors2.add(mat5setmatrixelement);

      // Flow.new matrix.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("mat1"));
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
      mat5setmatrixelement2.setIndex((Mat5ArrayElementIndex) argOption.valueOf("1;1;1"));
      argOption = (AbstractArgumentOption) mat5setmatrixelement2.getOptionManager().findByProperty("value");
      mat5setmatrixelement2.setValue((String) argOption.valueOf("1"));
      actors3.add(mat5setmatrixelement2);

      // Flow.new matrix (2).SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("mat2"));
      actors3.add(setstoragevalue2);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.new struct
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("new struct"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.new struct.NewMat5Struct
      NewMat5Struct newmat5struct = new NewMat5Struct();
      actors4.add(newmat5struct);

      // Flow.new struct.Mat5SetStructField (2)
      Mat5SetStructField mat5setstructfield = new Mat5SetStructField();
      argOption = (AbstractArgumentOption) mat5setstructfield.getOptionManager().findByProperty("name");
      mat5setstructfield.setName((String) argOption.valueOf("Mat5SetStructField (2)"));
      argOption = (AbstractArgumentOption) mat5setstructfield.getOptionManager().findByProperty("objectStorage");
      mat5setstructfield.setObjectStorage((StorageName) argOption.valueOf("mat2"));
      argOption = (AbstractArgumentOption) mat5setstructfield.getOptionManager().findByProperty("field");
      mat5setstructfield.setField((String) argOption.valueOf("matrix2"));
      actors4.add(mat5setstructfield);

      // Flow.new struct.Mat5SetStructField
      Mat5SetStructField mat5setstructfield2 = new Mat5SetStructField();
      argOption = (AbstractArgumentOption) mat5setstructfield2.getOptionManager().findByProperty("objectStorage");
      mat5setstructfield2.setObjectStorage((StorageName) argOption.valueOf("mat1"));
      argOption = (AbstractArgumentOption) mat5setstructfield2.getOptionManager().findByProperty("field");
      mat5setstructfield2.setField((String) argOption.valueOf("matrix1"));
      actors4.add(mat5setstructfield2);

      // Flow.new struct.Mat5StructInfo
      Mat5StructInfo mat5structinfo = new Mat5StructInfo();
      mat5structinfo.setOutputArray(true);

      argOption = (AbstractArgumentOption) mat5structinfo.getOptionManager().findByProperty("type");
      mat5structinfo.setType((InfoType) argOption.valueOf("FIELD_NAMES"));
      actors4.add(mat5structinfo);

      // Flow.new struct.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors4.add(stringjoin);

      // Flow.new struct.DumpFile
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

