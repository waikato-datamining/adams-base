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

import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.Report;
import adams.data.text.TextContainer;
import adams.flow.core.Token;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- globalinfo-start -->
 * * Decode the data in a barcode.
 * * <p/>
 * <!-- globalinfo-end -->
 * <p/>
 * <!-- options-start -->
 * * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * * &nbsp;&nbsp;&nbsp;default: WARNING
 * * </pre>
 * * 
 * * <pre>-name &lt;java.lang.String&gt; (property: name)
 * * &nbsp;&nbsp;&nbsp;The name of the actor.
 * * &nbsp;&nbsp;&nbsp;default: DecodeBarcode
 * * </pre>
 * * 
 * * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * * &nbsp;&nbsp;&nbsp;default: 
 * * </pre>
 * * 
 * * <pre>-skip &lt;boolean&gt; (property: skip)
 * * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * * &nbsp;&nbsp;&nbsp;as it is.
 * * &nbsp;&nbsp;&nbsp;default: false
 * * </pre>
 * * 
 * * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * * &nbsp;&nbsp;&nbsp;default: false
 * * </pre>
 * * 
 * * <pre>-silent &lt;boolean&gt; (property: silent)
 * * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * * &nbsp;&nbsp;&nbsp;default: false
 * * </pre>
 * * 
 * * <pre>-autoDetect &lt;boolean&gt; (property: autoDetect)
 * * &nbsp;&nbsp;&nbsp;Enable or disable barcode format auto-detection. If disabled, it will attempt 
 * * &nbsp;&nbsp;&nbsp;to decode using the specified format.
 * * &nbsp;&nbsp;&nbsp;default: true
 * * </pre>
 * * 
 * * <pre>-format &lt;AZTEC|CODABAR|CODE_39|CODE_93|CODE_128|DATA_MATRIX|EAN_8|EAN_13|ITF|MAXICODE|PDF_417|QR_CODE|RSS_14|RSS_EXPANDED|UPC_A|UPC_E|UPC_EAN_EXTENSION&gt; (property: format)
 * * &nbsp;&nbsp;&nbsp;Barcode format type to expect.
 * * &nbsp;&nbsp;&nbsp;default: QR_CODE
 * * </pre>
 * * 
 * <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class DecodeBarcode extends AbstractTransformer {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 6149942254926179607L;

  /**
   * Key name prefix for ZXing result metadata.
   */
  private static final String REPORT_PARAM_METADATA_PREFIX = "Metadata_";

  /**
   * Key name for barcode format in the report.
   */
  private static final String REPORT_PARAM_FORMAT = "Format";

  /**
   * Key name for barcode top-left x in report.
   */
  private static final String REPORT_PARAM_X = "X";

  /**
   * Key name for barcode top-left y in report.
   */
  private static final String REPORT_PARAM_Y = "Y";

  /**
   * Key name for barcode width in report.
   */
  private static final String REPORT_PARAM_WIDTH = "Width";

  /**
   * Key name for barcode height in report.
   */
  private static final String REPORT_PARAM_HEIGHT = "Height";

  /**
   * Barcode format auto-detection.
   */
  protected boolean m_AutoDetect;

  /**
   * Expected barcode format.
   */
  protected BarcodeFormat m_Format;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Decode the data in a barcode.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("autoDetect", "autoDetect", true);
    m_OptionManager.add("format", "format", BarcodeFormat.QR_CODE);
  }

  /**
   * Enables or disables barcode format auto-detection.
   *
   * @param value enable auto-detection?
   */
  public void setAutoDetect(boolean value) {
    m_AutoDetect = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String autoDetectTipText() {
    return "Enable or disable barcode format auto-detection; if disabled, it will attempt to decode using the specified format.";
  }

  /**
   * Gets whether barcode format auto-detection is enabled.
   *
   * @return enable auto-detection?
   */
  public boolean getAutoDetect() {
    return m_AutoDetect;
  }

  /**
   * Gets the barcode format hint.
   *
   * @return barcode format
   */
  public BarcodeFormat getFormat() {
    return m_Format;
  }

  /**
   * Set barcode format hint.
   *
   * @param value barcode format
   */
  public void setFormat(BarcodeFormat value) {
    m_Format = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "Barcode format type to expect.";
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
    String out = null;
    BufferedImage image = BufferedImageHelper.deepCopy((BufferedImage) ((AbstractImageContainer) m_InputToken.getPayload()).getImage());

    try {
      int width = image.getWidth();
      int height = image.getHeight();

      LuminanceSource source = new RGBLuminanceSource(width, height, image.getRGB(0, 0, width, height, null, 0, width));
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

      Reader reader = new MultiFormatReader();
      Map<DecodeHintType, Object> hints = new HashMap<>();
      if (!m_AutoDetect)
        hints.put(DecodeHintType.POSSIBLE_FORMATS, m_Format);
      Result data = reader.decode(bitmap, hints);

      TextContainer container = new TextContainer();
      container.setContent(data.getText());
      Report report = container.getReport();
      report.setStringValue(REPORT_PARAM_FORMAT, data.getBarcodeFormat().toString());
      for (Map.Entry<ResultMetadataType, ?> d : data.getResultMetadata().entrySet())
        report.addParameter(REPORT_PARAM_METADATA_PREFIX + d.getKey().name(), d.getValue().toString());
      ResultPoint[] points = data.getResultPoints();
      report.setNumericValue(REPORT_PARAM_X, points[0].getX());
      report.setNumericValue(REPORT_PARAM_Y, points[0].getY());
      report.setNumericValue(REPORT_PARAM_WIDTH, points[2].getX() - points[0].getX());
      report.setNumericValue(REPORT_PARAM_HEIGHT, points[1].getY() - points[0].getY());

      m_OutputToken = new Token(container);
    }
    catch (Exception e) {
      out = handleException("Failed to extract barcode!", e);
    }

    return out;
  }
}
