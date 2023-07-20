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
 * JSON.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io.filecomplete;

import adams.core.io.FileUtils;
import net.minidev.json.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Checks whether the JSON can be parsed.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JSON
  extends AbstractFileCompleteCheck {

  private static final long serialVersionUID = 8742612165238773767L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the JSON can be parsed.";
  }

  /**
   * Checks whether the byte buffer is complete.
   *
   * @param buffer the buffer to check
   * @return true if complete
   */
  @Override
  public boolean isComplete(byte[] buffer) {
    JSONParser 		parser;

    try {
      parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      parser.parse(buffer);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Checks whether the file is complete.
   *
   * @param file the file to check
   * @return true if complete
   */
  @Override
  public boolean isComplete(File file) {
    FileReader 		freader;
    BufferedReader 	breader;
    JSONParser 		parser;

    freader = null;
    breader = null;
    try {
      freader = new FileReader(file.getAbsolutePath());
      breader = new BufferedReader(freader);
      parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      parser.parse(breader);
      return true;
    }
    catch (Exception e) {
      return false;
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }
  }
}
