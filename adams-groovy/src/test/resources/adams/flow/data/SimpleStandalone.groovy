/*
 * A simple Groovy standalone that just outputs some stuff on the commandline.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.standalone.AbstractScript

class SimpleSingleton
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Just outputs some stuff on the commandline."
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    System.out.println("Hello World!")
    return null
  }
}