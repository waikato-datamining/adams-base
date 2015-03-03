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
 * SubSample.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.core;

import weka.core.Instances;
import weka.filters.Filter;

/**
 * Class for taking a smaller sample of a dataset. Used by the matrix class.
 *
 * @author msf8
 * @version $Revision$
 */
public class SubSample {

  /** Instances to plot */
  protected Instances m_Instances;

  /**Percentage of data to take */
  protected double m_Percentage;

  /**
   * constructor
   * @param inst			Instances for plotting
   * @param percent		Percent of data for sub sample
   */
  public SubSample(Instances inst, double percent) {
    m_Instances = inst;
    m_Percentage = percent;
  }

  /**
   * Take a sample of the dataset
   * @return				Instances containing specified instances of the original instances
   * @throws Exception
   */
  public Instances sample() throws Exception {

    m_Instances.setClassIndex(m_Instances.numAttributes() -1);
    int seed = 42;
    Filter filter = null;
    if(m_Instances.classAttribute().isNominal()) {
      weka.filters.supervised.instance.Resample resampleNom = new weka.
      filters.supervised.instance.Resample();
      resampleNom.setRandomSeed(seed);
      resampleNom.setSampleSizePercent(m_Percentage);
      filter = resampleNom;
    }
    else {
      weka.filters.unsupervised.instance.Resample resampleOther = new
      weka.filters.unsupervised.instance.Resample();
      resampleOther.setRandomSeed(seed);
      resampleOther.setSampleSizePercent(m_Percentage);
      filter = resampleOther;
    }
    filter.setInputFormat(m_Instances);
    Instances sample = Filter.useFilter(m_Instances, filter);
    return sample;
  }
}