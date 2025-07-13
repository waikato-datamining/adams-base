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
 * AbstractToolWithParameterPanel.java
 * Copyright (C) 2023-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.tool;

import adams.core.Utils;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextComponent;
import adams.gui.core.ParameterPanel;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Tool that uses a {@link ParameterPanel} for its parameters.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractToolWithParameterPanel
  extends AbstractTool
  implements CustomizableTool {

  private static final long serialVersionUID = -3238804649373495561L;

  /** the apply button. */
  protected BaseFlatButton m_ButtonApply;

  /** the parameter panel. */
  protected ParameterPanel m_ParameterPanel;

  /** whether to ignore the update to the options. */
  protected boolean m_IgnoreOptionsUpdate;

  /** initial settings. */
  protected Map<String,Object> m_InitialOptions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_InitialOptions = null;
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  protected abstract void addOptions(ParameterPanel paramPanel);

  /**
   * Creates the panel for setting the options.
   *
   * @return the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel		result;
    JPanel 		panel;
    JPanel		panel2;
    JPanel 		panelButton;

    result = new BasePanel();

    m_ButtonApply = createApplyButton();

    panel = new JPanel(new BorderLayout());
    result.add(panel, BorderLayout.NORTH);

    m_ParameterPanel = new ParameterPanel();
    addOptions(m_ParameterPanel);
    panel.add(m_ParameterPanel, BorderLayout.NORTH);

    panel2 = new JPanel(new BorderLayout());
    panel.add(panel2, BorderLayout.CENTER);

    panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelButton.add(m_ButtonApply);
    setApplyButtonState(m_ButtonApply, false);
    panel2.add(panelButton, BorderLayout.SOUTH);

    return result;
  }

  /**
   * Applies the settings (if valid).
   *
   * @return		true if applied
   * @see		#doApply()
   * @see		#checkBeforeApply()
   */
  public boolean apply(BaseFlatButton button) {
    boolean	result;

    result = super.apply(button);
    if (result) {
      if (!m_IgnoreOptionsUpdate)
	m_PanelCanvas.getOwner().toolOptionsUpdated();
    }

    return result;
  }

  /**
   * Applies the options.
   */
  @Override
  public void applyOptions() {
    if (m_ButtonApply != null) {
      if (apply(m_ButtonApply)) {
	if (!m_IgnoreOptionsUpdate)
	  m_PanelCanvas.getOwner().toolOptionsUpdated();
      }
    }
  }

  /**
   * Applies the options quietly, i.e., doesn't trigger an event.
   */
  @Override
  public void applyOptionsQuietly() {
    m_IgnoreOptionsUpdate = true;
    applyOptions();
    m_IgnoreOptionsUpdate = false;
  }

  /**
   * Hook method for reacting to custom component types, updating their setting.
   *
   * @param index	the parameter index
   * @param label	the current label
   * @param comp	the parameter to update
   * @param value	the value to update with
   * @return		true if successfully updated
   */
  protected boolean setOption(int index, String label, Component comp, Object value) {
    return false;
  }

  /**
   * Sets the options from the map.
   *
   * @param value	the options to use
   */
  protected void updateOptions(Map<String,Object> value) {
    int			i;
    String		label;
    Component 		comp;
    boolean		handled;

    if (m_ParameterPanel != null) {
      for (i = 0; i < m_ParameterPanel.getParameterCount(); i++) {
	label   = m_ParameterPanel.getLabel(i).getText();
	comp    = m_ParameterPanel.getParameter(i);
	handled = false;
	if (value.containsKey(label)) {
	  try {
	    if (comp instanceof JTextComponent) {
	      ((JTextComponent) comp).setText("" + value.get(label));
	      handled = true;
	    }
	    else if (comp instanceof BaseTextComponent) {
	      ((BaseTextComponent) comp).setText("" + value.get(label));
	      handled = true;
	    }
	    else if (comp instanceof JComboBox) {
	      ((JComboBox) comp).setSelectedIndex(((Number) value.get(label)).intValue());
	      handled = true;
	    }
	    else if (comp instanceof JCheckBox) {
	      ((JCheckBox) comp).setSelected((boolean) value.get(label));
	      handled = true;
	    }
	    else if (comp instanceof JRadioButton) {
	      ((JRadioButton) comp).setSelected((boolean) value.get(label));
	      handled = true;
	    }
	    else if (comp instanceof JSpinner) {
	      ((JSpinner) comp).setValue(value.get(label));
	      handled = true;
	    }
	    else if (comp instanceof AbstractButton) {
	      if (comp == m_ButtonApply)
		handled = true;
	    }
	    // alternative update?
	    if (!handled) {
	      if (!setOption(i, label, comp, value.get(label)))
		getLogger().warning("Unhandled parameter type at #" + (i + 1) + " (setOptions): " + Utils.classToString(comp));
	    }
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to set value for label '" + label + "': " + value.get(label), e);
	  }
	}
      }
    }

    m_IgnoreOptionsUpdate = true;
    applyOptions();
    m_IgnoreOptionsUpdate = false;
  }

  /**
   * Sets the options from the map.
   *
   * @param value	the options to use
   */
  @Override
  public void setInitialOptions(Map<String,Object> value) {
    m_InitialOptions = value;
  }

  /**
   * Hook method for reacting to custom component types, retrieving their setting.
   *
   * @param index	the parameter index
   * @param label	the current label
   * @param comp	the parameter to retrieve
   * @param map		the options map to update with the current value
   * @return		true if successfully retrieved
   */
  protected boolean getOption(int index, String label, Component comp, Map<String,Object> map) {
    return false;
  }

  /**
   * Returns the current options as a map.
   *
   * @return		the options
   */
  public Map<String,Object> retrieveCurrentOptions() {
    Map<String,Object>	result;
    int			i;
    String		label;
    Component 		comp;
    boolean		handled;

    result = new HashMap<>();

    if (m_ParameterPanel != null) {
      for (i = 0; i < m_ParameterPanel.getParameterCount(); i++) {
	label   = m_ParameterPanel.getLabel(i).getText();
	comp    = m_ParameterPanel.getParameter(i);
	handled = false;
	if (comp instanceof JTextComponent) {
	  result.put(label, ((JTextComponent) comp).getText());
	  handled = true;
	}
	else if (comp instanceof BaseTextComponent) {
	  result.put(label, ((BaseTextComponent) comp).getText());
	  handled = true;
	}
	else if (comp instanceof JComboBox) {
	  result.put(label, ((JComboBox) comp).getSelectedIndex());
	  handled = true;
	}
	else if (comp instanceof JCheckBox) {
	  result.put(label, ((JCheckBox) comp).isSelected());
	  handled = true;
	}
	else if (comp instanceof JRadioButton) {
	  result.put(label, ((JRadioButton) comp).isSelected());
	  handled = true;
	}
	else if (comp instanceof JSpinner) {
	  result.put(label, ((JSpinner) comp).getValue());
	  handled = true;
	}
	else if (comp instanceof AbstractButton) {
	  if (comp == m_ButtonApply)
	    handled = true;
	}
	// alternative retrieval?
	if (!handled) {
	  if (!getOption(i, label, comp, result))
	    getLogger().warning("Unhandled parameter type at #" + (i+1) + " (getOptions): " + Utils.classToString(comp));
	}
      }
    }

    return result;
  }

  /**
   * Hook method for post-processing the options before returning them.
   *
   * @param value	the options to post-process
   * @see		#getCurrentOptions()
   */
  protected void postProcessCurrentOptions(Map<String,Object> value) {
  }

  /**
   * Returns the current options as a map.
   *
   * @return		the options
   */
  @Override
  public Map<String,Object> getCurrentOptions() {
    Map<String,Object>	result;

    result = retrieveCurrentOptions();
    postProcessCurrentOptions(result);

    return result;
  }

  /**
   * Returns the panel for setting the options.
   *
   * @return		the options panel
   */
  @Override
  public BasePanel getOptionPanel() {
    BasePanel	result;

    result = super.getOptionPanel();
    if (m_InitialOptions != null) {
      updateOptions(m_InitialOptions);
      m_InitialOptions = null;
    }

    return result;
  }
}
