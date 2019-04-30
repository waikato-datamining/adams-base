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
 * FlowProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.processors;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.io.input.FlowReader;
import adams.data.io.output.FlowWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.SubProcess;
import adams.flow.core.Actor;
import adams.flow.core.Compatibility;
import adams.flow.core.Token;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.flow.FlowPanel;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Uses a subflow for processing the spreadsheet.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FlowProcessor
  extends AbstractProcessor {

  private static final long serialVersionUID = 2926743330826433963L;

  public static final String KEY_FLOW = "flow";

  /** the widget. */
  protected BasePanel m_PanelWidget;

  /** the flow panel. */
  protected FlowPanel m_PanelFlow;

  /** the "new flow" button. */
  protected BaseButton m_ButtonNew;

  /** the "load flow" button. */
  protected BaseButton m_ButtonLoad;

  /** the "save flow" button. */
  protected BaseButton m_ButtonSave;

  /** the "check flow" button. */
  protected BaseButton m_ButtonCheck;

  /** the "run flow" button. */
  protected BaseButton m_ButtonRun;

  /** the file chooser for the flows. */
  protected FlowFileChooser m_FileChooserFlow;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Flow";
  }

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  @Override
  public Component getWidget() {
    JPanel	panelButtons;
    JPanel	panelBottom;

    if (m_PanelWidget == null) {
      m_FileChooserFlow = new FlowFileChooser();

      m_PanelWidget = new BasePanel(new BorderLayout());

      m_PanelFlow   = new FlowPanel();
      m_PanelFlow.getTitleGenerator().setEnabled(false);
      m_PanelFlow.setMinimumSize(new Dimension(400, 0));
      m_PanelFlow.getUndo().clear();
      m_PanelWidget.add(m_PanelFlow, BorderLayout.CENTER);
      panelBottom = new JPanel(new BorderLayout());
      m_PanelWidget.add(panelBottom, BorderLayout.SOUTH);
      panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      panelBottom.add(panelButtons, BorderLayout.EAST);

      m_ButtonNew = new BaseButton(GUIHelper.getIcon("new.gif"));
      m_ButtonNew.addActionListener((ActionEvent e) -> newFlow());
      panelButtons.add(m_ButtonNew);

      m_ButtonLoad = new BaseButton(GUIHelper.getIcon("open.gif"));
      m_ButtonLoad.addActionListener((ActionEvent e) -> loadFlow());
      panelButtons.add(m_ButtonLoad);

      m_ButtonSave = new BaseButton(GUIHelper.getIcon("save.gif"));
      m_ButtonSave.addActionListener((ActionEvent e) -> saveFlow());
      panelButtons.add(m_ButtonSave);

      m_ButtonCheck = new BaseButton(GUIHelper.getIcon("validate.png"));
      m_ButtonCheck.addActionListener((ActionEvent e) -> checkFlow(false));
      panelButtons.add(m_ButtonCheck);

      m_ButtonRun = new BaseButton(GUIHelper.getIcon("run.gif"));
      m_ButtonRun.addActionListener((ActionEvent e) -> runFlow());
      panelButtons.add(m_ButtonRun);

      newFlow();
    }

    return m_PanelWidget;
  }

  /**
   * Replaces the current flow snippet with an empty one.
   */
  protected void newFlow() {
    m_PanelFlow.setCurrentFlow(new SubProcess());
  }

  /**
   * Allows the user to load a flow snippet.
   */
  protected void loadFlow() {
    int		retVal;
    FlowReader reader;
    Actor	actor;

    retVal = m_FileChooserFlow.showOpenDialog(m_Owner);
    if (retVal != FlowFileChooser.APPROVE_OPTION)
      return;

    reader = m_FileChooserFlow.getReader();
    actor  = reader.readActor(m_FileChooserFlow.getSelectedFile());
    if (actor instanceof SubProcess)
      m_PanelFlow.setCurrentFlow(actor);
    else
      GUIHelper.showErrorMessage(
        m_Owner, "The outermost actor in the flow must a " + SubProcess.class.getName()
	  + ", encountered: " + actor.getClass().getName());
  }

  /**
   * Allows the user to save the current flow snippet.
   */
  protected void saveFlow() {
    int		retVal;
    FlowWriter writer;

    retVal = m_FileChooserFlow.showSaveDialog(m_Owner);
    if (retVal != FlowFileChooser.APPROVE_OPTION)
      return;

    writer = m_FileChooserFlow.getWriter();
    if (!writer.write(m_PanelFlow.getCurrentFlow(), m_FileChooserFlow.getSelectedFile()))
      GUIHelper.showErrorMessage(
        m_Owner, "Failed to write flow snippet to: " + m_FileChooserFlow.getSelectedFile());
  }

  /**
   * Checks the flow.
   *
   * @param silent	only pops up a dialog if invalid flow
   * @return		true if flow ok
   */
  protected boolean checkFlow(boolean silent) {
    Actor actor;
    SubProcess		sub;
    String		msg;
    Compatibility comp;

    msg   = null;
    actor = m_PanelFlow.getCurrentFlow();
    sub   = null;

    // subprocess?
    if (!(actor instanceof SubProcess))
      msg = "Outermost actor must be a " + SubProcess.class.getName() + ", found: " + actor.getClass().getName();
    else
      sub = (SubProcess) actor;

    // check compatibility with spreadsheets
    if (msg == null) {
      comp = new Compatibility();
      if (!comp.isCompatible(new Class[]{SpreadSheet.class}, sub.accepts()))
	msg = "Flow snippet does not accept " + SpreadSheet.class.getClass() + ", found: " + Utils.classesToString(sub.accepts());
      else if (!comp.isCompatible(sub.generates(), new Class[]{SpreadSheet.class}))
	msg = "Flow snippet does not generate " + SpreadSheet.class.getClass() + ", found: " + Utils.classesToString(sub.generates());
    }

    if (msg != null)
      GUIHelper.showErrorMessage(m_Owner, "Flow failed test:\n" + msg);
    else if (!silent)
      GUIHelper.showInformationMessage(m_Owner,"Flow passed test!");

    return (msg == null);
  }

  /**
   * Runs the flow.
   */
  protected void runFlow() {
    notifyOwner(EventType.PROCESS_DATA, "Execute flow");
  }

  /**
   * Sets the flow.
   *
   * @param value	the flow
   */
  public void setCurrentFlow(Actor value) {
    m_PanelFlow.setCurrentFlow(value);
  }

  /**
   * Returns the flow.
   *
   * @return		the flow
   */
  public Actor getCurrentFlow() {
    return m_PanelFlow.getCurrentFlow();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    FlowProcessor widget;

    if (other instanceof FlowProcessor) {
      widget = (FlowProcessor) other;
      widget.getWidget();
      setCurrentFlow(widget.getCurrentFlow());
    }
  }

  /**
   * Serializes the setup from the widget.
   *
   * @return		the generated setup representation
   */
  public Object serialize() {
    Map<String,Object> result;

    result = new HashMap<>();
    result.put(KEY_FLOW, getCurrentFlow().toCommandLine());

    return result;
  }

  /**
   * Deserializes the setup and maps it onto the widget.
   *
  /**
   * Deserializes the setup and maps it onto the widget.
   *
   * @param data	the setup representation to use
   * @param errors	for collecting errors
   */
  public void deserialize(Object data, MessageCollection errors) {
    Map<String,Object>	map;

    if (data instanceof Map) {
      map = (Map<String,Object>) data;
      if (map.containsKey(KEY_FLOW)) {
	try {
	  setCurrentFlow((Actor) OptionUtils.forAnyCommandLine(Actor.class, (String) map.get(KEY_FLOW)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate flow from: " + map.get(KEY_FLOW));
	}
      }
      update();
    }
    else {
      errors.add(getClass().getName() + ": Deserialization data is not a map!");
    }
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonRun.setEnabled((m_Owner.getSourceData() != null));
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
    SpreadSheet		result;
    SubProcess		subflow;
    String		msg;

    result  = null;
    subflow = (SubProcess) m_PanelFlow.getCurrentFlow();

    // setup
    msg = subflow.setUp();
    if (msg != null)
      errors.add(msg);

    // process
    if (errors.isEmpty()) {
      subflow.input(new Token(data));
      msg = subflow.execute();
      if (msg != null)
	errors.add(msg);
    }

    // collect output
    if (errors.isEmpty()) {
      if (subflow.hasPendingOutput()) {
	result = (SpreadSheet) subflow.output().getPayload();
	notifyOwner(EventType.DATA_IS_PROCESSED, "Executed flow");
      }
      else {
	errors.add("Flow did not generate any data!");
      }
    }

    return result;
  }
}
