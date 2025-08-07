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
 * WekEvaluationRenderer.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import nz.ac.waikato.cms.locator.ClassLocator;
import weka.classifiers.Evaluation;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Renderer for Evaluation objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekEvaluationRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -1106862468119749048L;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls the class to check
   * @return true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(Evaluation.class, cls);
  }

  /**
   * Performs the actual rendering with a new renderer setup.
   *
   * @param obj   the object to render
   * @param panel the panel to render into
   * @param limit the limit to use for the rendering (if applicable), ignored if null
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel, Integer limit) {
    BaseTextArea	textArea;
    BaseScrollPane	scrollPane;

    textArea = new BaseTextArea(10, 40);
    textArea.setTextFont(Fonts.getMonospacedFont());
    textArea.setEditable(false);
    textArea.setText(((Evaluation) obj).toSummaryString(false));
    scrollPane = new BaseScrollPane(textArea);
    panel.add(scrollPane, BorderLayout.CENTER);

    return null;
  }
}
