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
 * GraphSource.java
 * Copyright (C) 2019-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import weka.classifiers.Classifier;
import weka.core.Drawable;

import javax.swing.JComponent;

/**
 * Displays the source code of the graph (dot or XML BIF).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GraphSource
  extends AbstractOutputGeneratorWithFoldModelsSupport {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the graph source (dot or XML BIF).";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Graph source";
  }

  /**
   * Checks whether the model can be handled.
   *
   * @param model	the model to check
   * @return		true if handled
   */
  @Override
  protected boolean canHandleModel(Classifier model) {
    return (model instanceof Drawable);
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
    BaseTextArea 	text;

    try {
      text = new BaseTextArea();
      text.setEditable(false);
      text.setTextFont(Fonts.getMonospacedFont());
      text.setText(((Drawable) model).graph());
      text.setCaretPosition(0);
      return new TextualContentPanel(text, true);
    }
    catch (Exception e) {
      errors.add("Failed to obtain graph source!", e);
      return null;
    }
  }
}
