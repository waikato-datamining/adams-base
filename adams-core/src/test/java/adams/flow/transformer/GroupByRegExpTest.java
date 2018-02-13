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
 * GroupByRegExpTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.DefaultCompare;
import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
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
 * Test for GroupByRegExp actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class GroupByRegExpTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GroupByRegExpTest(String name) {
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
    return new TestSuite(GroupByRegExpTest.class);
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
      stringconstants.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("20171106_101944_838_RGB-c1-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112412_031_RGB-c1-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112421_564_RGB-c1-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112412_031_RGB-c0-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112421_564_RGB-c1-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171106_101944_771_RGB-c1-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171106_101944_771_RGB-c0-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112412_031_RGB-c0-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112421_564_RGB-c0-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112414_897_RGB-c1-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171106_101944_771_RGB-c0-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171106_101944_838_RGB-c0-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171106_101944_838_RGB-c0-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112414_897_RGB-c0-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112414_897_RGB-c0-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112414_897_RGB-c1-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112421_564_RGB-c0-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171030_112412_031_RGB-c1-r1.png"));
      strings.add((BaseString) argOption.valueOf("20171106_101944_771_RGB-c1-r0.png"));
      strings.add((BaseString) argOption.valueOf("20171106_101944_838_RGB-c1-r1.png"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      StringToString stringtostring = new StringToString();
      stringconstants.setConversion(stringtostring);

      actors.add(stringconstants);

      // Flow.GroupByRegExp
      GroupByRegExp groupbyregexp = new GroupByRegExp();
      argOption = (AbstractArgumentOption) groupbyregexp.getOptionManager().findByProperty("find");
      groupbyregexp.setFind((BaseRegExp) argOption.valueOf("(.*)_RGB.*"));
      actors.add(groupbyregexp);

      // Flow.Sort
      Sort sort = new Sort();
      DefaultCompare defaultcompare = new DefaultCompare();
      sort.setComparator(defaultcompare);

      actors.add(sort);

      // Flow.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors.add(stringjoin);

      StringInsert insert = new StringInsert();
      insert.setValue(new BaseString("\\n"));
      insert.setAfter(true);
      insert.setPosition(new Index(Index.LAST));
      actors.add(insert);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

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

