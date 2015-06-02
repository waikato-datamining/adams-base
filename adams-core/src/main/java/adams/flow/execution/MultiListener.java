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
 * MultiListener.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.execution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashSet;

import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;

/**
 <!-- globalinfo-start -->
 * A meta-listener that executes all sub-listeners sequentially.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-listener &lt;adams.flow.execution.FlowExecutionListener&gt; [-listener ...] (property: subListeners)
 * &nbsp;&nbsp;&nbsp;The array of listeners to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiListener
  extends AbstractFlowExecutionListener
  implements GraphicalFlowExecutionListener {

  /** for serialization. */
  private static final long serialVersionUID = 2134802149210184280L;
  
  /** the listeners. */
  protected FlowExecutionListener[] m_Listeners;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A meta-listener that executes all sub-listeners sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "listener", "subListeners",
	    new FlowExecutionListener[0]);
  }

  /**
   * Sets the listeners to use.
   *
   * @param value	the listeners to use
   */
  public void setSubListeners(FlowExecutionListener[] value) {
    if (value != null) {
      m_Listeners = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": listener cannot be null!");
    }
  }

  /**
   * Returns the listeners in use.
   *
   * @return		the listeners
   */
  public FlowExecutionListener[] getSubListeners() {
    return m_Listeners;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String subListenersTipText() {
    return "The array of listeners to use.";
  }
  
  /**
   * Gets called when the flow execution starts.
   */
  @Override
  public void startListening() {
    super.startListening();
    for (FlowExecutionListener l: m_Listeners) {
      l.setOwner(getOwner());
      l.startListening();
    }
  }
  
  /**
   * Gets called before the actor receives the token.
   * 
   * @param actor	the actor that will receive the token
   * @param token	the token that the actor will receive
   */
  @Override
  public void preInput(Actor actor, Token token) {
    for (FlowExecutionListener l: m_Listeners)
      l.preInput(actor, token);
  }
  
  /**
   * Gets called after the actor received the token.
   * 
   * @param actor	the actor that received the token
   * @param token	the token that the actor received
   */
  @Override
  public void postInput(Actor actor) {
    for (FlowExecutionListener l: m_Listeners)
      l.postInput(actor);
  }
  
  /**
   * Gets called before the actor gets executed.
   * 
   * @param actor	the actor that will get executed
   */
  @Override
  public void preExecute(Actor actor) {
    for (FlowExecutionListener l: m_Listeners)
      l.preExecute(actor);
  }

  /**
   * Gets called after the actor was executed.
   * 
   * @param actor	the actor that was executed
   */
  @Override
  public void postExecute(Actor actor) {
    for (FlowExecutionListener l: m_Listeners)
      l.postExecute(actor);
  }
  
  /**
   * Gets called before a token gets obtained from the actor.
   * 
   * @param actor	the actor the token gets obtained from
   */
  @Override
  public void preOutput(Actor actor) {
    for (FlowExecutionListener l: m_Listeners)
      l.preOutput(actor);
  }
  
  /**
   * Gets called after a token was acquired from the actor.
   * 
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  @Override
  public void postOutput(Actor actor, Token token) {
    for (FlowExecutionListener l: m_Listeners)
      l.postOutput(actor, token);
  }

  /**
   * The title of this listener.
   * 
   * @return		the title
   */
  public String getListenerTitle() {
    return "Listeners";
  }
  
  /**
   * Creates a new tab title, ensures uniqueness.
   * 
   * @param titles	the titles so far
   * @param initial	the initial title
   * @return		the final title
   */
  protected String createTitle(HashSet<String> titles, String initial) {
    String	result;
    int		count;
    
    result = initial;
    count  = 1;
    
    while (titles.contains(result)) {
      count++;
      result = initial + " (" + count + ")";
    }
    
    titles.add(result);
    
    return result;
  }
  
  /**
   * Returns the panel to use.
   * 
   * @return		the panel, null if none available
   */
  public BasePanel newListenerPanel() {
    BasePanel		result;
    BaseTabbedPane	tabbed;
    boolean		found;
    BasePanel		subpanel;
    HashSet<String>	titles;
    
    result = new BasePanel();
    result.setLayout(new BorderLayout());
    tabbed = new BaseTabbedPane();
    result.add(tabbed, BorderLayout.CENTER);
    
    titles = new HashSet<String>();
    found  = false;
    for (FlowExecutionListener l: m_Listeners) {
      if (l instanceof GraphicalFlowExecutionListener) {
	subpanel = ((GraphicalFlowExecutionListener) l).newListenerPanel();
	if (subpanel != null) {
	  found = true;
	  tabbed.addTab(createTitle(titles, ((GraphicalFlowExecutionListener) l).getListenerTitle()), subpanel);
	}
      }
    }
    if (!found)
      return null;
    
    return result;
  }
  
  /**
   * Returns the default size for the frame.
   * 
   * @return		the frame size
   */
  public Dimension getDefaultFrameSize() {
    Dimension	result;
    Dimension	dim;
    
    result = new Dimension(0, 0);
    for (FlowExecutionListener l: m_Listeners) {
      if (l instanceof GraphicalFlowExecutionListener) {
	dim = ((GraphicalFlowExecutionListener) l).getDefaultFrameSize();
	if (dim.width > result.width)
	  result.width = dim.width;
	if (dim.height > result.height)
	  result.height = dim.height;
      }
    }
    
    // default size
    if (result.width == 0)
      result.width = 800;
    if (result.height == 0)
      result.height = 600;
    
    return result;
  }

  /**
   * Returns whether the frame should get disposed when the flow finishes.
   * <br><br>
   * Returns only true if all sub-listeners return true.
   *
   * @return		true if to dispose when flow finishes
   */
  public boolean getDisposeOnFinish() {
    boolean	result;

    result = true;

    for (FlowExecutionListener l: m_Listeners) {
      if (l instanceof GraphicalFlowExecutionListener) {
	if (!((GraphicalFlowExecutionListener) l).getDisposeOnFinish()) {
	  result = false;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Gets called when the flow execution ends.
   */
  @Override
  public void finishListening() {
    super.finishListening();
    for (FlowExecutionListener l: m_Listeners)
      l.finishListening();
  }
}
