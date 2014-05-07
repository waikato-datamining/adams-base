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
 * AbstractGlobalWekaClustererEvaluator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.source.WekaClustererSetup;

/**
 * Ancestor for clusterer evaluators that make use of a callable clusterer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGlobalWekaClustererEvaluator
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3440872619963043115L;

  /** the name of the callable weka clusterer. */
  protected CallableActorReference m_Clusterer;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "clusterer", "clusterer",
	    new CallableActorReference(WekaClustererSetup.class.getSimpleName()));
  }

  /**
   * Sets the name of the callable clusterer to use.
   *
   * @param value	the name
   */
  public void setClusterer(CallableActorReference value) {
    m_Clusterer = value;
    reset();
  }

  /**
   * Returns the name of the callable clusterer in use.
   *
   * @return		the name
   */
  public CallableActorReference getClusterer() {
    return m_Clusterer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String clustererTipText();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
   return QuickInfoHelper.toString(this, "clusterer", m_Clusterer);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      variable = getOptionManager().getVariableForProperty("clusterer");
      if (variable == null) {
	if (m_Clusterer.isEmpty())
	  result = "No clusterer specified!";
      }
    }

    return result;
  }

  /**
   * Returns an instance of the callable clusterer.
   *
   * @return		the clusterer
   */
  protected weka.clusterers.Clusterer getClustererInstance() {
    return (weka.clusterers.Clusterer) CallableActorHelper.getSetup(weka.clusterers.Clusterer.class, m_Clusterer, this);
  }
}
