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
 * BoofCVImageReader.java
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.io.output.BoofCVImageWriter;
import adams.data.io.output.ImageWriter;
import boofcv.io.image.UtilImageIO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * BoofCV image reader for: bmp, gif, jpeg, jpg, pgm, png, ppm, wbmp<br>
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
public class BoofCVImageReader
  extends AbstractImageReader<BoofCVImageContainer> {
  
  /** for serialization. */
  private static final long serialVersionUID = 5347100846354068540L;

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
	"BoofCV image reader for: " + Utils.flatten(getFormatExtensions(), ", ")
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
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  public ImageWriter getCorrespondingWriter() {
    return new BoofCVImageWriter();
  }

  /**
   * Performs the actual reading of the image file.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  @Override
  protected BoofCVImageContainer doRead(PlaceholderFile file) {
    BoofCVImageContainer	result;
    BufferedImage		image;
    
    result = null;
    image  = UtilImageIO.loadImage(file.getAbsolutePath());
    
    if (image != null) {
      result = new BoofCVImageContainer();
      result.setImage(BoofCVHelper.toBoofCVImage(image));
    }
    
    return result;
  }
}
