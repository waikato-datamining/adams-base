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
 * ActorHelpTab.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import adams.core.option.HtmlHelpProducer;
import adams.flow.core.AbstractActor;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BrowserHelper.DefaultHyperlinkListener;
import adams.gui.core.Fonts;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;

/**
 * Tab for displaying the help for the currently selected actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorHelpTab
  extends AbstractEditorTab
  implements SelectionAwareEditorTab {

  /** for serialization. */
  private static final long serialVersionUID = 3860012648562358118L;

  /** the default text if no actor has been selected. */
  public final static String DEFAULT_TEXT = "<html><center><b>No actor selected</b></center></html>";

  /** for displaying the help text. */
  protected JEditorPane m_TextArea;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextArea = new JEditorPane();
    m_TextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_TextArea.setEditable(false);
    m_TextArea.setFont(Fonts.getMonospacedFont());
    m_TextArea.setAutoscrolls(true);
    m_TextArea.setContentType("text/html");
    m_TextArea.setText(DEFAULT_TEXT);
    m_TextArea.addHyperlinkListener(new DefaultHyperlinkListener());

    add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Help";
  }

  /**
   * Notifies the tab of the currently selected actors.
   *
   *
   * @param paths	the selected paths
   * @param actors	the currently selected actors
   */
  public void actorSelectionChanged(TreePath[] paths, AbstractActor[] actors) {
    HtmlHelpProducer 	producer;
    Runnable		run;

    if (actors.length != 1) {
      m_TextArea.setText(DEFAULT_TEXT);
      return;
    }

    producer = new HtmlHelpProducer();
    producer.produce(actors[0]);

    run = () -> {
      m_TextArea.setText(producer.toString());
      m_TextArea.setCaretPosition(0);
    };
    SwingUtilities.invokeLater(run);
  }
}
