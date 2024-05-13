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
 * EncodeBarcode.java
 * Copyright (C) 2015-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.barcode.encode.AbstractBarcodeEncoder;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 * <br><br>
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 */
public class EncodeBarcode
  extends AbstractTransformer {

  /** For serialization. */
  private static final long serialVersionUID = 6149942254926179607L;

  /** The encoder. */
  protected AbstractBarcodeEncoder m_Encoder;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encodes the incoming payload into a barcode using the specified encoder.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "encoder", "encoder",
        new adams.data.barcode.encode.PassThrough());
  }

  /**
   * Sets the encoder to use.
   *
   * @param value the encoder to use
   */
  public void setEncoder(AbstractBarcodeEncoder value) {
    m_Encoder = value;
    reset();
  }

  /**
   * Returns the encoder in use.
   *
   * @return the encoder in use
   */
  public AbstractBarcodeEncoder getEncoder() {
    return m_Encoder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String encoderTipText() {
    return "The encoder algorithm to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "encoder", m_Encoder);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of objects that it generates
   */
  @Override
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String          		result;
    BufferedImageContainer   	cont;
    MessageCollection		errors;

    result = null;

    errors = new MessageCollection();
    try {
      cont = m_Encoder.encode(m_InputToken.getPayload(String.class), errors);
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to encode barcode!", e);
    }

    return result;
  }
}
