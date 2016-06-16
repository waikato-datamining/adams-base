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
 * SetMapValueTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToDouble;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.NewMap;
import adams.flow.source.SequenceSource;
import adams.flow.source.StringConstants;
import adams.flow.standalone.CallableActors;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for SetMapValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SetMapValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetMapValueTest(String name) {
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
    return new TestSuite(SetMapValueTest.class);
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

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.SequenceSource
      SequenceSource sequencesource = new SequenceSource();
      List<Actor> actors3 = new ArrayList<>();

      // Flow.CallableActors.SequenceSource.StringConstants
      StringConstants stringconstants = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("11.1"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      actors3.add(stringconstants);

      // Flow.CallableActors.SequenceSource.Convert
      Convert convert = new Convert();
      StringToDouble stringtodouble = new StringToDouble();
      convert.setConversion(stringtodouble);

      actors3.add(convert);
      sequencesource.setActors(actors3.toArray(new Actor[0]));

      actors2.add(sequencesource);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.NewMap
      NewMap newmap = new NewMap();
      actors.add(newmap);

      // Flow.SetMapValue
      SetMapValue setmapvalue = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("key");
      setmapvalue.setKey((String) argOption.valueOf("hello"));
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("value");
      setmapvalue.setValue((String) argOption.valueOf("world"));
      actors.add(setmapvalue);

      // Flow.SetMapValue-1
      SetMapValue setmapvalue2 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("name");
      setmapvalue2.setName((String) argOption.valueOf("SetMapValue-1"));
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("key");
      setmapvalue2.setKey((String) argOption.valueOf("number"));
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("source");
      setmapvalue2.setSource((CallableActorReference) argOption.valueOf("SequenceSource"));
      actors.add(setmapvalue2);

      // Flow.GetMapValue
      GetMapValue getmapvalue = new GetMapValue();
      argOption = (AbstractArgumentOption) getmapvalue.getOptionManager().findByProperty("key");
      getmapvalue.setKey((String) argOption.valueOf("number"));
      actors.add(getmapvalue);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      dumpfile.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.txt"));
      actors.add(dumpfile);
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

