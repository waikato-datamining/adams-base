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
 * BranchSelectionPage.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.wizard;

import adams.core.Properties;
import adams.core.Utils;
import adams.gui.core.BaseButton;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

/**
 * Page that lets the user select which sub-branch to follow by displaying
 * the page names of its children as buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BranchSelectionPage
  extends AbstractWizardPage {

  private static final long serialVersionUID = -4386331699718658675L;

  /** the panel with the selection buttons. */
  protected JPanel m_PanelSelection;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2, BorderLayout.NORTH);

    m_PanelSelection = new JPanel(new GridLayout(0, 1, 5, 5));
    panel2.add(m_PanelSelection);
  }

  /**
   * Sets the wizard this page belongs to.
   *
   * @param value	the owner
   * @see		WizardPaneWithBranches
   */
  public void setOwner(AbstractWizardPane value) {
    if (value instanceof WizardPaneWithBranches)
      super.setOwner(value);
    else
      throw new IllegalArgumentException("Owner must be an instance of " + Utils.classToString(WizardPaneWithBranches.class));
  }

  /**
   * Does nothing.
   *
   * @param value	ignored
   */
  public void setProperties(Properties value) {
    // does nothing
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   *
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    return new Properties();
  }

  /**
   * Returns its child pages.
   *
   * @return		the pages
   */
  protected AbstractWizardPage[] getChildPages() {
    if (getOwner() == null)
      return new AbstractWizardPage[0];
    else
      return ((WizardPaneWithBranches) getOwner()).getChildPages(this);
  }

  /**
   * Returns whether we can proceed with the next page.
   *
   * @return		always false
   */
  @Override
  public boolean canProceed() {
    return false;
  }

  /**
   * Updates the page.
   */
  @Override
  public void update() {
    AbstractWizardPage[]		pages;
    final WizardPaneWithBranches	owner;

    super.update();

    m_PanelSelection.removeAll();

    owner = (WizardPaneWithBranches) getOwner();
    pages = getChildPages();
    if (pages.length == 0) {
      m_PanelSelection.add(new JLabel("Nothing to select"));
    }
    else {
      for (final AbstractWizardPage page: pages) {
	final BaseButton button = new BaseButton(page.getPageName());
	button.addActionListener((ActionEvent e) -> owner.setSelectedPage(page));
	m_PanelSelection.add(button);
	page.invalidate();
      }
    }

    invalidate();
    validate();
    repaint();
  }
}
