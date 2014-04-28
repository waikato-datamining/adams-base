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
 * AddClassification.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import adams.core.base.BaseString;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Allows the user to select a pixel location and add a corresponding classification for it.<br/>
 * Stores the location with prefix 'Pixel.X.' and 'Pixel.Y.' and the classification using 'Classification.' as prefix.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-label &lt;adams.core.base.BaseString&gt; [-label ...] (property: labels)
 * &nbsp;&nbsp;&nbsp;The labels to offer the user for classifying the pixel.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-remember-last-selection (property: rememberLastSelection)
 * &nbsp;&nbsp;&nbsp;If enabled, the last selected label will get selected by default the next 
 * &nbsp;&nbsp;&nbsp;time this action is chosen.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AddClassification
  extends AbstractPixelSelectorAction {

  /** for serialization. */
  private static final long serialVersionUID = 553796438710725814L;

  /** the pixel location X prefix. */
  public final static String PIXEL_X = "Pixel.X.";

  /** the pixel location Y prefix. */
  public final static String PIXEL_Y = "Pixel.Y.";

  /** the classification prefix. */
  public final static String CLASSIFICATION = "Classification.";
  
  /** the classification labels to use. */
  protected BaseString[] m_Labels;

  /** whether to remember the last selection. */
  protected boolean m_RememberLastSelection;
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return 
	"Allows the user to select a pixel location and add a corresponding "
	+ "classification for it.\n"
	+ "Stores the location with prefix '" + PIXEL_X + "' and '" + PIXEL_Y + "' "
	+ "and the classification using '" + CLASSIFICATION + "' as prefix.";
  }

  /**
   * Returns the title of the action (used as menu item text).
   * 
   * @return		the title
   */
  protected String getTitle() {
    return "Add classification";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "label", "labels",
	    new BaseString[0]);

    m_OptionManager.add(
	    "remember-last-selection", "rememberLastSelection",
	    false);
  }

  /**
   * Sets the available classification labels.
   *
   * @param value	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the available classification labels.
   *
   * @return		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to offer the user for classifying the pixel.";
  }

  /**
   * Sets whether to remember the last selected label.
   *
   * @param value	true if to remember
   */
  public void setRememberLastSelection(boolean value) {
    m_RememberLastSelection = value;
    reset();
  }

  /**
   * Returns whether to remmeber the last selected label.
   *
   * @return		true if remembered
   */
  public boolean getRememberLastSelection() {
    return m_RememberLastSelection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rememberLastSelectionTipText() {
    return "If enabled, the last selected label will get selected by default the next time this action is chosen.";
  }

  /**
   * Returns the next free classification index.
   * 
   * @param report	the report to check against
   * @return		the next free index
   */
  protected int getNextClassificationIndex(Report report) {
    int		result;
    
    result = 0;
    do {
      result++;
    } 
    while (report.hasField(CLASSIFICATION + result));
    
    return result;
  }
  
  /**
   * Reacts to the action event.
   * 
   * @param e		the event
   * @return		true if to update the report table
   */
  protected boolean doProcessAction(ActionEvent e) {
    Report	report;
    Field	field;
    BaseString	label;
    int		index;
    
    if (m_RememberLastSelection && getPanel().hasLastActionResult(getClass()))
      label = (BaseString) getPanel().getLastActionResult(getClass());
    else
      label = m_Labels[0];
    
    label = (BaseString) JOptionPane.showInputDialog(
	getPanel(), 
	"Please select classification", 
	getName(), 
	JOptionPane.QUESTION_MESSAGE, 
	null, 
	m_Labels, 
	label);
    if (label == null)
      return false;
    
    if (m_RememberLastSelection)
      getPanel().setLastActionResult(getClass(), label);
    
    report = getPanel().getImage().getReport();
    index  = getNextClassificationIndex(report);
    
    field = new Field(PIXEL_X + index, DataType.NUMERIC);
    report.addField(field);
    report.setValue(field, getPixelPosition().getX());

    field = new Field(PIXEL_Y + index, DataType.NUMERIC);
    report.addField(field);
    report.setValue(field, getPixelPosition().getY());

    field = new Field(CLASSIFICATION + index, DataType.STRING);
    report.addField(field);
    report.setValue(field, label.getValue());
    
    return true;
  }
  
  /**
   * Performs a check on the setup.
   * 
   * @return		null if check passed, otherwise the error message
   */
  public String check() {
    if (m_Labels.length < 1)
      return "At least one label must be defined!";
    
    return super.check();
  }
}
