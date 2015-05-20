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
 *    NewNNSearch.java
 *    Copyright (C) 1999-2014 University of Waikato
 */

package weka.core.neighboursearch;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import adams.data.SortedList;

/**
 <!-- globalinfo-start -->
 * Class implementing the brute force search algorithm for nearest neighbour search.
 * <br><br>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -S
 *  Skip identical instances (distances equal to zero).
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author Dale
 * @version $Revision$
 */
public class NewNNSearch
  extends NearestNeighbourSearch {

  /** for serialization. */
  private static final long serialVersionUID = 1915484723703917241L;

  /** Array holding the distances of the nearest neighbours. It is filled up
   *  both by nearestNeighbour() and kNearestNeighbours(). 
   */
  protected double[] m_Distances;
    
  /** Whether to skip instances from the neighbours that are identical to the query instance. */
  protected boolean m_SkipIdentical = false;

  protected SortedList<InstanceNode> m_neighbours=new SortedList<InstanceNode>();

  public class InstanceNode implements Comparable, Serializable{
    int instance_index;
    Double distance;

    public InstanceNode(int ins,Double dist) {
      instance_index=ins;
      distance=dist;
    }

    public int compareTo(Object o) {
      return Double.compare(distance,((InstanceNode)o).distance);
    }
  }


  /**
   * Constructor. Needs setInstances(Instances) 
   * to be called before the class is usable.
   */
  public NewNNSearch() {
    super();
  }
  
  /**
   * Constructor that uses the supplied set of 
   * instances.
   * 
   * @param insts	the instances to use
   */
  public NewNNSearch(Instances insts) {
    super(insts);
    m_DistanceFunction.setInstances(insts);
  }
  
  /**
   * Returns a string describing this nearest neighbour search algorithm.
   * 
   * @return 		a description of the algorithm for displaying in the 
   * 			explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
        "Class implementing the brute force search algorithm for nearest "
      + "neighbour search.";  
  }
  
  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    
    result.add(new Option(
	"\tSkip identical instances (distances equal to zero).\n",
	"S", 1,"-S"));
    
    return result.elements();
  }
  
  /**
   * Parses a given list of options. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   * 
   * <pre> -S
   *  Skip identical instances (distances equal to zero).
   * </pre>
   * 
   <!-- options-end -->
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    super.setOptions(options);

    setSkipIdentical(Utils.getFlag('S', options));
  }

  /**
   * Gets the current settings.
   *
   * @return 		an array of strings suitable for passing to setOptions()
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;
    String[]		options;
    int			i;
    
    result = new Vector<String>();
    
    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);
    
    if (getSkipIdentical())
      result.add("-S");

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String skipIdenticalTipText() {
    return "Whether to skip identical instances (with distance 0 to the target)";
  }
  
  /**
   * Sets the property to skip identical instances (with distance zero from 
   * the target) from the set of neighbours returned.
   * 
   * @param skip 	if true, identical intances are skipped
   */
  public void setSkipIdentical(boolean skip) {
    m_SkipIdentical = skip;
  }
  
  /**
   * Gets whether if identical instances are skipped from the neighbourhood.
   * 
   * @return 		true if identical instances are skipped
   */
  public boolean getSkipIdentical() {
    return m_SkipIdentical;
  }

  
  /** 
   * Returns the nearest instance in the current neighbourhood to the supplied
   * instance.
   *  
   * @param target 	The instance to find the nearest neighbour for.
   * @return		the nearest instance
   * @throws Exception 	if the nearest neighbour could not be found.
   */
  @Override
  public Instance nearestNeighbour(Instance target) throws Exception {
    return (kNearestNeighbours(target, 1)).instance(0);
  }
  
  /**
   * Returns k nearest instances in the current neighbourhood to the supplied
   * instance.
   *  
   * @param target 	The instance to find the k nearest neighbours for.
   * @param kNN		The number of nearest neighbours to find.
   * @return		the k nearest neighbors
   * @throws Exception  if the neighbours could not be found.
   */
  @Override
  public Instances kNearestNeighbours(Instance target, int kNN) throws Exception {
  
    if(m_Stats!=null)
      m_Stats.searchStart();
    m_neighbours.clear();
    double distance; 
    double last_distance=Double.POSITIVE_INFINITY;
    for(int i=0; i<m_Instances.numInstances(); i++) {
      if(target == m_Instances.instance(i)) {//for hold-one-out cross-validation
        continue;
      }
      if(m_Stats!=null) {
        m_Stats.incrPointCount();
      }
      distance = m_DistanceFunction.distance(target, m_Instances.instance(i), last_distance, m_Stats);
      if(distance == 0.0 && m_SkipIdentical) {
	continue;
      }
      if (distance < last_distance) {
	this.m_neighbours.add(new InstanceNode(i,distance));
	if (m_neighbours.size() > kNN) {
	  m_neighbours.remove(m_neighbours.size()-1);
	  last_distance=m_neighbours.get(m_neighbours.size() - 1).distance;
	}
      }      
    }
    Instances neighbours = new Instances(m_Instances,m_neighbours.size() );   
    int index=0;
    m_Distances = new double[m_neighbours.size()];
    
    Iterator<InstanceNode> iter = m_neighbours.iterator();
    while(iter.hasNext()) {
      InstanceNode in=iter.next();
      m_Distances[index++]=in.distance;
      //System.err.print(in.distance+" ");
      neighbours.add(m_Instances.instance(in.instance_index));
    }
    
    m_DistanceFunction.postProcessDistances(m_Distances);
    if(m_Stats!=null)
      m_Stats.searchFinish();
    
    return neighbours;    
  }
  
  /** 
   * Returns the distances of the k nearest neighbours. The kNearestNeighbours
   * or nearestNeighbour must always be called before calling this function. If
   * this function is called before calling either the kNearestNeighbours or 
   * the nearestNeighbour, then it throws an exception. If, however, if either
   * of the nearestNeighbour functions are called at any point in the 
   * past then no exception is thrown and the distances of the training set from
   * the last supplied target instance (to either one of the nearestNeighbour 
   * functions) is/are returned.
   *
   * @return 		array containing the distances of the 
   * 			nearestNeighbours. The length and ordering of the 
   * 			array is the same as that of the instances returned 
   * 			by nearestNeighbour functions.
   * @throws Exception 	if called before calling kNearestNeighbours
   *            	or nearestNeighbours.
   */
  @Override
  public double[] getDistances() throws Exception {
    if(m_Distances==null)
      throw new Exception("No distances available. Please call either "+
                          "kNearestNeighbours or nearestNeighbours first.");
    return m_Distances;    
  }

  /** 
   * Sets the instances comprising the current neighbourhood.
   * 
   * @param insts 	The set of instances on which the nearest neighbour 
   * 			search is carried out. Usually this set is the 
   * 			training set. 
   * @throws Exception	if setting of instances fails
   */
  @Override
  public void setInstances(Instances insts) throws Exception {
    m_Instances = insts;
    m_DistanceFunction.setInstances(insts);
  }
  
  /** 
   * Updates the LinearNNSearch to cater for the new added instance. This 
   * implementation only updates the ranges of the DistanceFunction class, 
   * since our set of instances is passed by reference and should already have 
   * the newly added instance.
   * 
   * @param ins 	The instance to add. Usually this is the instance that 
   * 			is added to our neighbourhood i.e. the training 
   * 			instances.
   * @throws Exception	if the given instances are null
   */
  @Override
  public void update(Instance ins) throws Exception {
    if(m_Instances==null)
      throw new Exception("No instances supplied yet. Cannot update without"+
                          "supplying a set of instances first.");
    m_DistanceFunction.update(ins);
  }
  
  /** 
   * Adds the given instance info. This implementation updates the range
   * datastructures of the DistanceFunction class.
   * 
   * @param ins 	The instance to add the information of. Usually this is
   * 			the test instance supplied to update the range of 
   * 			attributes in the  distance function.
   */
  @Override
  public void addInstanceInfo(Instance ins) {
    if(m_Instances!=null)
      try{ update(ins); }
      catch(Exception ex) { ex.printStackTrace(); }
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
