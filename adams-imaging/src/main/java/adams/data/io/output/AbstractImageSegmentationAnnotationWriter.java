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
 * AbstractImageSegmentationAnnotationWriter.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.ImageSegmentationAnnotationReader;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.image.BufferedImage;

/**
 * Ancestor for writers for image segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageSegmentationAnnotationWriter
  extends AbstractOptionHandler 
  implements ImageSegmentationAnnotationWriter {

  private static final long serialVersionUID = -2475426542124421777L;

  /** whether to skip writing base image. */
  protected boolean m_SkipBaseImage;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "skip-base-image", "skipBaseImage",
        false);
  }

  /**
   * Sets whether to skip writing the base image.
   *
   * @param value 	true if to skip
   */
  public void setSkipBaseImage(boolean value) {
    m_SkipBaseImage = value;
    reset();
  }

  /**
   * Returns whether to skip writing the base image.
   *
   * @return 		true if to skip
   */
  public boolean getSkipBaseImage() {
    return m_SkipBaseImage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipBaseImageTipText() {
    return "If enabled, the base image is not written to disk (eg when updating only the layers).";
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  public abstract ImageSegmentationAnnotationReader getCorrespondingReader();

  /**
   * Hook method for performing checks before writing the data.
   *
   * @param file	the file to check
   * @param annotations the annotations to write
   * @return		null if no errors, otherwise error message
   */
  protected String check(PlaceholderFile file, ImageSegmentationContainer annotations) {
    if (file == null)
      return "No file provided!";
    return null;
  }

  /**
   * Writes the base image unless skipped.
   *
   * @param file	the filename for the base image
   * @param annotations	the container with the layers and base image
   * @return		null if successfully written, otherwise error message
   */
  protected String writeBaseImage(PlaceholderFile file, ImageSegmentationContainer annotations) {
    String			result;
    JAIImageWriter		writer;
    BufferedImageContainer cont;

    if (m_SkipBaseImage)
      return null;

    writer = new JAIImageWriter();
    if (isLoggingEnabled())
      getLogger().info("Writing base image to: " + file);
    cont   = new BufferedImageContainer();
    cont.setImage(annotations.getValue(ImageSegmentationContainer.VALUE_BASE, BufferedImage.class));
    result = writer.write(file, cont);
    if (result != null)
      result = "Failed to write base image: " + result;

    return result;
  }

  /**
   * Writes the image segmentation annotations.
   *
   * @param file	the file to write to
   * @param annotations the annotations to write
   * @return		null if successfully written, otherwise error message
   */
  protected abstract String doWrite(PlaceholderFile file, ImageSegmentationContainer annotations);

  /**
   * Writes the image segmentation annotations.
   *
   * @param file	the file to write to
   * @param annotations the annotations to write
   * @return		null if successfully written, otherwise error message
   */
  public String write(PlaceholderFile file, ImageSegmentationContainer annotations) {
    String	result;

    result = check(file, annotations);
    if (result == null)
      result = writeBaseImage(file, annotations);
    if (result == null)
      result = doWrite(file, annotations);

    return result;
  }
}
