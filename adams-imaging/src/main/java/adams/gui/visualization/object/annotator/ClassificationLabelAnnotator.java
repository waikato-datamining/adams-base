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
 * ClassificationLabelAnnotator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.awt.Graphics;

/**
 * Simply sets the chosen label in the report.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClassificationLabelAnnotator
  extends AbstractAnnotator {

  private static final long serialVersionUID = -7549055967417580749L;

  /** the field to store the classification label in. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply sets the chosen label in the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "field",
      new Field("Classification", DataType.STRING));
  }

  /**
   * Sets the field to use.
   *
   * @param value 	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to use.
   *
   * @return 		the field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The field to store the label in.";
  }

  /**
   * Installs the annotator with the owner.
   */
  @Override
  protected void doInstall() {
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  @Override
  protected void doUninstall() {
  }

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  @Override
  protected void doPaintSelection(Graphics g) {
  }

  /**
   * Gets called when the label changes.
   */
  @Override
  public void labelChanged() {
    Report	report;
    String	comment;

    super.labelChanged();

    report = getOwner().getReport().getClone();
    if (hasCurrentLabel()) {
      comment = "Setting label: " + getCurrentLabel();
      report.setValue(m_Field, getCurrentLabel());
    }
    else {
      comment = "Removing label";
      report.removeValue(m_Field);
    }
    getOwner().addUndoPoint(comment);
    getOwner().setReport(report);
  }
}
