/*
 * A simple Groovy transformer that just adds a user-supplied integer to the
 * integers that pass through.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.core.Token
import adams.flow.core.Unknown
import adams.flow.transformer.AbstractScript

class SimpleTransformer
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Just adds a user-supplied integer to the integers passing through.\n\nExpects an additional option called \"add\" with the number to add."
  }

  /**
   * Returns the class of objects that it accepts.
   *
   * @return		Integer.class
   */
  public Class[] accepts() {
    return [Integer.class] as Object[]
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		Integer.class
   */
  public Class[] generates() {
    return [Integer.class] as Object[]
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
    Integer input = (Integer) m_InputToken.getPayload()
    m_OutputToken = new Token(new Integer(input + getAdditionalOptions().getInteger("add", 1)))
    return null
  }
}
