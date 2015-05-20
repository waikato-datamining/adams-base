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
 * SerializationHelperTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.sink.Display;
import adams.flow.source.StringConstants;
import adams.flow.source.Variable;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.StringInsert;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;

/**
 * Tests the adams.core.SerializationHelper class. Run from commandline with: <br><br>
 * java adams.core.SerializationHelperTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializationHelperTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SerializationHelperTest(String name) {
    super(name);
  }
  
  /**
   * Tests the read/write methods.
   */
  public void testReadAndWrite() {
    TmpFile model = new TmpFile("test.model");
    TmpFile modelGZ = new TmpFile("test.model.gz");
    Flow flowIn = null;
    Flow flowOut = new Flow();
    flowOut.setName("blah");
    flowOut.setActors(new AbstractActor[]{
	new StringConstants(),
	new Display()
    });
    
    // regular
    try {
      SerializationHelper.write(model.getAbsolutePath(), flowOut);
      flowIn = (Flow) SerializationHelper.read(model.getAbsolutePath());
      assertEquals("Flows differ", flowOut, flowIn);
    }
    catch (Exception e) {
      fail("Failed to read/write: " + e);
    }
    assertTrue("Failed to delete " + model + "?", model.delete());
    
    // gzipped
    try {
      SerializationHelper.write(modelGZ.getAbsolutePath(), flowOut);
      flowIn = (Flow) SerializationHelper.read(modelGZ.getAbsolutePath());
      assertEquals("Flows differ", flowOut, flowIn);
    }
    catch (Exception e) {
      fail("Failed to read/write: " + e);
    }
    assertTrue("Failed to delete " + modelGZ + "?", modelGZ.delete());
  }
  
  /**
   * Tests the readAll/writeAll methods.
   */
  public void testReadAllAndWriteAll() {
    TmpFile model = new TmpFile("test.model");
    TmpFile modelGZ = new TmpFile("test.model.gz");
    Flow flowIn1 = null;
    Flow flowOut1 = new Flow();
    flowOut1.setName("blah");
    flowOut1.setActors(new AbstractActor[]{
	new StringConstants(),
	new Display()
    });
    Flow flowIn2 = null;
    Flow flowOut2 = new Flow();
    flowOut1.setName("bloerk");
    flowOut1.setActors(new AbstractActor[]{
	new SetVariable(),
	new Variable(),
	new StringInsert(),
	new Display()
    });
    Object[] objects = null;
    
    // regular
    try {
      SerializationHelper.writeAll(model.getAbsolutePath(), new Object[]{flowOut1, flowOut2});
      objects = SerializationHelper.readAll(model.getAbsolutePath());
      assertEquals("Number of objects differs", 2, objects.length);
      flowIn1 = (Flow) objects[0];
      flowIn2 = (Flow) objects[1];
      assertEquals("Flows differ (1)", flowOut1, flowIn1);
      assertEquals("Flows differ (2)", flowOut2, flowIn2);
    }
    catch (Exception e) {
      fail("Failed to readAll/writeAll: " + e);
    }
    assertTrue("Failed to delete " + model + "?", model.delete());
    
    // gzipped
    try {
      SerializationHelper.writeAll(modelGZ.getAbsolutePath(), new Object[]{flowOut1, flowOut2});
      objects = SerializationHelper.readAll(modelGZ.getAbsolutePath());
      assertEquals("Number of objects differs", 2, objects.length);
      flowIn1 = (Flow) objects[0];
      flowIn2 = (Flow) objects[1];
      assertEquals("Flows differ (1)", flowOut1, flowIn1);
      assertEquals("Flows differ (2)", flowOut2, flowIn2);
    }
    catch (Exception e) {
      fail("Failed to readAll/writeAll: " + e);
    }
    assertTrue("Failed to delete " + modelGZ + "?", modelGZ.delete());
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SerializationHelperTest.class);
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
