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

import adams.core.Utils;
import adams.core.discovery.genetic.GeneticHelper;
import weka.core.Instances;
import weka.core.WekaOptionUtils;
import weka.core.matrix.Matrix;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
public class SIMPLSMatrixFilterFromGeneticString
  extends PLSFilter {
  
  /** for serialization. */
  private static final long serialVersionUID = -5366730549674709661L;

  protected Matrix m_SIMPLS_MATRIX_LOCAL = null;

  public static final String BITSTRING="bitstring";
  protected String m_BitString="1";
  public static final String MIN="min";
  protected double m_Min;
  public static final String MAX="max";
  protected double m_Max;
  public static final String SPLITS="splits";
  protected int m_Splits;
  public static final String COLUMNS="columns";
  protected int m_Columns;
  public static final String ROWS="rows";
  protected int m_Rows;

  public void setBitstring(String bits){
    m_BitString=bits;
    reset();
  }
  public String getBitstring(){
    return(m_BitString);
  }
  public String bitstringTipText(){
    return("Bitstring from genetic algorithm");
  }

  public void setMin(double min){
    m_Min=min;
    reset();
  }
  public double getMin(){
    return(m_Min);
  }
  public String minTipText(){
    return("Min double from genetic algorithm");
  }

  public void setMax(double max){
    m_Max=max;
    reset();
  }
  public double getMax(){
    return(m_Max);
  }
  public String maxTipText(){
    return("Max double from genetic algorithm");
  }

  public void setSplits(int splits){
    m_Splits=splits;
    reset();
  }
  public int getSplits(){
    return(m_Splits);
  }
  public String splitsTipText(){
    return("Splits from genetic algorithm");
  }

  public void setRows(int rows){
    m_Rows=rows;
    reset();
  }
  public int getRows(){
    return(m_Rows);
  }
  public String rowsTipText(){
    return("Rows from genetic algorithm");
  }

  public void setColumns(int columns){
    m_Columns=columns;
    reset();
  }
  public int getColumns(){
    return(m_Columns);
  }
  public String columnsTipText(){
    return("Columns from genetic algorithm");
  }



  public void initialiseWeights(){
    m_SIMPLS_MATRIX_LOCAL=null;
  }

  public void initialiseW(Instances ins) throws Exception{
    m_SIMPLS_MATRIX_LOCAL= GeneticHelper.bitsToMatrix(m_BitString,m_Min,m_Max,calcNumBits(),m_Splits,m_Rows,m_Columns);
  }

  /**
   * Calculates the number of bits to use.
   *
   * @return		the number of bits
   */
  protected int calcNumBits(){
    return((int)(Math.floor(Utils.log2(m_Splits))+1));
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
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, bitstringTipText(), "1", BITSTRING);
    WekaOptionUtils.addOption(result, minTipText(), "0.0", MIN);
    WekaOptionUtils.addOption(result, maxTipText(), "0.0", MAX);
    WekaOptionUtils.addOption(result, splitsTipText(), "1", SPLITS);
    WekaOptionUtils.addOption(result, rowsTipText(), "1", ROWS);
    WekaOptionUtils.addOption(result, columnsTipText(), "1", COLUMNS);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }
  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setBitstring(WekaOptionUtils.parse(options, BITSTRING, "1"));
    setMin(WekaOptionUtils.parse(options, MIN, 0.0));
    setMax(WekaOptionUtils.parse(options, MAX, 0.0));
    setSplits(WekaOptionUtils.parse(options, SPLITS, 1));
    setRows(WekaOptionUtils.parse(options, ROWS, 1));
    setColumns(WekaOptionUtils.parse(options, COLUMNS, 1));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, BITSTRING, getBitstring());
    WekaOptionUtils.add(result, MIN, getMin());
    WekaOptionUtils.add(result, MAX, getMax());
    WekaOptionUtils.add(result, SPLITS, getSplits());
    WekaOptionUtils.add(result, ROWS, getRows());
    WekaOptionUtils.add(result, COLUMNS, getColumns());

    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
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
   * Resets the cleaner.
   */
  @Override
  protected void reset() {
    super.reset();

    m_SIMPLS_MATRIX_LOCAL = null;
  }


  /**
   * runs the filter with the given arguments.
   *
   * @param args      the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new SIMPLSMatrixFilterFromGeneticString(), args);
  }
}
