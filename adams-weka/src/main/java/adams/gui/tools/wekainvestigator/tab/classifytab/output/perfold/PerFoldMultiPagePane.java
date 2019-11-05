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
 * PerFoldMultiPagePane.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.perfold;

import adams.gui.core.BasePopupMenu;
import adams.gui.core.MultiPagePane;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator;

import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 * Specialized multi-page pane for managing per-fold data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PerFoldMultiPagePane
  extends MultiPagePane {

  public static final String KEY_FOLD = "Fold";

  private static final long serialVersionUID = 1861300659454587833L;

  public static final String UI_DIVIDERLOCATION = "DividerLocation";

  /** the output generator used. */
  protected AbstractOutputGenerator m_OutputGenerator;

  /** the underlying result item. */
  protected ResultItem m_Item;

  /**
   * Initializes the pane.
   *
   * @param generator 	the output generator that generated this pane
   * @param item	the underlying result item
   * @param uiclass 	the class the divider location is stored under
   */
  public PerFoldMultiPagePane(AbstractOutputGenerator generator, ResultItem item, Class uiclass) {
    super();
    setReadOnly(true);
    setDividerLocation(100);
    setUISettingsParameters(uiclass, UI_DIVIDERLOCATION);
    m_OutputGenerator = generator;
    m_Item            = item;
  }

  /**
   * Returns the underlying result item.
   *
   * @return		the item
   */
  public ResultItem getItem() {
    return m_Item;
  }

  /**
   * Returns the underlying output generator.
   *
   * @return		the generator
   */
  public AbstractOutputGenerator getOutputGenerator() {
    return m_OutputGenerator;
  }

  /**
   * Adds the page at the end.
   *
   * @param title	the title
   * @param comp	the page component
   * @param fold	the fold, -1 for non-fold related page, 0 for full, 1-* for fold indices
   */
  public void addPage(String title, Component comp, int fold) {
    PageContainer 	cont;

    cont = new PageContainer(title, comp);
    cont.getMetaData().put(KEY_FOLD, fold);
    if (fold < 0)
      cont.setRemovalAllowed(true);
    addPage(cont);
  }

  /**
   * Generates the right-click menu for the JList.
   * <br><br>
   * Derived classes should override this method instead of making use
   * of the PopupCustomizer.
   *
   * @param e		the event that triggered the popup
   * @return		the generated menu
   * @see		#showPopup(MouseEvent)
   */
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu	result;

    result = super.createPopup(e);

    AbstractPerFoldPopupMenuItem.updatePopupMenu(this, m_OutputGenerator, getSelectedIndices(), result);

    return result;
  }
}
