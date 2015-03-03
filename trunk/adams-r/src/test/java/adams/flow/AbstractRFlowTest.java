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
 * AbstractRFlowTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow;

/**
 * Ancestor for R tests. All derived tests can be collectively disabled
 * using the boolean property {@link #PROPERTY_DISABLED}. If set to true, the
 * R tests won't get executed.
 * <pre>
 * -Dadams.test.flow.r.disabled=true
 * </pre>
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRFlowTest
  extends AbstractFlowTest {

  /** the system property for specifying the dump file for the flow. */
  public final static String PROPERTY_DISABLED = "adams.test.flow.r.disabled";

  /**
   * Constructs the <code>AbstractFlowTest</code>. Called by subclasses.
   *
   * @param name the name of the test class
   */
  public AbstractRFlowTest(String name) {
    super(name);
  }

  /**
   * Skips the test if R tests are disabled.
   * 
   * @throws Throwable		any test failure
   */
  @Override
  public void runBare() throws Throwable {
    String	disabled;
    
    disabled = System.getProperty(PROPERTY_DISABLED);
    if ((disabled != null) && Boolean.parseBoolean(disabled))
      return;
    
    super.runBare();
  }
}
