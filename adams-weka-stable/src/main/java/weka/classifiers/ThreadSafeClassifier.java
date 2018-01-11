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
 * ThreadSafeClassifier.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import weka.core.Instance;
import weka.core.Instances;

/**
 * Indicator interface for thread-safe classifiers. It does not make a
 * classifier automatically thread-safe, that has be ensured by the actual
 * implementation. A first step is to make the {@link #buildClassifier(Instances)},
 * {@link #classifyInstance(Instance)} and {@link #distributionForInstance(Instance)}
 * mtehods synchronized.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ThreadSafeClassifier
  extends Classifier {

}
