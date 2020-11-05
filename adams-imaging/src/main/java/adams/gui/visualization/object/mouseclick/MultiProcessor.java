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
 * MultiProcessor.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.mouseclick;

import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.event.MouseEvent;

/**
 * Combines multiple click processors, iterates through them until event consumed.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiProcessor
  extends AbstractMouseClickProcessor {

  private static final long serialVersionUID = 8422134104160247274L;

  /** the processors. */
  protected AbstractMouseClickProcessor[] m_Processors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines multiple click processors, iterates through them until event consumed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("button");
    m_OptionManager.removeByProperty("shiftDown");
    m_OptionManager.removeByProperty("altDown");
    m_OptionManager.removeByProperty("ctrlDown");
    m_OptionManager.removeByProperty("metaDown");

    m_OptionManager.add(
      "processor", "processors",
      new AbstractMouseClickProcessor[0]);
  }

  /**
   * Adds the mouse click processors.
   *
   * @param value 	the processor
   * @return		itself
   */
  public MultiProcessor addProcessor(AbstractMouseClickProcessor value) {
    AbstractMouseClickProcessor[] 	processors;
    int			i;

    processors = new AbstractMouseClickProcessor[m_Processors.length + 1];
    for (i = 0; i < m_Processors.length; i++)
      processors[i] = m_Processors[i];
    processors[processors.length - 1] = value;

    setProcessors(processors);

    return this;
  }

  /**
   * Sets the click processors to manage.
   *
   * @param value 	the processors
   */
  public void setProcessors(AbstractMouseClickProcessor[] value) {
    m_Processors = value;
    reset();
  }

  /**
   * Returns the click processors to manage.
   *
   * @return 		the processors
   */
  public AbstractMouseClickProcessor[] getProcessors() {
    return m_Processors;
  }

  /**
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  @Override
  protected void doProcess(ObjectAnnotationPanel panel, MouseEvent e) {
    int		i;

    for (i = 0; i < m_Processors.length; i++) {
      m_Processors[i].process(panel, e);
      if (e.isConsumed())
        break;
    }
  }

  /**
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  public void process(ObjectAnnotationPanel panel, MouseEvent e) {
    if (!getEnabled())
      return;

    doProcess(panel, e);
  }
}
