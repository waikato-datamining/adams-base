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

/**
 * FlowStructureDotProducerTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.data.io.input.SingleStringTextReader;
import adams.env.Environment;
import adams.flow.control.Flow;

/**
 * Tests the FlowStructureDotProducer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowStructureDotProducerTest
  extends AbstractOptionProducerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FlowStructureDotProducerTest(String name) {
    super(name);
  }

  /**
   * Tests a simple option handler.
   */
  public void testSimple() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.csv"));
    handler.setAppend(true);
    
    m_OptionHandler = handler;

    FlowStructureDotProducer producer = new FlowStructureDotProducer();
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	  "digraph adams_flow_sink_DumpFile {\n"
	+ "  N1 [label=\"DumpFile\" shape=box]\n"
	+ "}\n",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	"" + producer.getOutput(),
	producer.toString());
  }

  /**
   * Tests a complex setup.
   */
  public void testComplex() {
    AbstractArgumentOption    argOption;

    Flow flow = new Flow();

    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[4];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${CWD}/parsers.xml")});

      tmp1[0] = tmp2;
      adams.flow.transformer.TextFileReader tmp4 = new adams.flow.transformer.TextFileReader();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputType");
      tmp4.setReader(new SingleStringTextReader());

      tmp1[1] = tmp4;
      adams.flow.control.Tee tmp6 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp7 = new adams.flow.core.AbstractActor[2];
      adams.flow.transformer.Convert tmp8 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("conversion");
      adams.data.conversion.LowerCase tmp10 = new adams.data.conversion.LowerCase();
      tmp8.setConversion(tmp10);

      tmp7[0] = tmp8;
      adams.flow.sink.Null tmp11 = new adams.flow.sink.Null();
      tmp7[1] = tmp11;
      tmp6.setActors(tmp7);

      tmp1[2] = tmp6;
      adams.flow.sink.Display tmp12 = new adams.flow.sink.Display();
      tmp1[3] = tmp12;
      flow.setActors(tmp1);
    }
    catch (Exception e) {
      fail("Set up of flow failed: " + e);
    }
    
    m_OptionHandler = flow;

    FlowStructureDotProducer producer = new FlowStructureDotProducer();
    producer.produce(flow);

    assertEquals(
	"getOutput() differs",
	"digraph adams_flow_control_Flow {\n"
	+ "  N1 [label=\"Flow\" shape=ellipse]\n"
	+ "  N1 -> N2\n"
	+ "  N2 [label=\"FileSupplier\" shape=parallelogram]\n"
	+ "  N1 -> N3\n"
	+ "  N3 [label=\"TextFileReader\" shape=box]\n"
	+ "  N1 -> N4\n"
	+ "  N4 [label=\"Tee\" shape=triangle]\n"
	+ "  N4 -> N5\n"
	+ "  N5 [label=\"Convert\" shape=box]\n"
	+ "  N4 -> N6\n"
	+ "  N6 [label=\"Null\" shape=box]\n"
	+ "  N1 -> N7\n"
	+ "  N7 [label=\"Display\" shape=box]\n"
	+ "}\n",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	"" + producer.getOutput(),
	producer.toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FlowStructureDotProducerTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
