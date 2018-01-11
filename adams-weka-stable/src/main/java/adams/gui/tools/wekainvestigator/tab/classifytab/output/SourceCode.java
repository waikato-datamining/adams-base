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
 * SourceCode.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Sourcable;

import javax.swing.JComponent;

/**
 * Outputs source code from the model (if classifier implements {@link Sourcable}).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SourceCode
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the classname to use. */
  protected String m_Classname;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs source code from the model (if the classifier implements " + Sourcable.class.getName() + ").";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "classname", "classname",
      "MyModel");
  }

  /**
   * Sets the classname to use.
   *
   * @param value	the classname
   */
  public void setClassname(String value) {
    m_Classname = value;
    reset();
  }

  /**
   * Returns the classanme to use.
   *
   * @return		the classname
   */
  public String getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The classname to use in the generated code.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Source code";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasModel() && (item.getModel() instanceof Sourcable);
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    BaseTextArea 	text;

    if (!item.hasModel()) {
      errors.add("No model available!");
      return null;
    }
    if (!(item.getModel() instanceof Sourcable)) {
      errors.add("Classifier does not implement " + Sourcable.class.getName() + "!");
      return null;
    }

    text = new BaseTextArea();
    text.setEditable(false);
    text.setTextFont(Fonts.getMonospacedFont());
    try {
      text.setText(((Sourcable) item.getModel()).toSource(m_Classname));
    }
    catch (Exception e) {
      errors.add("Failed to generate source code!", e);
      return null;
    }
    text.setCaretPosition(0);

    return new TextualContentPanel(text, true);
  }
}
