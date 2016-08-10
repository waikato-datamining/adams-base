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
 * PLSFilterNumComponents.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.genetic.AbstractGeneticAlgorithm;
import adams.genetic.Hermione;
import com.sun.org.apache.bcel.internal.generic.Select;
import weka.core.SelectedTag;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import weka.filters.supervised.attribute.PLSFilter;
import weka.filters.supervised.attribute.PLSFilterWithLoadings;
import weka.filters.supervised.attribute.SIMPLSMatrixFilter;

/**
 * SIMPLS pls internal weights handler.
 *
 * @author Dale (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SIMPLSWeightsMatrix
  extends AbstractGeneticDoubleMatrixDiscoveryHandler {


  private static final long serialVersionUID = 6077250097532777489L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Set the weights matrix .";
  }

  @Override
  protected int getDefaultSplits() {
    return 255;
  }

  /**
   * This is the number of attributes
   * @return
   */
  @Override
  protected int getDefaultRows() {
    return 1701;
  }

  /**
   * The number of PLS components
   * @return
   */
  @Override
  protected int getDefaultColumns() {
    return 20;
  }

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultMinimum() {
    return -1;
  }

  /**
   * Returns the default maximum.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultMaximum() {
    return 1;
  }


  /**
   * Returns the matrix  value from the property container.
   *
   * @param cont	the container
   * @return		the value
   */
  protected Matrix getValue(PropertyContainer cont) {
    return ((SIMPLSMatrixFilter) cont.getObject()).getMatrix();
  }

  /**
   * Sets the integer value in the property container.
   *
   * @param cont	the container
   * @param value	the value to set
   */
  protected void setValue(PropertyContainer cont, Matrix value) {
    ((SIMPLSMatrixFilter) cont.getObject()).setMatrix(value);
  }

  /**
   * Returns whether the handler requires an initialization.
   * <br>
   * Default implementation returns false.
   *
   * @return		true if necessary
   */
  public boolean requiresInitialization() {
    return true;
  }

  /**
   * Gets called for performing the initialization.
   * <br>
   * Apply simpls PLS filter and extract
   *
   * @param owner	the owning algorithm
   * @param cont	the property container to update
   */
  public void performInitialization(AbstractGeneticAlgorithm owner, PropertyContainer cont) {
    Matrix value;
    PLSFilterWithLoadings pls=new PLSFilterWithLoadings();
    pls.setNumComponents(getColumns());
    pls.setAlgorithm(new SelectedTag(PLSFilter.PREPROCESSING_CENTER,PLSFilter.TAGS_PREPROCESSING));
    pls.setAlgorithm(new SelectedTag(PLSFilter.ALGORITHM_SIMPLS,PLSFilter.TAGS_ALGORITHM));
    try {
      pls.setInputFormat(((Hermione)owner).getInstances());
      Filter.useFilter(((Hermione)owner).getInstances(),pls);
    } catch (Exception e) {
      e.printStackTrace();
    }
    value=pls.getSimplsW();
    ((SIMPLSMatrixFilter) cont.getObject()).setMatrix(value);
  }

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  protected boolean handles(Object obj) {
    return (obj instanceof PLSFilter);
  }
}
