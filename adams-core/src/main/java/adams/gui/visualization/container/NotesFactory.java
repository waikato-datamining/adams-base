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
 * NotesFactory.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import adams.core.Properties;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.id.DatabaseIDHandler;
import adams.data.report.ReportHandler;
import adams.env.Environment;
import adams.env.ScriptingDialogDefinition;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseMultiPagePane;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.scripting.SyntaxDocument;

/**
 * A factory for GUI components for notes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NotesFactory {

  /**
   * A specialized JTextPane for displaying the notes of a spectrum.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @param <T> the type of container to use
   */
  public static class TextPane<T extends AbstractContainer>
    extends JTextPane {

    /** for serialization. */
    private static final long serialVersionUID = -1236250353157866097L;

    /** the underlying data. */
    protected T m_Data;

    /**
     * Initializes the text pane with no notes.
     */
    public TextPane() {
      this(null);
    }

    /**
     * Initializes the text pane with the notes in the given spectrum.
     *
     * @param data	the data to initialize with
     */
    public TextPane(T data) {
      super();

      setDocument(createDocument());
      setEditable(false);

      setData(data);
    }

    /**
     * Creates a new document for the dialog, with syntax highlighting support.
     *
     * @return		the new document
     */
    protected Document createDocument() {
      Document	result;
      Properties	props;

      props  = Environment.getInstance().read(ScriptingDialogDefinition.KEY);
      result = new SyntaxDocument(props);

      return result;
    }

    /**
     * Sets the data and spectrum notes.
     *
     * @param data	the spectrum container containing the spectrum notes
     */
    public void setData(T data) {
      Notes		notes;
      StringBuilder	buffer;
      Notes		subnotes;

      m_Data = data;

      if (m_Data.getPayload() instanceof NotesHandler) {
	notes  = ((NotesHandler) m_Data.getPayload()).getNotes();
	buffer = new StringBuilder();

	// errors
	subnotes = notes.getErrors();
	buffer.append("Errors\n");
	if (subnotes.size() == 0)
	  buffer.append("-none-");
	else
	  buffer.append(subnotes.toString());
	buffer.append("\n");

	// warnings
	subnotes = notes.getWarnings();
	buffer.append("\n");
	buffer.append("Warnings\n");
	if (subnotes.size() == 0)
	  buffer.append("-none-");
	else
	  buffer.append(subnotes.toString());
	buffer.append("\n");

	// others
	subnotes = notes.getOthers();
	buffer.append("\n");
	buffer.append("Others\n");
	if (subnotes.size() == 0)
	  buffer.append("-none-");
	else
	  buffer.append(subnotes.toString());
	buffer.append("\n");

	// process information
	subnotes = notes.getProcessInformation();
	buffer.append("\n");
	buffer.append("Process information\n");
	if (subnotes.size() == 0)
	  buffer.append("-none-");
	else
	  buffer.append(subnotes.toString().replaceAll(Notes.PROCESS_INFORMATION + "[^\n]*\n", ""));
	buffer.append("\n");

	setText("");
	try {
	  getDocument().insertString(0, buffer.toString(), null);
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
      else {
	setText("");
      }
    }

    /**
     * Returns the underlying data.
     *
     * @return		the spectrum container
     */
    public T getData() {
      return m_Data;
    }

    /**
     * Sets the size of the text pane. Gets adapted.
     *
     * @param d		the dimension
     */
    @Override
    public void setSize(Dimension d) {
      if (d.width < getGraphicsConfiguration().getBounds().width)
        d.width = getGraphicsConfiguration().getBounds().width;
      super.setSize(d);
    }

    /**
     * Always returns false.
     *
     * @return		always false
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
      return false;
    }
  }

  /**
   * A specialized multi-page pane that displays container notes.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @param <T> the type of container to use
   */
  public static class MultiPagePane<T extends AbstractContainer>
    extends BaseMultiPagePane {

    /** for serialization. */
    private static final long serialVersionUID = -113778971321461204L;

    /** the underlying data. */
    protected List<T> m_Data;

    /**
     * Initializes the multi-page pane with not notes.
     */
    public MultiPagePane() {
      this(null);
    }

    /**
     * Initializes the multi-page pane with the given spectrum notes.
     *
     * @param data	the spectrum containers containing the spectrum notes
     */
    public MultiPagePane(List<T> data) {
      super();

      setData(data);
    }

    /**
     * Sets the data and spectrum notes.
     *
     * @param data	the spectrum containers containing the spectrum notes
     */
    public synchronized void setData(List<T> data) {
      int	i;

      m_Data = new ArrayList<T>();
      if (data != null) {
	for (i = 0; i < data.size(); i++)
	  m_Data.add(data.get(i));
      }
      update();
    }

    /**
     * Returns the underlying data.
     *
     * @return		the containers
     */
    public List<T> getData() {
      List<T>	result;

      result = new ArrayList<T>();
      result.addAll(m_Data);

      return result;
    }

    /**
     * updates the multi-page pane.
     */
    protected synchronized void update() {
      int		i;
      TextPane		text;
      ReportHandler	handler;
      String		title;

      removeAll();

      for (i = 0; i < m_Data.size(); i++) {
	if (!(m_Data.get(i).getPayload() instanceof ReportHandler))
	  continue;
	handler = (ReportHandler) m_Data.get(i).getPayload();
        if (!handler.hasReport())
          continue;

        text = getTextPane(m_Data.get(i));
        if (m_Data.get(i) instanceof NamedContainer) {
          title = ((NamedContainer) m_Data.get(i)).getID();
          if (m_Data.get(i).getPayload() instanceof DatabaseIDHandler)
            title += " (" +  ((DatabaseIDHandler) m_Data.get(i).getPayload()).getDatabaseID() + ")";
        }
        else {
          title = m_Data.get(i).toString();
        }
        addPage(title, new BaseScrollPane(text));
      }
    }
  }

  /**
   * A specialized dialog that displays informative statistics.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @param <T> the type of container to use
   */
  public static class Dialog<T extends AbstractContainer>
    extends BaseDialog {

    /** for serialization. */
    private static final long serialVersionUID = 1739123906988587074L;

    /** the default width. */
    public final static int DEFAULT_WIDTH = 600;

    /** the minimum height. */
    public final static int MIN_HEIGHT = 200;

    /** the dialog itself. */
    protected Dialog m_Self;

    /** the multi-page pane for displaying the statistics. */
    protected MultiPagePane m_MultiPagePane;

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
     * For initializing members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_Self = this;
    }

    /**
     * Initializes the components.
     */
    @Override
    protected void initGUI() {
      JPanel	panel;
      JButton	buttonOK;

      super.initGUI();

      setTitle("Notes");
      getContentPane().setLayout(new BorderLayout());

      // multi-page pane
      m_MultiPagePane = getMultiPagePane(null);
      getContentPane().add(m_MultiPagePane, BorderLayout.CENTER);

      // OK button
      panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(panel, BorderLayout.SOUTH);

      buttonOK = new JButton("OK");
      buttonOK.setMnemonic('O');
      buttonOK.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          m_Self.setVisible(false);
        }
      });
      panel.add(buttonOK);

      adjustSize();
      setLocationRelativeTo(getOwner());
    }

    /**
     * Adjusts the size of the dialog.
     */
    protected void adjustSize() {
      int	height;

      pack();

      height = (int) getSize().getHeight();
      if (height < MIN_HEIGHT)
	height = MIN_HEIGHT;

      setSize(new Dimension(DEFAULT_WIDTH, height));
    }

    /**
     * Sets the data to display.
     *
     * @param value	the underlying containers
     */
    public synchronized void setData(List<T> value) {
      m_MultiPagePane.setData(value);

      if (!isVisible()) {
	adjustSize();
	setLocationRelativeTo(getOwner());
	GUIHelper.setSizeAndLocation(this, this);
      }
    }

    /**
     * Returns the underlying data.
     *
     * @return		the containers
     */
    public List<T> getData() {
      return m_MultiPagePane.getData();
    }
  }

  /**
   * Returns a new text pane for the given notes.
   *
   * @param data	the container to create a text pane for
   * @return		the tabbed pane
   */
  public static TextPane getTextPane(AbstractContainer data) {
    return new TextPane(data);
  }

  /**
   * Returns a new multi-page page for the given notes.
   *
   * @param data	the containers to create a multi-page pane for
   * @return		the multi-page pane
   */
  public static MultiPagePane getMultiPagePane(List<AbstractContainer> data) {
    return new MultiPagePane(data);
  }

  /**
   * Returns a new dialog for displaying notes.
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Dialog owner, ModalityType modality) {
    return new Dialog(owner, modality);
  }

  /**
   * Returns a new dialog for displaying notes.
   *
   * @param owner	the owning component
   * @param modal	if true then the dialog will be modal
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Frame owner, boolean modal) {
    return new Dialog(owner, modal);
  }
}
