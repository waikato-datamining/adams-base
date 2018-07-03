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
 * SpreadSheetCellSelectionContainer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.SpreadSheetCellSelector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for storing output from the {@link SpreadSheetCellSelector}
 * transformer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetCellSelectionContainer
  extends AbstractContainer {

  private static final long serialVersionUID = -383683828748866169L;

  /** the key for the original spreadsheet. */
  public final static String VALUE_ORIGINAL = "Original";

  /** the key for the spreadsheet with the selected values. */
  public final static String VALUE_SELECTED = "Selected";

  /** the key for the spreadsheet with the subset of values. */
  public final static String VALUE_SUBSET = "Subset";

  /**
   * Default constructor.
   */
  public SpreadSheetCellSelectionContainer() {
    this(null, null, null);
  }

  /**
   * Initializes the container with the relevant spreadheets.
   *
   * @param original	the original spreadsheet
   * @param selected	the X/Y coordinates and values of the selected cells
   * @param subset	the spreadsheet with only the selected cells remaining
   */
  public SpreadSheetCellSelectionContainer(SpreadSheet original, SpreadSheet selected, SpreadSheet subset) {
    super();
    setValue(VALUE_ORIGINAL, original);
    setValue(VALUE_SELECTED, selected);
    setValue(VALUE_SUBSET,   subset);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> 	result;

    result = new ArrayList<>();
    result.add(VALUE_ORIGINAL);
    result.add(VALUE_SELECTED);
    result.add(VALUE_SUBSET);

    return result.iterator();
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();
    addHelp(VALUE_ORIGINAL, "original spreadsheet", SpreadSheet.class);
    addHelp(VALUE_SELECTED, "spreadsheet with coordinates of selected cells and associated values", SpreadSheet.class);
    addHelp(VALUE_SUBSET, "spreadsheet with only the selected values present", SpreadSheet.class);
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_ORIGINAL) && hasValue(VALUE_SELECTED) && hasValue(VALUE_SUBSET);
  }
}
