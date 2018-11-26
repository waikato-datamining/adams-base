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
 * AbstractMetaNegativeRegionsGenerator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;

/**
 * Ancestor for meta-algorithms.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMetaNegativeRegionsGenerator
  extends AbstractNegativeRegionsGenerator {

  private static final long serialVersionUID = -5375085537634934346L;

  /** the base generator to use. */
  protected AbstractNegativeRegionsGenerator m_Algorithm;

  /** the actual algorithm in use. */
  protected AbstractNegativeRegionsGenerator m_ActualAlgorithm;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "algorithm", "algorithm",
      getDefaultAlgorithm());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualAlgorithm = null;
  }

  /**
   * Returns the default algorithm.
   *
   * @return		the default
   */
  public AbstractNegativeRegionsGenerator getDefaultAlgorithm() {
    return new Null();
  }

  /**
   * Sets the algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(AbstractNegativeRegionsGenerator value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm in use.
   *
   * @return		the algorithm
   */
  public AbstractNegativeRegionsGenerator getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The algorithm to use for generating the negative regions.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "algorithm", m_Algorithm, "algorithm: ");
  }

  /**
   * Returns the actual algorithm to use.
   *
   * @return		the algorithm to use
   */
  protected AbstractNegativeRegionsGenerator getActualAlgorithm() {
    if (m_ActualAlgorithm == null)
      m_ActualAlgorithm = ObjectCopyHelper.copyObject(m_Algorithm);
    return m_ActualAlgorithm;
  }
}
