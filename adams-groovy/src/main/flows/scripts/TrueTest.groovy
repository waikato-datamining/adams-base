/**
 * Dummy condition that always succeeds.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.condition.test.AbstractScript

class TrueTest
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Dummy condition that always succeeds."
  }

  /**
   * Returns always null, i.e., the test succeeds.
   *
   * @return		always null
   */
  protected String performTest() {
    return null
  }
}
