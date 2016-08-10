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

import weka.core.*;
import weka.core.matrix.Matrix;
import weka.filters.SimpleBatchFilter;
import weka.filters.SupervisedFilter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/** 
<!-- globalinfo-start -->
* Runs Partial Least Square Regression over the given instances and computes the resulting beta matrix for prediction.<br>
* By default it replaces missing values and centers the data.<br>
* <br>
* Allows access to the internal matrices.<br>
* <br>
* For more information see:<br>
* <br>
* Tormod Naes, Tomas Isaksson, Tom Fearn, Tony Davies (2002). A User Friendly Guide to Multivariate Calibration and Classification. NIR Publications.<br>
* <br>
* StatSoft, Inc.. Partial Least Squares (PLS).<br>
* <br>
* Bent Jorgensen, Yuri Goegebeur. Module 7: Partial least squares regression I.<br>
* <br>
* S. de Jong (1993). SIMPLS: an alternative approach to partial least squares regression. Chemometrics and Intelligent Laboratory Systems. 18:251-263.
* <br><br>
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
* <br><br>
<!-- technical-bibtex-end -->
*
<!-- options-start -->
* Valid options are: <br><br>
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
* @version $Revision: 10824 $
*/
public class SIMPLSMatrixFilter
  extends PLSFilter {
  
  /** for serialization. */
  private static final long serialVersionUID = -5366730549674709661L;

  protected Matrix m_SIMPLS_MATRIX_LOCAL = null;

  public void initialiseWeights(){
    m_SIMPLS_MATRIX_LOCAL=null;
  }

  public void initialiseW(Instances ins) throws Exception{
    m_FirstBatchDone=false;
    super.processSIMPLS(ins);
    m_SIMPLS_MATRIX_LOCAL=m_SIMPLS_W.copy();
  }

  public void setMatrix(Matrix m){
    m_SIMPLS_MATRIX_LOCAL=m;
  }

  public Matrix getMatrix(){
    return( m_SIMPLS_MATRIX_LOCAL);
  }

  protected Instances processSIMPLS(Instances instances) throws Exception {

    if (m_SIMPLS_MATRIX_LOCAL == null){
      initialiseW(instances);
    }
    Instances result = new Instances(getOutputFormat());

    Matrix X = getX(instances);
    Matrix X_new = X.times(m_SIMPLS_MATRIX_LOCAL);

    Matrix y = getY(instances);

    result = toInstances(getOutputFormat(), X_new, y);
    return(result);
  }


  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
    "";
  }



  /**
   * runs the filter with the given arguments.
   *
   * @param args      the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new SIMPLSMatrixFilter(), args);
  }
}
