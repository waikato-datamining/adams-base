/*
 * InstancesSortSetupEvent.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.tools.spreadsheetviewer.SortDefinitionPanel;
import adams.gui.tools.spreadsheetviewer.SortPanel;
import adams.gui.visualization.instances.instancestable.InstancesSortDefinitionPanel;
import adams.gui.visualization.instances.instancestable.InstancesSortPanel;

import java.util.EventObject;

/**
 * Event that gets sent when the {@link InstancesSortPanel} setup changes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstancesSortSetupEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = -1322917092297786611L;

  /**
   * The type of event.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum EventType {
    /** a definition was added. */
    ADD,
    /** a definition was removed. */
    REMOVE,
    /** the setup was modified. */
    MODIFIED,
    /** the setup was reset. */
    RESET,
    /** the setup was moved. */
    MOVED
  }

  /** the definition panel that was added/removed. */
  protected InstancesSortDefinitionPanel m_SortDefinitionPanel;

  /** what type of event occurred. */
  protected EventType m_Type;

  /**
   * Initializes the event.
   *
   * @param source	the {@link SortPanel} that triggered the event
   * @param definition	the definition panel that was added or removed, null if reset
   * @param type	the type of event
   */
  public InstancesSortSetupEvent(InstancesSortPanel source, InstancesSortDefinitionPanel definition, EventType type) {
    super(source);
    m_SortDefinitionPanel = definition;
    m_Type                = type;
  }

  /**
   * Returns the {@link SortPanel} that triggered the event
   *
   * @return		 the panel
   */
  public InstancesSortPanel getSortPanel() {
    return (InstancesSortPanel) getSource();
  }

  /**
   * Returns the {@link SortDefinitionPanel} that was added/removed.
   *
   * @return		the panel, null in case of reset
   */
  public InstancesSortDefinitionPanel getSortDefinitionPanel() {
    return m_SortDefinitionPanel;
  }

  /**
   * Returns the event type.
   *
   * @return		true if the panel was added
   */
  public EventType getType() {
    return m_Type;
  }

  /**
   * Returns a short description of the object.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "sortpanel=" + getSortPanel().hashCode() + ", sortdefinitionpanel=[" + getSortDefinitionPanel() + "], type=" + m_Type;
  }
}
