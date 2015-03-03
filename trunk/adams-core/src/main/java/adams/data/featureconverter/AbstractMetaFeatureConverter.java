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
 * AbstractMetaFeatureConverter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for converters that use a base converter.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaFeatureConverter
  extends AbstractFeatureConverter {

  /** for serialization. */
  private static final long serialVersionUID = 3518281033354364298L;
  
  /** the base converter. */
  protected AbstractFeatureConverter m_Converter;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "converter", "converter",
	    getDefaultConverter());
  }
  
  /**
   * Returns the default converter to use.
   * 
   * @return		the converter
   */
  protected abstract AbstractFeatureConverter getDefaultConverter();

  /**
   * Sets the base feature converter to use.
   *
   * @param value	the converter
   */
  public void setConverter(AbstractFeatureConverter value) {
    m_Converter = value;
    reset();
  }

  /**
   * Returns the base feature converter in use.
   *
   * @return		the converter
   */
  public AbstractFeatureConverter getConverter() {
    return m_Converter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String converterTipText();

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "converter", m_Converter, "converter: ");
  }
}
