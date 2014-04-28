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
 * PLSClassifierWeightedWithLoadings.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.functions;

import weka.core.PLSMatrixAccess;
import weka.core.RevisionUtils;
import weka.core.matrix.Matrix;
import weka.filters.supervised.attribute.PLSFilter;
import weka.filters.supervised.attribute.PLSFilterWithLoadings;


/**
<!-- globalinfo-start -->
* A wrapper classifier for the PLSFilter, utilizing the PLSFilter's ability to perform predictions.<br/>
* <br/>
* Allows access to the PLS matrices in case the filter is a PLSFilterWithLoadings instance.
* <p/>
<!-- globalinfo-end -->
*
<!-- options-start -->
* Valid options are: <p/>
* 
* <pre> -filter &lt;filter specification&gt;
*  The PLS filter to use. Full classname of filter to include,  followed by scheme options.
*  (default: weka.filters.supervised.attribute.PLSFilter)</pre>
* 
* <pre> -D
*  If set, classifier is run in debug mode and
*  may output additional info to the console</pre>
* 
* <pre> 
* Options specific to filter weka.filters.supervised.attribute.PLSFilter ('-filter'):
* </pre>
* 
* <pre> -D
*  Turns on output of debugging information.</pre>
* 
* <pre> -C &lt;num&gt;
*  The number of components to compute.
*  (default: 20)</pre>
* 
* <pre> -U
*  Updates the class attribute as well.
*  (default: off)</pre>
* 
* <pre> -M
*  Turns replacing of missing values on.
*  (default: off)</pre>
* 
* <pre> -A &lt;SIMPLS|PLS1&gt;
*  The algorithm to use.
*  (default: PLS1)</pre>
* 
* <pre> -P &lt;none|center|standardize&gt;
*  The type of preprocessing that is applied to the data.
*  (default: center)</pre>
* 
<!-- options-end -->
*
* @author  fracpete (fracpete at waikato dot ac dot nz)
* @version $Revision$
*/
public class PLSClassifierWeightedWithLoadings
  extends PLSClassifierWeighted 
  implements PLSMatrixAccess {

  /** for serialization. */
  private static final long serialVersionUID = -1425982657996193266L;

  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
        "A wrapper classifier for the PLSFilter, utilizing the PLSFilter's "
      + "ability to perform predictions.\n\n"
      + "Allows access to the PLS matrices in case the filter is a PLSFilterWithLoadings instance.";
  }

  /**
   * Returns the default PLS filter.
   * 
   * @return		the default filter
   */
  @Override
  public PLSFilter getDefaultFilter() {
    return new PLSFilterWithLoadings();
  }

  /**
   * Returns the reg vector.
   * 
   * @return		the vector
   */
  public Matrix getPLS1RegVector() {
    if (m_ActualFilter instanceof PLSFilterWithLoadings)
      return ((PLSFilterWithLoadings) m_ActualFilter).getPLS1RegVector(); 
    else
      return null;
  }

  /**
   * Returns the PLS1 P matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1P() { 
    if (m_ActualFilter instanceof PLSFilterWithLoadings)
      return ((PLSFilterWithLoadings) m_ActualFilter).getPLS1P(); 
    else
      return null;
  }
  
  /**
   * Returns the PLS1 W matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1W() { 
    if (m_ActualFilter instanceof PLSFilterWithLoadings)
      return ((PLSFilterWithLoadings) m_ActualFilter).getPLS1W(); 
    else
      return null;
  }
  
  /**
   * Returns the PLS1 b "hat" matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1bHat() { 
    if (m_ActualFilter instanceof PLSFilterWithLoadings)
      return ((PLSFilterWithLoadings) m_ActualFilter).getPLS1bHat(); 
    else
      return null;
  }
  
  /**
   * Returns the SIMPLS W matrix.
   * 
   * @return		the matrix
   */
  public Matrix getSimplsW() {
    if (m_ActualFilter instanceof PLSFilterWithLoadings)
      return ((PLSFilterWithLoadings) m_ActualFilter).getSimplsW(); 
    else
      return null;
  }
  
  /**
   * Returns the SIMPLS B matrix.
   * 
   * @return		the matrix
   */
  public Matrix getSimplsB() {
    if (m_ActualFilter instanceof PLSFilterWithLoadings)
      return ((PLSFilterWithLoadings) m_ActualFilter).getSimplsB(); 
    else
      return null;
  }
  
  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this classifier from commandline.
   *
   * @param args 	the options
   */
  public static void main(String[] args) {
    runClassifier(new PLSClassifierWeightedWithLoadings(), args);
  }
}
