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
 * ArrayConsumerTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

/**
 * Tests the ArrayConsumer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayConsumerTest
  extends AbstractOptionConsumerTestCase<String[]> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayConsumerTest(String name) {
    super(name);
  }

  /**
   * Tests generating an  option handler from a string, which includes a Weka class.
   */
  public void testFromStringWeka() {
    adams.flow.transformer.InstantiatableTransformer handler = new adams.flow.transformer.InstantiatableTransformer();
    adams.flow.transformer.WekaFilter wekafilter = new adams.flow.transformer.WekaFilter();
    handler.setActor(wekafilter);
    weka.filters.unsupervised.attribute.Remove remove = new weka.filters.unsupervised.attribute.Remove();
    wekafilter.setFilter(remove);
    remove.setAttributeIndices("1-10");

    performFromStringTest(
	ArrayConsumer.class,
	"adams.flow.transformer.InstantiatableTransformer -actor \"adams.flow.transformer.WekaFilter -filter \\\"weka.filters.unsupervised.attribute.Remove -R 1-10\\\"\"",
	handler);
  }
}
