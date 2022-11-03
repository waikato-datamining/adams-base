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
 * OpenCVImageWriter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.OpenCVImageReader;
import adams.data.opencv.OpenCVImageContainer;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpenCVImageWriter
  extends AbstractImageWriter<OpenCVImageContainer> {

  private static final long serialVersionUID = 7557585219819025299L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes images using OpenCV.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new OpenCVImageReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new OpenCVImageReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return new OpenCVImageReader();
  }

  /**
   * Performs the actual writing of the image file.
   *
   * @param file the file to write to
   * @param cont the image container to write
   * @return null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, OpenCVImageContainer cont) {
    if (imwrite(file.getAbsolutePath(), cont.getContent()))
      return null;
    else
      return "Failed to write image to: " + file;
  }
}
