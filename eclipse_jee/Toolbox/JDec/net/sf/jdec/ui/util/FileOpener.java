/*
 *  FileOpener.java Copyright (c) 2006,07 Swaroop Belur
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.sf.jdec.ui.util;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

public class FileOpener extends JFrame {
    
    private File SelectedFile=null;
    public File getSelectedFile( ) {
        return SelectedFile;
    }
    public FileOpener(FileFilter filter) {
        super("File Chooser Test Frame");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container c = getContentPane( );
        c.setLayout(new FlowLayout( ));
        JFileChooser chooser = new JFileChooser(".");
        if(filter!=null)
            chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(true);
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File[] sf = chooser.getSelectedFiles( );
            String filelist = "";
            if (sf!=null && sf.length > 0) {
                filelist = sf[0].getName( );
                String filePath=sf[0].getAbsolutePath();
                if(sf[0].isFile()) {
                    this.SelectedFile=sf[0];
                    int lastSlash=filePath.lastIndexOf(File.separator);
                }
            } else {
                SelectedFile=null;
            }
        }
    }
}