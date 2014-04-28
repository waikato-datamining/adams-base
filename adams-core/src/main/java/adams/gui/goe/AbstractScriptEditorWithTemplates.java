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
 * AbstractScriptEditorWithTemplates.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import adams.core.Properties;
import adams.env.Environment;
import adams.env.ScriptEditorTemplatesDefinition;

/**
 * Ancestor for script editors that support text templates.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptEditorWithTemplates
  extends AbstractScriptEditor {

  /** the name of the props file. */
  public final static String FILENAME = "AbstractScriptEditorTemplates.props";

  /** the general properties. */
  protected static Properties m_Properties;

  /**
   * Returns the key in the props file to retrieve.
   * 
   * @return		the key
   */
  protected abstract String getKey();
  
  /**
   * Returns the templates to display.
   * 
   * @return		the template file names
   */
  protected String[] getTemplates() {
    List<String>	result;
    String		value;
    String[]		parts;
    
    result = new ArrayList<String>();
    
    value  = getProperties().getProperty(getKey());
    if (value != null) {
      parts = value.replaceAll(" ", "").split(",");
      for (String part: parts)
	result.add(part);
    }
    
    return result.toArray(new String[result.size()]);
  }

  /**
   * Loads the template.
   * 
   * @param name	the resource name
   * @return		the template content, null in case of error
   */
  protected String loadTemplate(String name) {
    StringBuilder	result;
    InputStream		stream;
    byte[]		buffer;
    int			read;
    
    result = new StringBuilder();
    
    try {
      buffer = new byte[1024];
      stream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(name));
      while ((read = stream.read(buffer)) >= 0)
	result.append(new String(buffer, 0, read));
    }
    catch (Exception e) {
      System.err.println("Failed to load template: " + name);
      e.printStackTrace();
      return null;
    }
    
    return result.toString();
  }
  
  /**
   * Hook-method to add further menu items to the menu of the "..." button.
   * <p/>
   * Adds the templates, if any to the menu.
   * 
   * @param menu	the popup menu for the button
   */
  @Override
  protected void addAdditionalMenuItems(JPopupMenu menu) {
    JMenu	submenu;
    JMenuItem	menuitem;
    String[]	templates;
    File	file;
    
    super.addAdditionalMenuItems(menu);
    
    templates = getTemplates();
    if (templates.length == 0)
      return;
    
    submenu = new JMenu("Templates");
    menu.add(submenu);
    
    for (final String template: templates) {
      file     = new File(template);
      menuitem = new JMenuItem(file.getName());
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String content = loadTemplate(template);
          if (content != null)
            m_TextStatement.setContent(content);
        }
      });
      submenu.add(menuitem);
    }
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(ScriptEditorTemplatesDefinition.KEY);

    return m_Properties;
  }
}
