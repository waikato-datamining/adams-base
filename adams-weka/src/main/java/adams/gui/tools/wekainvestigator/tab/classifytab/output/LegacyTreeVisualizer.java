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
 * LegacyTreeVisualizer.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.flow.core.Token;
import adams.flow.sink.WekaTreeVisualizer;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.output.GraphHelper;
import weka.classifiers.Classifier;
import weka.core.Drawable;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Displays the tree that the model generated (legacy Weka output).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LegacyTreeVisualizer
  extends AbstractOutputGeneratorWithFoldModelsSupport {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a tree visualization of the model (legacy Weka output).";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Tree visualizer (legacy)";
  }

  /**
   * Checks whether the model can be handled.
   *
   * @param model	the model to check
   * @return		true if handled
   */
  @Override
  protected boolean canHandleModel(Classifier model) {
    return (model instanceof Drawable) && GraphHelper.hasGraph((Drawable) model);
  }

  /**
   * Generates the output for the model.
   *
   * @param model		the model to use as basis
   * @param errors 		for collecting errors
   * @return			the generated table, null if failed to generate
   */
  @Override
  protected JComponent createOutput(Classifier model, MessageCollection errors) {
    WekaTreeVisualizer	sink;
    JPanel 		panel;

    sink  = new WekaTreeVisualizer();
    panel = sink.createDisplayPanel(new Token(model));

    return new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane());
  }
}
