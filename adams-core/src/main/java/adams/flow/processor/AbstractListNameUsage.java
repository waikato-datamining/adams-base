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
 * AbstractListNameUsage.java
 * Copyright (C) 2015 University of Waikato, Hamilton,  Zealand
 */
package adams.flow.processor;

import adams.core.option.OptionHandler;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanel;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ancestor for processors that locate usages of a certain name.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of
 */
public abstract class AbstractListNameUsage<T>
  extends AbstractListingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 7133896476260133469L;

  /** the name to look for. */
  protected String m_Name;

  /** the current actor being processed. */
  protected transient AbstractActor m_Current;
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"name", "name",
	"");
  }

  /**
   * Sets the name to look for.
   *
   * @param value 	the name
   */
  public void setName(String value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the name to look for.
   *
   * @return 		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String nameTipText();

  /**
   * Checks whether the located object matches the name that we are looking for.
   *
   * @param obj		the object to check
   * @return		true if a match
   */
  protected abstract boolean isNameMatch(Object obj);

  /**
   * Creates a location string used in the list.
   *
   * @param owner	the option handler
   * @param obj		the object where the name was located
   * @return		the generated location string
   */
  protected abstract String createLocation(OptionHandler owner, Object obj);

  /**
   * Checks whether the object is valid and should be added to the list.
   *
   * @param handler	the option handler this object belongs to
   * @param obj		the object to check
   * @return		true if valid
   */
  @Override
  protected boolean isValid(OptionHandler handler, Object obj) {
    return isNameMatch(obj);
  }

  /**
   * Returns the string representation of the object that is added to the list.
   *
   * @param handler	the option handler this object belongs to
   * @param obj		the object to turn into a string
   * @return		the string representation, null if to ignore the item
   */
  @Override
  protected String objectToString(OptionHandler handler, Object obj) {
    return createLocation(handler, obj);
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  protected void processActor(AbstractActor actor) {
    m_Current = actor;
    super.processActor(actor);
  }
  

  /**
   * Returns whether the list should be sorted.
   *
   * @return		true if the list should get sorted
   */
  @Override
  protected boolean isSortedList() {
    return true;
  }

  /**
   * Returns whether the list should not contain any duplicates.
   *
   * @return		true if the list contains no duplicates
   */
  @Override
  protected boolean isUniqueList() {
    return true;
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  @Override
  public Component getGraphicalOutput() {
    final BaseListWithButtons	result;
    DefaultListModel<String>	model;
    final JButton		buttonCopy;
    final JButton		buttonJumpTo;
    final Flow			flow;

    if (m_Current instanceof Flow)
      flow = (Flow) m_Current;
    else if ((m_Current != null) && (m_Current.getRoot() instanceof Flow))
      flow = (Flow) m_Current.getRoot();
    else
      flow = null;

    result = new BaseListWithButtons();
    model = new DefaultListModel<>();
    for (String item: m_List)
      model.addElement(item);
    result.setModel(model);

    buttonCopy = new JButton("Copy");
    buttonCopy.setEnabled(false);
    buttonCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	Object[] values = result.getSelectedValues();
	StringBuilder content = new StringBuilder();
	for (Object value: values) {
	  if (content.length() > 0)
	    content.append("\n");
	  content.append("" + value);
	}
	GUIHelper.copyToClipboard(content.toString());
      }
    });
    result.addToButtonsPanel(buttonCopy);

    if ((flow != null) && (flow.getParentComponent() != null)) {
      buttonJumpTo = new JButton("Jump to");
      buttonJumpTo.setEnabled(false);
      buttonJumpTo.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  if (result.getSelectedIndex() > -1) {
	    if (flow.getParentComponent() instanceof FlowPanel) {
	      ((FlowPanel) flow.getParentComponent()).getTree().locateAndDisplay(
		"" + result.getSelectedValue());
	    }
	  }
	}
      });
      result.addToButtonsPanel(buttonJumpTo);
    }
    else {
      buttonJumpTo = null;
    }

    result.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	buttonCopy.setEnabled(result.getSelectedIndices().length > 0);
	if (buttonJumpTo != null)
	  buttonJumpTo.setEnabled(result.getSelectedIndices().length == 1);
      }
    });

    return result;
  }
}
