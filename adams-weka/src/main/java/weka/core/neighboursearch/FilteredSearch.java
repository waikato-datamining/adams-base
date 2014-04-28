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
 *    FilteredSearch.java
 *    Copyright (C) 1999-2007 University of Waikato
 */

package weka.core.neighboursearch;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;

/**
 <!-- globalinfo-start -->
 * Class implementing the brute force search algorithm for nearest neighbour search, filtered using PLS.
 * <p/>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * Valid options are: <p/>
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
public class FilteredSearch
  extends TransformNNSearch {
 
  /**suid.*/ 
  private static final long serialVersionUID = -7892640764629075977L;
  
  /** The filter */
  protected Filter m_Filter = new weka.filters.supervised.attribute.AttributeSelection();
  
  /**
   * Constructor. Needs setInstances(Instances) 
   * to be called before the class is usable.
   */
  public FilteredSearch() {
    super();
  }
  
  
  protected Instances transformInstances(Instances in) throws Exception{ 
    
    Instances ret=null;
  /*  m_plsfilter.setInputFormat(in);
    for (int i=0;i<in.numInstances();i++){
      Instance inst=in.instance(i);
      m_plsfilter.input(inst);
    }
    m_plsfilter.batchFinished();
    Instances filteredData = Filter.useFilter(in, m_plsfilter);
    
    Instances ret=new Instances(filteredData,0);
    for (int i=0;i<in.numInstances();i++){
      Instance inst=in.instance(i);
      m_plsfilter.input(inst);
      m_plsfilter.batchFinished();        
      ret.add(m_plsfilter.output());
    }*/
   
    m_Filter.setInputFormat(in);  // filter capabilities are checked here
    ret = Filter.useFilter(in, m_Filter);
    return(ret);   
  }
  
  public Instance transformInstance(Instance in) throws Exception { 
    m_Filter.input(in);
    m_Filter.batchFinished();     
    return(m_Filter.output());    
  }
  
  /**
   * Constructor that uses the supplied set of 
   * instances.
   * 
   * @param insts	the instances to use
   */
  public FilteredSearch(Instances insts) {
    super(insts);
    try {
      m_Instances=insts;
      Instances t_instances=this.transformInstances(insts);
      m_myInstances= t_instances;
      m_DistanceFunction.setInstances(t_instances);
      //m_DistanceFunction.setInstances(transformInstances(insts));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
  public Instances kNearestNeighbours(Instance target, int kNN) throws Exception {
  
    if(m_Stats!=null)
      m_Stats.searchStart();
    m_neighbours.clear();
    double distance; 
    double last_distance=Double.POSITIVE_INFINITY;
    Instance t_instance=transformInstance(target);
    for(int i=0; i<m_Instances.numInstances(); i++) {
      if(target == m_Instances.instance(i)) {//for hold-one-out cross-validation
        continue;
      }
      if(m_Stats!=null) {
        m_Stats.incrPointCount();
      }
      distance = m_DistanceFunction.distance(t_instance, m_myInstances.instance(i), last_distance, m_Stats);
      if(distance == 0.0 && m_SkipIdentical) {
	continue;
      }
      if (distance < last_distance){
	this.m_neighbours.add(new InstanceNode(i,distance));
	if (m_neighbours.size() > kNN){
	  m_neighbours.remove(m_neighbours.size()-1);
	  last_distance=m_neighbours.last().distance;
	}
      }      
    }
    Instances neighbours = new Instances(m_Instances,m_neighbours.size() );   
    int index=0;
    m_Distances = new double[m_neighbours.size()];
    Iterator<InstanceNode> iter = m_neighbours.iterator();

    while(iter.hasNext()){
      InstanceNode in=iter.next();
      m_Distances[index++]=in.distance;
      //System.err.print(in.distance+" ");
      neighbours.add(m_Instances.instance(in.instance_index));
    }
    //System.err.println("\n"+neighbours.toString()+"\n");
    m_DistanceFunction.postProcessDistances(m_Distances);
    if(m_Stats!=null)
      m_Stats.searchFinish();
    
    return neighbours;    
  }
  
  
  /** 
   * Sets the instances comprising the current neighbourhood.
   * 
   * @param insts 	The set of instances on which the nearest neighbour 
   * 			search is carried out. Usually this set is the 
   * 			training set. 
   * @throws Exception	if setting of instances fails
   */
  public void setInstances(Instances insts) throws Exception {
    m_Instances=insts;
    Instances t_instances=this.transformInstances(insts);
    m_myInstances= t_instances;
    m_DistanceFunction.setInstances(t_instances);
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
  public void update(Instance ins) throws Exception {
    if(m_Instances==null)
      throw new Exception("No instances supplied yet. Cannot update without"+
                          "supplying a set of instances first.");
    m_DistanceFunction.update(this.transformInstance(ins));
  }
  
  /** 
   * Adds the given instance info. This implementation updates the range
   * datastructures of the DistanceFunction class.
   * 
   * @param ins 	The instance to add the information of. Usually this is
   * 			the test instance supplied to update the range of 
   * 			attributes in the  distance function.
   */
  public void addInstanceInfo(Instance ins) {
    if(m_Instances!=null)
      try{ update(ins); }
      catch(Exception ex) { ex.printStackTrace(); }
  }
  
  public Enumeration listOptions() {
    Vector result = new Vector();

    result.addElement(new Option(
	      "\tFull class name of filter to use, followed\n"
	      + "\tby filter options.\n"
	      + "\teg: \"weka.filters.unsupervised.attribute.Remove -V -R 1,2\"",
	      "F", 1, "-F <filter specification>"));
    
    
    Enumeration en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    return result.elements();
  }
  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String filterTipText() {
    return "The filter to be used.";
  }

  /**
   * Sets the filter
   *
   * @param filter the filter with all options set.
   */
  public void setFilter(Filter filter) {

    m_Filter = filter;
  }

  /**
   * Gets the filter used.
   *
   * @return the filter
   */
  public Filter getFilter() {

    return m_Filter;
  }
  
  /**
   * Gets the filter specification string, which contains the class name of
   * the filter and any options to the filter
   *
   * @return the filter string.
   */
  protected String getFilterSpec() {
    
    Filter c = getFilter();
    if (c instanceof OptionHandler) {
      return c.getClass().getName() + " "
	+ Utils.joinOptions(((OptionHandler)c).getOptions());
    }
    return c.getClass().getName();
  }

  
  public void setOptions(String[] options) throws Exception {
    
 // Same for filter
    String filterString = Utils.getOption('F', options);
    if (filterString.length() > 0) {
      String [] filterSpec = Utils.splitOptions(filterString);
      if (filterSpec.length == 0) {
	throw new IllegalArgumentException("Invalid filter specification string");
      }
      String filterName = filterSpec[0];
      filterSpec[0] = "";
      setFilter((Filter) Utils.forName(Filter.class, filterName, filterSpec));
    } else {
      setFilter(new weka.filters.supervised.attribute.Discretize());
    }

    super.setOptions(options);
    super.setOptions(options);
  }

  
  public String[] getOptions() {
    
    String [] superOptions = super.getOptions();
    String [] options = new String [superOptions.length + 2];
    int current = 0;

    options[current++] = "-F";
    options[current++] = "" + getFilterSpec();

    System.arraycopy(superOptions, 0, options, current, 
		     superOptions.length);
    return options;
    
  }
}
