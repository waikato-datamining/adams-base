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
 * ImageMetaDataHelper.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.io.File;
import java.util.HashSet;

import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.common.ImageMetadata;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Helper class for reading meta-data from images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMetaDataHelper {

  /**
   * Reads the meta-data from the file.
   * 
   * @param file	the file to read the meta-data from
   * @return		the meta-data
   * @throws Exception	if failed to read meta-data
   */
  public static SpreadSheet getMetaData(File file) throws Exception {
    SpreadSheet				sheet;
    Row					row;
    IImageMetadata			meta;
    String[]				parts;
    String				key;
    String				value;
    org.apache.sanselan.ImageInfo	info;
    String				infoStr;
    String[]				lines;
    HashSet<String>			keys;

    sheet = new SpreadSheet();
    
    // header
    row = sheet.getHeaderRow();
    row.addCell("K").setContent("Key");
    row.addCell("V").setContent("Value");

    keys = new HashSet<String>();
    
    // meta-data
    meta = Sanselan.getMetadata(file);
    if (meta != null) {
      for (Object item: meta.getItems()) {
	key   = null;
	value = null;
	if (item instanceof ImageMetadata.Item) {
	  key   = ((ImageMetadata.Item) item).getKeyword().trim();
	  value = ((ImageMetadata.Item) item).getText().trim();
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
	    row.addCell("V").setContent(Utils.unquote(value));
	  }
	}
      }
    }
    
    // image info
    info = Sanselan.getImageInfo(file);
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
}
