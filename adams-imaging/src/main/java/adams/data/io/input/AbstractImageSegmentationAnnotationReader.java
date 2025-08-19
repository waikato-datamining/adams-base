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
 * AbstractImageSegmentationAnnotationReader.java
 * Copyright (C) 2020-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.io.output.ImageSegmentationAnnotationWriter;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Ancestor for readers for image segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageSegmentationAnnotationReader
  extends AbstractOptionHandler
  implements ImageSegmentationAnnotationReader {

  private static final long serialVersionUID = -2475426542124421777L;

  /** whether to use an alternative annotation directory. */
  protected boolean m_UseAlternativeAnnotationDir;

  /** the alternative annotation directory. */
  protected PlaceholderDirectory m_AlternativeAnnotationDir;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-alternative-annotation-dir", "useAlternativeAnnotationDir",
      false);

    m_OptionManager.add(
      "alternative-annotation-dir", "alternativeAnnotationDir",
      new PlaceholderDirectory());
  }

  /**
   * Sets whether to use an alternative directory for the annotations.
   *
   * @param value	true if to use alternative dir
   */
  @Override
  public void setUseAlternativeAnnotationDir(boolean value) {
    m_UseAlternativeAnnotationDir = value;
    reset();
  }

  /**
   * Returns whether to use an alternative directory for the annotations.
   *
   * @return		true if to use alternative dir
   */
  @Override
  public boolean getUseAlternativeAnnotationDir() {
    return m_UseAlternativeAnnotationDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAlternativeAnnotationDirTipText() {
    return "If enabled, uses the specified directory to locate annotations.";
  }

  /**
   * Sets the alternative directory for the annotations.
   *
   * @param value	the alternative dir
   */
  @Override
  public void setAlternativeAnnotationDir(PlaceholderDirectory value) {
    m_AlternativeAnnotationDir = value;
    reset();
  }

  /**
   * Returns the alternative directory for the annotations.
   *
   * @return		the alternative dir
   */
  @Override
  public PlaceholderDirectory getAlternativeAnnotationDir() {
    return m_AlternativeAnnotationDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alternativeAnnotationDirTipText() {
    return "The alternative directory to look for annotations.";
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  public abstract ImageSegmentationAnnotationWriter getCorrespondingWriter();

  /**
   * Hook method for performing checks before reading the data.
   *
   * @param file	the file to check
   * @return		null if no errors, otherwise error message
   */
  protected String check(PlaceholderFile file) {
    if (file == null)
      return "No file provided!";

    if (m_UseAlternativeAnnotationDir) {
      if (!m_AlternativeAnnotationDir.exists())
	return "Alternative annotation directory does not exist: " + m_AlternativeAnnotationDir;
      if (m_AlternativeAnnotationDir.isFile())
	return "Alternative annotation directory points to a file: " + m_AlternativeAnnotationDir;
    }

    return null;
  }

  /**
   * Reads the image segmentation annotations.
   *
   * @param file	the file to read from
   * @return		the annotations
   */
  protected abstract ImageSegmentationContainer doRead(PlaceholderFile file);

  /**
   * Reads the image segmentation annotations.
   *
   * @param file	the file to read from
   * @return		the annotations
   */
  public ImageSegmentationContainer read(PlaceholderFile file) {
    String	msg;

    msg = check(file);
    if (msg != null)
      throw new IllegalStateException("Failed to read annotations from: " + file + "\n" + msg);

    return doRead(file);
  }
}
