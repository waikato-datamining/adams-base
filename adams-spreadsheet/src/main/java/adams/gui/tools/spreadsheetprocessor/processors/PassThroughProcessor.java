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
 * PassThroughProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.processors;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseButton;
import adams.gui.core.ImageManager;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Just passes through the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThroughProcessor
  extends AbstractProcessor {

  private static final long serialVersionUID = 2926743330826433963L;

  /** the panel. */
  protected JPanel m_Panel;

  /** the button for processing. */
  protected BaseButton m_ButtonExecute;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Passthrough";
  }

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  @Override
  public Component getWidget() {
    if (m_Panel == null) {
      m_Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      m_ButtonExecute = new BaseButton(ImageManager.getIcon("run.gif"));
      m_ButtonExecute.addActionListener((ActionEvent e) -> execute());
      m_ButtonExecute.setToolTipText("Passes through the data (Alt+X)");
      m_Panel.add(m_ButtonExecute);
    }

    return m_Panel;
  }

  /**
   * Executes the query.
   */
  protected void execute() {
    notifyOwner(EventType.PROCESS_DATA, "Pass through data");
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    PassThroughProcessor widget;

    if (other instanceof PassThroughProcessor) {
      widget = (PassThroughProcessor) other;
      widget.getWidget();
    }
  }

  /**
   * Serializes the setup from the widget.
   *
   * @return		the generated setup representation
   */
  public Object serialize() {
    return new HashMap<String,Object>();
  }

  /**
   * Deserializes the setup and maps it onto the widget.
   *
   * @param data	the setup representation to use
   * @param errors	for collecting errors
   */
  public void deserialize(Object data, MessageCollection errors) {
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonExecute.setEnabled((m_Owner.getSourceData() != null));
  }

  /**
   * Processes the data.
   *
   * @param data	the input data
   * @param errors	for storing errors
   * @return		the generated data, null in case of an error
   */
  @Override
  protected SpreadSheet doProcess(SpreadSheet data, MessageCollection errors) {
    notifyOwner(EventType.DATA_IS_PROCESSED, "Passed through data");
    return data;
  }
}
