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
 * AbstractDisplay.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoHelper;
import adams.flow.control.Flow;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.flow.FlowPanel;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.util.Hashtable;

/**
 * Ancestor for actors that display stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDisplay
  extends AbstractActor {

  /** for serialization. */
  private static final long serialVersionUID = 8175993838879683118L;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the current token. */
  protected transient Token m_InputToken;

  /** whether to use just the actor name or the full name as title. */
  protected boolean m_ShortTitle;
  
  /** the width of the dialog. */
  protected int m_Width;

  /** the height of the dialog. */
  protected int m_Height;

  /** the X position of the dialog. */
  protected int m_X;

  /** the Y position of the dialog. */
  protected int m_Y;

  /** whether to display the panel in the editor rather than in a separate frame. */
  protected boolean m_DisplayInEditor;
  
  /** the panel to display. */
  protected BasePanel m_Panel;

  /** the dialog that's being displayed. */
  protected BaseFrame m_Frame;

  /** whether to create the frame or just the panel. */
  protected boolean m_CreateFrame;
  
  /** whether the GUI is currently being updated. */
  protected Boolean m_Updating;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "short-title", "shortTitle",
	    getDefaultShortTitle());

    m_OptionManager.add(
	    "display-in-editor", "displayInEditor",
	    getDefaultDisplayInEditor());

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), -1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), -1, null);

    m_OptionManager.add(
	    "x", "x",
	    getDefaultX(), -3, null);

    m_OptionManager.add(
	    "y", "y",
	    getDefaultY(), -3, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    if (m_X == -1)
      value = "left";
    else if (m_X == -2)
      value = "center";
    else if (m_X == -3)
      value = "right";
    else
      value = "" + m_X;
    result = QuickInfoHelper.toString(this, "x", value, "X:");

    if (m_Y == -1)
      value = "top";
    else if (m_Y == -2)
      value = "center";
    else if (m_Y == -3)
      value = "bottom";
    else
      value = "" + m_Y;
    result += QuickInfoHelper.toString(this, "y", value, ", Y:");
    
    result += QuickInfoHelper.toString(this, "width", m_Width, ", W:");
    result += QuickInfoHelper.toString(this, "height", m_Height, ", H:");
    result += QuickInfoHelper.toString(this, "shortTitle", m_ShortTitle, "short title", ", ");
    
    return result;
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_InputToken != null)
      result.put(BACKUP_INPUT, m_InputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   * @see		#m_InputToken
   */
  public void input(Token token) {
    if (!m_Skip)
      m_InputToken = token;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();

    if (m_DisplayInEditor && deregisterInWrapUp())
      deregisterWithEditor();
    
    m_InputToken = null;
  }

  /**
   * Returns the default value for short title.
   * 
   * @return		the default
   */
  protected boolean getDefaultShortTitle() {
    return false;
  }

  /**
   * Returns the default value for displaying the panel in the editor
   * rather than in a separate frame.
   * 
   * @return		the default
   */
  protected boolean getDefaultDisplayInEditor() {
    return false;
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  protected int getDefaultX() {
    return -1;
  }

  /**
   * Returns the default Y position for the dialog.
   *
   * @return		the default Y position
   */
  protected int getDefaultY() {
    return -1;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets whether to use just the name of the actor or the full name.
   *
   * @param value 	if true just the name will get used, otherwise the full name
   */
  public void setShortTitle(boolean value) {
    m_ShortTitle = value;
    reset();
  }

  /**
   * Returns whether to use just the name of the actor or the full name.
   *
   * @return 		true if just the name used, otherwise full name
   */
  public boolean getShortTitle() {
    return m_ShortTitle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shortTitleTipText() {
    return "If enabled uses just the name for the title instead of the actor's full name.";
  }

  /**
   * Sets the width of the dialog.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the dialog.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the dialog.";
  }

  /**
   * Sets the height of the dialog.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the dialog.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the dialog.";
  }

  /**
   * Sets the X position of the dialog.
   *
   * @param value 	the X position
   */
  public void setX(int value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the currently set X position of the dialog.
   *
   * @return 		the X position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String xTipText() {
    return "The X position of the dialog (>=0: absolute, -1: left, -2: center, -3: right).";
  }

  /**
   * Sets the Y position of the dialog.
   *
   * @param value 	the Y position
   */
  public void setY(int value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the currently set Y position of the dialog.
   *
   * @return 		the Y position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String yTipText() {
    return "The Y position of the dialog (>=0: absolute, -1: top, -2: center, -3: bottom).";
  }

  /**
   * Sets whether to display the panel in the flow editor rather than
   * in a separate frame.
   *
   * @param value 	true if to display in editor
   */
  public void setDisplayInEditor(boolean value) {
    m_DisplayInEditor = value;
    if (m_DisplayInEditor)
      setCreateFrame(false);
    reset();
  }
  
  /**
   * Returns whether to display the panel in the flow editor rather than
   * in a separate frame.
   *
   * @return 		true if to display in editor
   */
  public boolean getDisplayInEditor() {
    return m_DisplayInEditor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayInEditorTipText() {
    return 
	"If enabled displays the panel in a tab in the flow editor rather "
	+ "than in a separate frame.";
  }

  /**
   * Resets the object. Removes graphical components as well.
   */
  @Override
  protected void reset() {
    super.reset();

    cleanUpGUI();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Panel       = null;
    m_Frame       = null;
    m_Updating    = false;
    m_CreateFrame = true;
  }

  /**
   * Clears the content of the panel.
   */
  public abstract void clearPanel();

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  protected abstract BasePanel newPanel();

  /**
   * Returns the panel.
   * 
   * @return		the panel, null if not available
   */
  public BasePanel getPanel() {
    return m_Panel;
  }
  
  /**
   * Creates a title for the dialog. Default implementation only returns
   * the full name of the actor.
   *
   * @return		the title of the dialog
   */
  protected String createTitle() {
    if (m_ShortTitle)
      return getName();
    else
      return getFullName().replace("\\.", ".");
  }

  /**
   * Sets whether to create the frame or just the panel.
   * 
   * @param value	if true, only the panel is created
   */
  public void setCreateFrame(boolean value) {
    m_CreateFrame = value;
  }
  
  /**
   * Returns whether the frame is created as well as the panel.
   * 
   * @return		true if the panel is created as well
   */
  public boolean getCreateFrame() {
    return m_CreateFrame;
  }
  
  /**
   * Hook method before the frame gets created.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param panel	the panel to display in the frame
   */
  protected void preCreateFrame(BasePanel panel) {
  }

  /**
   * Returns the default operation when closing the frame.
   *
   * @return		the operation
   */
  protected int getFrameDefaultCloseOperation() {
    if ((getRoot() != null) && (getRoot() instanceof Flow))
      return ((Flow) getRoot()).getDefaultCloseOperation();
    else
      return BaseDialog.HIDE_ON_CLOSE;
  }

  /**
   * Creates the actual frame.
   *
   * @param panel	the panel to display in the frame
   * @return		the created frame
   */
  protected BaseFrame doCreateFrame(BasePanel panel) {
    BaseFrame	result;
    ImageIcon	icon;

    result = new BaseFrame(createTitle());

    result.getContentPane().setLayout(new BorderLayout());
    result.getContentPane().add(panel, BorderLayout.CENTER);
    result.setDefaultCloseOperation(getFrameDefaultCloseOperation());
    result.setSize(ActorUtils.determineSize(result, m_X, m_Y, m_Width, m_Height));
    icon = GUIHelper.getIcon(getClass());
    if (icon != null)
      result.setIconImage(icon.getImage());
    else
      result.setIconImage(GUIHelper.getIcon("flow.gif").getImage());
    if (panel instanceof MenuBarProvider)
      result.setJMenuBar(((MenuBarProvider) panel).getMenuBar());
    else if (this instanceof MenuBarProvider)
      result.setJMenuBar(((MenuBarProvider) this).getMenuBar());
    result.setLocation(ActorUtils.determineLocation(result, m_X, m_Y));

    return result;
  }

  /**
   * Hook method after the frame got created.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param frame	the frame that got just created
   * @param panel	the panel displayed in the frame
   */
  protected void postCreateFrame(BaseFrame frame, BasePanel panel) {
  }

  /**
   * Creates and initializes a frame with the just created panel.
   *
   * @param panel	the panel to use in the frame
   * @return		the created frame
   */
  protected BaseFrame createFrame(BasePanel panel) {
    BaseFrame	result;

    preCreateFrame(panel);
    result = doCreateFrame(panel);
    postCreateFrame(result, panel);

    return result;
  }

  /**
   * Returns the frame.
   * 
   * @return		the frame, null if not available
   */
  public BaseFrame getFrame() {
    return m_Frame;
  }
  
  /**
   * Registers the actor with the flow editor, if possible.
   */
  protected void registerWithEditor() {
    if (getParentComponent() instanceof FlowPanel)
      ((FlowPanel) getParentComponent()).registerDisplay(getClass(), getName(), this);
  }
  
  /**
   * Deregisters the actor from the flow editor, if possible.
   */
  protected void deregisterWithEditor() {
    if (getParentComponent() instanceof FlowPanel)
      ((FlowPanel) getParentComponent()).deregisterDisplay(getClass(), getName());
  }
  
  /**
   * Returns whether to de-register in {@link #wrapUp()} or wait till 
   * {@link #cleanUpGUI()}.
   * <br><br>
   * Default returns false.
   * 
   * @return		true if to deregister already in {@link #wrapUp()}
   */
  protected boolean deregisterInWrapUp() {
    return false;
  }

  /**
   * Returns a runnable that displays frame, etc.
   * Must call notifyAll() on the m_Self object and set m_Updating to false.
   *
   * @return		the runnable
   * @see		#m_Updating
   */
  protected abstract Runnable newDisplayRunnable();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    Runnable	runnable;

    if (!isHeadless()) {
      if (m_Panel == null) {
	m_Panel = newPanel();
	if (m_CreateFrame)
	  m_Frame = createFrame(m_Panel);
	else if (m_DisplayInEditor)
	  registerWithEditor();
      }

      m_Updating = true;
      runnable   = newDisplayRunnable();

      synchronized(m_Self) {
	SwingUtilities.invokeLater(runnable);
	try {
	  m_Self.wait();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return null;
  }

  /**
   * Removes all graphical components.
   */
  protected void cleanUpGUI() {
    if (m_Panel != null) {
      if (m_Panel instanceof CleanUpHandler)
	((CleanUpHandler) m_Panel).cleanUp();
      m_Panel = null;
    }
    
    if (m_Executed && m_DisplayInEditor)
      deregisterWithEditor();
    
    if (m_Frame != null) {
      m_Frame.setVisible(false);
      m_Frame.dispose();
      m_Frame = null;
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    try {
      synchronized(m_Self) {
	m_Self.notifyAll();
      }
    }
    catch (Exception e) {
      // ignored
    }

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    Runnable	runnable;

    super.cleanUp();

    runnable = new Runnable() {
      public void run() {
	cleanUpGUI();
      }
    };
    SwingUtilities.invokeLater(runnable);
  }
}
