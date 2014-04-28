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
 * TweetReplayTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for TweetReplay actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TweetReplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TweetReplayTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
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
    return new TestSuite(TweetReplayTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("annotations");
      flow.setAnnotations((adams.core.base.BaseText) argOption.valueOf("Demonstrates how to replay archived tweets.\nUseful when experimenting with algorithms processing \ntweets and require the same tweet stream each time."));
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors2 = new adams.flow.core.AbstractActor[3];

      // Flow.TweetReplay
      adams.flow.source.TweetReplay tweetreplay3 = new adams.flow.source.TweetReplay();
      argOption = (AbstractArgumentOption) tweetreplay3.getOptionManager().findByProperty("replay");
      adams.data.io.input.FixedTweets fixedtweets5 = new adams.data.io.input.FixedTweets();
      argOption = (AbstractArgumentOption) fixedtweets5.getOptionManager().findByProperty("tweets");
      adams.core.base.BaseString[] tweets6 = new adams.core.base.BaseString[5];
      tweets6[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      tweets6[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      tweets6[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      tweets6[3] = (adams.core.base.BaseString) argOption.valueOf("4");
      tweets6[4] = (adams.core.base.BaseString) argOption.valueOf("5");
      fixedtweets5.setTweets(tweets6);
      tweetreplay3.setReplay(fixedtweets5);

      actors2[0] = tweetreplay3;

      // Flow.TwitterConverter
      adams.flow.transformer.TwitterConverter twitterconverter7 = new adams.flow.transformer.TwitterConverter();
      argOption = (AbstractArgumentOption) twitterconverter7.getOptionManager().findByProperty("converter");
      adams.data.twitter.TextConverter textconverter9 = new adams.data.twitter.TextConverter();
      argOption = (AbstractArgumentOption) textconverter9.getOptionManager().findByProperty("separator");
      textconverter9.setSeparator((java.lang.String) argOption.valueOf("\t"));
      twitterconverter7.setConverter(textconverter9);

      actors2[1] = twitterconverter7;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile11 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile11.getOptionManager().findByProperty("outputFile");
      dumpfile11.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile11.setAppend(true);
      actors2[2] = dumpfile11;
      flow.setActors(actors2);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener14 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener14);

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

