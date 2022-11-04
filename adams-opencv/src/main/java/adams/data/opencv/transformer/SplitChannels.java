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
 * SplitChannels.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.opencv.transformer;

import adams.data.opencv.OpenCVImageContainer;
import org.bytedeco.opencv.opencv_core.MatVector;

import static org.bytedeco.opencv.global.opencv_core.split;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SplitChannels
    extends AbstractOpenCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the channels in the incoming image into separate containers.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected OpenCVImageContainer[] doTransform(OpenCVImageContainer img) {
    OpenCVImageContainer[]	result;
    MatVector			channels;
    int				i;

    result   = new OpenCVImageContainer[img.getContent().channels()];
    channels = new MatVector();
    split(img.getContent(), channels);
    for (i = 0; i < result.length; i++) {
      result[i] = (OpenCVImageContainer) img.getHeader();
      result[i].setContent(channels.get(i));
      result[i].getReport().setNumericValue("Channel", i);
    }

    return result;
  }
}
