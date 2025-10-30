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
 * ExtractID.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.idextraction.IDExtractor;
import adams.data.idextraction.Manual;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ExtractID
  extends AbstractTransformer {

  private static final long serialVersionUID = -2978040822861434285L;

  /** the group extractor to use. */
  protected IDExtractor m_Extractor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts the ID from the object and forwards it.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "extractor", "extractor",
      new Manual());
  }

  /**
   * Sets the ID extractor to use.
   *
   * @param value	the extractor
   */
  public void setExtractor(IDExtractor value) {
    m_Extractor = value;
    reset();
  }

  /**
   * Returns the ID extractor to use.
   *
   * @return		the extractor
   */
  public IDExtractor getExtractor() {
    return m_Extractor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extractorTipText() {
    return "The scheme to use for extracting the ID.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "extractor", m_Extractor, "extractor: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (!m_Extractor.handles(m_InputToken.getPayload()))
      result = m_InputToken.unhandledData();

    try {
      m_OutputToken = new Token(m_Extractor.extractID(m_InputToken.getPayload()));
    }
    catch (Exception e) {
      result = handleException("Failed to extract ID from: " + m_InputToken.getPayload(), e);
    }

    return result;
  }
}
