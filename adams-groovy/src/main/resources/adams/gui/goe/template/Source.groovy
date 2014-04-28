/*
 * Template of a Groovy source.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.core.Token
import adams.flow.core.Unknown
import adams.flow.source.AbstractScript

import java.util.Random
import java.util.Vector

class TemplateSource
  extends AbstractScript {

  /** contains all the tokens to send. */
  protected Vector m_Tokens

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "FIXME."
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUp() {
    def result = super.setUp()
    if (result == null)
      m_Tokens = new Vector()
    return result
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
    return null
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    def result = m_Tokens.get(0)
    m_Tokens.remove(0)
    return result
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_Tokens.size() > 0)
  }

  /**
   * Cleans up after the execution has finished.
   */
  public void wrapUp() {
    super.wrapUp()
    m_Tokens = null
  }
}