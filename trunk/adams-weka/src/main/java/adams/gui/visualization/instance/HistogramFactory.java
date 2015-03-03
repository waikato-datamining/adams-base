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
 * HistogramFactory.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import java.awt.Dialog.ModalityType;
import java.util.List;

import adams.data.instance.Instance;
import adams.data.instance.InstancePoint;
import adams.data.statistics.ArrayHistogram;

/**
 * A factory for histogram related objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistogramFactory {

  /**
   * A panel for displaying a histogram based on the GC data of a instance.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Panel
    extends adams.gui.visualization.statistics.HistogramFactory.Panel {

    /** for serialization. */
    private static final long serialVersionUID = -1990327944906647098L;

    /**
     * Adds a plot of the given instance.
     * It counts how many GC points have the same abundance.
     *
     * @param hist	for generating the histogram
     * @param c		the instance to create a plot for
     */
    public void add(ArrayHistogram hist, Instance c) {
      add(hist, c, "ID " + c.getID());
    }

    /**
     * Adds a plot of the given instance.
     * It counts how many GC points have the same abundance.
     *
     * @param hist	for generating the histogram
     * @param c		the instance to create a plot for
     * @param name	the name of the tab
     */
    public void add(ArrayHistogram hist, Instance c, String name) {
      Double[]			data;
      List<InstancePoint> 	points;
      int			i;

      data   = new Double[c.size()];
      points = c.toList();
      for (i = 0; i < points.size(); i++)
	data[i] = new Double(points.get(i).getY());

      add(hist, data, name);
    }
  }

  /**
   * A dialog that queries the user about parameters for displaying histograms.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SetupDialog
    extends adams.gui.visualization.statistics.HistogramFactory.SetupDialog {

    /** for serialization. */
    private static final long serialVersionUID = 4215632076348292959L;

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modality	the type of modality
     */
    public SetupDialog(java.awt.Dialog owner, ModalityType modality) {
      super(owner, modality);
    }

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modal	if true then the dialog will be modal
     */
    public SetupDialog(java.awt.Frame owner, boolean modal) {
      super(owner, modal);
    }
  }

  /**
   * Dialog for displaying histograms generated from instances.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Dialog
    extends adams.gui.visualization.statistics.HistogramFactory.Dialog {

    /** for serialization. */
    private static final long serialVersionUID = 2464157048335973279L;

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modality	the type of modality
     */
    public Dialog(java.awt.Dialog owner, ModalityType modality) {
      super(owner, modality);
    }

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modal	if true then the dialog will be modal
     */
    public Dialog(java.awt.Frame owner, boolean modal) {
      super(owner, modal);
    }

    /**
     * Adds a plot of the given instance.
     * It counts how many GC points have the same abundance.
     *
     * @param hist	for generating the histogram
     * @param c		the instance to create a plot for
     */
    public void add(ArrayHistogram hist, Instance c) {
      add(hist, c, "ID " + c.getID());
    }

    /**
     * Adds a plot of the given instance.
     * It counts how many GC points have the same abundance.
     *
     * @param hist	for generating the histogram
     * @param c		the instance to create a plot for
     * @param name	the name of the tab
     */
    public void add(ArrayHistogram hist, Instance c, String name) {
      Panel	panel;

      panel = new HistogramFactory.Panel();
      panel.add(hist, c, name);
      m_TabbedPane.addTab(name, panel);
    }
  }

  /**
   * Returns an instance of a new panel for displaying histograms.
   *
   * @return		the panel
   */
  public static Panel getPanel() {
    return new Panel();
  }

  /**
   * Returns an instance of a setup dialog for displaying histograms.
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static SetupDialog getSetupDialog(java.awt.Dialog owner, ModalityType modality) {
    return new SetupDialog(owner, modality);
  }

  /**
   * Returns an instance of a setup dialog for displaying histograms.
   *
   * @param owner	the owning component
   * @param modal	if true then the dialog will be modal
   * @return		the dialog
   */
  public static SetupDialog getSetupDialog(java.awt.Frame owner, boolean modal) {
    return new SetupDialog(owner, modal);
  }

  /**
   * Returns a new dialog for displaying histograms.
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Dialog owner, ModalityType modality) {
    return new Dialog(owner, modality);
  }

  /**
   * Returns a new dialog for displaying displaying histograms.
   *
   * @param owner	the owning component
   * @param modal	if true then the dialog will be modal
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Frame owner, boolean modal) {
    return new Dialog(owner, modal);
  }
}
