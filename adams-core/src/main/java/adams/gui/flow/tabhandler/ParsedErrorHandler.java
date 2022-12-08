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
 * ParsedErrorHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tabhandler;

import adams.core.DateUtils;
import adams.flow.core.ActorUtils;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tab.ParsedErrorTab;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages the output from actor processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ParsedErrorHandler
  extends AbstractTabHandler {

  private static final long serialVersionUID = -6875576523702915436L;

  /**
   * Container class.
   */
  public static class ParseOutput
    implements Serializable {

    private static final long serialVersionUID = -7492661828510106722L;

    /** the owner. */
    protected FlowPanel m_Owner;

    /** the title. */
    protected String m_Title;

    /** an optional error. */
    protected String m_Error;

    /** the extracted paths. */
    protected List<String> m_Paths;

    /** the panel. */
    protected BaseSplitPane m_Pane;

    /** the list with the paths. */
    protected BaseListWithButtons m_List;

    /**
     * Initializes the container.
     *
     * @param owner	the flow panel this is related to
     * @param error	the error to parse
     */
    public ParseOutput(FlowPanel owner, String error) {
      m_Owner = owner;
      m_Title = DateUtils.getTimeFormatter().format(new Date());
      m_Error = error;
      m_Paths = ActorUtils.extractActorNames(m_Owner.getCurrentFlow(), error);

      initGUI();
    }

    /**
     * Initializes the display.
     */
    protected void initGUI() {
      BaseTextArea		text;
      DefaultListModel<String>	model;
      final BaseButton		buttonJump;
      final BaseButton		buttonCopy;

      m_Pane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
      m_Pane.setResizeWeight(0.0);
      m_Pane.setDividerLocation(150);

      text = new BaseTextArea(4, 40);
      text.setEditable(false);
      text.setFont(Fonts.getMonospacedFont());
      text.setText(m_Error);
      text.setLineWrap(true);
      text.setWrapStyleWord(true);
      m_Pane.setTopComponent(new BaseScrollPane(text));

      model = new DefaultListModel<>();
      for (String path: m_Paths)
        model.addElement(path);
      m_List = new BaseListWithButtons(model);
      m_Pane.setBottomComponent(m_List);
      
      buttonJump = new BaseButton("Jump");
      buttonJump.addActionListener((ActionEvent e) -> jump());
      m_List.addToButtonsPanel(buttonJump);
      m_List.setDoubleClickButton(buttonJump);

      buttonCopy = new BaseButton("Copy");
      buttonCopy.addActionListener((ActionEvent e) -> copy());
      m_List.addToButtonsPanel(buttonCopy);
    }

    /**
     * Returns the title for the output.
     *
     * @return		the title
     */
    public String getTitle() {
      return m_Title;
    }

    /**
     * Returns the stored error.
     *
     * @return		the error
     */
    public String getError() {
      return m_Error;
    }

    /**
     * Returns the extracted paths.
     *
     * @return		the paths
     */
    public List<String> getPaths() {
      return m_Paths;
    }

    /**
     * Returns the generated split pane.
     *
     * @return		the pane
     */
    public BaseSplitPane getPane() {
      return m_Pane;
    }

    /**
     * Performs the jump to the selected actor.
     */
    protected void jump() {
      if (m_List.getSelectedValue() != null)
	m_Owner.getTree().locateAndDisplay("" + m_List.getSelectedValue(), true);
    }

    /**
     * Copies the selected actor.
     */
    protected void copy() {
      if (m_List.getSelectedValue() != null)
	ClipboardHelper.copyToClipboard("" + m_List.getSelectedValue());
    }
  }

  /** the output. */
  protected List<ParseOutput> m_Outputs;

  /**
   * Initializes the tab handler
   *
   * @param owner the owning panel
   */
  public ParsedErrorHandler(FlowPanel owner) {
    super(owner);
  }

  /**
   * Method for initializing member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Outputs = new ArrayList<>();
  }

  /**
   * Parses the error and adds the output component.
   *
   * @param owner 	the flow panel this error relates to
   * @param error	the error
   */
  public void add(FlowPanel owner, String error) {
    m_Outputs.add(new ParseOutput(owner, error));
    update(true);
  }

  /**
   * Returns the outputs.
   *
   * @return		the outputs
   */
  public List<ParseOutput> getOutputs() {
    return new ArrayList<>(m_Outputs);
  }

  /**
   * Removes the specified output.
   *
   * @param index	the index of the output to remove
   */
  public void remove(int index) {
    m_Outputs.remove(index);
  }

  /**
   * Removes the specified output.
   *
   * @param comp	the component to remove
   */
  public void remove(Component comp) {
    int		i;

    for (i = 0; i < m_Outputs.size(); i++) {
      if (m_Outputs.get(i).getPane() == comp) {
	remove(i);
	break;
      }
    }
  }

  /**
   * Returns whether any outputs are present.
   *
   * @return		true if outputs avaialble
   */
  public boolean hasOutputs() {
    return (m_Outputs.size() > 0);
  }

  /**
   * Notifies the {@link ParsedErrorTab} instance of a change.
   *
   * @param show	whether to show the tab or leave as is
   */
  protected void update(boolean show) {
    Runnable	run;

    run = () -> {
      if (!getEditor().getTabs().isVisible(ParsedErrorTab.class) && show)
	getEditor().getTabs().setVisible(ParsedErrorTab.class, true, false);
      ParsedErrorTab tab = (ParsedErrorTab) getEditor().getTabs().getTab(ParsedErrorTab.class);
      if (tab != null)
	tab.update();
    };
    SwingUtilities.invokeLater(run);

    // close displays?
    run = () -> {
      if (!hasOutputs())
	getEditor().getTabs().setVisible(ParsedErrorTab.class, false, false);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Gets called when the page changes.
   */
  public void display() {
    getEditor().getTabs().setVisible(
      ParsedErrorTab.class,
      hasOutputs(),
      false);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Outputs.clear();
    // notify panel
    update(false);
  }
}
