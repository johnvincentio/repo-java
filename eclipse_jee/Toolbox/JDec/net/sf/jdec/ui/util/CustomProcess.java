/*
 *  CustomProcess.java Copyright (c) 2006,07 Swaroop Belur
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

import net.sf.jdec.ui.main.UILauncher;

import javax.swing.*;
import java.io.IOException;


public class CustomProcess  {

    public static void main(String[] args) throws IOException {
        Runtime rt=Runtime.getRuntime();
        Process p=rt.exec("D:/SOFTWARE/UltraEdit/uedit32.exe "+args[0]);
    }

    public void launch(String process,Object args[])
    {
        try
        {
          Runtime rt=Runtime.getRuntime();
          String allargs="";
          for(int z=0;args!=null && z<args.length;z++)
          {
            allargs+=args[z];
            if(z < args.length-1)allargs+=" ";
          }
          Process p=rt.exec(process+" "+allargs);
        }
        catch(IOException ioe)
        {
            JOptionPane.showMessageDialog(UILauncher.getMainFrame(),"Failed to execute process");
        }
    }

//    public void restartJDEC()
//    {
//        String folder=UIUtil.jdecFolder;
//        String process="cmd /c java -cp "+folder+";"+folder+File.separator+"jdec.jar "+folder+File.separator+"UIJDec.bat";
//        launch(process,null);
//    }


}
