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
 * FlowStructureGraphMLProducerTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.data.io.input.SingleStringTextReader;
import adams.flow.control.Flow;

/**
 * Tests the FlowStructureGraphMLProducer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowStructureGraphMLProducerTest
  extends AbstractOptionProducerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FlowStructureGraphMLProducerTest(String name) {
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

    FlowStructureGraphMLProducer producer = new FlowStructureGraphMLProducer();
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	+ "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
	+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
	+ "    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n"
	+ "  <key id=\"d0\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n"
	+ "  <graph id=\"adams\" edgedefault=\"directed\">\n"
	+ "  <node id=\"n1\">\n"
	+ "    <data key=\"d0\">DumpFile</data>\n"
	+ "  </node>\n"
	+ "  </graph>\n"
	+ "</graphml>\n",
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

    FlowStructureGraphMLProducer producer = new FlowStructureGraphMLProducer();
    producer.produce(flow);

    assertEquals(
	"getOutput() differs",
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	+ "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
	+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
	+ "    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n"
	+ "  <key id=\"d0\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n"
	+ "  <graph id=\"adams\" edgedefault=\"directed\">\n"
	+ "  <node id=\"n1\">\n"
	+ "    <data key=\"d0\">Flow</data>\n"
	+ "  </node>\n"
	+ "  <edge id=\"e1\" directed=\"true\" source=\"n1\" target=\"n2\"/>\n"
	+ "  <node id=\"n2\">\n"
	+ "    <data key=\"d0\">FileSupplier</data>\n"
	+ "  </node>\n"
	+ "  <edge id=\"e2\" directed=\"true\" source=\"n1\" target=\"n3\"/>\n"
	+ "  <node id=\"n3\">\n"
	+ "    <data key=\"d0\">TextFileReader</data>\n"
	+ "  </node>\n"
	+ "  <edge id=\"e3\" directed=\"true\" source=\"n1\" target=\"n4\"/>\n"
	+ "  <node id=\"n4\">\n"
	+ "    <data key=\"d0\">Tee</data>\n"
	+ "  </node>\n"
	+ "  <edge id=\"e4\" directed=\"true\" source=\"n4\" target=\"n5\"/>\n"
	+ "  <node id=\"n5\">\n"
	+ "    <data key=\"d0\">Convert</data>\n"
	+ "  </node>\n"
	+ "  <edge id=\"e5\" directed=\"true\" source=\"n4\" target=\"n6\"/>\n"
	+ "  <node id=\"n6\">\n"
	+ "    <data key=\"d0\">Null</data>\n"
	+ "  </node>\n"
	+ "  <edge id=\"e6\" directed=\"true\" source=\"n1\" target=\"n7\"/>\n"
	+ "  <node id=\"n7\">\n"
	+ "    <data key=\"d0\">Display</data>\n"
	+ "  </node>\n"
	+ "  </graph>\n"
	+ "</graphml>\n",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	"" + producer.getOutput(),
	producer.toString());
  }
}
