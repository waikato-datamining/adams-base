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
 * AbstractComparisonPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.compareobjectlocations;

import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseToggleButton;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for comparison panels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractComparisonPanel
  extends BasePanel {

  private static final long serialVersionUID = 5857461459837580117L;

  /** the panel with the labels. */
  protected JPanel m_PanelLabels;

  /** the toggle buttons. */
  protected List<BaseToggleButton> m_ButtonLabels;

  /** the button group. */
  protected ButtonGroup m_ButtonGroup;

  /** the last selected label. */
  protected String m_LastLabel;

  /** the annotations object prefix. */
  protected String m_AnnotationsPrefix;

  /** the predictions object prefix. */
  protected String m_PredictionsPrefix;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ButtonLabels = new ArrayList<>();
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;

    super.initGUI();

    setLayout(new BorderLayout());

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.WEST);
    m_PanelLabels = new JPanel(new GridBagLayout());
    panel.add(new BaseScrollPane(m_PanelLabels), BorderLayout.CENTER);

    m_ButtonGroup = new ButtonGroup();
  }

  /**
   * Sets the object prefix to use for the annotations.
   *
   * @param value 	the object prefix
   */
  public void setAnnotationsPrefix(String value) {
    m_AnnotationsPrefix = value;
  }

  /**
   * Sets the object prefix to use for the predictions.
   *
   * @param value 	the object prefix
   */
  public void setPredictionsPrefix(String value) {
    m_PredictionsPrefix = value;
  }

  /**
   * Filters the objects using the specified label and returns the newly generated report.
   *
   * @param label	the label to restrict display to, empty/null for all
   * @param objects 	the objects to filter
   * @param suffix 	the suffix in the meta-data that contains the label
   * @param prefix 	the prefix to use for the report
   * @return 		the filtered report
   */
  protected Report filterObjects(String label, LocatedObjects objects, String suffix, String prefix) {
    LocatedObjects 	filtered;

    if (label == null)
      label = "";

    if (label.isEmpty()) {
      return objects.toReport(prefix);
    }
    else {
      filtered = new LocatedObjects();
      for (LocatedObject obj : objects) {
	if (obj.getMetaData().containsKey(suffix)) {
	  if (obj.getMetaData().get(suffix).toString().equals(label))
	    filtered.add(obj.getClone());
	}
      }
      return filtered.toReport(prefix);
    }
  }

  /**
   * Filters the objects using the specified label and updates the GUI.
   *
   * @param label	the label to restrict display to, empty/null for all
   */
  protected abstract void filterObjects(String label);

  /**
   * Updates the panel with the buttons.
   *
   * @param labels	the labels to display
   */
  protected void updateButtons(List<String> labels) {
    BaseToggleButton		button;
    GridBagLayout 		layout;
    GridBagConstraints 		con;
    int 			gapVertical;
    int 			gapHorizontal;
    int				i;
    JPanel			panel;

    gapHorizontal = 5;
    gapVertical   = 2;
    layout = new GridBagLayout();
    m_PanelLabels.setLayout(layout);

    for (BaseToggleButton b: m_ButtonLabels)
      m_ButtonGroup.remove(b);
    m_ButtonLabels.clear();
    m_PanelLabels.removeAll();
    button = new BaseToggleButton("All");
    button.addActionListener((ActionEvent e) -> filterObjects(""));
    button.setToolTipText(button.getText());
    m_ButtonGroup.add(button);
    m_ButtonLabels.add(button);
    for (final String label: labels) {
      button = new BaseToggleButton(label);
      button.addActionListener((ActionEvent e) -> filterObjects(label));
      button.setToolTipText(button.getText());
      m_ButtonGroup.add(button);
      m_ButtonLabels.add(button);
    }

    for (i = 0; i < m_ButtonLabels.size(); i++) {
      con = new GridBagConstraints();
      con.anchor  = GridBagConstraints.WEST;
      con.fill    = GridBagConstraints.HORIZONTAL;
      con.gridy   = i;
      con.gridx   = 0;
      con.weightx = 100;
      con.ipadx   = 20;
      con.insets  = new Insets(gapVertical, gapHorizontal, gapVertical, gapHorizontal);
      layout.setConstraints(m_ButtonLabels.get(i), con);
      m_PanelLabels.add(m_ButtonLabels.get(i));
    }

    // filler at bottom
    panel         = new JPanel();
    con           = new GridBagConstraints();
    con.anchor    = GridBagConstraints.WEST;
    con.fill      = GridBagConstraints.BOTH;
    con.gridy     = m_ButtonLabels.size();
    con.gridx     = 0;
    con.weighty   = 100;
    con.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(panel, con);
    m_PanelLabels.add(panel);

    // use last label again, if possible
    if (!labels.contains(m_LastLabel))
      m_LastLabel = "";
    if (m_LastLabel.isEmpty())
      m_ButtonLabels.get(0).doClick();
    else if (labels.contains(m_LastLabel))
      m_ButtonLabels.get(labels.indexOf(m_LastLabel) + 1).doClick();
  }

  /**
   * Clears the content of the panel.
   */
  public abstract void clearPanel();

  /**
   * Displays the new image.
   *
   * @param cont	the image to display
   * @param labels 	the object labels
   * @param reportAnn	the report with the annotations (ground truth)
   * @param objAnn	the object locations (ground truth from report)
   * @param reportPred	the report with the predictions
   * @param objPred	the object locations (predictions)
   */
  public abstract void display(AbstractImageContainer cont, List<String> labels, Report reportAnn, LocatedObjects objAnn, Report reportPred, LocatedObjects objPred);
}
