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
 * SetStorageFlagTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.StorageFlagSet;
import adams.flow.control.Flow;
import adams.flow.control.IfThenElse;
import adams.flow.control.Sequence;
import adams.flow.control.StorageName;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SetStorageFlag actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetStorageFlagTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetStorageFlagTest(String name) {
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
    return new TestSuite(SetStorageFlagTest.class);
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

      // Flow.StringConstants
      StringConstants stringconstants = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("hello"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      StringToString stringtostring = new StringToString();
      stringconstants.setConversion(stringtostring);

      actors.add(stringconstants);

      // Flow.SetStorageFlag
      SetStorageFlag setstorageflag = new SetStorageFlag();
      argOption = (AbstractArgumentOption) setstorageflag.getOptionManager().findByProperty("storageName");
      setstorageflag.setStorageName((StorageName) argOption.valueOf("flag"));
      setstorageflag.setFlagValue(false);

      actors.add(setstorageflag);

      // Flow.IfThenElse
      IfThenElse ifthenelse = new IfThenElse();
      StorageFlagSet storageflagset = new StorageFlagSet();
      argOption = (AbstractArgumentOption) storageflagset.getOptionManager().findByProperty("storageName");
      storageflagset.setStorageName((StorageName) argOption.valueOf("flag"));
      ifthenelse.setCondition(storageflagset);


      // Flow.IfThenElse.then
      Sequence sequence = new Sequence();
      argOption = (AbstractArgumentOption) sequence.getOptionManager().findByProperty("name");
      sequence.setName((String) argOption.valueOf("then"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.IfThenElse.then.StringInsert
      StringInsert stringinsert = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("position");
      stringinsert.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("value");
      stringinsert.setValue((BaseString) argOption.valueOf("set: "));
      actors2.add(stringinsert);

      // Flow.IfThenElse.then.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors2.add(dumpfile);
      sequence.setActors(actors2.toArray(new Actor[0]));

      ifthenelse.setThenActor(sequence);


      // Flow.IfThenElse.else
      Sequence sequence2 = new Sequence();
      argOption = (AbstractArgumentOption) sequence2.getOptionManager().findByProperty("name");
      sequence2.setName((String) argOption.valueOf("else"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.IfThenElse.else.StringInsert
      StringInsert stringinsert2 = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("position");
      stringinsert2.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("value");
      stringinsert2.setValue((BaseString) argOption.valueOf("not set: "));
      actors3.add(stringinsert2);

      // Flow.IfThenElse.else.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors3.add(dumpfile2);
      sequence2.setActors(actors3.toArray(new Actor[0]));

      ifthenelse.setElseActor(sequence2);

      actors.add(ifthenelse);
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

