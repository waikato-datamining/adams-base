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
 *    SAXDistance.java
 *    Copyright (C) 1999-2007 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core;

import java.util.Enumeration;
import java.util.Vector;

import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.neighboursearch.PerformanceStats;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

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
 * @version $Revision$
 */
public class SAXDistance
  extends NormalizableDistance
  implements Cloneable, TechnicalInformationHandler {

  private static final long serialVersionUID = 2677863364387767459L;
  /** suid.*/

  protected Normalize m_norm=null;
  
  /** number of gaussian bins. (labels) */
  protected int m_bins = 5;

  /** pre-sax number of attributes. */
  protected int m_n = 80;
  
  protected double[][] m_distMatrix=null;
  
  /**
   * Constructs an Euclidean Distance object, Instances must be still set.
   */
  public SAXDistance() {
    super();
  }

  /**
   * Constructs an Euclidean Distance object and automatically initializes the
   * ranges.
   * 
   * @param data 	the instances the distance function should work on
   */
  public SAXDistance(Instances data) {
    super(data);
  }
  /**
   * initializes the ranges and the attributes being used.
   */
  protected void initialize() {
    super.initialize();

   
    double[] get=adams.data.utils.SAXUtils.calcBreakPoints(m_bins);
    m_distMatrix=adams.data.utils.SAXUtils.calcDistMatrix(get);
  }
  /**
   * Returns a string describing this object.
   * 
   * @return 		a description of the evaluator suitable for
   * 			displaying in the explorer/experimenter gui
   */
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
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
	"\tNumber of gaussian bins (>1).\n"
	+ "\t(default: 5)",
	"bins", 5, "-bins <int>"));

    result.addElement(new Option(
	"\tNumber of Original attributes before SAX (>1).\n"
	+ "\t(default: 80)",
	"n", 80, "-n <int>"));
    
    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   * Also resets the state of the filter (this reset doesn't affect the
   * options).
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   * @see    		#reset()
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

   // reset();

    tmpStr = Utils.getOption("bins", options);
    if (tmpStr.length() > 0)
      setBins(Integer.parseInt(tmpStr));
    else
      setBins(5);
    
    tmpStr = Utils.getOption("n", options);
    if (tmpStr.length() > 0)
      setN(Integer.parseInt(tmpStr));
    else
      setN(80);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-bins");
    result.add("" + getBins());
    
    result.add("-n");
    result.add("" + getN());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the nth point setting.
   *
   * @param value 	the nth point
   */
  public void setBins(int value) {
    if (value > 0) {
      m_bins = value;
      double[] get=adams.data.utils.SAXUtils.calcBreakPoints(m_bins);
      m_distMatrix=adams.data.utils.SAXUtils.calcDistMatrix(get);
      //reset();
    }
    else {
      System.err.println(
	  "'n' must be larger than 0 (provided: " + value + ")!");
    }
  }
  
  /**
   * Sets the nth point setting.
   *
   * @param value 	the nth point
   */
  public void setN(int value) {
    if (value > 0) {
      m_n = value;
      //reset();
    }
    else {
      System.err.println(
	  "'n' must be larger than 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the nth point setting.
   *
   * @return 		the order
   */
  public int getBins() {
    return m_bins;
  }
  
  /**
   * Returns the nth point setting.
   *
   * @return 		the order
   */
  public int getN() {
    return m_n;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binsPointTipText() {
    return "number of gaussian bins (>0).";
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nPointTipText() {
    return "number of pre-sax attribues (>0).";
  }


  /**
   * Returns an instance of a TechnicalInformation object, containing 
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   * 
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;
    
    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "Wikipedia");
    result.setValue(Field.TITLE, "Euclidean distance");
    result.setValue(Field.URL, "http://en.wikipedia.org/wiki/Euclidean_distance");

    return result;
  }
  
  /**
   * Calculates the distance between two instances.
   * 
   * @param first 	the first instance
   * @param second 	the second instance
   * @return 		the distance between the two given instances
   */
  public double distance(Instance first, Instance second) {
   
    return Math.sqrt((double)m_n/(double)first.numAttributes()-1)*Math.sqrt(distance(transform(first), transform(second), Double.POSITIVE_INFINITY));
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
  public double distance(Instance first, Instance second, PerformanceStats stats) { //debug method pls remove after use
    return Math.sqrt((double)m_n/(double)first.numAttributes()-1)*Math.sqrt(distance(transform(first), transform(second), Double.POSITIVE_INFINITY, stats));
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
  protected double updateDistance(double currDist, double diff) {
    double	result;
    
    result  = currDist;
    result += diff * diff;
    
    return result;
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
  protected double difference(int index, double val1, double val2) {
    //if (m_DontNormalize){ 
    //System.err.println("diff is:"+(val1-val2)*this.m_Coefficients[index]+", would be:"+(val1-val2)+", factor:"+m_Coefficients[index]);
    //return((val1-val2)*this.m_Coefficients[index]);
    	
    if (val1 < 0 || val2 < 0 || val1 >=m_bins || val2 >= m_bins){
      System.err.println("val out of range! "+val1+","+val2);
      return(0);
    }
    return(this.m_distMatrix[(int)val1][(int)val2]);
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
  public void postProcessDistances(double distances[]) {
    for(int i = 0; i < distances.length; i++) {
      distances[i] = Math.sqrt((double)m_n/((double)m_Data.numAttributes()-1.0))*Math.sqrt(distances[i]);
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

  protected Instance transform(Instance i){
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
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }
}
