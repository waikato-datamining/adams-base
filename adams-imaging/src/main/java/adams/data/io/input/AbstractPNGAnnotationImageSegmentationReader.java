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
 * AbstractPNGAnnotationImageSegmentationReader.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.io.File;

/**
 * Ancestor for readers that read the annotations from a single PNG file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPNGAnnotationImageSegmentationReader
    extends AbstractImageSegmentationAnnotationReader
    implements ImageSegmentationAnnotationReaderWithLayerNames {

  private static final long serialVersionUID = -5567473437385041915L;

  /** whether to skip the first layer (usually background). */
  protected boolean m_SkipFirstLayer;

  /** the layer names. */
  protected BaseString[] m_LayerNames;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"skip-first-layer", "skipFirstLayer",
	true);

    m_OptionManager.add(
	"layer-name", "layerNames",
	new BaseString[0]);
  }

  /**
   * Sets whether to skip the first layer.
   *
   * @param value	true if to skip
   */
  public void setSkipFirstLayer(boolean value) {
    m_SkipFirstLayer = value;
    reset();
  }

  /**
   * Returns whether to skip the first layer.
   *
   * @return		true if to skip
   */
  public boolean getSkipFirstLayer() {
    return m_SkipFirstLayer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipFirstLayerTipText() {
    return "If enabled, the first layer gets skipped (usually the background).";
  }

  /**
   * Sets the names for the layers to use.
   *
   * @param value	the names
   */
  public void setLayerNames(BaseString[] value) {
    m_LayerNames = value;
    reset();
  }

  /**
   * Returns the names for the layers to use.
   *
   * @return		the names
   */
  public BaseString[] getLayerNames() {
    return m_LayerNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerNamesTipText() {
    return "The names to use for the layers; if additional layers should be present in the data, names get assigned automatically.";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"jpg"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "jpg";
  }

  /**
   * Locates the PNG annotation file.
   *
   * @param file	the JPG file to determine the PNG file for
   * @return		the PNG file, or null if failed to determine
   */
  protected PlaceholderFile locatePNG(PlaceholderFile file) {
    PlaceholderFile	png;

    png = FileUtils.replaceExtension(file, ".png");
    if (png.exists())
      return png;

    png = FileUtils.replaceExtension(file, ".PNG");
    if (png.exists())
      return png;

    return null;
  }

  /**
   * Hook method for performing checks before reading the data.
   *
   * @param file	the file to check
   * @return		null if no errors, otherwise error message
   */
  @Override
  protected String check(PlaceholderFile file) {
    String	result;
    File	png;

    result = super.check(file);

    if (result == null) {
      png = locatePNG(file);
      if (png == null)
	result = "Associated PNG file with annotations is missing!";
    }

    return result;
  }
}
