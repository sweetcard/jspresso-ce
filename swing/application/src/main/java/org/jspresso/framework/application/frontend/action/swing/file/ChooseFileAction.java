/*
 * Copyright (c) 2005-2010 Vincent Vandenschrick. All rights reserved.
 *
 *  This file is part of the Jspresso framework.
 *
 *  Jspresso is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jspresso is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Jspresso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jspresso.framework.application.frontend.action.swing.file;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.jspresso.framework.application.frontend.action.swing.AbstractSwingAction;

/**
 * Initiates a file choosing action.
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class ChooseFileAction extends AbstractSwingAction {

  private String                    defaultFileName;
  private Map<String, List<String>> fileFilter;

  /**
   * Sets the defaultFileName.
   * 
   * @param defaultFileName
   *          the defaultFileName to set.
   */
  public void setDefaultFileName(String defaultFileName) {
    this.defaultFileName = defaultFileName;
  }

  /**
   * Sets the fileFilter. Filter file types are a map of descriptions keying
   * file extension arays.
   * 
   * @param fileFilter
   *          the fileFilter to set.
   */
  public void setFileFilter(Map<String, List<String>> fileFilter) {
    this.fileFilter = fileFilter;
  }

  /**
   * Gets the file chooser.
   * 
   * @param context
   *          the action context.
   * @return the file chooser.
   */
  protected JFileChooser createFileChooser(Map<String, Object> context) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle(getI18nName(getTranslationProvider(context),
        getLocale(context)));
    Map<String, List<String>> executionFileFilter = getFileFilter(context);
    if (executionFileFilter != null) {
      for (Map.Entry<String, List<String>> fileTypeEntry : executionFileFilter
          .entrySet()) {
        StringBuffer extensionsDescription = new StringBuffer(" (");
        for (String fileExtension : fileTypeEntry.getValue()) {
          extensionsDescription.append("*").append(fileExtension).append(" ");
        }
        extensionsDescription.append(")");
        fileChooser.addChoosableFileFilter(new FileFilterAdapter(fileTypeEntry
            .getValue(), getTranslationProvider(context).getTranslation(
            fileTypeEntry.getKey(), getLocale(context))
            + extensionsDescription.toString()));
      }
    }
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    String fileName = getFileName(context);
    if (fileName != null) {
      fileChooser.setSelectedFile(new File(fileName));
    }
    return fileChooser;
  }

  /**
   * Gets the fileFilter.
   * 
   * @param context
   *          the action context.
   * @return the fileFilter.
   */
  protected Map<String, List<String>> getFileFilter(Map<String, Object> context) {
    return fileFilter;
  }

  private static class FileFilterAdapter extends FileFilter {

    private Collection<String> allowedExtensions;
    private String             description;

    /**
     * Constructs a new <code>FileFilterAdapter</code> instance.
     * 
     * @param description
     * @param allowedExtensions
     */
    public FileFilterAdapter(Collection<String> allowedExtensions,
        String description) {
      this.allowedExtensions = new HashSet<String>(allowedExtensions);
      this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }
      String ext = null;
      String s = f.getName();
      int i = s.lastIndexOf('.');
      if (i > 0 && i < s.length() - 1) {
        ext = s.substring(i).toLowerCase();
      }
      return ext != null && allowedExtensions.contains(ext.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
      return description;
    }

  }

  /**
   * Computes a file name to save the file. Defaults to the action default file
   * name parameterized in the action.
   * 
   * @param context
   *          the action context.
   * @return the file name to save the file under.
   */
  protected String getFileName(Map<String, Object> context) {
    return defaultFileName;
  }
}
