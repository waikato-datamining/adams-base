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
 * JsonParameterMapWriter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.JsonParameterMapReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Writes parameters in JSON format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JsonParameterMapWriter
  extends AbstractParameterMapWriter {

  private static final long serialVersionUID = -8694223705531146951L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes parameters in JSON format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new JsonParameterMapReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new JsonParameterMapReader().getFormatExtensions();
  }

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return new JsonParameterMapReader().getDefaultFormatExtension();
  }

  /**
   * Reads the parameters from the file.
   *
   * @param params	the parameters to write
   * @param output	the output file to write to
   * @throws Exception	if reading fails
   */
  @Override
  protected void doWrite(Map<String, Object> params, PlaceholderFile output) throws Exception {
    Gson 	gson;
    String	msg;

    gson = new GsonBuilder().setPrettyPrinting().create();
    msg  = FileUtils.writeToFileMsg(output.getAbsolutePath(), gson.toJson(params), false, null);
    if (msg != null)
      throw new Exception(msg);
  }
}
