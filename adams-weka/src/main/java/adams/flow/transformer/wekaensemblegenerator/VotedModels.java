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
 * VotedModels.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekaensemblegenerator;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Vote;

/**
 * Generates a Vote meta-classifier from the incoming pre-built classifier models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class VotedModels
  extends AbstractWekaEnsembleGenerator {

  private static final long serialVersionUID = -7876743086471307294L;

  /** the vote template. */
  protected Vote m_Template;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a Vote meta-classifier from the incoming pre-built classifier models.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "template", "template",
      new Vote());
  }

  /**
   * Sets the Vote template to use.
   *
   * @param value	the template
   */
  public void setTemplate(Vote value) {
    m_Template = value;
    reset();
  }

  /**
   * Returns the Vote template to use.
   *
   * @return		the template
   */
  public Vote getTemplate() {
    return m_Template;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String voteTipText() {
    return "The template of the Vote classifier to use.";
  }

  /**
   * Returns the input data the generator processes.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Classifier[].class};
  }

  /**
   * Returns the output data the generator generates.
   *
   * @return		the generated classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{Vote.class};
  }

  /**
   * Check method before generating the ensemble.
   *
   * @param input	the input to use
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check(Object input) {
    String	result;

    result = super.check(input);

    if (result == null) {
      if (!(input instanceof Classifier[]))
        result = "Input does not represent a classifier array (" + Utils.classToString(Classifier[].class) + ")!";
    }

    return result;
  }

  /**
   * Generates the ensemble from the input.
   *
   * @param input	the input to use
   * @return		the generated ensemble
   */
  @Override
  protected Object doGenerate(Object input) {
    Vote		result;
    Classifier[]	classifiers;

    classifiers = (Classifier[]) input;
    result      = ObjectCopyHelper.copyObject(m_Template);
    result.setClassifiers(classifiers);

    return result;
  }
}
