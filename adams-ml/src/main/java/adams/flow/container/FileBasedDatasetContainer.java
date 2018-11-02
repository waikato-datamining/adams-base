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
 * FileBasedDatasetContainer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for storing file names for file-based dataset generation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileBasedDatasetContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 1960872156580346093L;

  /** the identifier for the training files. */
  public final static String VALUE_TRAIN = "Train";

  /** the identifier for the test files. */
  public final static String VALUE_TEST = "Test";

  /** the identifier for the validation files. */
  public final static String VALUE_VALIDATION = "Validation";

  /** the identifier for the negative files. */
  public final static String VALUE_NEGATIVE = "Negative";

  /**
   * Initializes the container.
   */
  public FileBasedDatasetContainer() {
    this(null, null, null, null);
  }

  /**
   * Initializes the container with the specified file names.
   *
   * @param train	the training files
   * @param test	the test files (optional)
   * @param validation	the validation files (optional)
   * @param negative	the negative files (optional)
   */
  public FileBasedDatasetContainer(String[] train, String[] test, String[] validation, String[] negative) {
    super();

    store(VALUE_TRAIN, train);
    store(VALUE_TEST, test);
    store(VALUE_VALIDATION, validation);
    store(VALUE_NEGATIVE, negative);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_TRAIN, "training files", String[].class);
    addHelp(VALUE_TEST, "test files (optional)", String[].class);
    addHelp(VALUE_VALIDATION, "validation files (optional)", String[].class);
    addHelp(VALUE_NEGATIVE, "negative files (optional)", String[].class);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<>();

    result.add(VALUE_TRAIN);
    result.add(VALUE_TEST);
    result.add(VALUE_VALIDATION);
    result.add(VALUE_NEGATIVE);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_TRAIN);
  }
}
