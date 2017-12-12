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
 * MultiQRCode.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.barcode.decode;

import adams.core.Utils;
import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import adams.data.text.TextContainer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Decodes all the QR codes using the ZXing library.<br>
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
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 */
public class MultiQRCode
  extends AbstractBarcodeDecoder {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = 6149942254926179607L;

  /**
   * Key name for code count.
   */
  public static final String REPORT_PARAM_COUNT = "Count";

  /**
   * Key name prefix for ZXing result metadata.
   */
  public static final String REPORT_PARAM_METADATA_PREFIX = "Metadata-";

  /**
   * Key name for text data in the report.
   */
  public static final String REPORT_PARAM_DATA = "Data";

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
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Decodes all the QR codes using the ZXing library.\n"
        + "For more information see:\n"
        + "https://github.com/zxing/zxing";
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

      MultipleBarcodeReader reader = new QRCodeMultiReader();
      Result[] data = reader.decodeMultiple(bitmap);

      result = new TextContainer();
      Report report = result.getReport();
      report.setNumericValue(REPORT_PARAM_COUNT, data.length);
      for (int i = 0; i < data.length; i++) {
        report.setStringValue(REPORT_PARAM_DATA + "." + (i+1), data[i].getText());
        report.setStringValue(REPORT_PARAM_FORMAT + "." + (i+1), data[i].getBarcodeFormat().toString());
        for (Map.Entry<ResultMetadataType, ?> d : data[i].getResultMetadata().entrySet()) {
          if (d.getValue() instanceof Collection) {
            StringBuilder sb = new StringBuilder();
            if (sb.length() > 0)
              sb.append(",");
            for (Object o : ((Collection) d.getValue())) {
              if (o.getClass().isArray()) {
                sb.append("[");
                sb.append(Utils.arrayToString(o));
                sb.append("]");
              }
              else {
                sb.append(o.toString());
              }
            }
            report.setStringValue(REPORT_PARAM_METADATA_PREFIX + d.getKey().name() + "." + (i+1), sb.toString());
          }
          else if (d.getValue().getClass().isArray())
            report.setStringValue(REPORT_PARAM_METADATA_PREFIX + d.getKey().name() + "." + (i+1), Utils.arrayToString(d.getValue()));
          else
            report.setStringValue(REPORT_PARAM_METADATA_PREFIX + d.getKey().name() + "." + (i+1), d.getValue().toString());
        }
        ResultPoint[] points = data[i].getResultPoints();
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (ResultPoint p : points) {
          minX = Math.min(minX, (int) p.getX());
          maxX = Math.max(maxX, (int) p.getX());
          minY = Math.min(minY, (int) p.getY());
          maxY = Math.max(maxY, (int) p.getY());
        }
        if (isLoggingEnabled())
          getLogger().info("minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY=" + maxY);
        report.setNumericValue(REPORT_PARAM_X + "." + (i+1), minX);
        report.setNumericValue(REPORT_PARAM_Y + "." + (i+1), minY);
        report.setNumericValue(REPORT_PARAM_WIDTH + "." + (i+1), maxX - minX + 1);
        report.setNumericValue(REPORT_PARAM_HEIGHT + "." + (i+1), maxY - minY + 1);
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to decode barcode!", e);
      result = null;
    }


    return result;
  }
}
