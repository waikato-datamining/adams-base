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
 * ListenerUtils.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.flow.control.Flow;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowTreeHandler;
import adams.gui.flow.tree.Tree;
import adams.gui.goe.FlowHelper;

import java.awt.BorderLayout;
import java.awt.Container;

/**
 * Helper class for flow execution listening related stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListenerUtils {

  /**
   * Specialized frame for graphical listeners.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ListenerFrame
    extends BaseFrame
    implements FlowTreeHandler {

    private static final long serialVersionUID = 8433088861525467980L;

    /** the listener this frame is for. */
    protected GraphicalFlowExecutionListener m_Listener;

    /**
     * Initializes the frame with no title.
     */
    public ListenerFrame() {
      super();
    }

    /**
     * Initializes the frame with the given title.
     *
     * @param title	the title of the frame
     */
    public ListenerFrame(String title) {
      super(title);
    }

    /**
     * Sets the listener.
     *
     * @param value	the listener
     */
    public void setListener(GraphicalFlowExecutionListener value) {
      m_Listener = value;
    }

    /**
     * Returns the listener.
     *
     * @return		the listener
     */
    public GraphicalFlowExecutionListener getListener() {
      return m_Listener;
    }

    /**
     * Returns the tree.
     *
     * @return		the tree
     */
    @Override
    public Tree getTree() {
      Tree	result;
      Flow 	flow;

      result = null;

      if (m_Listener != null) {
	flow = m_Listener.getOwner();
	if (flow.getParentComponent() != null) {
	  if (flow.getParentComponent() instanceof Container)
	  result = FlowHelper.getTree((Container) flow.getParentComponent());
	}
      }

      return result;
    }
  }

  /**
   * Creates a frame for the graphical flow execution listener (if required).
   * 
   * @param supporter	the supporter to create the frame for
   * @return		the frame, null if none could be created (eg if no graphical listeners)
   */
  public static BaseFrame createFrame(FlowExecutionListeningSupporter supporter) {
    ListenerFrame			result;
    GraphicalFlowExecutionListener	graphical;
    
    result = null;
    
    if (supporter.getFlowExecutionListener() instanceof GraphicalFlowExecutionListener) {
      graphical = (GraphicalFlowExecutionListener) supporter.getFlowExecutionListener();
      result    = new ListenerFrame();
      result.setListener(graphical);
      result.setTitle(graphical.getListenerTitle());
      result.setDefaultCloseOperation(BaseFrame.HIDE_ON_CLOSE);
      result.getContentPane().setLayout(new BorderLayout());
      result.getContentPane().add(graphical.newListenerPanel(), BorderLayout.CENTER);
      result.setSize(graphical.getDefaultFrameSize());
      GUIHelper.setSizeAndLocation(result, GUIHelper.calcTopPosition(result, -2), GUIHelper.calcLeftPosition(result, -2));
      result.setVisible(true);
    }
    
    return result;
  }
}
