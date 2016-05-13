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
 * AbstractOptionTest.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.Utils;
import adams.test.AdamsTestCase;

import java.io.Serializable;
import java.util.List;

/**
 * Ancestor for option tests.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOptionTest
  extends AdamsTestCase {

  /**
   * Ancestor for the dummy classes to test.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public abstract static class AbstractOptionClass
    implements OptionHandler, Serializable {

    /** for serialization. */
    private static final long serialVersionUID = 1455059869697429814L;

    /** for managing the available options. */
    protected OptionManager m_OptionManager;

    /**
     * Initializes the object.
     */
    public AbstractOptionClass() {
      super();
      defineOptions();
      getOptionManager().setDefaults();
    }

    /**
     * Adds options to the internal list of options.
     */
    public void defineOptions() {
      m_OptionManager = new OptionManager(this);
    }

    /**
     * Returns the option manager.
     *
     * @return		the manager
     */
    public OptionManager getOptionManager() {
      return m_OptionManager;
    }

    /**
     * Cleans up the options.
     */
    public void cleanUpOptions() {
      if (m_OptionManager != null) {
	m_OptionManager.cleanUp();
	m_OptionManager = null;
      }
    }

    /**
     * Frees up memory in a "destructive" non-reversible way.
     * <br><br>
     * Cleans up the options.
     *
     * @see	#cleanUpOptions()
     */
    public void destroy() {
      cleanUpOptions();
    }

    /**
     * Returns the commandline string.
     *
     * @return		 the commandline
     */
    @Override
    public String toCommandLine() {
      return OptionUtils.getCommandLine(this);
    }
  }

  /** the dummy object to test. */
  protected OptionHandler m_OptionHandler;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractOptionTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. Sets up the dummy object that
   * is used for testing the options.
   *
   * @throws Exception 	if an error occurs during set up
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_OptionHandler = getOptionHandler();
  }

  /**
   * Called by JUnit after each test method. Freeing up memory etc.
   *
   * @throws Exception	if an error occurs during finishing up the test
   */
  protected void tearDown() throws Exception {
    super.tearDown();

    m_OptionHandler.cleanUpOptions();
    m_OptionHandler = null;
  }

  /**
   * Returns the fully setup dummy object to test.
   *
   * @return		the object to use for testing the options
   */
  protected abstract OptionHandler getOptionHandler();

  /**
   * Tests setting a zero-length array.
   */
  public void testEmptyArray() {
    ArrayConsumer.setOptions(m_OptionHandler, new String[0]);
  }

  /**
   * Tests getting the string array.
   */
  public void testObtainingArray() {
    String[]	options;

    options = ArrayProducer.getOptions(m_OptionHandler);
    assertNotNull("options array must not be null", options);
  }

  /**
   * Tests whether options before and after setting defaults are the same.
   */
  public void testDefaultOptions() {
    String	before;
    String	after;
    String[]	options;

    before = OptionUtils.getCommandLine(m_OptionHandler);
    options = ArrayProducer.getOptions(m_OptionHandler);
    ArrayConsumer.setOptions(m_OptionHandler, options);
    after = OptionUtils.getCommandLine(m_OptionHandler);
    assertEquals("setting the current options must result in the same option string", before, after);
  }

  /**
   * For classes (with default constructor) that are serializable, are tested
   * whether they are truly serializable.
   */
  public void testSerializable() {
    List<AbstractOption> options = m_OptionHandler.getOptionManager().getOptionsList();
    for (AbstractOption option: options) {
      assertNotNull("Not serializable: " + option, Utils.deepCopy(option));
    }
  }
}
