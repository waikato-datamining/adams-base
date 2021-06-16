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
 * Merge.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.multimapoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Merges the maps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Merge
  extends AbstractMultiMapOperation<Map>
  implements InPlaceProcessing {

  private static final long serialVersionUID = 5831884654010979232L;

  /** whether to overwrite existing keys. */
  protected boolean m_Overwrite;

  /** whether to skip creating a copy of the map. */
  protected boolean m_NoCopy;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Merges the maps.\n"
      + "Overwriting existing keys is optional.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "overwrite", "overwrite",
      false);

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);
  }

  /**
   * Sets whether to overwrite existing keys.
   *
   * @param value	true if to overwrite
   */
  public void setOverwrite(boolean value) {
    m_Overwrite = value;
    reset();
  }

  /**
   * Returns whether to overwrite existing keys.
   *
   * @return		true if to overwrite
   */
  public boolean getOverwrite() {
    return m_Overwrite;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overwriteTipText() {
    return "If enabled, existing keys can be overwritten by subsequent maps.";
  }

  /**
   * Sets whether to skip creating a copy of the map before merging.
   *
   * @param value	true if to skip creating copy
   */
  @Override
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the map before merging.
   *
   * @return		true if copying is skipped
   */
  @Override
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String noCopyTipText() {
    return "If enabled, no copy of the map is created before merging.";
  }

  /**
   * Returns the minimum number of maps that are required for the operation.
   *
   * @return the number of maps that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumMapsRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of maps that are required for the operation.
   *
   * @return the number of maps that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumMapsRequired() {
    return -1;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    List<String> options;

    result  = "";
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "overwrite", m_Overwrite, "overwrite"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no-copy"));
    result += QuickInfoHelper.flatten(options);

    return result.trim();
  }

  /**
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Performs the actual processing of the maps.
   *
   * @param maps 	the containers to process
   * @param errors	for collecting errors
   * @return 		the generated data
   */
  @Override
  protected Map doProcess(Map[] maps, MessageCollection errors) {
    Map		result;
    int		i;

    if (m_NoCopy)
      result = maps[0];
    else
      result = new HashMap(maps[0]);

    for (i = 1; i < maps.length; i++) {
      for (Object key: maps[i].keySet()) {
        if (!result.containsKey(key) || (m_Overwrite && result.containsKey(key)))
          result.put(key, maps[i].get(key));
      }
    }

    return result;
  }
}
