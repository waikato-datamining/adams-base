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
 * DecodeBarcode.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.barcode.decode.AbstractBarcodeDecoder;
import adams.data.image.AbstractImageContainer;
import adams.data.text.TextContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Decodes the data in a barcode using the specified decoder.
 * <p/>
 <!-- globalinfo-end -->
 * <p/>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DecodeBarcode
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-decoder &lt;adams.data.barcode.decode.AbstractBarcodeDecoder&gt; (property: decoder)
 * &nbsp;&nbsp;&nbsp;The decoder algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.barcode.decode.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class DecodeBarcode
  extends AbstractTransformer {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 6149942254926179607L;

  /**
   * The decoder.
   */
  protected AbstractBarcodeDecoder m_Decoder;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Decodes the data in a barcode using the specified decoder.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "decoder", "decoder",
        new adams.data.barcode.decode.PassThrough());
  }

  /**
   * Sets the decoder to use.
   *
   * @param value the decoder to use
   */
  public void setDecoder(AbstractBarcodeDecoder value) {
    m_Decoder = value;
    reset();
  }

  /**
   * Returns the decoder in use.
   *
   * @return the decoder in use
   */
  public AbstractBarcodeDecoder getDecoder() {
    return m_Decoder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String decoderTipText() {
    return "The decoder algorithm to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "decoder", m_Decoder);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of objects that it generates
   */
  @Override
  public Class[] generates() {
    return new Class[]{TextContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String          result;
    TextContainer   cont;

    result = null;

    try {
      cont = m_Decoder.decode((AbstractImageContainer) m_InputToken.getPayload());
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to extract barcode!", e);
    }

    return result;
  }
}
