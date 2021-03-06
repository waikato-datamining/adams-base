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
 * ImageMetaDataHelper.java
 * Copyright (C) 2014-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import adams.core.Utils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.GenericImageMetadata.GenericImageMetadataItem;

import java.io.File;
import java.util.HashSet;

/**
 * Helper class for reading meta-data from images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageMetaDataHelper {

  /** the date/time regexp mask. */
  public final static String DATETIME_MASK = "([0-9][0-9][0-9][0-9]):([0-9][0-9]):([0-9][0-9]) ([0-9][0-9]):([0-9][0-9]):([0-9][0-9])";
  
  /**
   * Fixes date/time strings. Replaces the ":" in the date with "-" to be
   * ADAMS compatible.
   * 
   * @param s		the string to inspect
   * @return		the (potentially) fixed string
   */
  protected static String fixDateTime(String s) {
    String	result;
    
    if (s.matches(DATETIME_MASK))
      result = s.replaceAll(DATETIME_MASK, "$1-$2-$3 $4:$5:$6");
    else
      result = s;
    
    return result;
  }

  /**
   * Reads the meta-data from the file (Commons Imaging).
   *
   * @param file	the file to read the meta-data from
   * @return		the meta-data
   * @throws Exception	if failed to read meta-data
   */
  public static SpreadSheet commons(File file) throws Exception {
    SpreadSheet						sheet;
    Row							row;
    org.apache.commons.imaging.common.ImageMetadata	meta;
    String[]						parts;
    String						key;
    String						value;
    org.apache.commons.imaging.ImageInfo 		info;
    String						infoStr;
    String[]						lines;
    HashSet<String>					keys;

    sheet = new DefaultSpreadSheet();

    // header
    row = sheet.getHeaderRow();
    row.addCell("K").setContent("Key");
    row.addCell("V").setContent("Value");

    keys = new HashSet<String>();

    // meta-data
    meta = Imaging.getMetadata(file.getAbsoluteFile());
    if (meta != null) {
      for (Object item: meta.getItems()) {
	key   = null;
	value = null;
	if (item instanceof GenericImageMetadataItem) {
	  key   = ((GenericImageMetadataItem) item).getKeyword().trim();
	  value = ((GenericImageMetadataItem) item).getText().trim();
	}
	else {
	  parts = item.toString().split(": ");
	  if (parts.length == 2) {
	    key   = parts[0].trim();
	    value = parts[1].trim();
	  }
	}
	if (key != null) {
	  if (!keys.contains(key)) {
	    keys.add(key);
	    row = sheet.addRow();
	    row.addCell("K").setContent(key);
	    row.addCell("V").setContent(fixDateTime(Utils.unquote(value)));
	  }
	}
      }
    }

    // image info
    info = Imaging.getImageInfo(file.getAbsoluteFile());
    if (info != null) {
      infoStr = info.toString();
      lines = infoStr.split(System.lineSeparator());
      for (String line: lines) {
	parts = line.split(": ");
	if (parts.length == 2) {
	  key   = parts[0].trim();
	  value = parts[1].trim();
	  if (!keys.contains(key)) {
	    row   = sheet.addRow();
	    row.addCell("K").setContent(key);
	    row.addCell("V").setContent(Utils.unquote(value));
	    keys.add(key);
	  }
	}
      }
    }

    return sheet;
  }

  /**
   * Reads the meta-data from the file (using meta-data extractor).
   * 
   * @param file	the file to read the meta-data from
   * @return		the meta-data
   * @throws Exception	if failed to read meta-data
   */
  public static SpreadSheet metaDataExtractor(File file) throws Exception {
    SpreadSheet			sheet;
    Row				row;
    com.drew.metadata.Metadata 	metadata;

    sheet = new DefaultSpreadSheet();
    
    // header
    row = sheet.getHeaderRow();
    row.addCell("K").setContent("Key");
    row.addCell("V").setContent("Value");
    
    metadata = ImageMetadataReader.readMetadata(file.getAbsoluteFile());
    for (Directory directory : metadata.getDirectories()) {
      for (Tag tag : directory.getTags()) {
	row = sheet.addRow();
	row.addCell("K").setContent(tag.getTagName());
	row.addCell("V").setContent(fixDateTime(tag.getDescription()));
      }
  }    
    return sheet;
  }
}
