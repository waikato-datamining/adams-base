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
 * AbstractMetaTimeseriesFeatureGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for generators that use a base generator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaTimeseriesFeatureGenerator
  extends AbstractTimeseriesFeatureGenerator<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 3518281033354364298L;
  
  /** the base generator. */
  protected AbstractTimeseriesFeatureGenerator m_Generator;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    getDefaultGenerator());
  }
  
  /**
   * Returns the default generator to use.
   * 
   * @return		the generator
   */
  protected abstract AbstractTimeseriesFeatureGenerator getDefaultGenerator();

  /**
   * Sets the base feature generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractTimeseriesFeatureGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the base feature generator in use.
   *
   * @return		the generator
   */
  public AbstractTimeseriesFeatureGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The base generator to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
  }
}
