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
 * WekaPredictionContainer.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import weka.core.Instance;
import weka.core.Utils;

/**
 * A container for predictions made by a classifier.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPredictionContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 872330681430825295L;

  /** the identifier for the Instance. */
  public final static String VALUE_INSTANCE = "Instance";

  /** the identifier for the Classification. */
  public final static String VALUE_CLASSIFICATION = "Classification";

  /** the identifier for the Classification's label. */
  public final static String VALUE_CLASSIFICATION_LABEL = "Classification label";

  /** the identifier for the Distribution. */
  public final static String VALUE_DISTRIBUTION = "Distribution";

  /** the identifier for the Range check. */
  public final static String VALUE_RANGECHECK = "Range check";

  /** the identifier for the classification of an abstaining classifier. */
  public final static String VALUE_ABSTENTION_CLASSIFICATION = "Abstention classification";

  /** the identifier for the classification label of an abstaining classifier. */
  public final static String VALUE_ABSTENTION_CLASSIFICATION_LABEL = "Abstention classification label";

  /** the identifier for the distribution of an abstaining classifier. */
  public final static String VALUE_ABSTENTION_DISTRIBUTION = "Abstention distribution";

  /**
   * Initializes the container.
   * <p/>
   * Only used for generating help information.
   */
  public WekaPredictionContainer() {
    this(null, Utils.missingValue(), new double[0]);
  }

  /**
   * Initializes the container.
   *
   * @param inst	the instance that was used for prediction
   * @param cls		the classification
   * @param dist	the class distribution
   */
  public WekaPredictionContainer(Instance inst, double cls, double[] dist) {
    this(inst, cls, dist, null);
  }

  /**
   * Initializes the container.
   *
   * @param inst	the instance that was used for prediction
   * @param cls		the classification
   * @param dist	the class distribution
   * @param rangeCheck	the range check, null if not available
   */
  public WekaPredictionContainer(Instance inst, double cls, double[] dist, String rangeCheck) {
    super();

    if (inst != null)
      store(VALUE_INSTANCE, (Instance) inst.copy());
    store(VALUE_CLASSIFICATION, cls);
    store(VALUE_DISTRIBUTION, dist.clone());
    if ((inst != null) && inst.classAttribute().isNominal())
      store(VALUE_CLASSIFICATION_LABEL, inst.classAttribute().value((int) cls));
    if (rangeCheck != null)
      store(VALUE_RANGECHECK, rangeCheck);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_INSTANCE);
    result.add(VALUE_CLASSIFICATION);
    result.add(VALUE_CLASSIFICATION_LABEL);
    result.add(VALUE_DISTRIBUTION);
    result.add(VALUE_RANGECHECK);
    result.add(VALUE_ABSTENTION_CLASSIFICATION);
    result.add(VALUE_ABSTENTION_CLASSIFICATION_LABEL);
    result.add(VALUE_ABSTENTION_DISTRIBUTION);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return   (hasValue(VALUE_INSTANCE) && hasValue(VALUE_CLASSIFICATION) && hasValue(VALUE_DISTRIBUTION) && !hasValue(VALUE_CLASSIFICATION_LABEL))
           | (hasValue(VALUE_INSTANCE) && hasValue(VALUE_CLASSIFICATION) && hasValue(VALUE_DISTRIBUTION) &&  hasValue(VALUE_CLASSIFICATION_LABEL));
  }
}
