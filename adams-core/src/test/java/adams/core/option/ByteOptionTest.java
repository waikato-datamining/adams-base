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
 * ByteOptionTest.java
 * Copyright (C) 2010-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for all byte options. Run from the command line with: <br><br>
 * java adams.core.option.ByteOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ByteOptionTest
  extends AbstractNumericOptionTest {

  /**
   * Dummy class for testing byte options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class ByteOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = -8653800898932789033L;

    protected byte m_BytePrim;

    protected byte[] m_BytePrimArray;

    protected Byte m_ByteObj;

    protected Byte[] m_ByteObjArray;

    /**
     * Adds options to the internal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "byte-prim", "bytePrim",
	  (byte) 1);

      m_OptionManager.add(
	  "byte-prim-array", "bytePrimArray",
	  new byte[]{1, 2, 3});

      m_OptionManager.add(
	  "byte-obj", "byteObj",
	  (byte) 2, (byte) -10, (byte) +10);

      m_OptionManager.add(
	  "byte-obj-array", "byteObjArray",
	  new Byte[]{(byte) 4, (byte) 5, (byte) 6});
    }

    public void setBytePrim(byte value) {
      m_BytePrim = value;
    }

    public byte getBytePrim() {
      return m_BytePrim;
    }

    public String bytePrimTipText() {
      return "bytePrim";
    }

    public void setBytePrimArray(byte[] value) {
      m_BytePrimArray = value;
    }

    public byte[] getBytePrimArray() {
      return m_BytePrimArray;
    }

    public String bytePrimArrayTipText() {
      return "bytePrimArray";
    }

    public void setByteObj(Byte value) {
      m_ByteObj = value;
    }

    public Byte getByteObj() {
      return m_ByteObj;
    }

    public String byteObjTipText() {
      return "byteObj";
    }

    public void setByteObjArray(Byte[] value) {
      m_ByteObjArray = value;
    }

    public Byte[] getByteObjArray() {
      return m_ByteObjArray;
    }

    public String byteObjArrayTipText() {
      return "byteObjArray";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ByteOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new ByteOptionClass();
  }

  /**
   * Returns the name of the property to use for testing the lower bound.
   *
   * @return		the property
   */
  protected String getLowerBoundTestProperty() {
    return "byteObj";
  }

  /**
   * Returns the (outside) value to test the lower bound with.
   *
   * @return		the value
   */
  protected Byte getLowerBoundTestValue() {
    return (byte) -30;
  }

  /**
   * Returns the name of the property to use for testing the upper bound.
   *
   * @return		the property
   */
  protected String getUpperBoundTestProperty() {
    return "byteObj";
  }

  /**
   * Returns the (outside) value to test the upper bound with.
   *
   * @return		the value
   */
  protected Byte getUpperBoundTestValue() {
    return (byte) 30;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ByteOptionTest.class);
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
