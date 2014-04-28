/*
 * A simple Groovy condition that always succeeds.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.condition.test.AbstractScript

class SimpleCondition
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "The condition always succeeds."
  }

  /**
   * Performs the actual testing of the condition.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  protected String performTest() {
    return null
  }
}