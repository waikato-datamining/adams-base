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
 * SerializedObjectPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.gui.core.BasePanel;

/**
 * Panel for displaying a serialized object.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializedObjectPanel
  extends BasePanel 
  implements ActionListener {

  /** for serialization. */
  private static final long serialVersionUID = -4489513186403584128L;

  /** the session file with the preferred viewers. */
  public final static String SESSION_FILE = "PreviewBrowserSerializedFileViewers.props";

  /** the prefix for the preferred viewer keys in the props file. */
  public final static String PREFIX_PREFERRED_VIEWER = "PreferredViewer";

  /** the properties. */
  protected static Properties m_Properties;

  /** the available viewers. */
  protected static SerializedObjectViewer[] m_Viewers;

  /** panel for the actual view. */
  protected JPanel m_PanelView;
  
  /** the panel for the combobox with available viewers. */
  protected JPanel m_PanelCombobox;
  
  /** the combobox with the viewers. */
  protected JComboBox m_ComboBoxViewers;

  /** the currently displayed object. */
  protected Object m_Current;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Current = null;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_PanelView = new JPanel(new BorderLayout());
    add(m_PanelView, BorderLayout.CENTER);
    
    m_PanelCombobox = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(m_PanelCombobox, BorderLayout.SOUTH);
    
    m_ComboBoxViewers = new JComboBox();
    m_PanelCombobox.add(new JLabel("Preferred viewer"));
    m_PanelCombobox.add(m_ComboBoxViewers);
  }

  /**
   * Sets the object to display.
   * 
   * @param value	the object
   */
  public void setCurrent(Object value) {
    m_Current = value;
    update();
  }
  
  /**
   * Returns the currently displayed object.
   * 
   * @return		the current object, null if none set
   */
  public Object getCurrent() {
    return m_Current;
  }
  
  /**
   * Updates the view and available viewers.
   */
  protected void update() {
    SerializedObjectViewer[]		viewers;
    ArrayList<SerializedObjectViewer>	list;
    SerializedObjectViewer		preferred;
    int					index;

    m_PanelView.removeAll();
    m_PanelCombobox.setVisible(false);
    
    if (m_Current == null)
      return;
    
    viewers = getViewers();
    list    = new ArrayList<SerializedObjectViewer>();
    for (SerializedObjectViewer viewer: viewers) {
      if (viewer instanceof DefaultSerializedObjectViewer)
	continue;
      if (viewer.handles(m_Current))
	list.add(viewer);
    }
    
    if (list.size() == 0) {
      updateView(new DefaultSerializedObjectViewer());
    }
    else {
      m_PanelCombobox.setVisible(true);
      list.add(0, new DefaultSerializedObjectViewer());
      m_ComboBoxViewers.setModel(new DefaultComboBoxModel(list.toArray(new SerializedObjectViewer[list.size()])));
      m_ComboBoxViewers.addActionListener(this);
      // select preferred viewer
      preferred = getPreferredViewer(m_Current.getClass());
      if (preferred == null)
	preferred = new DefaultSerializedObjectViewer();
      index = list.indexOf(preferred);
      m_ComboBoxViewers.setSelectedIndex(index);
      updateView(list.get(index));
    }
  }
  
  /**
   * Updates the view.
   * 
   * @param viewer	the viewer to create the view
   */
  protected void updateView(SerializedObjectViewer viewer) {
    m_PanelView.removeAll();
    m_PanelView.add(viewer.getPreview(m_Current), BorderLayout.CENTER);
  }
  
  /**
   * Returns all available viewers.
   * 
   * @return		the viewers
   */
  protected synchronized static SerializedObjectViewer[] getViewers() {
    String[]				classnames;
    ArrayList<SerializedObjectViewer>	viewers;
    Class				cls;
    
    if (m_Viewers == null) {
      classnames = AbstractSerializedObjectViewer.getViewers();
      viewers    = new ArrayList<SerializedObjectViewer>();
      for (String classname: classnames) {
	try {
	  cls = Class.forName(classname);
	  viewers.add((SerializedObjectViewer) cls.newInstance());
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate viewer '" + classname + "':");
	  e.printStackTrace();
	}
      }
      m_Viewers = viewers.toArray(new SerializedObjectViewer[viewers.size()]);
    }
    
    return m_Viewers;
  }

  /**
   * Returns the preferred viewer.
   *
   * @param cls		the class to get the preferred viewer for
   * @return		the preferred viewer
   */
  protected SerializedObjectViewer getPreferredViewer(Class cls) {
    SerializedObjectViewer	result;
    Properties			props;
    String			viewer;

    result = null;

    props = getProperties();
    if (props.hasKey(PREFIX_PREFERRED_VIEWER + "-" + cls.getName())) {
      viewer = props.getProperty(PREFIX_PREFERRED_VIEWER + "-" + cls.getName());
      try {
	result = (SerializedObjectViewer) OptionUtils.forCommandLine(SerializedObjectViewer.class, viewer);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate viewer: " + viewer);
	e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Updates the preferred viewer.
   * 
   * @param cls		the class of the current object
   * @param combobox	the current state of viewers
   */
  protected void updatePreferredViewer(Class cls, JComboBox combobox) {
    SerializedObjectViewer	viewer;
    Properties			props;
    String			filename;

    props = getProperties();
    if (combobox.getSelectedIndex() < 0)
      viewer = (SerializedObjectViewer) combobox.getItemAt(0);
    else
      viewer = (SerializedObjectViewer) combobox.getSelectedItem();

    // update props
    props.setProperty(PREFIX_PREFERRED_VIEWER + "-" + cls.getName(), OptionUtils.getCommandLine(viewer));

    // save props
    filename = Environment.getInstance().getHome() + File.separator + SESSION_FILE;
    if (!props.save(filename))
      System.err.println("Failed to save properties to '" + filename + "'!");
  }
  
  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null) {
      m_Properties = new Properties();
      m_Properties.load(Environment.getInstance().getHome() + File.separator + SESSION_FILE);
    }

    return m_Properties;
  }

  /**
   * For changes in the viewer combobox.
   * 
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (m_ComboBoxViewers.getSelectedIndex() == -1)
      return;
    SerializedObjectViewer viewer = (SerializedObjectViewer) m_ComboBoxViewers.getSelectedItem();
    updateView(viewer);
    updatePreferredViewer(m_Current.getClass(), m_ComboBoxViewers);
  }
}
