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
 * JsonClassDescriptionProducerTest.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the JsonClassDescriptionProducer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonClassDescriptionProducerTest
  extends AbstractOptionProducerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public JsonClassDescriptionProducerTest(String name) {
    super(name);
  }

  /**
   * Tests a simple option handler.
   */
  public void testTypical() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.arff"));
    handler.setAppend(true);

    JsonClassDescriptionProducer producer = new JsonClassDescriptionProducer();
    producer.produce(handler);

    assertEquals(
      "getOutput() differs",
      "{\"options\":[{\"help\":\"The logging level for outputting errors and debugging output.\",\"property\":\"loggingLevel\",\"multiple\":false,\"type\":\"adams.core.logging.LoggingLevel\",\"option\":\"-logging-level\"},{\"help\":\"The name of the actor.\",\"property\":\"name\",\"multiple\":false,\"type\":\"java.lang.String\",\"option\":\"-name\"},{\"help\":\"The annotations to attach to this actor.\",\"property\":\"annotations\",\"multiple\":false,\"type\":\"adams.core.base.BaseAnnotation\",\"option\":\"-annotation\"},{\"help\":\"If set to true, transformation is skipped and the input token is just forwarded as it is.\",\"property\":\"skip\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-skip\"},{\"help\":\"If set to true, the flow execution at this level gets stopped in case this actor encounters an error; the error gets propagated; useful for critical actors.\",\"property\":\"stopFlowOnError\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-stop-flow-on-error\"},{\"help\":\"If enabled, then no errors are output in the console; Note: the enclosing actor handler must have this enabled as well.\",\"property\":\"silent\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-silent\"},{\"help\":\"The name of the output file.\",\"property\":\"outputFile\",\"multiple\":false,\"type\":\"adams.core.io.PlaceholderFile\",\"option\":\"-output\"},{\"help\":\"If set to true, file gets only appended.\",\"property\":\"append\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-append\"},{\"help\":\"The type of encoding to use when writing to the file, use empty string for default.\",\"property\":\"encoding\",\"multiple\":false,\"type\":\"adams.core.base.BaseCharset\",\"option\":\"-encoding\"},{\"help\":\"The number of attempts for writing the data.\",\"property\":\"numAttempts\",\"multiple\":false,\"type\":\"int\",\"option\":\"-num-attempts\"},{\"help\":\"The time in msec to wait before the next attempt.\",\"property\":\"attemptInterval\",\"multiple\":false,\"type\":\"int\",\"option\":\"-attempt-interval\"},{\"help\":\"The number of lines to buffer before writing to disk, in order to improve I\\/O performance.\",\"property\":\"bufferSize\",\"multiple\":false,\"type\":\"int\",\"option\":\"-buffer-size\"}],\"class\":\"adams.flow.sink.DumpFile\"}",
      "" + producer.getOutput());
    assertEquals(
      "toString() differs",
      "{\"options\":[{\"help\":\"The logging level for outputting errors and debugging output.\",\"property\":\"loggingLevel\",\"multiple\":false,\"type\":\"adams.core.logging.LoggingLevel\",\"option\":\"-logging-level\"},{\"help\":\"The name of the actor.\",\"property\":\"name\",\"multiple\":false,\"type\":\"java.lang.String\",\"option\":\"-name\"},{\"help\":\"The annotations to attach to this actor.\",\"property\":\"annotations\",\"multiple\":false,\"type\":\"adams.core.base.BaseAnnotation\",\"option\":\"-annotation\"},{\"help\":\"If set to true, transformation is skipped and the input token is just forwarded as it is.\",\"property\":\"skip\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-skip\"},{\"help\":\"If set to true, the flow execution at this level gets stopped in case this actor encounters an error; the error gets propagated; useful for critical actors.\",\"property\":\"stopFlowOnError\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-stop-flow-on-error\"},{\"help\":\"If enabled, then no errors are output in the console; Note: the enclosing actor handler must have this enabled as well.\",\"property\":\"silent\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-silent\"},{\"help\":\"The name of the output file.\",\"property\":\"outputFile\",\"multiple\":false,\"type\":\"adams.core.io.PlaceholderFile\",\"option\":\"-output\"},{\"help\":\"If set to true, file gets only appended.\",\"property\":\"append\",\"multiple\":false,\"type\":\"boolean\",\"option\":\"-append\"},{\"help\":\"The type of encoding to use when writing to the file, use empty string for default.\",\"property\":\"encoding\",\"multiple\":false,\"type\":\"adams.core.base.BaseCharset\",\"option\":\"-encoding\"},{\"help\":\"The number of attempts for writing the data.\",\"property\":\"numAttempts\",\"multiple\":false,\"type\":\"int\",\"option\":\"-num-attempts\"},{\"help\":\"The time in msec to wait before the next attempt.\",\"property\":\"attemptInterval\",\"multiple\":false,\"type\":\"int\",\"option\":\"-attempt-interval\"},{\"help\":\"The number of lines to buffer before writing to disk, in order to improve I\\/O performance.\",\"property\":\"bufferSize\",\"multiple\":false,\"type\":\"int\",\"option\":\"-buffer-size\"}],\"class\":\"adams.flow.sink.DumpFile\"}",
      producer.toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(JsonClassDescriptionProducerTest.class);
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
