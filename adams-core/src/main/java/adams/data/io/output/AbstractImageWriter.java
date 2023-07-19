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
 * AbstractImageWriter.java
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.ClassLister;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;
import adams.data.io.input.ImageReader;

/**
 * Ancestor for image writers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageWriter<T extends AbstractImageContainer>
  extends AbstractOptionHandler
  implements ImageWriter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -6170592942529644703L;
  
  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  @Override
  public abstract ImageReader getCorrespondingReader();
  
  /**
   * Returns whether the writer is actually available.
   * 
   * @return		true if available and ready to use
   */
  @Override
  public boolean isAvailable() {
    return true;
  }

  /**
   * Performs checks.
   * 
   * @param cont	the image container to check
   */
  protected void check(T cont) {
    if (cont == null)
      throw new IllegalStateException("No image container provided!");
  }
  
  /**
   * Performs the actual writing of the image file.
   * 
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  protected abstract String doWrite(PlaceholderFile file, T cont);
  
  /**
   * Writes the image file.
   * 
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  public String write(PlaceholderFile file, T cont) {
    check(cont);
    return doWrite(file, cont);
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return		the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(ImageWriter.class);
  }
}
