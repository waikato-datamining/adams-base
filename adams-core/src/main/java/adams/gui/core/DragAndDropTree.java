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
 * DragAndDropTree.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2006 tony@stupidjavatricks.com (original drag'n'drop)
 * Copyright (C) 2011 Matt Crinklaw-Vogt (delayed opening of collapsed node when hovering over it during d'n'd)
 */

package adams.gui.core;

import adams.core.EnumWithCustomDisplay;
import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.gui.event.NodeDroppedEvent;
import adams.gui.event.NodeDroppedEvent.NotificationTime;
import adams.gui.event.NodeDroppedListener;
import adams.gui.flow.tree.Node;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A BaseTree ehanced with drag'n'drop.
 * <br><br>
 * Drag'n'Drop originally taken from the DnDJTree referenced
 * <a href="http://www.stupidjavatricks.com/?p=17" target="_blank">here</a>.
 * <br><br>
 * Delayed expansion of collapsed nodes during drag'n'drop originally taken
 * from <a href="http://stackoverflow.com/questions/5507098/controlling-jtree-expansion-delay-on-drag-drop/5507194#5507194" target="_blank">here</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  tony@stupidjavatricks.com
 * @author  Matt Crinklaw-Vogt
 * @version $Revision$
 */
@MixedCopyright(
    author = "tony@stupidjavatricks.com",
    copyright = "2006 Stupid Java Tricks",
    license = License.GPL3,
    url = "http://www.stupidjavatricks.com/?p=17 and http://www.null-device.com/mycode/DnDTree/",
    note = "Drag'n'drop; commercial license available"
)
public class DragAndDropTree
  extends BaseTree
  implements DropTargetListener, DragSourceListener, DragGestureListener {

  /** for serialization. */
  private static final long serialVersionUID = 9095408256996103054L;

  /**
   * Enumeration for where to drop the node.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum DropPosition
    implements EnumWithCustomDisplay<DropPosition> {

    /** here at this position. */
    BENEATH("Beneath"),
    /** here at this position. */
    HERE("Here"),
    /** after this position. */
    AFTER("After");

    /** the display string. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /**
     * The constructor.
     *
     * @param display	the string to use as display
     */
    private DropPosition(String display) {
      m_Display = display;
      m_Raw     = super.toString();
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Display;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return toDisplay();
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public DropPosition parse(String s) {
      DropPosition	result;

      result = null;

      // default parsing
      try {
        result = valueOf(s);
      }
      catch (Exception e) {
        // ignored
      }

      // try display
      if (result == null) {
	for (DropPosition dt: values()) {
	  if (dt.toDisplay().equals(s)) {
	    result = dt;
	    break;
	  }
	}
      }

      return result;
    }
  }

  /**
   * Enumeration for where to drop the node.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum DropMenu {
    /** Add. */
    ADD,
    /** Move. */
    MOVE,
    /** here at this position. */
    BENEATH(DropPosition.BENEATH),
    /** here at this position. */
    HERE(DropPosition.HERE),
    /** after this position. */
    AFTER(DropPosition.AFTER),
    /** cancelling. */
    CANCEL;

    /** the drop position. */
    private DropPosition m_Position;

    /**
     * Initializes the menu action.
     */
    private DropMenu() {
      this(null);
    }

    /**
     * Initializes the menu action with the specified drop position.
     *
     * @param position	the position to use
     */
    private DropMenu(DropPosition position) {
      m_Position = position;
    }

    /**
     * Returns the drop position.
     *
     * @return		the position, can be nulls
     */
    public DropPosition getPosition() {
      return m_Position;
    }
  }

  /** the offset for the "ghost" image. */
  protected Point m_Offset;

  /** the amount of pixels for the cue line to extend further out. */
  protected int m_CueLineExtension;

  /** The color of the line hinting whether the node is dropped. */
  protected Color m_ColorCueLine;

  /** The rectangle for the line hinting where to drop the node. */
  protected Rectangle2D m_RectCueLine;

  /** the source node of the d'n'd. */
  protected BaseTreeNode[] m_SourceNode;

  /** manages the drag source. */
  protected DragSource m_DragSource;

  /** manages the drop target. */
  protected DropTarget m_DropTarget;

  /** the listeners for node-drop events. */
  protected HashSet<NodeDroppedListener> m_NodeDroppedListeners;

  /** used for expanding nodes during drag'n'drop. */
  protected ScheduledExecutorService m_ExpansionExecutor;

  /** the time in msecs before expanding a node during drag'n'drop. */
  protected int m_ExpansionDelay;

  /**
   * Initializes the tree.
   */
  public DragAndDropTree() {
    super();
  }

  /**
   * Initializes the tree with the given model.
   *
   * @param model	the tree model to use
   */
  public DragAndDropTree(TreeModel model) {
    super(model);
  }

  /**
   * Initializes the tree with the given root node.
   *
   * @param root	the root node to use
   */
  public DragAndDropTree(TreeNode root) {
    super(root);
  }

  /**
   * Further initialization of the tree.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RectCueLine      = new Rectangle2D.Float();
    m_DragSource       = DragSource.getDefaultDragSource();
    m_Offset           = new Point();
    m_CueLineExtension = 5;
    m_ColorCueLine     = new Color(
	SystemColor.controlShadow.getRed(),
	SystemColor.controlShadow.getGreen(),
	SystemColor.controlShadow.getBlue(),
	64);
    DragGestureRecognizer dgr = m_DragSource.createDefaultDragGestureRecognizer(
	this,                     // DragSource
	DnDConstants.ACTION_MOVE, // specifies valid actions
	this                      //DragGestureListener
    );
    dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_DOWN_MASK);

    m_DropTarget = new DropTarget(this, this);
    m_DropTarget.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);

    m_NodeDroppedListeners = new HashSet<NodeDroppedListener>();

    m_ExpansionExecutor = Executors.newScheduledThreadPool(1);
    m_ExpansionDelay    = 1000;
  }

  /**
   * Sets the delay in msecs before expanding a collapsed node during a
   * drag'n'drop operation.
   *
   * @param value	the delay in msecs, use 0 or smaller to turn delay off
   */
  public void setExpansionDelay(int value) {
    m_ExpansionDelay = value;
  }

  /**
   * Returns the delay in msecs before expanding a collapsed node during a
   * drag'n'drop operation.
   *
   * @return		the delay in msecs
   */
  public int getExpansionDelay() {
    return m_ExpansionDelay;
  }

  /**
   * Creates a new TreeNode for this tree.
   * <br><br>
   * Default implementation uses the toString() method of the transferable
   * as user object (and splits it on the "\n").
   *
   * @param data	the data to use
   * @return		the new nodes
   */
  protected BaseTreeNode[] newTreeNodes(Transferable data) {
    BaseTreeNode[]	result;
    String[]		parts;
    int			i;

    parts  = data.toString().split("\n");
    result = new BaseTreeNode[parts.length];
    for (i = 0; i < parts.length; i++)
      result[i] = new BaseTreeNode(parts[i]);

    return result;
  }

  /**
   * Called while a drag operation is ongoing, when the mouse pointer enters
   * the operable part of the drop site for the <code>DropTarget</code>
   * registered with this listener.
   *
   * @param dtde the <code>DropTargetDragEvent</code>
   */
  public void dragEnter(DropTargetDragEvent dtde) {
    // ignored
  }

  /**
   * Called when a drag operation is ongoing, while the mouse pointer is still
   * over the operable part of the drop site for the <code>DropTarget</code>
   * registered with this listener.
   *
   * @param e the <code>DropTargetDragEvent</code>
   */
  @MixedCopyright(
      author = "Matt Crinklaw-Vogt",
      copyright = "2011 Matt Crinklaw-Vogt",
      license = License.CC_BY_SA_3,
      url = "http://stackoverflow.com/questions/5507098/controlling-jtree-expansion-delay-on-drag-drop/5507194#5507194",
      note = "delayed expansion"
  )
  public void dragOver(DropTargetDragEvent e) {
    Point 		p;
    Graphics2D 		g2;
    final TreePath 	path;
    Rectangle 		rect;

    p    = e.getLocation();
    path = getClosestPathForLocation(p.x, p.y);

    if (!isExpanded(path)) {
      if (m_ExpansionDelay > 0) {
	m_ExpansionExecutor.schedule(new Runnable() {
	  public void run() {
	    // see if the node that the mouse is over now is the same node it was over 2 seconds ago
	    Point newPos = getMousePosition();
	    TreePath newPath = getClosestPathForLocation(newPos.x, newPos.y);
	    if (newPath.getLastPathComponent() == path.getLastPathComponent())
	      expandPath(path);
	  }
	}, m_ExpansionDelay, TimeUnit.MILLISECONDS);
      }
      else {
        expandPath(path);
      }
    }

    if (!DragSource.isDragImageSupported() && isDropEnabled()) {
      // remove old cue
      rect = m_RectCueLine.getBounds();
      paintImmediately(rect.x, rect.y, rect.width + 1, rect.height + 1);

      // new cue
      rect = getPathBounds(path);
      g2   = (Graphics2D) getGraphics();
      g2.setColor(m_ColorCueLine);
      m_RectCueLine.setRect(rect);
      g2.draw(m_RectCueLine);
    }
  }

  /**
   * Called if the user has modified
   * the current drop gesture.
   * <P>
   * @param dtde the <code>DropTargetDragEvent</code>
   */
  public void dropActionChanged(DropTargetDragEvent dtde) {
    // ignored
  }

  /**
   * Called while a drag operation is ongoing, when the mouse pointer has
   * exited the operable part of the drop site for the
   * <code>DropTarget</code> registered with this listener.
   *
   * @param dte the <code>DropTargetEvent</code>
   */
  public void dragExit(DropTargetEvent dte) {
    // we need to get rid of the cue line
    repaint();
  }

  /**
   * Called when the drag operation has terminated with a drop on
   * the operable part of the drop site for the <code>DropTarget</code>
   * registered with this listener.
   * <p>
   * This method is responsible for undertaking
   * the transfer of the data associated with the
   * gesture. The <code>DropTargetDropEvent</code>
   * provides a means to obtain a <code>Transferable</code>
   * object that represents the data object(s) to
   * be transfered.<P>
   * From this method, the <code>DropTargetListener</code>
   * shall accept or reject the drop via the
   * acceptDrop(int dropAction) or rejectDrop() methods of the
   * <code>DropTargetDropEvent</code> parameter.
   * <P>
   * Subsequent to acceptDrop(), but not before,
   * <code>DropTargetDropEvent</code>'s getTransferable()
   * method may be invoked, and data transfer may be
   * performed via the returned <code>Transferable</code>'s
   * getTransferData() method.
   * <P>
   * At the completion of a drop, an implementation
   * of this method is required to signal the success/failure
   * of the drop by passing an appropriate
   * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
   * dropComplete(boolean success) method.
   * <P>
   * Note: The data transfer should be completed before the call  to the
   * <code>DropTargetDropEvent</code>'s dropComplete(boolean success) method.
   * After that, a call to the getTransferData() method of the
   * <code>Transferable</code> returned by
   * <code>DropTargetDropEvent.getTransferable()</code> is guaranteed to
   * succeed only if the data transfer is local; that is, only if
   * <code>DropTargetDropEvent.isLocalTransfer()</code> returns
   * <code>true</code>. Otherwise, the behavior of the call is
   * implementation-dependent.
   * <P>
   * @param e the <code>DropTargetDropEvent</code>
   */
  public void drop(DropTargetDropEvent e) {
    Point 		p;
    TreePath 		dropPath;
    BaseTreeNode	target;

    p        = e.getLocation();
    dropPath = getClosestPathForLocation(p.x, p.y);
    if (dropPath != null)
      target = (BaseTreeNode) dropPath.getLastPathComponent();
    else
      target = null;

    try {
      if (isDropEnabled() && (target != null)) {
	e.acceptDrop(DnDConstants.ACTION_MOVE);
	showDropMenu(e, target);
      }
      else {
	e.getDropTargetContext().dropComplete(false);
	e.rejectDrop();
      }
    }
    catch (Exception ex) {
      e.getDropTargetContext().dropComplete(false);
      ex.printStackTrace();
      e.rejectDrop();
    }

    repaint();
  }

  /**
   * Called as the cursor's hotspot enters a platform-dependent drop site.
   * This method is invoked when all the following conditions are true:
   * <UL>
   * <LI>The cursor's hotspot enters the operable part of a platform-
   * dependent drop site.
   * <LI>The drop site is active.
   * <LI>The drop site accepts the drag.
   * </UL>
   *
   * @param dsde the <code>DragSourceDragEvent</code>
   */
  public void dragEnter(DragSourceDragEvent dsde) {
    // ignored
  }

  /**
   * Called as the cursor's hotspot moves over a platform-dependent drop site.
   * This method is invoked when all the following conditions are true:
   * <UL>
   * <LI>The cursor's hotspot has moved, but still intersects the
   * operable part of the drop site associated with the previous
   * dragEnter() invocation.
   * <LI>The drop site is still active.
   * <LI>The drop site accepts the drag.
   * </UL>
   *
   * @param dsde the <code>DragSourceDragEvent</code>
   */
  public void dragOver(DragSourceDragEvent dsde) {
    // ignored
  }

  /**
   * Called when the user has modified the drop gesture.
   * This method is invoked when the state of the input
   * device(s) that the user is interacting with changes.
   * Such devices are typically the mouse buttons or keyboard
   * modifiers that the user is interacting with.
   *
   * @param dsde the <code>DragSourceDragEvent</code>
   */
  public void dropActionChanged(DragSourceDragEvent dsde) {
    // ignored
  }

  /**
   * Called as the cursor's hotspot exits a platform-dependent drop site.
   * This method is invoked when any of the following conditions are true:
   * <UL>
   * <LI>The cursor's hotspot no longer intersects the operable part
   * of the drop site associated with the previous dragEnter() invocation.
   * </UL>
   * OR
   * <UL>
   * <LI>The drop site associated with the previous dragEnter() invocation
   * is no longer active.
   * </UL>
   * OR
   * <UL>
   * <LI> The drop site associated with the previous dragEnter() invocation
   * has rejected the drag.
   * </UL>
   *
   * @param dse the <code>DragSourceEvent</code>
   */
  public void dragExit(DragSourceEvent dse) {
    // ignored
  }

  /**
   * Checks whether the source data can be dropped here.
   * <br><br>
   * The default implementation always allows the drop.
   *
   * @param source	the data being transferred
   * @param target	the target node
   * @param position	where to drop the data
   * @return		true if can be dropped
   * @see		BaseTreeNode
   */
  protected boolean canDrop(Transferable source, TreeNode target, DropPosition position) {
    boolean	result;
    Node	parent;

    parent = (Node) target.getParent();

    switch (position) {
      case BENEATH:
	result = true;
	break;

      case HERE:
      case AFTER:
	result = (parent != null);
	break;

      default:
	result = false;
	System.err.println("canDrop/Unhandled drop position: " + position);
	break;
    }

    return result;
  }

  /**
   * This method is invoked to signify that the Drag and Drop
   * operation is complete. The getDropSuccess() method of
   * the <code>DragSourceDropEvent</code> can be used to
   * determine the termination state. The getDropAction() method
   * returns the operation that the drop site selected
   * to apply to the Drop operation. Once this method is complete, the
   * current <code>DragSourceContext</code> and
   * associated resources become invalid.
   *
   * @param e the <code>DragSourceDropEvent</code>
   */
  public void dragDropEnd(DragSourceDropEvent e) {
    // we need to get rid of the cue line
    repaint();
  }

  /**
   * Returns the string for the specified action.
   *
   * @param action	the action to get the string for
   * @return		the caption
   */
  protected String getDropMenuActionCaption(DropMenu action) {
    if (action == DropMenu.ADD)
      return "Add";
    else if (action == DropMenu.MOVE)
      return "Move";
    else if (action == DropMenu.CANCEL)
      return "Cancel";
    else if (action.getPosition() != null)
      return action.getPosition().toDisplay();
    else
      return action.toString();
  }

  /**
   * Returns the icon for the drop action.
   *
   * @param action	the action to get the icon for
   * @return		the icon or null if none available
   */
  protected ImageIcon getDropMenuActionIcon(DropMenu action) {
    return null;
  }

  /**
   * Displays a drop menu of how to drop the data.
   *
   * @param e		the drop event
   * @param target	the node that is targeted by the drop
   */
  protected void showDropMenu(DropTargetDropEvent e, BaseTreeNode target) {
    JPopupMenu			menu;
    JMenuItem			menuitem;
    final DropTargetDropEvent 	fEvent;
    final BaseTreeNode 		fTarget;
    final Transferable		fData;
    DropMenu			action;

    fEvent  = e;
    fTarget = target;
    fData   = e.getTransferable();

    // finish drop
    fEvent.getDropTargetContext().dropComplete(true);

    // build menu
    menu = new JPopupMenu();
    if (m_SourceNode == null)
      action = DropMenu.ADD;
    else
      action = DropMenu.MOVE;
    menuitem = new JMenuItem(getDropMenuActionCaption(action), getDropMenuActionIcon(action));
    menuitem.setEnabled(false);
    menu.add(menuitem);
    menu.addSeparator();

    for (DropPosition pos: DropPosition.values()) {
      final DropPosition fPos = pos;
      menuitem = new JMenuItem(fPos.toDisplay());
      menuitem.setEnabled(canDrop(fData, target, fPos));
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          doDrop(fData, fTarget, fPos);
          m_SourceNode = null;
        }
      });
      menu.add(menuitem);
    }

    menuitem = new JMenuItem("Cancel");
    menuitem.setIcon(getDropMenuActionIcon(DropMenu.CANCEL));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	// don't do anything
      }
    });
    menu.addSeparator();
    menu.add(menuitem);

    menu.show(this, e.getLocation().x, e.getLocation().y);
  }

  /**
   * Performs the actual drop of the dragged data.
   *
   * @param source	the data dragged from the source
   * @param target	the target node
   * @param position	the drop position
   */
  protected void doDrop(Transferable source, BaseTreeNode target, DropPosition position) {
    BaseTreeNode		top;
    final BaseTreeNode		fTop;
    BaseTreeNode 		parent;
    final BaseTreeNode 		fParent;
    int 			targetIndex;
    final int 			fTargetIndex;
    int 			sourceIndex;
    BaseTreeNode[]		newNodes;
    final BaseTreeNode[]	fNewNodes;
    final List<TreePath>	exp;

    exp = getExpandedNodes();

    if (m_SourceNode != null)
      newNodes = m_SourceNode;
    else
      newNodes = newTreeNodes(source);
    fNewNodes = newNodes;

    notifyNodeDroppedListeners(
	new NodeDroppedEvent(this, newNodes, NotificationTime.BEFORE));

    top     = null;
    parent  = null;
    try {
      switch (position) {
	case BENEATH:
	  parent  = target;
	  fParent = parent;
	  if (m_SourceNode != null)
	    top = getCommonAncestor((BaseTreeNode) m_SourceNode[0].getParent(), parent);
	  else
	    top = parent;
	  SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
	      for (BaseTreeNode node: fNewNodes)
		fParent.add(node);
	    }
	  });
	  break;

	case HERE:
	case AFTER:
	  parent      = (BaseTreeNode) target.getParent();
	  targetIndex = parent.getIndex(target);
	  if (position == DropPosition.AFTER)
	    targetIndex++;

	  if (m_SourceNode != null) {
	    if (newNodes[0].getParent().equals(parent)) {
	      top         = parent;
	      sourceIndex = parent.getIndex(newNodes[0]);
	      if (sourceIndex < targetIndex)
		targetIndex--;
	    }
	    else {
	      top = getCommonAncestor(newNodes[0], parent);
	    }
	  }
	  else {
	    top = parent;
	  }
	  fParent      = parent;
	  fTargetIndex = targetIndex;
	  SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
	      int targetIndex = fTargetIndex;
	      for (BaseTreeNode node: fNewNodes) {
		fParent.insert(node, targetIndex);
		targetIndex++;
	      }
	    }
	  });
	  break;

	default:
	  throw new IllegalStateException("Unhandled drop position: " + position);
      }
    }
    catch (IllegalArgumentException ex) {
      ex.printStackTrace();
    }

    // update tree
    if (top != null) {
      final BaseTreeNode finTop = top;
      SwingUtilities.invokeLater(new Runnable() {
	@Override
	public void run() {
	  ((DefaultTreeModel) getModel()).nodeStructureChanged(finTop);
	}
      });
    }
    
    // restore expansion state
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
	setExpandedNodes(exp);
      }
    });

    if (parent != null) {
      final BaseTreeNode finParent = parent;
      SwingUtilities.invokeLater(new Runnable() {
	public void run() {
	  // expand the parent
	  expand(finParent);
	  // notify listeners
	  if (newNodes != null)
	    notifyNodeDroppedListeners(new NodeDroppedEvent(DragAndDropTree.this, fNewNodes, NotificationTime.FINISHED));
	}
      });
    }
  }

  /**
   * Returns the appropriate cursor based on the DnD action.
   *
   * @param action	the DnD action
   * @return		the cursor
   */
  protected Cursor selectCursor (int action) {
    return ((action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop : DragSource.DefaultCopyDrop);
  }

  /**
   * Returns whether dragging is enabled.
   * <br><br>
   * The default implementation always returns false
   *
   * @return		true if dragging is enabled
   */
  protected boolean isDragEnabled() {
    return false;
  }

  /**
   * Returns whether dropping is enabled.
   * <br><br>
   * The default implementation always returns false
   *
   * @return		true if dropping is enabled
   */
  protected boolean isDropEnabled() {
    return false;
  }

  /**
   * Checks whether the source node can be dragged at all.
   * <br><br>
   * The default implementation allows all nodes to be dragged.
   *
   * @param source	the source node that is about to be dragged
   * @return		true if the source node can be dragged
   */
  protected boolean canStartDrag(BaseTreeNode[] source) {
    return true;
  }

  /**
   * Creates a new collection for transfer.
   *
   * @param nodes	the nodes to package
   * @return		the new collection
   */
  protected DragAndDropTreeNodeCollection newNodeCollection(BaseTreeNode[] nodes) {
    return new DragAndDropTreeNodeCollection(nodes);
  }

  /**
   * A <code>DragGestureRecognizer</code> has detected
   * a platform-dependent drag initiating gesture and
   * is notifying this listener
   * in order for it to initiate the action for the user.
   * <P>
   * @param e the <code>DragGestureEvent</code> describing
   * the gesture that has just occurred
   */
  public void dragGestureRecognized(DragGestureEvent e) {
    Cursor 	cursor;
    int		i;

    m_SourceNode = null;

    if (!isDragEnabled())
      return;
    if (getSelectionPath() == null)
      return;
    if (e.getTriggerEvent().isMetaDown())
      return;

    if (getSelectionModel().getSelectionMode() == TreeSelectionModel.SINGLE_TREE_SELECTION) {
      m_SourceNode    = new BaseTreeNode[1];
      m_SourceNode[0] = (BaseTreeNode) getSelectionPath().getLastPathComponent();
    }
    else {
      m_SourceNode = new BaseTreeNode[getSelectionPaths().length];
      for (i = 0; i < m_SourceNode.length; i++)
	m_SourceNode[i] = (BaseTreeNode) getSelectionPaths()[i].getLastPathComponent();
    }

    if (    (m_SourceNode == null)
	 || (m_SourceNode[0] == null)
	 || (m_SourceNode[0] == getModel().getRoot())
	 || !canStartDrag(m_SourceNode) )
      return;

    // start drag
    cursor = selectCursor(e.getDragAction());
    m_DragSource.startDrag(e, cursor, newNodeCollection(m_SourceNode), this);
  }

  /**
   * Adds the listener to the internal list of node drop listeners.
   *
   * @param l		the listener to add
   */
  public void addNodeDroppedListener(NodeDroppedListener l) {
    m_NodeDroppedListeners.add(l);
  }

  /**
   * Removes the listener from the internal list of node drop listeners.
   *
   * @param l		the listener to remove
   */
  public void removeNodeDroppedListener(NodeDroppedListener l) {
    m_NodeDroppedListeners.remove(l);
  }

  /**
   * Notifies all node dropped listeners with the specified event.
   *
   * @param e		the node dropped event to send
   */
  protected void notifyNodeDroppedListeners(NodeDroppedEvent e) {
    for (NodeDroppedListener l: m_NodeDroppedListeners)
      l.nodeDropped(e);
  }
}
