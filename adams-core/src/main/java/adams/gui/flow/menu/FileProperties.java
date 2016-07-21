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
 * FileProperties.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.menu;

import adams.core.ByteFormat;
import adams.data.statistics.InformativeStatistic;
import adams.flow.core.ActorStatistic;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.statistics.InformativeStatisticFactory;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Displays properties of a flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileProperties
  extends AbstractFlowEditorMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Properties...";
  }

  /**
   * Displays statistics about the current flow.
   */
  protected void showStatistics() {
    ActorStatistic			stats;
    InformativeStatisticFactory.Dialog	dialog;
    Vector<InformativeStatistic>	statsList;

    if (m_State.getCurrentTree().getSelectedNode() != null)
      stats = new ActorStatistic(m_State.getCurrentTree().getSelectedNode().getFullActor());
    else if (m_State.getCurrentFlow() != null)
      stats = new ActorStatistic(m_State.getCurrentFlow());
    else
      stats = new ActorStatistic(m_State.getCurrentFlow());
    statsList = new Vector<InformativeStatistic>();
    statsList.add(stats);

    if (getParentDialog() != null)
      dialog = InformativeStatisticFactory.getDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = InformativeStatisticFactory.getDialog(getParentFrame(), true);
    dialog.setStatistics(statsList);
    dialog.setTitle("Actor statistics");
    dialog.pack();
    dialog.setLocationRelativeTo(m_State);
    dialog.setVisible(true);
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ApprovalDialog	dialog;
    ParameterPanel	params;
    String		file;
    String		size;
    JButton		buttonStats;
    final JTextField	textFile;
    JTextField		textSize;

    if (m_State.getCurrentFile() != null)
      file = m_State.getCurrentFile().toString();
    else
      file = "N/A";
    if ((m_State.getCurrentFile() != null) && !m_State.isModified())
      size = ByteFormat.toKiloBytes(m_State.getCurrentFile().length(), 1);
    else
      size = "N/A";
    buttonStats = new JButton("Display", GUIHelper.getIcon("statistics.png"));
    buttonStats.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	showStatistics();
      }
    });

    params = new ParameterPanel();
    textFile = new JTextField(file, 20);
    textFile.setEditable(false);
    textFile.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  e.consume();
	  BasePopupMenu menu = new BasePopupMenu();
	  JMenuItem menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
	  menuitem.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	      ClipboardHelper.copyToClipboard(textFile.getText());
	    }
	  });
	  menu.add(menuitem);
	  menu.showAbsolute(textFile, e);
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    params.addParameter("File", textFile);
    textSize = new JTextField(size, 7);
    textSize.setEditable(false);
    params.addParameter("Size", textSize);
    params.addParameter("Statistics", buttonStats);

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog());
    else
      dialog = new ApprovalDialog(getParentFrame());
    dialog.setTitle("Properties");
    dialog.setCancelVisible(false);
    dialog.setApproveVisible(true);
    dialog.setDiscardVisible(false);
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(m_State);
    dialog.setVisible(true);
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.hasCurrentPanel());
  }
}
