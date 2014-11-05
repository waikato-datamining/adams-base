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

/**
 * JAIOutputConsumer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.im4java.process.OutputConsumer;

/**
 * {@link OutputConsumer} that uses JAI to read the input stream and
 * turns it into a {@link BufferedImage}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JAIOutputConsumer
  implements OutputConsumer {

  /** the buffered image that was read from the stream. */
  protected BufferedImage m_Image;
  
  /**
   * Reads the input stream into a {@link BufferedImage}.
   * 
   * @param stream		the stream to read from
   * @throws IOException	if reading from stream fails
   */
  @Override
  public void consumeOutput(InputStream stream) throws IOException {
    m_Image = null;
    m_Image = ImageIO.read(stream);
  }
  
  /**
   * Returns the image that was read from the stream.
   * 
   * @return		 the image, null if none available (eg due to error)
   */
  public BufferedImage getImage() {
    return m_Image;
  }
}
