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
 * RemoteControlCenterEnginesPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter;

import adams.core.CleanUpHandler;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.event.RemoteScriptingEngineUpdateEvent;
import adams.gui.event.RemoteScriptingEngineUpdateListener;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.scripting.engine.DefaultScriptingEngine;
import adams.scripting.engine.MultiScriptingEngine;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.requesthandler.SimpleLogPanelRequestHandler;
import adams.scripting.responsehandler.SimpleLogPanelResponseHandler;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Interface for adding/removing/starting/stopping scripting engines.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteControlCenterEnginesPanel
  extends BasePanel
  implements RemoteScriptingEngineUpdateListener, CleanUpHandler {

  private static final long serialVersionUID = -4281172076210274495L;

  /** the owner. */
  protected RemoteControlCenterManagerPanel m_Owner;

  /** the GOE with the engine. */
  protected GenericObjectEditorPanel m_GOEEngine;

  /** list for the scripting engines. */
  protected BaseListWithButtons m_ListEngines;

  /** the button for refreshing the engine list. */
  protected BaseButton m_ButtonRefresh;

  /** the button for adding the engine. */
  protected BaseButton m_ButtonAdd;

  /** the button for copying the engine. */
  protected BaseButton m_ButtonCopy;

  /** the button for removing the engine. */
  protected BaseButton m_ButtonRemove;

  /** the button for stopping engines. */
  protected BaseButton m_ButtonStop;

  /** the button for starting engines. */
  protected BaseButton m_ButtonStart;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    DefaultScriptingEngine	engine;

    super.initGUI();

    setLayout(new BorderLayout(5, 5));

    engine = new DefaultScriptingEngine();
    engine.setRequestHandler(new SimpleLogPanelRequestHandler());
    engine.setResponseHandler(new SimpleLogPanelResponseHandler());
    m_GOEEngine = new GenericObjectEditorPanel(RemoteScriptingEngine.class, engine, true);
    m_GOEEngine.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    add(m_GOEEngine, BorderLayout.NORTH);

    m_ListEngines = new BaseListWithButtons();
    m_ListEngines.addListSelectionListener((ListSelectionEvent e) -> updateButtons());
    add(m_ListEngines, BorderLayout.CENTER);

    m_ButtonRefresh = new BaseButton("Refresh", ImageManager.getIcon("refresh.gif"));
    m_ButtonRefresh.addActionListener((ActionEvent e) -> refreshEngines());
    m_ListEngines.addToButtonsPanel(m_ButtonRefresh);

    m_ListEngines.addToButtonsPanel(new JLabel());

    m_ButtonAdd = new BaseButton("Add", ImageManager.getIcon("add.gif"));
    m_ButtonAdd.addActionListener((ActionEvent e) -> addEngine());
    m_ListEngines.addToButtonsPanel(m_ButtonAdd);

    m_ButtonRemove = new BaseButton("Remove", ImageManager.getIcon("remove.gif"));
    m_ButtonRemove.addActionListener((ActionEvent e) -> removeEngines());
    m_ListEngines.addToButtonsPanel(m_ButtonRemove);

    m_ButtonCopy = new BaseButton("Copy", ImageManager.getIcon("copy.gif"));
    m_ButtonCopy.addActionListener((ActionEvent e) -> copyEngine());
    m_ListEngines.addToButtonsPanel(m_ButtonCopy);

    m_ListEngines.addToButtonsPanel(new JLabel());

    m_ButtonStart = new BaseButton("Start", ImageManager.getIcon("run.gif"));
    m_ButtonStart.addActionListener((ActionEvent e) -> startEngines());
    m_ListEngines.addToButtonsPanel(m_ButtonStart);

    m_ButtonStop = new BaseButton("Stop", ImageManager.getIcon("stop_blue.gif"));
    m_ButtonStop.addActionListener((ActionEvent e) -> stopEngines());
    m_ListEngines.addToButtonsPanel(m_ButtonStop);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Sets the owning application.
   *
   * @param value	the owner
   */
  public void setOwner(RemoteControlCenterManagerPanel value) {
    if (getApplicationFrame() != null)
      getApplicationFrame().removeRemoteScriptingEngineUpdateListener(this);

    m_Owner = value;

    if (getApplicationFrame() != null) {
      getApplicationFrame().addRemoteScriptingEngineUpdateListener(this);
      refreshEngines();
    }
  }

  /**
   * Returns the owning application.
   *
   * @return		the owner
   */
  public RemoteControlCenterManagerPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the application frame this panel belongs to.
   *
   * @return		the frame, null if not part of an app frame
   */
  public AbstractApplicationFrame getApplicationFrame() {
    if (getOwner() != null)
      return getOwner().getApplicationFrame();
    return null;
  }

  /**
   * Returns the log panel.
   *
   * @return		the panel, null if no owner set
   */
  public RemoteControlCenterLogPanel getLogPanel() {
    if (getOwner() != null)
      return getOwner().getLogPanel();
    return null;
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    int				selCount;
    RemoteScriptingEngine	selEngine;

    selCount  = m_ListEngines.getSelectedIndices().length;
    selEngine = null;
    if (selCount == 1)
      selEngine = (RemoteScriptingEngine) m_ListEngines.getSelectedValue();

    m_ButtonRemove.setEnabled(selCount > 0);
    m_ButtonCopy.setEnabled(selCount == 1);
    m_ButtonStart.setEnabled((selCount > 1) || ((selCount == 1) && !selEngine.isRunning()));
    m_ButtonStop.setEnabled((selCount > 1) || ((selCount == 1) && selEngine.isRunning()));
  }

  /**
   * Refreshes the list of engines.
   */
  protected void refreshEngines() {
    Set<String> 				current;
    TIntArrayList				selected;
    RemoteScriptingEngine			engine;
    MultiScriptingEngine			multi;
    DefaultListModel<RemoteScriptingEngine>	model;

    // backup currently selected cmdlines
    current = new HashSet<>();
    for (int index: m_ListEngines.getSelectedIndices()) {
      engine = (RemoteScriptingEngine) m_ListEngines.getModel().getElementAt(index);
      current.add(engine.toCommandLine());
    }

    selected = new TIntArrayList();
    model    = new DefaultListModel<>();
    if (m_Owner.getRemoteScriptingEngine() instanceof MultiScriptingEngine) {
      multi = (MultiScriptingEngine) m_Owner.getRemoteScriptingEngine();
      for (RemoteScriptingEngine r: multi.getEngines()) {
	model.addElement(r);
	if (current.contains(r.toCommandLine()))
	  selected.add(model.size() - 1);
      }
    }
    else {
      if (m_Owner.getRemoteScriptingEngine() != null) {
        model.addElement(m_Owner.getRemoteScriptingEngine());
        if (current.contains(m_Owner.getRemoteScriptingEngine().toCommandLine()))
          selected.add(model.size() - 1);
      }
    }
    m_ListEngines.setModel(model);

    // restore selected cmdlines
    m_ListEngines.setSelectedIndices(selected.toArray());
  }

  /**
   * Adds the engine to the list.
   */
  protected void addEngine() {
    RemoteScriptingEngine	engine;

    engine = (RemoteScriptingEngine) m_GOEEngine.getCurrent();
    if (engine.getRequestHandler() instanceof SimpleLogPanelRequestHandler)
      ((SimpleLogPanelRequestHandler) engine.getRequestHandler()).setLog(getLogPanel().getRequestLog());
    if (engine.getResponseHandler() instanceof SimpleLogPanelResponseHandler)
      ((SimpleLogPanelResponseHandler) engine.getResponseHandler()).setLog(getLogPanel().getResponseLog());
    if (getApplicationFrame() != null)
      getApplicationFrame().addRemoteScriptingEngine(engine);
  }

  /**
   * Copies the engine from the list.
   */
  protected void copyEngine() {
    if (m_ListEngines.getSelectedIndices().length != 1)
      return;

    m_GOEEngine.setCurrent(m_ListEngines.getSelectedValue());
  }

  /**
   * Removes the engines from the list.
   */
  protected void removeEngines() {
    List<RemoteScriptingEngine>	engines;
    RemoteScriptingEngine	engine;

    engines = new ArrayList<>();

    for (int index: m_ListEngines.getSelectedIndices()) {
      engine = (RemoteScriptingEngine) m_ListEngines.getModel().getElementAt(index);
      engines.add(engine);
    }

    for (RemoteScriptingEngine e: engines)
      getApplicationFrame().removeRemoteScriptingEngine(e);
  }

  /**
   * Starts the selected engines.
   */
  protected void startEngines() {
    for (int index: m_ListEngines.getSelectedIndices()) {
      final RemoteScriptingEngine engine = (RemoteScriptingEngine) m_ListEngines.getModel().getElementAt(index);
      if (!engine.isRunning())
	new Thread(() -> engine.execute()).start();
    }

    updateButtons();
  }

  /**
   * Stops the selected engines.
   */
  protected void stopEngines() {
    RemoteScriptingEngine	engine;

    for (int index: m_ListEngines.getSelectedIndices()) {
      engine = (RemoteScriptingEngine) m_ListEngines.getModel().getElementAt(index);
      if (engine.isRunning())
	engine.stopExecution();
    }

    updateButtons();
  }

  /**
   * Gets called in case the remote scripting engine got updated.
   *
   * @param e		the event
   */
  public void remoteScriptingEngineUpdated(RemoteScriptingEngineUpdateEvent e) {
    refreshEngines();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (getApplicationFrame() != null) {
      getApplicationFrame().removeRemoteScriptingEngineUpdateListener(this);
      m_Owner = null;
    }
  }
}
