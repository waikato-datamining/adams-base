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
 * BooleanOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.ArrayConsumer;
import adams.core.option.ArrayProducer;
import adams.env.Environment;

/**
 * Test class for all boolean options. Run from the command line with: <p/>
 * java adams.core.option.BooleanOptionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BooleanOptionTest
  extends AbstractOptionTest {

  /**
   * Dummy class for testing boolean options.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class BooleanOptionClass
    extends AbstractOptionClass {

    /** for serialization. */
    private static final long serialVersionUID = -5064138646192507858L;

    protected boolean m_BooleanPrim;

    protected Boolean m_BooleanObj;

    protected boolean m_BooleanPrimInv;

    protected Boolean m_BooleanObjInv;

    /**
     * Adds options to the internal list of options.
     */
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
	  "boolean-prim", "booleanPrim",
	  false);

      m_OptionManager.add(
	  "boolean-obj", "booleanObj",
	  new Boolean(false));

      m_OptionManager.add(
	  "boolean-prim-inv", "booleanPrimInv",
	  true);

      m_OptionManager.add(
	  "boolean-obj-inv", "booleanObjInv",
	  new Boolean(true));
    }

    public void setBooleanPrim(boolean value) {
      m_BooleanPrim = value;
    }

    public boolean getBooleanPrim() {
      return m_BooleanPrim;
    }

    public String booleanPrimTipText() {
      return "booleanPrim";
    }

    public void setBooleanObj(Boolean value) {
      m_BooleanObj = value;
    }

    public Boolean getBooleanObj() {
      return m_BooleanObj;
    }

    public String booleanObjTipText() {
      return "booleanObj";
    }

    public void setBooleanPrimInv(boolean value) {
      m_BooleanPrimInv = value;
    }

    public boolean getBooleanPrimInv() {
      return m_BooleanPrimInv;
    }

    public String booleanPrimInvTipText() {
      return "booleanPrimInv";
    }

    public void setBooleanObjInv(Boolean value) {
      m_BooleanObjInv = value;
    }

    public Boolean getBooleanObjInv() {
      return m_BooleanObjInv;
    }

    public String booleanObjInvTipText() {
      return "booleanObjInv";
    }
  }

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BooleanOptionTest(String name) {
    super(name);
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected OptionHandler getOptionHandler() {
    return new BooleanOptionClass();
  }

  /**
   * Tests flipping the value of a boolean option.
   */
  public void testFlip() {
    String	before;
    String	after;
    String[]	options;

    ((BooleanOptionClass) m_OptionHandler).setBooleanPrim(true);
    before = OptionUtils.getCommandLine(m_OptionHandler);
    options = ArrayProducer.getOptions(m_OptionHandler);
    ArrayConsumer.setOptions(m_OptionHandler, options);
    after = OptionUtils.getCommandLine(m_OptionHandler);
    assertEquals("Commandline string should be the same", before, after);
  }

  /**
   * Tests flipping the value of a boolean option that is inverted.
   */
  public void testFlipInv() {
    String	before;
    String	after;
    String[]	options;

    ((BooleanOptionClass) m_OptionHandler).setBooleanPrimInv(false);
    before = OptionUtils.getCommandLine(m_OptionHandler);
    options = ArrayProducer.getOptions(m_OptionHandler);
    ArrayConsumer.setOptions(m_OptionHandler, options);
    after = OptionUtils.getCommandLine(m_OptionHandler);
    assertEquals("Commandline string should be the same", before, after);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BooleanOptionTest.class);
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
