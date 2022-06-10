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
 * AbstractCustomPNGAnnotationImageSegmentationReader.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.PlaceholderFile;

import java.awt.image.BufferedImage;

/**
 * Ancestor for readers that read the annotations from a single PNG file and allow the reader to be specified.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCustomPNGAnnotationImageSegmentationReader
    extends AbstractPNGAnnotationImageSegmentationReader {

  private static final long serialVersionUID = -5567473437385041915L;

  /** the image reader for the PNG. */
  protected AbstractImageReader m_Reader;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"reader", "reader",
	getDefaultReader());
  }

  /**
   * Returns the default image reader.
   *
   * @return		the default
   */
  protected AbstractImageReader getDefaultReader() {
    return new PNGImageReader();
  }

  /**
   * Sets the image reader to use.
   *
   * @param value	the image reader
   */
  public void setReader(AbstractImageReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the imag reader to use.
   *
   * @return		the image reader
   */
  public AbstractImageReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String readerTipText() {
    return "The image reader to use.";
  }

  /**
   * Reads the PNG file associated with the specified JPG file.
   *
   * @param file	the JPG file to load the PNG file for
   * @return		the image
   */
  protected BufferedImage readPNG(PlaceholderFile file) {
    return m_Reader.read(locatePNG(file)).toBufferedImage();
  }
}
