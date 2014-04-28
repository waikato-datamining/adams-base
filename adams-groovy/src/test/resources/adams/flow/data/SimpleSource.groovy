/*
 * A simple Groovy source that just generates a few random integers as
 * tokens (0-99).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.flow.core.Token
import adams.flow.core.Unknown
import adams.flow.source.AbstractScript

import java.util.Random
import java.util.Vector

class SimpleSource
  extends AbstractScript {

  /** contains all the tokens to send. */
  protected Vector m_Tokens

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Just generates a bunch of random integer tokens (0-99)."
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
   * @return		Integer.class
   */
  public Class[] generates() {
    def result = new Object[1]
    result[0] = Integer.class
    return result
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String doExecute() {
  	def rand = new Random(1)
  	for (i in 1..10)
  	  m_Tokens.add(new Token(new Integer(rand.nextInt(100))))
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