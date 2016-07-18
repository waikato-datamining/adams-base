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
 * AbstractClassifierEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.core.ClassLister;
import adams.gui.tools.wekainvestigator.tab.ClassifyTab;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Ancestor for classifier evaluation setups.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierEvaluation {

  /** the owner. */
  protected ClassifyTab m_Owner;

  /** the panel with the options. */
  protected JPanel m_PanelOptions;

  /**
   * Constructor.
   */
  protected AbstractClassifierEvaluation() {
    initialize();
    initGUI();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
  }

  /**
   * Initializes the panel.
   */
  protected void initGUI() {
    m_PanelOptions = new JPanel(new BorderLayout());
  }

  /**
   * Sets the owner.
   *
   * @param value	the owning tab
   */
  public void setOwner(ClassifyTab value) {
    m_Owner = value;
    update();
  }

  /**
   * Returns the owner.
   *
   * @return		the owning tab, null if none set
   */
  public ClassifyTab getOwner() {
    return m_Owner;
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * Returns a panel with options to display.
   *
   * @return		the panel
   */
  public JPanel getPanel() {
    return m_PanelOptions;
  }

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		true if possible
   */
  public abstract boolean canEvaluate(Classifier classifier);

  /**
   * Evaluates the classifier and returns the generated evaluation object.
   *
   * @return		the evaluation
   * @throws Exception	if evaluation fails
   */
  public abstract Evaluation evaluate(Classifier classifier) throws Exception;

  /**
   * Returns just the name of the evaluation.
   *
   * @return		the evaluation
   */
  public String toString() {
    return getName();
  }

  /**
   * Updates the settings panel.
   */
  public abstract void update();

  /**
   * Returns the available actions.
   *
   * @return		the action classnames
   */
  public static Class[] getEvaluations() {
    return ClassLister.getSingleton().getClasses(AbstractClassifierEvaluation.class);
  }
}
