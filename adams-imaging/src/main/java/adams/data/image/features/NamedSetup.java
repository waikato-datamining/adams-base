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
 * NamedSetup.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import java.util.List;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Applies a feature generator that is referenced via its global setup name (see 'NamedSetups').
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheetFeatureConverter -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-setup &lt;adams.core.NamedSetup&gt; (property: setup)
 * &nbsp;&nbsp;&nbsp;The name of the setup to use.
 * &nbsp;&nbsp;&nbsp;default: name_of_setup
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedSetup
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3442872825798982018L;

  /** the name of the setup to load. */
  protected adams.core.NamedSetup m_Setup;

  /** the actual scheme. */
  protected AbstractBufferedImageFeatureGenerator m_ActualScheme;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies a feature generator that is referenced via its global setup name (see 'NamedSetups').";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "setup", "setup",
	    new adams.core.NamedSetup());
  }

  /**
   * Resets the filter.
   */
  @Override
  public void reset() {
    super.reset();

    m_ActualScheme = null;
  }

  /**
   * Sets the setup name.
   *
   * @param value	the name
   */
  public void setSetup(adams.core.NamedSetup value) {
    m_Setup = value;
    if (!m_Setup.isDummy() && !m_Setup.exists())
      getLogger().severe("Warning: named setup '" + m_Setup + "' unknown!");
    reset();
  }

  /**
   * Returns the setup name.
   *
   * @return		the name
   */
  public adams.core.NamedSetup getSetup() {
    return m_Setup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String setupTipText() {
    return "The name of the setup to use.";
  }

  /**
   * Returns the named setup.
   *
   * @return		the actual scheme to use
   */
  protected AbstractBufferedImageFeatureGenerator getActualScheme() {
    if (m_ActualScheme == null) {
      m_ActualScheme = (AbstractBufferedImageFeatureGenerator) m_Setup.getSetup();
      if (m_ActualScheme == null)
	throw new IllegalStateException(
	    "Failed to instantiate named setup '" + m_Setup + "'!");
    }

    return m_ActualScheme;
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    return getActualScheme().createHeader(img);
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    return getActualScheme().generateRows(img);
  }
}
