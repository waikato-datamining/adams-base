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
 * ExifTagEnum.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.exif.commons;

import adams.core.ConfigurableEnumeration;
import adams.data.exif.commons.ExifTagEnum.Item;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Enumeration of EXIF tags using the Apache Commons Imaging library.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ExifTagEnum
  extends ConfigurableEnumeration<Item> {

  private static final long serialVersionUID = -4797024800799130165L;

  /** the singleton. */
  protected static ExifTagEnum m_Singleton;

  public class Item
    extends ConfigurableEnumeration.AbstractItem {

    private static final long serialVersionUID = 436668216654476094L;

    /** the wrapped tag. */
    protected transient TagInfo m_TagInfo;

    /**
     * Initializes the enum type.
     *
     * @param enumeration 	the owning enumeration
     * @param tagInfo		the taginfo to wrap
     */
    public Item(ExifTagEnum enumeration, TagInfo tagInfo) {
      super(enumeration, "" + tagInfo.tag, tagInfo.getDescription());
      m_TagInfo = tagInfo;
    }

    /**
     * The wrapped taginfo.
     *
     * @return		the taginfo
     */
    public TagInfo getTagInfo() {
      // null after serialization?
      if (m_TagInfo == null) {
	for (TagInfo tagInfo : ExifTagConstants.ALL_EXIF_TAGS) {
	  if (tagInfo.name.equals(getID())) {
	    m_TagInfo = tagInfo;
	    break;
	  }
	}
      }
      return m_TagInfo;
    }
  }

  /**
   * Initializes the items.
   *
   * @return		the items
   */
  protected Item[] initialize() {
    List<Item> 	result;
    Set<String>	unique;

    result = new ArrayList<>();
    unique = new HashSet<>();
    for (TagInfo tagInfo: ExifTagConstants.ALL_EXIF_TAGS) {
      if (unique.contains("" + tagInfo.tag))
        continue;
      result.add(new Item(this, tagInfo));
      unique.add("" + tagInfo.tag);
    }

    return result.toArray(new Item[0]);
  }

  /**
   * Initializes the enum type.
   *
   * @param id		the ID of the enum type, can be null
   * @param display	the display text, can be null
   */
  public Item newItem(String id, String display) {
    for (TagInfo tagInfo: ExifTagConstants.ALL_EXIF_TAGS) {
      if (tagInfo.name.equals(id))
        return new Item(this, tagInfo);
    }
    return null;
  }

  /**
   * Returns the singleton instance.
   *
   * @return		the singleton
   */
  public synchronized static ExifTagEnum getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ExifTagEnum();
    return m_Singleton;
  }
}
