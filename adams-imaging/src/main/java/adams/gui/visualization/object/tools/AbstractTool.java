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
 * AbstractTool.java
 * Copyright (C) 2023-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.tools;

import adams.core.logging.CustomLoggingLevelObject;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Cursors;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.visualization.object.CanvasPanel;

import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Ancestor for tools.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTool
  extends CustomLoggingLevelObject
  implements Tool {

  private static final long serialVersionUID = -6782161796343153566L;

  /** the canvas panel to operate on. */
  protected CanvasPanel m_CanvasPanel;

  /** the mouse listener. */
  protected ToolMouseAdapter m_Listener;

  /** the mouse motion listener. */
  protected ToolMouseMotionAdapter m_MotionListener;

  /** the key listener. */
  protected ToolKeyAdapter m_KeyListener;

  /** the options panel. */
  protected BasePanel m_PanelOptions;

  /** the full option panel. */
  protected BasePanel m_PanelFullOptions;

  /** whether the settings are currently modified. */
  protected boolean m_Modified;

  /**
   * Initializes the tool.
   */
  protected AbstractTool() {
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_CanvasPanel    = null;
    m_PanelOptions   = null;
    m_Listener       = null;
    m_MotionListener = null;
    m_KeyListener    = null;
    m_Modified       = false;
  }

  /**
   * Sets the panel to use.
   *
   * @param value 	the panel
   */
  @Override
  public void setCanvas(CanvasPanel value) {
    m_CanvasPanel = value;
    update();
  }

  /**
   * Returns the currently set canvas panel.
   *
   * @return		the panel, null if none set
   */
  @Override
  public CanvasPanel getCanvas() {
    return m_CanvasPanel;
  }

  /**
   * Checks whether an image is currently available.
   *
   * @return		true if available
   */
  @Override
  public boolean hasImage() {
    return (getImage() != null);
  }

  /**
   * Returns the current image.
   *
   * @return		the image or null if none available
   */
  @Override
  public BufferedImage getImage() {
    if (m_CanvasPanel != null)
      return m_CanvasPanel.getImage();
    else
      return null;
  }

  /**
   * Returns the current zoom.
   *
   * @return		the zoom (1.0 = 100%)
   */
  @Override
  public double getZoom() {
    if (m_CanvasPanel == null)
      return 1.0;
    else
      return m_CanvasPanel.getZoom();
  }

  /**
   * Creates the mouse cursor to use.
   *
   * @return		the cursor
   */
  protected abstract Cursor createCursor();

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  public Cursor getCursor() {
    if (!hasImage())
      return Cursors.disabled();
    else
      return createCursor();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return		the listener, null if not applicable
   */
  protected abstract ToolMouseAdapter createMouseListener();

  /**
   * Returns the mouse listener to use.
   *
   * @return		the listener
   */
  @Override
  public ToolMouseAdapter getMouseListener() {
    if (m_Listener == null) {
      m_Listener = createMouseListener();
      if (m_Listener == null)
	m_Listener = new ToolMouseAdapter(this);
    }
    return m_Listener;
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  protected abstract ToolMouseMotionAdapter createMouseMotionListener();

  /**
   * Returns the mouse motion listener to use.
   *
   * @return		the listener
   */
  @Override
  public ToolMouseMotionAdapter getMouseMotionListener() {
    if (m_MotionListener == null) {
      m_MotionListener = createMouseMotionListener();
      if (m_MotionListener == null)
	m_MotionListener = new ToolMouseMotionAdapter(this);
    }
    return m_MotionListener;
  }

  /**
   * Creates the key listener to use.
   * <br>
   * Default implementation just returns null.
   *
   * @return		the listener, null if not applicable
   */
  protected ToolKeyAdapter createKeyListener() {
    return null;
  }

  /**
   * Returns the mouse listener to use.
   *
   * @return		the listener
   */
  @Override
  public ToolKeyAdapter getKeyListener() {
    if (m_KeyListener == null) {
      m_KeyListener = createKeyListener();
      if (m_KeyListener == null)
	m_KeyListener = new ToolKeyAdapter(this);
    }
    return m_KeyListener;
  }

  /**
   * Checks the parameters before applying them.
   * <br>
   * Default implementation just returns null.
   *
   * @return		null if checks passed, otherwise error message (gets displayed in GUI)
   */
  protected String checkBeforeApply() {
    return null;
  }

  /**
   * Applies the settings.
   */
  protected abstract void doApply();

  /**
   * Applies the settings (if valid).
   *
   * @return		true if applied
   * @see		#doApply()
   * @see		#checkBeforeApply()
   */
  public boolean apply(BaseFlatButton button) {
    String		msg;

    msg = checkBeforeApply();
    if (msg != null) {
      GUIHelper.showErrorMessage(getCanvas(), "Failed to apply settings:\n" + msg);
      return false;
    }

    setApplyButtonState(button, false);
    doApply();
    getCanvas().setCursor(getCursor());
    return true;
  }

  /**
   * Returns the icon name for the unmodified state.
   *
   * @return		the image name
   */
  protected String getUnmodifiedIcon() {
    return "validate.png";
  }

  /**
   * Returns the icon name for the modified state.
   *
   * @return		the image name
   */
  protected String getModifiedIcon() {
    return "validate_blue.png";
  }

  /**
   * Generates the apply button.
   *
   * @return		the button
   */
  protected BaseFlatButton createApplyButton() {
    BaseFlatButton 	result;

    result = new BaseFlatButton(ImageManager.getIcon(getUnmodifiedIcon()));
    result.setToolTipText("Apply current values");
    result.addActionListener((ActionEvent e) -> apply((BaseFlatButton) e.getSource()));

    return result;
  }

  /**
   * Sets the state of the "Apply" button according to the modified flag.
   *
   * @param button	the button to update
   * @param modified	whether applying needs doing or not
   */
  protected void setApplyButtonState(BaseFlatButton button, boolean modified) {
    m_Modified = modified;
    if (modified)
      button.setIcon(ImageManager.getIcon(getModifiedIcon()));
    else
      button.setIcon(ImageManager.getIcon(getUnmodifiedIcon()));
  }

  /**
   * Returns whether the settings are currently modified.
   *
   * @return		true if modified
   */
  @Override
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return		the options panel
   */
  protected abstract BasePanel createOptionPanel();

  /**
   * Returns the panel for setting the options.
   *
   * @return		the options panel
   */
  @Override
  public BasePanel getOptionPanel() {
    BaseTextArea	textArea;
    String		info;
    BaseTabbedPane	tabbedPane;

    if (m_PanelOptions == null) {
      m_PanelOptions = createOptionPanel();
      m_PanelFullOptions = new BasePanel(new BorderLayout(5, 5));
      info = globalInfo();
      if (info != null) {
	textArea = new BaseTextArea();
	textArea.setEditable(false);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setColumns(20);
	textArea.setRows(4);
	textArea.setText(globalInfo());
	textArea.setCaretPosition(0);
	tabbedPane = new BaseTabbedPane(BaseTabbedPane.SCROLL_TAB_LAYOUT);
	tabbedPane.addTab("Options", m_PanelOptions);
	tabbedPane.addTab("Help", textArea);
	m_PanelFullOptions.add(tabbedPane, BorderLayout.CENTER);
      }
      else {
	m_PanelFullOptions.add(m_PanelOptions, BorderLayout.CENTER);
      }
      m_PanelFullOptions.setBorder(BorderFactory.createTitledBorder(getName()));
    }
    return m_PanelFullOptions;
  }

  /**
   * Gets called to activate the tool.
   * <br>
   * Default implementation repaints the canvas.
   */
  @Override
  public void activate() {
    getCanvas().repaint();
  }

  /**
   * Gets called to deactivate the tool.
   * <br>
   * Default implementation does nothing.
   */
  public void deactivate() {
  }

  /**
   * Called when image or annotations change.
   */
  @Override
  public void update() {
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
  }
}
