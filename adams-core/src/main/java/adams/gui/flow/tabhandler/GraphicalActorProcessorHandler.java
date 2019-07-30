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
 * GraphicalActorProcessorHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tabhandler;

import adams.gui.flow.FlowPanel;
import adams.gui.flow.tab.GraphicalActorProcessorTab;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the output from actor processors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GraphicalActorProcessorHandler
  extends AbstractTabHandler {

  private static final long serialVersionUID = -6875576523702915436L;

  /**
   * Container class.
   */
  public static class Output
    implements Serializable {

    private static final long serialVersionUID = -7492661828510106722L;

    /** the title. */
    public String title;

    /** the generated output. */
    public Component component;

    /** an optional error. */
    public String error;

    /**
     * Initializes the container.
     *
     * @param title	the title
     * @param component	the output component, can be null
     * @param error	the optional error, can be null
     */
    public Output(String title, Component component, String error) {
      this.title     = title;
      this.component = component;
      this.error     = error;
    }

    /**
     * Returns whether an error message is present.
     *
     * @return		true if error available
     */
    public boolean hasError() {
      return (error != null);
    }
  }

  /** the output. */
  protected List<Output> m_Outputs;

  /**
   * Initializes the tab handler
   *
   * @param owner the owning panel
   */
  public GraphicalActorProcessorHandler(FlowPanel owner) {
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
   * Adds the output component.
   *
   * @param title	the title
   * @param comp	the graphical component
   * @param error	the optional error, can be null
   */
  public void add(String title, Component comp, String error) {
    m_Outputs.add(new Output(title, comp, error));
    update(true);
  }

  /**
   * Returns the outputs.
   *
   * @return		the outputs
   */
  public List<Output> getOutputs() {
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
      if (m_Outputs.get(i).component == comp) {
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
   * Notifies the {@link GraphicalActorProcessorTab} instance of a change.
   *
   * @param show	whether to show the tab or leave as is
   */
  protected void update(boolean show) {
    Runnable	run;

    run = () -> {
      if (!getEditor().getTabs().isVisible(GraphicalActorProcessorTab.class) && show)
	getEditor().getTabs().setVisible(GraphicalActorProcessorTab.class, true, false);
      GraphicalActorProcessorTab tab = (GraphicalActorProcessorTab) getEditor().getTabs().getTab(GraphicalActorProcessorTab.class);
      if (tab != null)
	tab.update();
    };
    SwingUtilities.invokeLater(run);

    // close displays?
    run = () -> {
      if (!hasOutputs())
	getEditor().getTabs().setVisible(GraphicalActorProcessorTab.class, false, false);
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Gets called when the page changes.
   */
  public void display() {
    getEditor().getTabs().setVisible(
      GraphicalActorProcessorTab.class,
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
