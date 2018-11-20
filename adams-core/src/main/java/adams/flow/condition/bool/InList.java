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

/*
 * InList.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.flow.core.Actor;
import adams.flow.core.Token;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Checks whether the incoming string is among the specified items.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-item &lt;adams.core.base.BaseString&gt; [-item ...] (property: items)
 * &nbsp;&nbsp;&nbsp;The items to match the string against.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InList
  extends AbstractBooleanCondition {

  private static final long serialVersionUID = -8098607684693546619L;

  /** the strings to match against. */
  protected BaseString[] m_Items;

  /** the set to use for checks. */
  protected transient Set<String> m_ItemSet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the incoming string is among the specified items.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "item", "items",
      new BaseString[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ItemSet = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "items", m_Items, "items: ");
  }

  /**
   * Sets the string items to check against.
   *
   * @param value	the items
   */
  public void setItems(BaseString[] value) {
    m_Items = value;
    reset();
  }

  /**
   * Returns the string items to check against.
   *
   * @return		the items
   */
  public BaseString[] getItems() {
    return m_Items;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String itemsTipText() {
    return "The items to match the string against.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    if (m_ItemSet == null) {
      m_ItemSet = new HashSet<>();
      for (BaseString item: m_Items)
        m_ItemSet.add(item.getValue());
    }
    return m_ItemSet.contains(token.getPayload(String.class));
  }
}
