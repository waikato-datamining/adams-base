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
 * OpenCVImageReader.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.io.PlaceholderFile;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.OpenCVImageWriter;
import adams.data.opencv.ImreadMode;
import adams.data.opencv.OpenCVImageContainer;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

/**
 <!-- globalinfo-start -->
 * Reads images using OpenCV.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-mode &lt;IMREAD_UNCHANGED|IMREAD_GRAYSCALE|IMREAD_COLOR|IMREAD_ANYDEPTH|IMREAD_ANYCOLOR|IMREAD_LOAD_GDAL|IMREAD_REDUCED_GRAYSCALE_2|IMREAD_REDUCED_COLOR_2|IMREAD_REDUCED_GRAYSCALE_4|IMREAD_REDUCED_COLOR_4|IMREAD_REDUCED_GRAYSCALE_8|IMREAD_REDUCED_COLOR_8|IMREAD_IGNORE_ORIENTATION&gt; (property: mode)
 * &nbsp;&nbsp;&nbsp;How to read the image.
 * &nbsp;&nbsp;&nbsp;default: IMREAD_UNCHANGED
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpenCVImageReader
    extends AbstractImageReader<OpenCVImageContainer> {

  private static final long serialVersionUID = -114794988520002906L;

  /** the format extensions. */
  protected String[] m_FormatExtensions;

  /** the mode. */
  protected ImreadMode m_Mode;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads images using OpenCV.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "mode", "mode",
        ImreadMode.IMREAD_UNCHANGED);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    List<String> formats;

    super.initialize();

    formats = new ArrayList<>();
    formats.addAll(Arrays.asList(ImageIO.getReaderFileSuffixes()));
    if (!formats.contains("ppm"))
      formats.add("ppm");
    if (!formats.contains("pgm"))
      formats.add("pgm");
    Collections.sort(formats);
    m_FormatExtensions = formats.toArray(new String[formats.size()]);
  }

  /**
   * Sets the read mode.
   *
   * @param value	the mode
   */
  public void setMode(ImreadMode value) {
    m_Mode = value;
    reset();
  }

  /**
   * Returns the read mode.
   *
   * @return		the mode
   */
  public ImreadMode getMode() {
    return m_Mode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String modeTipText() {
    return "How to read the image.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "OpenCV";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return m_FormatExtensions;
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return the writer, null if none available
   */
  @Override
  public AbstractImageWriter getCorrespondingWriter() {
    return new OpenCVImageWriter();
  }

  /**
   * Performs the actual reading of the image file.
   *
   * @param file the file to read
   * @return the image container, null if failed to read
   */
  @Override
  protected OpenCVImageContainer doRead(PlaceholderFile file) {
    OpenCVImageContainer	result;
    Mat 			image;

    result = null;
    image  = imread(file.getAbsolutePath(), m_Mode.getMode());
    if (image != null) {
      result = new OpenCVImageContainer();
      result.setImage(image);
    }

    return result;
  }
}
