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
 * CrossValidationFoldGenerator.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers;
import java.util.NoSuchElementException;

import weka.core.Instances;
import adams.flow.container.WekaTrainTestSetContainer;

/**
 * Helper class for generating cross-validation folds.
 * <p/>
 * The template for the relation name accepts the following placeholders:
 * @ = original relation name, $T = type (train/test), $N = current fold number
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CrossValidationFoldGenerator
  extends AbstractSplitGenerator {
  
  /** for serialization. */
  private static final long serialVersionUID = -8387205583429213079L;

  /** the placeholder for the (original) relation name. */
  public final static String PLACEHOLDER_ORIGINAL = "@";

  /** the placeholder for "train" or "test" type. */
  public final static String PLACEHOLDER_TYPE = "$T";

  /** the placeholder for the current fold number. */
  public final static String PLACEHOLDER_CURRENTFOLD = "$N";
  
  /** the number of folds. */
  protected int m_NumFolds;
  
  /** whether to stratify the data (in case of nominal class). */
  protected boolean m_Stratify;
  
  /** the current fold. */
  protected int m_CurrentFold;
  
  /** the template for the relation name. */
  protected String m_RelationName;

  /**
   * Initializes the generator.
   * 
   * @param data	the full dataset
   * @param numFolds	the number of folds, leave-one-out if less than 2
   * @param seed	the seed for randomization
   * @param stratify	whether to perform stratified CV
   */
  public CrossValidationFoldGenerator(Instances data, int numFolds, long seed, boolean stratify) {
    this(data, numFolds, seed, stratify, null);
  }

  /**
   * Initializes the generator.
   * 
   * @param data	the full dataset
   * @param numFolds	the number of folds
   * @param numFolds	the number of folds, leave-one-out if less than 2
   * @param stratify	whether to perform stratified CV
   * @param relName	the relation name template, use null to ignore
   */
  public CrossValidationFoldGenerator(Instances data, int numFolds, long seed, boolean stratify, String relName) {
    super(data, seed);

    if (data.classIndex() == -1)
      throw new IllegalArgumentException("No class attribute set!");

    if (numFolds < 2)
      m_NumFolds = data.numInstances();
    else
      m_NumFolds = numFolds;
    
    if (data.numInstances() < m_NumFolds)
      throw new IllegalArgumentException(
	  "Cannot have less data than folds: "
	      + "required=" + m_NumFolds + ", provided=" + data.numInstances());
    
    if ((relName == null) || (relName.length() == 0))
      relName = PLACEHOLDER_ORIGINAL;
    
    m_RelationName = relName;
    m_CurrentFold  = 1;
    m_Random       = null;
    m_Stratify     = stratify;
  }
  
  /**
   * Returns the number of folds.
   * 
   * @return		the number of folds
   */
  public int getNumFolds() {
    return m_NumFolds;
  }
  
  /**
   * Returns whether to stratify the data (in case of nominal class).
   * 
   * @return		true if to stratify
   */
  public boolean getStratify() {
    return m_Stratify;
  }
  
  /**
   * Returns the relation name template.
   * 
   * @return		the template
   */
  public String getRelationName() {
    return m_RelationName;
  }

  /**
   * Returns whether randomization is enabled.
   * 
   * @return		true if to randomize
   */
  @Override
  protected boolean canRandomize() {
    return true;
  }

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   *
   * @return 		<tt>true</tt> if the iterator has more elements.
   */
  @Override
  protected boolean checkNext() {
    return (m_CurrentFold <= m_NumFolds);
  }

  /**
   * Generates a relation name for the current fold.
   *
   * @param train	whether train or test set
   * @return		the relation name
   */
  protected String createRelationName(boolean train) {
    StringBuilder	result;
    String		name;
    int			len;

    result = new StringBuilder();
    name   = m_RelationName;

    while (name.length() > 0) {
      if (name.startsWith(PLACEHOLDER_ORIGINAL)) {
	len = 1;
	result.append(m_Data.relationName());
      }
      else if (name.startsWith(PLACEHOLDER_TYPE)) {
	len = 2;
	if (train)
	  result.append("train");
	else
	  result.append("test");
      }
      else if (name.startsWith(PLACEHOLDER_CURRENTFOLD)) {
	len = 2;
	result.append(Integer.toString(m_CurrentFold));
      }
      else {
	len = 1;
	result.append(name.charAt(0));
      }

      name = name.substring(len);
    }

    return result.toString();
  }

  /**
   * Initializes the iterator, randomizes the data if required.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    if (m_Stratify && m_Data.classAttribute().isNominal() && (m_NumFolds < m_Data.numInstances()))
      m_Data.stratify(m_NumFolds);
  }

  /**
   * Returns the next element in the iteration.
   *
   * @return 				the next element in the iteration.
   * @throws NoSuchElementException 	iteration has no more elements.
   */
  @Override
  protected WekaTrainTestSetContainer createNext() {
    WekaTrainTestSetContainer	result;
    Instances 			train;
    Instances 			test;

    if (m_CurrentFold > m_NumFolds)
      throw new NoSuchElementException("No more folds available!");
    
    // generate fold pair
    train = m_Data.trainCV(m_NumFolds, m_CurrentFold - 1, m_Random);
    test  = m_Data.testCV(m_NumFolds, m_CurrentFold - 1);

    // rename datasets
    train.setRelationName(createRelationName(true));
    test.setRelationName(createRelationName(false));

    result = new WekaTrainTestSetContainer(train, test, m_Seed, m_CurrentFold, m_NumFolds);
    m_CurrentFold++;

    return result;
  }
  
  /**
   * Returns a short description of the generator.
   * 
   * @return		a short description
   */
  @Override
  public String toString() {
    return super.toString() + ", numFolds=" + m_NumFolds + ", relName=" + m_RelationName;
  }
}
