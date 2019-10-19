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
 * YamlParameterMapReader.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * Stores the parameters in YAML format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class YamlParameterMapReader
  extends AbstractParameterMapReader {

  private static final long serialVersionUID = -7080929826308953435L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stores the parameters in YAML format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "YAML map";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"yaml", "yml"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "yaml";
  }

  /**
   * Reads the parameters from the file.
   *
   * @param input	the input file to read
   * @return		the parameters that were read
   * @throws Exception	if reading fails
   */
  @Override
  protected Map<String, Object> doRead(PlaceholderFile input) throws Exception {
    Map<String,Object> 	result;
    Yaml 		yaml;

    yaml   = new Yaml();
    result = yaml.load(Utils.flatten(FileUtils.loadFromFile(input), "\n"));
    if (result == null)
      throw new Exception("Failed to load parameters from: " + input);
    return result;
  }
}
