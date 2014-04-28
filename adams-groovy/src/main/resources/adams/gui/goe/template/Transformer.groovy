/*
 * Template of a Groovy transformer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.core.Token
import adams.flow.core.Unknown
import adams.flow.transformer.AbstractScript

class TemplateTransformer
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
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  public Class[] generates() {
    return [Object.class] as Object[]  // FIXME
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    // FIXME
    m_OutputToken = new Token(m_InputToken.getPayload())
    return null
  }
}
