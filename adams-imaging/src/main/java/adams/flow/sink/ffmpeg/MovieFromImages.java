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
 * MovieFromImages.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.ffmpeg;

/**
 * Creates a movie out of a range of images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MovieFromImages
  extends AbstractFFmpegPluginWithOptions {
  
  /** for serialization. */
  private static final long serialVersionUID = -59377070948215137L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a movie from image files.\n"
	+ "The input file name has to use an ffmpeg wildcard, e.g., "
	+ "'img%04d.png'. This will use all images that have 4 digits, "
	+ "i.e., 'img0001.png, img0002.png, ...'.";
  }
  
  /**
   * Assembles the command-line, excluding additional options 
   * and executable.
   * 
   * @return		the command-line
   */
  @Override
  protected String assembleActualInputOptions() {
    return "";
  }
  
  /**
   * Assembles the command-line, excluding additional options 
   * and executable.
   * 
   * @return		the command-line
   */
  @Override
  protected String assembleActualOutputOptions() {
    return "";
  }
}
