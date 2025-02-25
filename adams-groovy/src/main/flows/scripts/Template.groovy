import adams.flow.core.Actor
import adams.flow.template.AbstractScript
import adams.flow.source.StringConstants

/**
 * A dummy source template, only generates a subflow with a StringConstants 
 * actor.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
class DummySource
  extends AbstractScript {

  public String globalInfo() {
    return "A dummy source template, only generates a subflow with a StringConstants actor."
  }

  protected Actor doGenerate() {
    return new StringConstants();
  }

  /**
   * Whether the flow generated is an interactive one.
   *
   * @return		true if interactive
   */
  @Override
  public boolean isInteractive() {
    return false;
  }
}

