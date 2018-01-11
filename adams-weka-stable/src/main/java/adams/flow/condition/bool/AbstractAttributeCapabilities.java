/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * AbstractAttributeCapabilities.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.Utils;
import adams.flow.core.Actor;
import adams.flow.core.Capability;
import adams.flow.core.Token;

/**
 * Ancestor for capabilities-based conditions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAttributeCapabilities
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 3278345095591806425L;

  /** the class index. */
  protected Capability[] m_Capabilities;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /** the capabilities object to use. */
  protected weka.core.Capabilities m_ActualCapabilities;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "capability", "capabilities",
	    new Capability[]{});

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualCapabilities = null;
  }

  /**
   * Sets the capabilities.
   *
   * @param value	the capabilities
   */
  public void setCapabilities(Capability[] value) {
    m_Capabilities = value;
    reset();
  }

  /**
   * Returns the capabilities.
   *
   * @return		the capabilities
   */
  public Capability[] getCapabilities() {
    return m_Capabilities;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String capabilitiesTipText() {
    return "The capabilities that the objects must match.";
  }

  /**
   * Sets whether to invert the matching sense of the capabilities.
   *
   * @param value	if true then the matching sense gets inverted
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether the matching sense of the capabilities is inverted.
   *
   * @return		true if the matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return
        "If set to true, then objects that failed the capabilities test "
      + "will pass through and all others get discarded.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		always null
   */
  @Override
  public String getQuickInfo() {
    return (m_Invert ? "! " : "") + QuickInfoHelper.toString(this, "capabilities", Shortening.shortenEnd(Utils.arrayToString(m_Capabilities), 50));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public abstract Class[] accepts();

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String					result;
    HashSet<Capability>				capabilities;
    HashSet<weka.core.Capabilities.Capability>	unwanted;
    Iterator<weka.core.Capabilities.Capability>	iter;
    weka.core.Capabilities.Capability		cap;

    result = super.setUp(owner);

    if (result == null) {
      m_ActualCapabilities = new weka.core.Capabilities(null);

      // add capabilities
      capabilities = new HashSet<Capability>(Arrays.asList(m_Capabilities));
      for (Capability c: m_Capabilities)
	m_ActualCapabilities.enable(Capability.toWeka(c));

      // remove unwanted, implied capabilities
      unwanted = new HashSet<weka.core.Capabilities.Capability>();
      iter     = m_ActualCapabilities.capabilities();
      while (iter.hasNext()) {
	cap = iter.next();
	if (!capabilities.contains(Capability.toAdams(cap)))
	  unwanted.add(cap);
      }
      for (weka.core.Capabilities.Capability c: unwanted)
	m_ActualCapabilities.disable(c);

      if (isLoggingEnabled())
	getLogger().info("Capabilites: " + m_ActualCapabilities);
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected abstract boolean doEvaluate(Actor owner, Token token);
}
