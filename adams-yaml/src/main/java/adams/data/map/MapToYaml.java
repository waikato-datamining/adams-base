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
 * MapToYaml.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.map;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.Map;

/**
 * Converts the Map into a YAML string.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MapToYaml
  extends AbstractMapToString {

  private static final long serialVersionUID = -7558243826149418238L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the Map into a YAML string.";
  }

  /**
   * Converts the map into a string.
   *
   * @param map the map to convert
   * @return the generated string
   */
  @Override
  protected String toString(Map map) {
    Yaml yaml;

    yaml = new Yaml();
    return yaml.dumpAs(map, Tag.MAP, FlowStyle.BLOCK);
  }
}
