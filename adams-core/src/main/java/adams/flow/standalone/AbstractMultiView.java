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
 * AbstractMultiView.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHandler;
import adams.flow.core.Flushable;
import adams.flow.core.InputConsumer;
import adams.flow.sink.ComponentSupplier;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.TextSupplier;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.print.JComponentWriter;
import adams.gui.print.PNGWriter;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for graphical actors that display multiple views.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiView
  extends AbstractDisplay
  implements CallableActorHandler, MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4454052058077687116L;
  
  /**
   * A wrapper for the actual actors.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ViewWrapper
    extends AbstractDisplay
    implements InputConsumer, ComponentSupplier, TextSupplier, Flushable {
    
    /** for serialization. */
    private static final long serialVersionUID = -1571827759359015717L;
    
    /** the actor to wrap. */
    protected AbstractDisplay m_Wrapped;
    
    /**
     * Returns a string describing the object.
     *
     * @return 			a description suitable for displaying in the gui
     */
    @Override
    public String globalInfo() {
      return "Wrapper for sinks to be used in the multi-view sink.";
    }

    /**
     * Adds options to the internal list of options.
     */
    @Override
    public void defineOptions() {
      super.defineOptions();

      m_OptionManager.add(
  	    "wrapped", "wrapped",
  	    new SequencePlotter());
    }

    /**
     * Sets the actor to wrap.
     *
     * @param value	the actor to wrap
     */
    public void setWrapped(Actor value) {
      m_Wrapped = (AbstractDisplay) value;
      m_Wrapped.setParent(this);
      reset();
    }

    /**
     * Returns the wrapped actor.
     *
     * @return 		the wrapped actor
     */
    public Actor getWrapped() {
      return m_Wrapped;
    }

    /**
     * Returns the tip text for this property.
     *
     * @return		tip text for this property suitable for
     *             	displaying in the GUI or for listing the options.
     */
    public String wrappedTipText() {
      return "The actor to wrap.";
    }

    /**
     * Returns a quick info about the actor, which will be displayed in the GUI.
     *
     * @return		null if no info available, otherwise short string
     */
    @Override
    public String getQuickInfo() {
      return QuickInfoHelper.toString(this, "wrapped", m_Wrapped);
    }

    /**
     * Returns the class that the consumer accepts.
     * 
     * @return		the Class of objects that can be processed
     */
    @Override
    public Class[] accepts() {
      return ((InputConsumer) m_Wrapped).accepts();
    }
    
    /**
     * Updates the Variables instance in use, if different from current one.
     * <br><br>
     * Updates the {@link #m_Wrapped} actor as well.
     *
     * @param value	the instance to use
     */
    @Override
    public synchronized void setVariables(Variables value) {
      super.setVariables(value);
      m_Wrapped.setVariables(value);
    }
    
    /**
     * Executes the flow item.
     *
     * @return		null if everything is fine, otherwise error message
     */
    @Override
    protected String doExecute() {
      String	result;

      result = null;
      
      if (m_Panel == null) {
	result = m_Wrapped.execute();
	if (result == null) {
	  m_Panel = ((AbstractDisplay) m_Wrapped).getPanel();
	  ((AbstractMultiView) getParent()).addPanel(m_Wrapped, m_Panel);
	}
      }

      if (result == null) {
	m_Wrapped.input(m_InputToken);
	result = m_Wrapped.execute();
      }
      
      return result;
    }
    
    /**
     * Stops the processing of tokens without stopping the flow.
     */
    public void flushExecution() {
      if (m_Wrapped instanceof ActorHandler)
	((ActorHandler) m_Wrapped).flushExecution();
    }

    /**
     * Cleans up after the execution has finished.
     */
    @Override
    public void wrapUp() {
      if (m_Wrapped != null)
	m_Wrapped.wrapUp();
      
      super.wrapUp();
    }
    
    /**
     * Cleans up after the execution has finished. Also removes graphical
     * components.
     */
    @Override
    public void cleanUp() {
      if (m_Panel != null) {
	m_Wrapped.clearPanel();
	m_Wrapped.cleanUp();
	m_Panel = null;
      }
      
      super.cleanUp();
    }
    
    /**
     * Clears the panel.
     */
    @Override
    public void clearPanel() {
      if (m_Panel != null)
	m_Wrapped.clearPanel();
    }

    /**
     * Returns a custom file filter for the file chooser.
     * 
     * @return		the file filter, null if to use default one
     */
    @Override
    public ExtensionFileFilter getCustomTextFileFilter() {
      if (getParent() instanceof AbstractMultiView)
	((AbstractMultiView) getParent()).makeVisible(this);
      if (getParent() instanceof TextSupplier)
	return ((TextSupplier) getParent()).getCustomTextFileFilter();
      else
	return null;
    }

    /**
     * Supplies the text. May get called even if actor hasn't been executed yet.
     *
     * @return		the text, null if none available
     */
    @Override
    public String supplyText() {
      if (getParent() instanceof AbstractMultiView)
	((AbstractMultiView) getParent()).makeVisible(this);
      if (getParent() instanceof TextSupplier)
	return ((TextSupplier) getParent()).supplyText();
      else
	return null;
    }

    /**
     * Supplies the component. May get called even before actor has been executed.
     *
     * @return		the component, null if none available
     */
    @Override
    public JComponent supplyComponent() {
      if (getParent() instanceof AbstractMultiView)
	((AbstractMultiView) getParent()).makeVisible(this);
      if (getParent() instanceof ComponentSupplier)
	return ((ComponentSupplier) getParent()).supplyComponent();
      else
	return null;
    }

    /**
     * Creates the panel to display in the dialog.
     *
     * @return		null, m_Panel gets generated in execute
     */
    @Override
    protected BasePanel newPanel() {
      return null;
    }

    /**
     * Returns a runnable that displays frame, etc.
     * Must call notifyAll() on the m_Self object and set m_Updating to false.
     *
     * @return		null, as not necessary
     */
    @Override
    protected Runnable newDisplayRunnable() {
      return null;
    }
  }
  
  /** the underlying display actors. */
  protected List<Actor> m_Actors;
  
  /** the panels in use. */
  protected List<ViewWrapper> m_Wrappers;

  /** the writer to use. */
  protected JComponentWriter m_Writer;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "actor", "actors",
	    new Actor[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Actors   = new ArrayList<>();
    m_Wrappers = null;
  }

  /**
   * Sets the panel providers.
   *
   * @param value 	the panel providers
   */
  public void setActors(Actor[] value) {
    int		i;
    String	msg;

    ActorUtils.uniqueNames(value);

    for (i = 0; i < value.length; i++) {
      msg = check(value[i]);
      if (msg != null) {
	getLogger().severe(msg);
	return;
      }
    }

    m_Actors.clear();
    for (i = 0; i < value.length; i++)
      m_Actors.add(value[i]);

    updateParent();
    reset();
  }

  /**
   * Returns the panel providers.
   *
   * @return 		the panel providers
   */
  public Actor[] getActors() {
    return m_Actors.toArray(new Actor[m_Actors.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String actorsTipText();
  
  /**
   * Checks whether the actor is valid.
   * 
   * @param actor	the actor to check
   * @return		null if OK, otherwise error message
   */
  protected String check(Actor actor) {
    if (!(actor instanceof AbstractDisplay))
      return "Actor '" + actor.getName() + "' not derived from " + adams.flow.sink.AbstractDisplay.class.getName() + "!";
    if (!ActorUtils.isSink(actor))
      return "Actor '" + actor.getName() + "' is not a sink!";
    if (getScopeHandler() != null)
      return getScopeHandler().addCallableName(actor);
    return null;
  }

  /**
   * Updates the parent of all actors in this group.
   */
  protected void updateParent() {
    int		i;

    for (i = 0; i < size(); i++) {
      get(i).setParent(null);
      get(i).setParent(this);
    }
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  @Override
  public void add(Actor actor) {
    String	msg;
    
    msg = check(actor);
    if (msg == null)
      m_Actors.add(actor);
    else
      throw new IllegalArgumentException(msg);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  @Override
  public void add(int index, Actor actor) {
    String	msg;
    
    msg = check(actor);
    if (msg == null)
      m_Actors.add(index, actor);
    else
      throw new IllegalArgumentException(msg);
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  @Override
  public Actor remove(int index) {
    return m_Actors.remove(index);
  }

  /**
   * Removes all actors.
   */
  @Override
  public void removeAll() {
    m_Actors.clear();
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, false, ActorExecution.PARALLEL, false, new Class[]{adams.flow.sink.AbstractDisplay.class});
  }
  
  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String check() {
    String	result;
    int		i;
    
    result = null;
    
    for (i = 0; i < m_Actors.size(); i++) {
      result = check(m_Actors.get(i));
      if (result != null)
	break;
    }
    
    return result;
  }

  /**
   * Returns the size of the group.
   *
   * @return		the size
   */
  @Override
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public Actor get(int index) {
    if (m_Wrappers != null)
      return m_Wrappers.get(index);
    else
      return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, Actor actor) {
    String	msg;
    
    msg = check(actor);
    if (msg == null)
      m_Actors.set(index, actor);
    else
      throw new IllegalArgumentException(msg);
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    int		result;
    int		i;
    
    result = -1;
    
    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i).getName().equals(actor)) {
	result = i;
	break;
      }
    }
    
    return result;
  }

  /**
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  @Override
  public Actor firstActive() {
    Actor	result;
    int		i;

    result = null;
    for (i = 0; i < size(); i++) {
      if (!get(i).getSkip()) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  @Override
  public Actor lastActive() {
    Actor	result;
    int		i;

    result = null;
    for (i = size() - 1; i >= 0; i--) {
      if (!get(i).getSkip()) {
	result = get(i);
	break;
      }
    }

    return result;
  }

  /**
   * Ensures that the wrapper is visible.
   * 
   * @param wrapper	the wrapper to make visible
   * @return		true if successful
   */
  public abstract boolean makeVisible(ViewWrapper wrapper);
  
  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Wrappers != null) {
      for (ViewWrapper wrapper: m_Wrappers)
	wrapper.clearPanel();
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected abstract BasePanel newPanel();

  /**
   * Returns a runnable that displays frame, etc.
   * Must call notifyAll() on the m_Self object and set m_Updating to false.
   *
   * @return		the runnable
   */
  @Override
  protected Runnable newDisplayRunnable() {
    Runnable	result;

    result = new Runnable() {
      public void run() {
	if (getCreateFrame() && !m_Frame.isVisible())
	  m_Frame.setVisible(true);
	synchronized(m_Self) {
	  m_Self.notifyAll();
	}
	m_Updating = false;
      }
    };

    m_InputToken = null;

    return result;
  }
  
  /**
   * Updates the Variables instance in use, if different from current one.
   * <br><br>
   * Also updates the {@link ViewWrapper} instances.
   *
   * @param value	the instance to use
   */
  @Override
  public synchronized void setVariables(Variables value) {
    super.setVariables(value);
    
    if (m_Wrappers != null) {
      for (ViewWrapper wrapper: m_Wrappers)
	wrapper.setVariables(value);
    }
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#reset()
   */
  @Override
  public String setUp() {
    String		result;
    ViewWrapper	wrapper;
    
    result = super.setUp();
    
    if (result == null)
      result = check();
    
    if (result == null) {
      m_Wrappers = new ArrayList<ViewWrapper>();
      for (Actor actor: m_Actors) {
	((AbstractDisplay) actor).setCreateFrame(false);
	((AbstractDisplay) actor).setDisplayInEditor(false);
	wrapper = new ViewWrapper();
	wrapper.setParent(this);
	wrapper.setName(actor.getName());
	wrapper.setWrapped(actor);
	result = wrapper.setUp();
	if (result != null) {
	  result = "Failed to wrap actor '" + actor.getName() + "': " + result;
	  break;
	}
	m_Wrappers.add(wrapper);
      }
    }
    
    return result;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    // update variables
    for (ViewWrapper wrapper: m_Wrappers)
      wrapper.setVariables(getVariables());
    
    return super.doExecute();
  }
  
  /**
   * Replaces the current dummy panel with the actual panel.
   * 
   * @param actor	the actor this panel is for
   * @param panel	the panel to replace the dummy one
   */
  public abstract void addPanel(Actor actor, BasePanel panel);
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_Wrappers != null) {
      for (ViewWrapper wrapper: m_Wrappers) {
	wrapper.flushExecution();
      }
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Wrappers != null) {
      for (ViewWrapper wrapper: m_Wrappers)
	wrapper.stopExecution();
    }
    
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_Wrappers != null) {
      for (ViewWrapper wrapper: m_Wrappers)
	wrapper.wrapUp();
    }
    
    super.wrapUp();
  }
  
  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_Wrappers != null) {
      for (ViewWrapper wrapper: m_Wrappers)
	wrapper.cleanUp();
      m_Wrappers.clear();
      m_Wrappers = null;
    }
    
    super.cleanUp();
  }

  /**
   * Assembles the menu bar.
   *
   * @return		the menu bar
   */
  protected JMenuBar createMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    result = new JMenuBar();

    // File
    menu = new JMenu("File");
    result.add(menu);
    menu.setMnemonic('F');
    menu.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateMenu();
      }
    });

    // File/Send to
    if (SendToActionUtils.addSendToSubmenu(this, menu))
      menu.addSeparator();

    // File/Close
    menuitem = new JMenuItem("Close");
    menu.add(menuitem);
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
    menuitem.setIcon(GUIHelper.getIcon("exit.png"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	close();
      }
    });
    m_MenuItemFileClose = menuitem;

    return result;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    if (m_MenuBar == null) {
      m_MenuBar = createMenuBar();
      updateMenu();
    }

    return m_MenuBar;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    m_Panel.closeParent();
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    List<Class>		result;
    boolean		add;
    
    result = new ArrayList<Class>();
    
    if (this instanceof SpreadSheetSupporter) {
      add = !isExecuted() || (isExecuted() && (((SpreadSheetSupporter) this).toSpreadSheet() != null));
      if (add)
	result.add(SpreadSheet.class);
    }
    
    if (this instanceof TextSupplier) {
      add = !isExecuted() || (isExecuted() && (((TextSupplier) this).supplyText() != null));
      if (add)
	result.add(String.class);
    }
    
    if (this instanceof ComponentSupplier) {
      add = !isExecuted() || (isExecuted() && (((ComponentSupplier) this).supplyComponent() != null));
      if (add) {
	result.add(PlaceholderFile.class);
	result.add(JComponent.class);
      }
    }
    
    return result.toArray(new Class[result.size()]);
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    if (    (this instanceof TextSupplier) 
	 && (((TextSupplier) this).supplyText() != null)
	 && (SendToActionUtils.isAvailable(new Class[]{String.class}, cls)) )
      return true;

    if (    (this instanceof ComponentSupplier) 
	 && (((ComponentSupplier) this).supplyComponent() != null)
	 && (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JComponent.class}, cls)) )
      return true;

    if (    (this instanceof SpreadSheetSupporter) 
	 && (((SpreadSheetSupporter) this).toSpreadSheet() != null)
	 && (SendToActionUtils.isAvailable(new Class[]{SpreadSheet.class}, cls)) )
      return true;
    
    return false;
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object		result;
    JComponent		comp;
    PNGWriter		writer;

    result = null;

    if ((this instanceof TextSupplier) && SendToActionUtils.isAvailable(new Class[]{String.class}, cls)) {
      result = ((TextSupplier) this).supplyText();
    }
    else if ((this instanceof SpreadSheetSupporter) && SendToActionUtils.isAvailable(new Class[]{SpreadSheet.class}, cls)) {
      result = ((SpreadSheetSupporter) this).toSpreadSheet();
    }
    else if (this instanceof ComponentSupplier) {
      comp = ((ComponentSupplier) this).supplyComponent();
      if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
	if (comp != null) {
	  result = SendToActionUtils.nextTmpFile("actor-" + getName(), "png");
	  writer = new PNGWriter();
	  writer.setFile((PlaceholderFile) result);
	  writer.setComponent(comp);
	  try {
	    writer.generateOutput();
	  }
	  catch (Exception e) {
	    handleException("Failed to write image to " + result + ":", e);
	    result = null;
	  }
	}
      }
      else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
	result = comp;
      }
    }

    return result;
  }
}
