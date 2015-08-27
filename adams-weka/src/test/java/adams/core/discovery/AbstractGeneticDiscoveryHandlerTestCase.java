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
 * AbstractGeneticDiscoveryHandlerTestCase.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.discovery.PropertyPath.PropertyContainer;

/**
 * Ancestor for genetic discovery handler test cases.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticDiscoveryHandlerTestCase
  extends AbstractDiscoveryHandlerTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public AbstractGeneticDiscoveryHandlerTestCase(String name) {
    super(name);
  }

  /**
   * Returns the handler instance to use for testing in the {@link #testPackUnpack()}
   * method.
   *
   * @return		the handler instance
   */
  protected abstract AbstractGeneticDiscoveryHandler getPackUnpackHandler();

  /**
   * Returns the property container to use for testing in the {@link #testPackUnpack()}
   * method.
   *
   * @return		the handler instance
   */
  protected abstract PropertyContainer getPackUnpackContainer();

  /**
   * Tests the default pack and unpack values.
   */
  public void testPackUnpack() {
    AbstractGeneticDiscoveryHandler	handler;
    PropertyContainer			cont;
    String				packed;
    String				packedAgain;

    handler = getPackUnpackHandler();
    cont    = getPackUnpackContainer();

    packed = handler.pack(cont);
    assertNotNull("initial packed string null", packed);

    handler.unpack(cont, packed);

    packedAgain = handler.pack(cont);
    assertNotNull("second packed string null", packedAgain);
    assertEquals("packed strings differ", packed, packedAgain);
  }
}
