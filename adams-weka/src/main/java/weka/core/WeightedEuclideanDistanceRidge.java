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
 *    WeightedEuclideanDistance.java
 *    Copyright (C) 1999-2007 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core;

import weka.classifiers.functions.LinearRegressionJ;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.neighboursearch.PerformanceStats;
import weka.filters.unsupervised.attribute.Normalize;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Implementing Euclidean distance (or similarity) function.<br>
 * <br>
 * One object defines not one distance but the data model in which the distances between objects of that data model can be computed.<br>
 * <br>
 * Attention: For efficiency reasons the use of consistency checks (like are the data models of the two instances exactly the same), is low.<br>
 * <br>
 * For more information, see:<br>
 * <br>
 * Wikipedia. Euclidean distance. URL http://en.wikipedia.org/wiki/Euclidean_distance.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{missing_id,
 *    author = {Wikipedia},
 *    title = {Euclidean distance},
 *    URL = {http://en.wikipedia.org/wiki/Euclidean_distance}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -D
 *  Turns off the normalization of attribute 
 *  values in distance calculation.</pre>
 * 
 * <pre> -R &lt;col1,col2-col4,...&gt;
 *  Specifies list of columns to used in the calculation of the 
 *  distance. 'first' and 'last' are valid indices.
 *  (default: first-last)</pre>
 * 
 * <pre> -V
 *  Invert matching sense of column indices.</pre>
 * 
 <!-- options-end --> 
 *
 * @author dale (dale at waikato dot ac dot nz)
 * @version $Revision: 12763 $
 */
public class WeightedEuclideanDistanceRidge
  extends NormalizableDistance
  implements Cloneable, TechnicalInformationHandler {


  /** suid.*/
  private static final long serialVersionUID = -8518647249857218705L;
  /** Array for storing coefficients of linear regression. */
  protected double[] m_Coefficients=null;
  protected LinearRegressionJ m_LR=new LinearRegressionJ();
  protected Normalize m_norm=null;
  protected double m_Ridge=0.00000001;
  /**
   * Constructs an Euclidean Distance object, Instances must be still set.
   */
  public WeightedEuclideanDistanceRidge() {
    super();
  }

  /**
   * Constructs an Euclidean Distance object and automatically initializes the
   * ranges.
   *
   * @param data 	the instances the distance function should work on
   */
  public WeightedEuclideanDistanceRidge(Instances data) {
    super(data);
  }
  /**
   * initializes the ranges and the attributes being used.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_LR.setAttributeSelectionMethod(new SelectedTag(LinearRegressionJ.SELECTION_NONE,
	LinearRegressionJ.TAGS_SELECTION));
    m_LR.setEliminateColinearAttributes(false);
    m_LR.setRidge(getRidge());
    //m_norm=new Normalize();
    
    try {
      //m_norm.setInputFormat(m_Data);
      //Instances in=Filter.useFilter(m_Data, m_norm);   
      m_LR.buildClassifier(m_Data);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //System.out.println("**\n"+m_Data.toString());
    //System.out.println();
    this.m_Coefficients=m_LR.coefficients();
    for (int i=0;i<m_Coefficients.length;i++) {
      //System.out.print(m_Coefficients[i]+",");
    }
    //System.out.println();
    //System.out.println(m_LR.toString());
    //System.out.println("*********************************************");
  }
  /**
   * Returns a string describing this object.
   * 
   * @return 		a description of the evaluator suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
        "Implementing Euclidean distance (or similarity) function.\n\n"
      + "One object defines not one distance but the data model in which "
      + "the distances between objects of that data model can be computed.\n\n"
      + "Attention: For efficiency reasons the use of consistency checks "
      + "(like are the data models of the two instances exactly the same), "
      + "is low.\n\n"
      + "For more information, see:\n\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing 
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   * 
   * @return 		the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;
    
    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Wikipedia");
    result.setValue(Field.TITLE, "Euclidean distance");
    result.setValue(Field.URL, "http://en.wikipedia.org/wiki/Euclidean_distance");

    return result;
  }


  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> newVector = new Vector<Option>();

    newVector.addElement(new Option(
      "\tSet ridge parameter (default 1.0e-8).\n", "R", 1, "-R <double>"));


    newVector.addAll(Collections.list(super.listOptions()));

    return newVector.elements();
  }
  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String> result = new Vector<String>();


    result.add("-R");
    result.add("" + getRidge());

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses a given list of options.
   * <p/>
   *
   <!-- options-start -->
   * Valid options are:
   * <p/>
   *
   * <pre>
   * -S &lt;number of selection method&gt;
   *  Set the attribute selection method to use. 1 = None, 2 = Greedy.
   *  (default 0 = M5' method)
   * </pre>
   *
   * <pre>
   * -C
   *  Do not try to eliminate colinear attributes.
   * </pre>
   *
   * <pre>
   * -R &lt;double&gt;
   *  Set ridge parameter (default 1.0e-8).
   * </pre>
   *
   * <pre>
   * -minimal
   *  Conserve memory, don't keep dataset header and means/stdevs.
   *  Model cannot be printed out if this option is enabled. (default: keep data)
   * </pre>
   *
   * <pre>
   * -additional-stats
   *  Output additional statistics.
   * </pre>
   *
   * <pre>
   * -output-debug-info
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console
   * </pre>
   *
   * <pre>
   * -do-not-check-capabilities
   *  If set, classifier capabilities are not checked before classifier is built
   *  (use with caution).
   * </pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {


    String ridgeString = Utils.getOption('R', options);
    if (ridgeString.length() != 0) {
      setRidge(new Double(ridgeString).doubleValue());
    } else {
      setRidge(1.0e-8);
    }

    super.setOptions(options);
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String ridgeTipText() {
    return "The value of the Ridge parameter.";
  }

  /**
   * Get the value of Ridge.
   *
   * @return Value of Ridge.
   */
  public double getRidge() {

    return m_Ridge;
  }

  /**
   * Set the value of Ridge.
   *
   * @param newRidge Value to assign to Ridge.
   */
  public void setRidge(double newRidge) {

    m_Ridge = newRidge;
  }


  /**
   * Calculates the distance between two instances.
   * 
   * @param first 	the first instance
   * @param second 	the second instance
   * @return 		the distance between the two given instances
   */
  @Override
  public double distance(Instance first, Instance second) {
   
    return Math.sqrt(distance(transform(first), transform(second), Double.POSITIVE_INFINITY));
  }
  
  /**
   * Calculates the distance (or similarity) between two instances. Need to
   * pass this returned distance later on to postprocess method to set it on
   * correct scale. <br>
   * P.S.: Please don't mix the use of this function with
   * distance(Instance first, Instance second), as that already does post
   * processing. Please consider passing Double.POSITIVE_INFINITY as the cutOffValue to
   * this function and then later on do the post processing on all the
   * distances.
   *
   * @param first 	the first instance
   * @param second 	the second instance
   * @param stats 	the structure for storing performance statistics.
   * @return 		the distance between the two given instances or 
   * 			Double.POSITIVE_INFINITY.
   */
  @Override
  public double distance(Instance first, Instance second, PerformanceStats stats) { //debug method pls remove after use
    return Math.sqrt(distance(transform(first), transform(second), Double.POSITIVE_INFINITY, stats));
  }
  
  /**
   * Updates the current distance calculated so far with the new difference
   * between two attributes. The difference between the attributes was 
   * calculated with the difference(int,double,double) method.
   * 
   * @param currDist	the current distance calculated so far
   * @param diff	the difference between two new attributes
   * @return		the update distance
   * @see		#difference(int, double, double)
   */
  @Override
  protected double updateDistance(double currDist, double diff) {
    double	result;
    
    result  = currDist;
    result += diff * diff;
    
    return result;
  }
  
  @Override
  public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
    return(super.distance(transform(first),transform(second),cutOffValue,stats));
  }
  /**
   * Computes the difference between two given attribute
   * values.
   * 
   * @param index	the attribute index
   * @param val1	the first value
   * @param val2	the second value
   * @return		the difference
   */
  @Override
  protected double difference(int index, double val1, double val2) {
    //if (m_DontNormalize) { 
    //System.err.println("diff is:"+(val1-val2)*this.m_Coefficients[index]+", would be:"+(val1-val2)+", factor:"+m_Coefficients[index]);
    //return((val1-val2)*this.m_Coefficients[index]);
    if (index == m_Data.classIndex()) {
      return(0);
    }
    return((val1-val2)*m_Coefficients[index]);
    //}
    //return((norm(val1, index) - norm(val2, index))*this.m_Coefficients[index]);
  }
  /**
   * Does post processing of the distances (if necessary) returned by
   * distance(distance(Instance first, Instance second, double cutOffValue). It
   * is necessary to do so to get the correct distances if
   * distance(distance(Instance first, Instance second, double cutOffValue) is
   * used. This is because that function actually returns the squared distance
   * to avoid inaccuracies arising from floating point comparison.
   * 
   * @param distances	the distances to post-process
   */
  @Override
  public void postProcessDistances(double distances[]) {
    for(int i = 0; i < distances.length; i++) {
      distances[i] = Math.sqrt(distances[i]);
    }
  }
  
  /**
   * Returns the squared difference of two values of an attribute.
   * 
   * @param index	the attribute index
   * @param val1	the first value
   * @param val2	the second value
   * @return		the squared difference
   */
  public double sqDifference(int index, double val1, double val2) {
    double val = difference(index, val1, val2);
    return val*val;
  }
  
  /**
   * Returns value in the middle of the two parameter values.
   * 
   * @param ranges 	the ranges to this dimension
   * @return 		the middle value
   */
  public double getMiddle(double[] ranges) {

    double middle = ranges[R_MIN] + ranges[R_WIDTH] * 0.5;
    return middle;
  }

  protected Instance transform(Instance i) {
 /*   try{
      m_norm.input(i);
      m_norm.batchFinished();
    }catch(Exception e) {
      e.printStackTrace();
    }
    return(m_norm.output());*/
    return(i);
  }
  
  /**
   * Returns true if the value of the given dimension is smaller or equal the
   * value to be compared with.
   * 
   * @param instance 	the instance where the value should be taken of
   * @param dim 	the dimension of the value
   * @param value 	the value to compare with
   * @return 		true if value of instance is smaller or equal value
   */
  public boolean valueIsSmallerEqual(Instance instance, int dim,
      				     double value) {  //This stays
    return instance.value(dim) <= value;
  }
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 12763 $");
  }
}
