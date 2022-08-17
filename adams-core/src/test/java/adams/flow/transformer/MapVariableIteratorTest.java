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
 * MapVariableIteratorTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.DefaultCompare;
import adams.core.VariableName;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.ObjectToObject;
import adams.data.conversion.StringToDouble;
import adams.data.conversion.StringToInt;
import adams.data.conversion.StringToString;
import adams.data.io.input.LineArrayTextReader;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.CombineVariables;
import adams.flow.source.NewMap;
import adams.flow.source.Start;
import adams.flow.source.Variable;
import adams.flow.standalone.DeleteFile;
import adams.flow.standalone.SetVariable;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for MapVariableIterator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class MapVariableIteratorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MapVariableIteratorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    //m_TestHelper.copyResourceToTmp("some.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    //m_TestHelper.deleteFileFromTmp("some.csv");
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
    return new TestSuite(MapVariableIteratorTest.class);
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

      // Flow.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("tmp_file"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("${TMP}/map.txt"));
      actors.add(setvariable);

      // Flow.DeleteFile
      DeleteFile deletefile = new DeleteFile();
      argOption = (AbstractArgumentOption) deletefile.getOptionManager().findByProperty("directory");
      deletefile.setDirectory((PlaceholderDirectory) argOption.valueOf("${TMP}"));
      argOption = (AbstractArgumentOption) deletefile.getOptionManager().findByProperty("regExp");
      deletefile.setRegExp((BaseRegExp) argOption.valueOf("map.txt"));
      actors.add(deletefile);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.create
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("create"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.create.NewMap
      NewMap newmap = new NewMap();
      actors2.add(newmap);

      // Flow.create.SetMapValue (3)
      SetMapValue setmapvalue = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("name");
      setmapvalue.setName((String) argOption.valueOf("SetMapValue (3)"));
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("key");
      setmapvalue.setKey((String) argOption.valueOf("c"));
      argOption = (AbstractArgumentOption) setmapvalue.getOptionManager().findByProperty("value");
      setmapvalue.setValue((String) argOption.valueOf("12.3"));
      StringToDouble stringtodouble = new StringToDouble();
      setmapvalue.setConversion(stringtodouble);

      actors2.add(setmapvalue);

      // Flow.create.SetMapValue
      SetMapValue setmapvalue2 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("key");
      setmapvalue2.setKey((String) argOption.valueOf("a"));
      argOption = (AbstractArgumentOption) setmapvalue2.getOptionManager().findByProperty("value");
      setmapvalue2.setValue((String) argOption.valueOf("1"));
      StringToInt stringtoint = new StringToInt();
      setmapvalue2.setConversion(stringtoint);

      actors2.add(setmapvalue2);

      // Flow.create.SetMapValue (2)
      SetMapValue setmapvalue3 = new SetMapValue();
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("name");
      setmapvalue3.setName((String) argOption.valueOf("SetMapValue (2)"));
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("key");
      setmapvalue3.setKey((String) argOption.valueOf("b"));
      argOption = (AbstractArgumentOption) setmapvalue3.getOptionManager().findByProperty("value");
      setmapvalue3.setValue((String) argOption.valueOf("xyz"));
      ObjectToObject objecttoobject = new ObjectToObject();
      setmapvalue3.setConversion(objecttoobject);

      actors2.add(setmapvalue3);

      // Flow.create.MapVariableIterator
      MapVariableIterator mapvariableiterator = new MapVariableIterator();
      argOption = (AbstractArgumentOption) mapvariableiterator.getOptionManager().findByProperty("regExp");
      mapvariableiterator.setRegExp((BaseRegExp) argOption.valueOf("(a|c)"));
      actors2.add(mapvariableiterator);

      // Flow.create.save
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("save"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.create.save.CombineVariables
      CombineVariables combinevariables = new CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables.getOptionManager().findByProperty("expression");
      combinevariables.setExpression((BaseText) argOption.valueOf("@{key}: @{value}"));
      StringToString stringtostring = new StringToString();
      combinevariables.setConversion(stringtostring);

      actors3.add(combinevariables);

      // Flow.create.save.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      argOption.setVariable("@{tmp_file}");
      dumpfile.setAppend(true);

      actors3.add(dumpfile);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors2.add(trigger2);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.load
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("load"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.load.Variable
      Variable variable = new Variable();
      argOption = (AbstractArgumentOption) variable.getOptionManager().findByProperty("variableName");
      variable.setVariableName((VariableName) argOption.valueOf("tmp_file"));
      StringToString stringtostring2 = new StringToString();
      variable.setConversion(stringtostring2);

      actors4.add(variable);

      // Flow.load.TextFileReader
      TextFileReader textfilereader = new TextFileReader();
      LineArrayTextReader linearraytextreader = new LineArrayTextReader();
      textfilereader.setReader(linearraytextreader);

      actors4.add(textfilereader);

      // Flow.load.Sort
      Sort sort = new Sort();
      DefaultCompare defaultcompare = new DefaultCompare();
      sort.setComparator(defaultcompare);

      actors4.add(sort);

      // Flow.load.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors4.add(stringjoin);

      // Flow.load.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors4.add(dumpfile2);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger3);

      // Flow.clean up
      Trigger trigger4 = new Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
      trigger4.setName((String) argOption.valueOf("clean up"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.clean up.DeleteFile (2)
      DeleteFile deletefile2 = new DeleteFile();
      argOption = (AbstractArgumentOption) deletefile2.getOptionManager().findByProperty("name");
      deletefile2.setName((String) argOption.valueOf("DeleteFile (2)"));
      argOption = (AbstractArgumentOption) deletefile2.getOptionManager().findByProperty("directory");
      deletefile2.setDirectory((PlaceholderDirectory) argOption.valueOf("${TMP}"));
      argOption = (AbstractArgumentOption) deletefile2.getOptionManager().findByProperty("regExp");
      deletefile2.setRegExp((BaseRegExp) argOption.valueOf("map.txt"));
      actors5.add(deletefile2);
      trigger4.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger4);
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

