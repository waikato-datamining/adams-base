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
 * FilteredSearchWithIndices.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.core.neighboursearch;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instance;
import weka.core.RevisionUtils;

import java.util.Iterator;

/**
 * FilteredSearch with index support.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FilteredSearchWithIndices
  extends FilteredSearch
  implements NearestNeighbourSearchWithIndices {

  private static final long serialVersionUID = 8865889822956816622L;

  /**
   * Returns a string describing this nearest neighbour search algorithm.
   *
   * @return 		a description of the algorithm for displaying in the
   * 			explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "FilteredSearch with index support.";
  }

  /**
   * Returns the indices of the k nearest instances in the current neighbourhood to the supplied
   * instance.
   *
   * @param target 	The instance to find the k nearest neighbours for.
   * @param kNN		The number of nearest neighbours to find.
   * @return		the indices of the k nearest neighbors in the dataset
   * @throws Exception  if the neighbours could not be found.
   */
  @Override
  public int[] kNearestNeighboursIndices(Instance target, int kNN) throws Exception {
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
	  last_distance=m_neighbours.get(m_neighbours.size() - 1).distance;
	}
      }
    }
    TIntList neighbours = new TIntArrayList();
    int index=0;
    m_Distances = new double[m_neighbours.size()];
    Iterator<InstanceNode> iter = m_neighbours.iterator();

    while(iter.hasNext()){
      InstanceNode in=iter.next();
      m_Distances[index++]=in.distance;
      neighbours.add(in.instance_index);
    }
    m_DistanceFunction.postProcessDistances(m_Distances);
    if(m_Stats!=null)
      m_Stats.searchFinish();

    return neighbours.toArray();
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
