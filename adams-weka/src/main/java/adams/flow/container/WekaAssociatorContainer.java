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
 * WekaAssociatorContainer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import weka.associations.AssociationRule;
import weka.associations.Associator;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container for associators and their rules.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAssociatorContainer
  extends WekaModelContainer {

  private static final long serialVersionUID = 5598838063694695406L;

  /** the identifier for the rules. */
  public final static String VALUE_RULES = "Rules";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public WekaAssociatorContainer() {
    this(null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   */
  public WekaAssociatorContainer(Associator model) {
    this(model, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   * @param header	the header to use
   */
  public WekaAssociatorContainer(Associator model, Instances header) {
    this(model, header, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   * @param header	the header to use
   * @param data	the data to use
   */
  public WekaAssociatorContainer(Associator model, Instances header, Instances data) {
    this(model, header, data, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param model	the model to use
   * @param header	the header to use
   * @param data	the data to use
   * @param rules	the rules
   */
  public WekaAssociatorContainer(Associator model, Instances header, Instances data, List<AssociationRule> rules) {
    super();

    store(VALUE_MODEL, model);
    store(VALUE_HEADER, header);
    store(VALUE_DATASET, data);
    store(VALUE_RULES, rules);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_MODEL, "rules; " + List.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> 	result;
    Iterator<String>	other;

    other = super.names();
    result = new ArrayList<>();
    while (other.hasNext())
      result.add(other.next());

    result.add(VALUE_RULES);

    return result.iterator();
  }
}
