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

import java.awt.BorderLayout;

import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;

/**
 * Helper class for flow execution listening related stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListenerUtils {

  /**
   * Creates a frame for the graphical flow execution listener (if required).
   * 
   * @param supporter	the supporter to create the frame for
   * @return		the frame, null if none could be created (eg if no graphical listeners)
   */
  public static BaseFrame createFrame(FlowExecutionListeningSupporter supporter) {
    BaseFrame				result;
    GraphicalFlowExecutionListener	graphical;
    
    result = null;
    
    if (supporter.getFlowExecutionListener() instanceof GraphicalFlowExecutionListener) {
      graphical = (GraphicalFlowExecutionListener) supporter.getFlowExecutionListener();
      result    = new BaseFrame();
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
