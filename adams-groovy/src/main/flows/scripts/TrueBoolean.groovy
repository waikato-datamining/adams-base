import adams.flow.core.Actor
import adams.flow.core.Token
import adams.flow.core.Unknown

/**
 * Always evaluates to true.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class True
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Always evaluates to 'true'."
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		always null
   */
  public String getQuickInfo() {
    return "true"
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  public Class[] accepts() {
    return [Unknown.class] as Object[]
  }

  /**
   * Configures the condition.
   *
   * @return		always null
   */
  public String setUp() {
    return null
  }

  /**
   * Evaluates whether to executed the "then" or "else" branch.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through the actor
   * @return		always true
   */
  protected boolean doEvaluate(Actor owner, Token token) {
    return true
  }
}
