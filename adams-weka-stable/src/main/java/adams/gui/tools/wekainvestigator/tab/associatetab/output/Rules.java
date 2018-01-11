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
 * ModelOutput.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.associatetab.output;

import adams.core.MessageCollection;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.associatetab.ResultItem;
import weka.associations.AssociationRule;
import weka.associations.AssociationRulesProducer;

import javax.swing.JComponent;

/**
 * Outputs the rules if available {@link AssociationRulesProducer}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rules
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the rules if available (associator must implement " + AssociationRulesProducer.class.getName() + ").";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Rules";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasModel()
      && (item.getModel() instanceof AssociationRulesProducer)
      && ((AssociationRulesProducer) item.getModel()).canProduceRules();
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    BaseTextArea 		text;
    AssociationRulesProducer	producer;
    StringBuilder		rules;

    if (!item.hasModel()) {
      errors.add("No model available!");
      return null;
    }
    if (!(item.getModel() instanceof AssociationRulesProducer)) {
      errors.add("Model does not implement " + AssociationRulesProducer.class.getName() + "!");
      return null;
    }
    if (!((AssociationRulesProducer) item.getModel()).canProduceRules()) {
      errors.add("Model cannot produce rules!");
      return null;
    }

    producer = (AssociationRulesProducer) item.getModel();
    rules    = new StringBuilder();
    for (AssociationRule rule: producer.getAssociationRules().getRules())
      rules.append(rule.toString()).append("\n");

    text = new BaseTextArea();
    text.setEditable(false);
    text.setTextFont(Fonts.getMonospacedFont());
    text.setText(rules.toString());
    text.setCaretPosition(0);

    return new TextualContentPanel(text, true);
  }
}
