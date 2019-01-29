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
 * AbstractApacheCommonsExifTagOperation.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.exiftagoperation;

import adams.core.QuickInfoHelper;
import adams.data.exif.commons.ExifTagEnum;

/**
 * Ancestor for EXIF tag operations using the Apache Commons Imaging library.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractApacheCommonsExifTagOperation
  extends AbstractExifTagOperation {

  private static final long serialVersionUID = 3967360712684705885L;

  /** the tag to process. */
  protected ExifTagEnum.Item m_Tag;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "tag", "tag",
      ExifTagEnum.getSingleton().values()[0]);
  }

  /**
   * Sets the tag to process.
   *
   * @param value	the tag
   */
  public void setTag(ExifTagEnum.Item value) {
    m_Tag = value;
    reset();
  }

  /**
   * Returns the tag to process.
   *
   * @return		the tag
   */
  public ExifTagEnum.Item getTag() {
    return m_Tag;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tagTipText() {
    return "The tag to process.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "tag", m_Tag, "tag: ");
  }
}
