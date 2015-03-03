/*
 * Template of a Groovy sink.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.core.Token
import adams.flow.core.Unknown
import adams.flow.sink.AbstractScript

class TemplateSink
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "FIXME."
  }

  /**
   * Returns the class of objects that it accepts.
   *
   * @return		the accepted classes
   */
  public Class[] accepts() {
    return [Object.class] as Object[]  // FIXME
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    // FIXME
    return null
  }
}
