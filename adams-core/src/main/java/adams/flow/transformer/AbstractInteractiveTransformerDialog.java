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
 * AbstractInteractiveTransformerDialog.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoHelper;
import adams.flow.core.ActorUtils;
import adams.flow.core.InteractiveActor;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;

/**
 * Ancestor for graphical actors that are interactive.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInteractiveTransformerDialog
  extends AbstractTransformer
  implements InteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = -9002360111241208309L;

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

  /** the panel to display. */
  protected BasePanel m_Panel;

  /** the dialog that's being displayed. */
  protected BaseDialog m_Dialog;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "short-title", "shortTitle",
	    false);

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), 1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), 1, null);

    m_OptionManager.add(
	    "x", "x",
	    getDefaultX(), -3, null);

    m_OptionManager.add(
	    "y", "y",
	    getDefaultY(), -3, null);

    m_OptionManager.add(
	    "stop-if-canceled", "stopFlowIfCanceled",
	    false);

    m_OptionManager.add(
	    "custom-stop-message", "customStopMessage",
	    "");
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

    return result;
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
   * Sets whether to stop the flow if dialog canceled.
   *
   * @param value	if true flow gets stopped if dialog canceled
   */
  @Override
  public void setStopFlowIfCanceled(boolean value) {
    m_StopFlowIfCanceled = value;
    reset();
  }

  /**
   * Returns whether to stop the flow if dialog canceled.
   *
   * @return 		true if the flow gets stopped if dialog canceled
   */
  @Override
  public boolean getStopFlowIfCanceled() {
    return m_StopFlowIfCanceled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  @Override
  public String stopFlowIfCanceledTipText() {
    return "If enabled, the flow gets stopped in case the user cancels the dialog.";
  }

  /**
   * Sets the custom message to use when stopping the flow.
   *
   * @param value	the stop message
   */
  @Override
  public void setCustomStopMessage(String value) {
    m_CustomStopMessage = value;
    reset();
  }

  /**
   * Returns the custom message to use when stopping the flow.
   *
   * @return		the stop message
   */
  @Override
  public String getCustomStopMessage() {
    return m_CustomStopMessage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  @Override
  public String customStopMessageTipText() {
    return
        "The custom stop message to use in case a user cancelation stops the "
      + "flow (default is the full name of the actor)";
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

    m_Panel = null;
    m_Dialog = null;
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
   * Hook method before the dialog gets created.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param panel	the panel to display in the dialog
   */
  protected void preCreateDialog(BasePanel panel) {
  }

  /**
   * Creates the actual dialog.
   *
   * @param panel	the panel to display in the dialog
   * @return		the created dialog
   */
  protected BaseDialog doCreateDialog(BasePanel panel) {
    BaseDialog			result;
    ImageIcon			icon;
    int				width;
    int				height;
    GraphicsConfiguration	gc;

    gc = GUIHelper.getGraphicsConfiguration(getParentComponent());

    result = new BaseDialog(null, createTitle(), ModalityType.DOCUMENT_MODAL);
    result.setLocation(gc.getBounds().x, gc.getBounds().y);

    // limit width/height to screen size (taking X/Y into account)
    width  = Math.min(GUIHelper.getScreenBounds(gc).width - (m_X >= 0 ? m_X : 0), getWidth());
    height = Math.min(GUIHelper.getScreenBounds(gc).height - (m_Y >= 0 ? m_Y : 0), getHeight());

    result.getContentPane().setLayout(new BorderLayout());
    result.getContentPane().add(panel, BorderLayout.CENTER);
    result.setDefaultCloseOperation(BaseDialog.HIDE_ON_CLOSE);
    result.setSize(width, height);
    result.setLocationRelativeTo(getParentComponent());
    icon = GUIHelper.getIcon(getClass());
    if (icon != null)
      result.setIconImage(icon.getImage());
    else
      result.setIconImage(GUIHelper.getIcon("flow.gif").getImage());
    if (panel instanceof MenuBarProvider)
      result.setJMenuBar(((MenuBarProvider) panel).getMenuBar());
    else if (this instanceof MenuBarProvider)
      result.setJMenuBar(((MenuBarProvider) this).getMenuBar());
    result.setLocation(ActorUtils.determineLocation(gc, new Dimension(width, height), m_X, m_Y));

    return result;
  }

  /**
   * Hook method after the dialog got created.
   * <br><br>
   * Default implementation does nothing.
   *
   * @param dialog	the dialog that got just created
   * @param panel	the panel displayed in the frame
   */
  protected void postCreateDialog(BaseDialog dialog, BasePanel panel) {
  }

  /**
   * Creates and initializes a dialog with the just created panel.
   *
   * @param panel	the panel to use in the dialog
   * @return		the created dialog
   */
  protected BaseDialog createDialog(BasePanel panel) {
    BaseDialog	result;

    preCreateDialog(panel);
    result = doCreateDialog(panel);
    postCreateDialog(result, panel);

    return result;
  }

  /**
   * Returns the dialog.
   * 
   * @return		the dialog, null if not available
   */
  public BaseDialog getDialog() {
    return m_Dialog;
  }

  /**
   * Performs the interaction with the user.
   * <br><br>
   * Default implementation simply displays the dialog and returns always true.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    m_Dialog.setVisible(true);
    return true;
  }

  /**
   * Returns whether headless interaction is supported.
   *
   * @return		true if interaction in headless environment is possible
   */
  public boolean supportsHeadlessInteraction() {
    return false;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteractHeadless() {
    return true;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    result = null;
    
    if (!isHeadless()) {
      if (m_Panel == null) {
	m_Panel = newPanel();
	m_Dialog = createDialog(m_Panel);
      }
      
      if (!doInteract()) {
	if (m_StopFlowIfCanceled) {
	  if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
	    stopExecution("Flow canceled: " + getFullName());
	  else
	    stopExecution(m_CustomStopMessage);
	}
	else {
	  result = "User cancelled dialog!";
	}
      }
    }
    else if (supportsHeadlessInteraction()) {
      if (!doInteractHeadless()) {
	if (m_StopFlowIfCanceled) {
	  if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
	    stopExecution("Flow canceled: " + getFullName());
	  else
	    stopExecution(m_CustomStopMessage);
	}
	else {
	  result = "User cancelled dialog!";
	}
      }
    }

    return result;
  }

  /**
   * Removes all graphical components.
   */
  protected void cleanUpGUI() {
    if (m_Dialog != null) {
      if (m_Panel instanceof CleanUpHandler)
	((CleanUpHandler) m_Panel).cleanUp();

      m_Dialog.setVisible(false);
      m_Dialog.dispose();

      m_Dialog = null;
      m_Panel  = null;
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Dialog != null) {
      if (m_Dialog.isVisible())
	m_Dialog.setVisible(false);
    }

    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    SwingUtilities.invokeLater(() -> cleanUpGUI());
  }
}
