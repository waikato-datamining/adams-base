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
 * AbstractDataPreparation.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta.socketfacade;

import adams.core.option.AbstractOptionHandler;
import weka.classifiers.meta.SocketFacade;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Ancestor for classes that prepare data for the {@link SocketFacade}
 * classifier.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDataPreparation
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 2063517191431196592L;

  /**
   * Prepares the data for training.
   *
   * @param data	the data to use
   * @param facade	the classifier using the data preparation
   * @return 		the prepared data
   */
  public abstract byte[] prepareTrain(Instances data, SocketFacade facade);

  /**
   * Prepares the instance for the {@link weka.classifiers.Classifier#classifyInstance(Instance)} method.
   *
   * @param inst	the data to use
   * @param facade	the classifier using the data preparation
   * @return 		the prepared data
   */
  public abstract byte[] prepareClassify(Instance inst, SocketFacade facade);

  /**
   * Prepares the instance for the {@link weka.classifiers.Classifier#distributionForInstance(Instance)} method.
   *
   * @param inst	the data to use
   * @param facade	the classifier using the data preparation
   * @return 		the prepared data
   */
  public abstract byte[] prepareDistribution(Instance inst, SocketFacade facade);

  /**
   * Parses the data received from the process from the training process.
   *
   * @param data	the data to parse
   * @return 		null if successful, otherwise error message
   */
  public abstract String parseTrain(byte[] data);

  /**
   * Parses the data received from the process, to be returned by the
   * {@link weka.classifiers.Classifier#classifyInstance(Instance)} method.
   *
   * @param data	the data to parse
   * @return 		the classification
   */
  public abstract double parseClassify(byte[] data);

  /**
   * Parses the data received from the process, to be returned by the
   * {@link weka.classifiers.Classifier#distributionForInstance(Instance)} method.
   *
   * @param data	the data to parse
   * @param numClasses  the number of classes
   * @return 		the class distribution
   */
  public abstract double[] parseDistribution(byte[] data, int numClasses);
}
