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
 * NewMat5File.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.Mat5ArrayDimensions;
import adams.flow.core.Token;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.types.Struct;

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
public class NewMat5Struct
  extends AbstractSimpleSource {

  private static final long serialVersionUID = -3376485047370616035L;

  /** the dimensions of the matrix. */
  protected Mat5ArrayDimensions m_Dimensions;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a new Matlab struct.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dimensions", "dimensions",
      new Mat5ArrayDimensions("1;1"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "dimensions", m_Dimensions, "dims: ");

    return result;
  }

  /**
   * Sets the dimensions to obtain.
   *
   * @param value	the dimensions
   */
  public void setDimensions(Mat5ArrayDimensions value) {
    m_Dimensions = value;
    reset();
  }

  /**
   * Returns the dimensions to obtain.
   *
   * @return		the dimensions
   */
  public Mat5ArrayDimensions getDimensions() {
    return m_Dimensions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dimensionsTipText() {
    return "The dimensions of the struct.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Struct.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String 	result;

    result = null;
    try {
      m_OutputToken = new Token(Mat5.newStruct(m_Dimensions.indexValue()));
    }
    catch (Exception e) {
      result = handleException("Failed to create struct using dimensions: " + m_Dimensions, e);
    }

    return result;
  }
}
