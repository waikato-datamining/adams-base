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

package adams.data.barcode.decode;

import adams.core.Utils;
import adams.data.image.AbstractImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.text.TextContainer;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Decodes the data in a barcode using the ZXing library.<br>
 * For more information see:<br>
 * https:&#47;&#47;github.com&#47;zxing&#47;zxing
 * <br><br>
 <!-- globalinfo-end -->
 * <br><br>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-autoDetect &lt;boolean&gt; (property: autoDetect)
 * &nbsp;&nbsp;&nbsp;Enable or disable barcode format auto-detection; if disabled, it will attempt 
 * &nbsp;&nbsp;&nbsp;to decode using the specified format.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-format &lt;AZTEC|CODABAR|CODE_39|CODE_93|CODE_128|DATA_MATRIX|EAN_8|EAN_13|ITF|MAXICODE|PDF_417|QR_CODE|RSS_14|RSS_EXPANDED|UPC_A|UPC_E|UPC_EAN_EXTENSION&gt; [-format ...] (property: format)
 * &nbsp;&nbsp;&nbsp;Barcode format type to expect.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class ZXing
  extends AbstractBarcodeDecoder {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 6149942254926179607L;

  /**
   * Key name prefix for ZXing result metadata.
   */
  public static final String REPORT_PARAM_METADATA_PREFIX = "Metadata-";

  /**
   * Key name for barcode format in the report.
   */
  public static final String REPORT_PARAM_FORMAT = "Format";

  /**
   * Key name for barcode top-left x in report.
   */
  public static final String REPORT_PARAM_X = "X";

  /**
   * Key name for barcode top-left y in report.
   */
  public static final String REPORT_PARAM_Y = "Y";

  /**
   * Key name for barcode width in report.
   */
  public static final String REPORT_PARAM_WIDTH = "Width";

  /**
   * Key name for barcode height in report.
   */
  public static final String REPORT_PARAM_HEIGHT = "Height";

  /**
   * Barcode format auto-detection.
   */
  protected boolean m_AutoDetect;

  /**
   * Expected barcode format.
   */
  protected BarcodeFormat[] m_Format;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Decodes the data in a barcode using the ZXing library.\n"
        + "For more information see:\n"
        + "https://github.com/zxing/zxing";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "autoDetect", "autoDetect",
      true);

    m_OptionManager.add(
      "format", "format",
      new BarcodeFormat[0]);
  }

  /**
   * Enables or disables barcode format auto-detection.
   *
   * @param value enable auto-detection?
   */
  public void setAutoDetect(boolean value) {
    m_AutoDetect = value;
    reset();
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
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String autoDetectTipText() {
    return "Enable or disable barcode format auto-detection; if disabled, it will attempt to decode using the specified format.";
  }

  /**
   * Set barcode format hint.
   *
   * @param value barcode format
   */
  public void setFormat(BarcodeFormat[] value) {
    m_Format = value;
    reset();
  }

  /**
   * Gets the barcode format hint.
   *
   * @return barcode format
   */
  public BarcodeFormat[] getFormat() {
    return m_Format;
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
   * Performs the actual decoding.
   *
   * @param image the image to extract the barcode from
   * @return a TextContainer with the decoded barcode text and (optional) meta-data
   */
  protected TextContainer doDecode(AbstractImageContainer image) {
    TextContainer result;

    try {
      BufferedImage img = image.toBufferedImage();
      int width = img.getWidth();
      int height = img.getHeight();

      LuminanceSource source = new RGBLuminanceSource(width, height, img.getRGB(0, 0, width, height, null, 0, width));
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

      Reader reader = new MultiFormatReader();
      Map<DecodeHintType, Object> hints = new HashMap<>();
      if (!m_AutoDetect)
        hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(m_Format));
      Result data = reader.decode(bitmap, hints);

      result = new TextContainer();
      result.setContent(data.getText());
      Report report = result.getReport();
      report.addField(new Field(REPORT_PARAM_FORMAT, DataType.STRING));
      report.setStringValue(REPORT_PARAM_FORMAT, data.getBarcodeFormat().toString());
      for (Map.Entry<ResultMetadataType, ?> d : data.getResultMetadata().entrySet()) {
        report.addField(new Field(REPORT_PARAM_METADATA_PREFIX + d.getKey().name(), DataType.STRING));
        if (d.getValue() instanceof Collection) {
          StringBuilder sb = new StringBuilder();
          if (sb.length() > 0)
            sb.append(",");
          for (Object o: ((Collection) d.getValue())) {
            if (o.getClass().isArray()) {
              sb.append("[");
              sb.append(Utils.arrayToString(o));
              sb.append("]");
            }
            else {
              sb.append(o.toString());
            }
          }
          report.setStringValue(REPORT_PARAM_METADATA_PREFIX + d.getKey().name(), sb.toString());
        }
        else if (d.getValue().getClass().isArray())
          report.setStringValue(REPORT_PARAM_METADATA_PREFIX + d.getKey().name(), Utils.arrayToString(d.getValue()));
        else
          report.setStringValue(REPORT_PARAM_METADATA_PREFIX + d.getKey().name(), d.getValue().toString());
      }
      ResultPoint[] points = data.getResultPoints();
      int minX = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxY = Integer.MIN_VALUE;
      for (ResultPoint p: points) {
	minX = Math.min(minX, (int) p.getX());
	maxX = Math.max(maxX, (int) p.getX());
	minY = Math.min(minY, (int) p.getY());
	maxY = Math.max(maxY, (int) p.getY());
      }
      if (isLoggingEnabled())
	getLogger().info("minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY=" + maxY);
      report.setNumericValue(REPORT_PARAM_X, minX);
      report.setNumericValue(REPORT_PARAM_Y, minY);
      report.setNumericValue(REPORT_PARAM_WIDTH, maxX - minX + 1);
      report.setNumericValue(REPORT_PARAM_HEIGHT, maxY - minY + 1);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to decode barcode!", e);
      result = null;
    }

    return result;
  }
}
