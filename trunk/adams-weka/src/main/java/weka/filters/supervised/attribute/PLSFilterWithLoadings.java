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
 * PLSFilterWithLoadings.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.filters.supervised.attribute;

import weka.core.PLSMatrixAccess;
import weka.core.RevisionUtils;
import weka.core.matrix.Matrix;

/** 
<!-- globalinfo-start -->
* Runs Partial Least Square Regression over the given instances and computes the resulting beta matrix for prediction.<br/>
* By default it replaces missing values and centers the data.<br/>
* <br/>
* Allows access to the internal matrices.<br/>
* <br/>
* For more information see:<br/>
* <br/>
* Tormod Naes, Tomas Isaksson, Tom Fearn, Tony Davies (2002). A User Friendly Guide to Multivariate Calibration and Classification. NIR Publications.<br/>
* <br/>
* StatSoft, Inc.. Partial Least Squares (PLS).<br/>
* <br/>
* Bent Jorgensen, Yuri Goegebeur. Module 7: Partial least squares regression I.<br/>
* <br/>
* S. de Jong (1993). SIMPLS: an alternative approach to partial least squares regression. Chemometrics and Intelligent Laboratory Systems. 18:251-263.
* <p/>
<!-- globalinfo-end -->
*
<!-- technical-bibtex-start -->
* BibTeX:
* <pre>
* &#64;book{Naes2002,
*    author = {Tormod Naes and Tomas Isaksson and Tom Fearn and Tony Davies},
*    publisher = {NIR Publications},
*    title = {A User Friendly Guide to Multivariate Calibration and Classification},
*    year = {2002},
*    ISBN = {0-9528666-2-5}
* }
* 
* &#64;misc{missing_id,
*    author = {StatSoft, Inc.},
*    booktitle = {Electronic Textbook StatSoft},
*    title = {Partial Least Squares (PLS)},
*    HTTP = {http://www.statsoft.com/textbook/stpls.html}
* }
* 
* &#64;misc{missing_id,
*    author = {Bent Jorgensen and Yuri Goegebeur},
*    booktitle = {ST02: Multivariate Data Analysis and Chemometrics},
*    title = {Module 7: Partial least squares regression I},
*    HTTP = {http://statmaster.sdu.dk/courses/ST02/module07/}
* }
* 
* &#64;article{Jong1993,
*    author = {S. de Jong},
*    journal = {Chemometrics and Intelligent Laboratory Systems},
*    pages = {251-263},
*    title = {SIMPLS: an alternative approach to partial least squares regression},
*    volume = {18},
*    year = {1993}
* }
* </pre>
* <p/>
<!-- technical-bibtex-end -->
*
<!-- options-start -->
* Valid options are: <p/>
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
* @author FracPete (fracpete at waikato dot ac dot nz)
* @version $Revision$
*/
public class PLSFilterWithLoadings
  extends PLSFilter
  implements PLSMatrixAccess {
  
  /** for serialization. */
  private static final long serialVersionUID = -5366730549674709661L;
  
  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
        "Runs Partial Least Square Regression over the given instances "
      + "and computes the resulting beta matrix for prediction.\n"
      + "By default it replaces missing values and centers the data.\n\n"
      + "Allows access to the internal matrices.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns the reg vector.
   * 
   * @return		the vector
   */
  public Matrix getPLS1RegVector() { 
    return m_PLS1_RegVector; 
  }

  /**
   * Returns the PLS1 P matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1P() { 
    return m_PLS1_P; 
  }
  
  /**
   * Returns the PLS1 W matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1W() { 
    return m_PLS1_W; 
  }
  
  /**
   * Returns the PLS1 b "hat" matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1bHat() { 
    return m_PLS1_b_hat;
  }
  
  /**
   * Returns the SIMPLS W matrix.
   * 
   * @return		the matrix
   */
  public Matrix getSimplsW() {
    return m_SIMPLS_W; 
  }
  
  /**
   * Returns the SIMPLS B matrix.
   * 
   * @return		the matrix
   */
  public Matrix getSimplsB() {
    return m_SIMPLS_B;
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
   * runs the filter with the given arguments.
   *
   * @param args      the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new PLSFilterWithLoadings(), args);
  }
}
