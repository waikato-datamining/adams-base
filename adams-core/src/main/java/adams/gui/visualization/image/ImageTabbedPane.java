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
 * ImageTabbedPane.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.SwingUtilities;

import adams.core.Properties;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;

/**
 * Specialized {@link BaseTabbedPane} for managing images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageTabbedPane
  extends BaseTabbedPane {

  /** for serialization. */
  private static final long serialVersionUID = 4949565559707097445L;
  
  /** the owner. */
  protected ImageViewerPanel m_Owner;
  
  /**
   * Initializes the tabbed pane.
   * 
   * @param owner	the viewer this pane belongs to
   */
  public ImageTabbedPane(ImageViewerPanel owner) {
    super();
    m_Owner = owner;
  }
  
  /**
   * Returns the owner.
   * 
   * @return		the owner
   */
  public ImageViewerPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the image panel in the currently selected tab.
   *
   * @return		the image panel, null if none available
   */
  public ImagePanel getCurrentPanel() {
    return getPanelAt(getSelectedIndex());
  }

  /**
   * Returns the image panel of the specified tab.
   *
   * @param index	the tab index
   * @return		the image panel, null if none available
   */
  public ImagePanel getPanelAt(int index) {
    if ((index < 0) || (index >= getTabCount()))
      return null;
    else
      return (ImagePanel) getComponentAt(index);
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public ImagePanel[] getAllPanels() {
    ImagePanel[]	result;
    int			i;
    
    result = new ImagePanel[getTabCount()];
    for (i = 0; i < getTabCount(); i++)
      result[i] = (ImagePanel) getComponentAt(i);
    
    return result;
  }

  /**
   * Returns the underlying image.
   *
   * @return		the current image, can be null
   */
  public BufferedImage getCurrentImage() {
    return getImageAt(getSelectedIndex());
  }

  /**
   * Returns the underlying image.
   *
   * @param index	the tab index
   * @return		the current image, can be null
   */
  public BufferedImage getImageAt(int index) {
    BufferedImage	result;
    ImagePanel		panel;

    result = null;
    panel  = getPanelAt(index);
    if (panel != null)
      result = panel.getCurrentImage();

    return result;
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return getFileAt(getSelectedIndex());
  }

  /**
   * Returns the current filename.
   *
   * @param index	the tab index
   * @return		the current filename, can be null
   */
  public File getFileAt(int index) {
    File	result;
    ImagePanel	panel;

    result = null;
    panel  = getPanelAt(index);
    if (panel != null)
      result = panel.getCurrentFile();

    return result;
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the flow or discarded the changes.
   *
   * @param panel	the panel to check
   * @return		true if safe to proceed
   */
  protected boolean checkForModified(ImagePanel panel) {
    boolean 	result;
    int		retVal;
    String	msg;

    if (panel == null)
      return true;

    result = !panel.isModified();

    if (!result) {
      if (panel.getCurrentFile() == null)
	msg = "Image not saved - save?";
      else
	msg = "Image not saved - save?\n" + panel.getCurrentFile();
      retVal = GUIHelper.showConfirmMessage(this, msg, "Image not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  getOwner().saveAs();
	  result = !panel.isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Hook method that checks whether the specified tab can really be closed
   * with a click of the middle mouse button.
   * <p/>
   * Checks modified state.
   *
   * @param index	the tab index
   * @return		true if tab can be closed
   * @see		#getCloseTabsWithMiddelMouseButton()
   */
  @Override
  protected boolean canCloseTabWithMiddleMouseButton(int index) {
    return checkForModified((ImagePanel) getComponentAt(index));
  };

  /**
   * Updates the title of all tabs, takes modified state into account.
   */
  public void updateTabTitles() {
    int		i;
    
    for (i = 0; i < getTabCount(); i++)
      updateTabTitle(i);
  }

  /**
   * Updates the title of the current tab, takes modified state into account.
   */
  public void updateCurrentTabTitle() {
    updateTabTitle(getSelectedIndex());
  }

  /**
   * Updates the title of the specified tab, takes modified state into account.
   * 
   * @param index	the index of the tab
   */
  public void updateTabTitle(int index) {
    String	title;
    boolean	modified;

    if (index >= 0) {
      title   = getTitleAt(index);
      modified = title.startsWith("*");
      if (modified)
	title = title.substring(1);
      if (getPanelAt(index).isModified() != modified) {
	if (getPanelAt(index).isModified())
	  title = "*" + title;
	setTitleAt(index, title);
      }
    }
  }

  /**
   * Loads the specified file in a new panel.
   *
   * @param file	the file to load
   * @return		true if successfully loaded
   */
  public boolean load(File file) {
    final ImagePanel	panel;
    final double	zoom;
    Properties		props;
    Runnable		run;

    panel = new ImagePanel();
    panel.setSelectionEnabled(true);
    if (!panel.load(file)) {
      GUIHelper.showErrorMessage(
	  this, "Failed to open image '" + file + "'!");
      return false;
    }
    else {
      props = ImageViewerPanel.getProperties();
      panel.setShowProperties(props.getBoolean("ShowProperties", true));
      panel.setShowLog(props.getBoolean("ShowLog", true));
      panel.getSplitPane().setDividerLocation(props.getInteger("DividerLocation", 500));
      panel.getPropertiesScrollPane().setPreferredSize(new Dimension(props.getInteger("PropertiesWidth", 300), 100));
      addTab(file.getName(), panel);
      setSelectedComponent(panel);
      zoom = props.getDouble("ZoomLevel") / 100;
      run  = new Runnable() {
	@Override
	public void run() {
	  panel.setScale(zoom);
	}
      };
      SwingUtilities.invokeLater(run);
      return true;
    }
  }
}
