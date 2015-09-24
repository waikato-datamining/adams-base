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
 * BoofCVImageWriter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.BoofCVImageReader;
import boofcv.io.image.UtilImageIO;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * BoofCV image writer for: bmp, gif, jpeg, jpg, pgm, png, ppm, wbmp<br>
 * For more information see:<br>
 * http:&#47;&#47;boofcv.org&#47;
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVImageWriter
  extends AbstractImageWriter<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6385191315392140321L;

  /** the format extensions. */
  protected String[] m_FormatExtensions;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"BoofCV image writer for: " + Utils.flatten(getFormatExtensions(), ", ")
	+ "\n"
	+ "For more information see:\n"
	+ "http://boofcv.org/";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    List<String>	formats;
    
    super.initialize();
    
    formats = new ArrayList<String>();
    formats.addAll(Arrays.asList(ImageIO.getWriterFileSuffixes()));
    if (!formats.contains("ppm"))
      formats.add("ppm");
    if (!formats.contains("pgm"))
      formats.add("pgm");
    Collections.sort(formats);
    m_FormatExtensions = formats.toArray(new String[formats.size()]);
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "BoofCV";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return m_FormatExtensions;
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return new BoofCVImageReader();
  }

  /**
   * Performs the actual writing of the image file.
   * 
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		always null
   */
  @Override
  protected String doWrite(PlaceholderFile file, BufferedImageContainer cont) {
    UtilImageIO.saveImage(cont.getImage(), file.getAbsolutePath());
    return null;
  }
}
