/*
*  FAQ.java Copyright (c) 2006,07 Swaroop Belur
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

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;


public class FAQ extends JFrame {

    public static void main(String[] args) {
        new FAQ();
    }
    public FAQ()
    {
        super("Jdec FAQ");
        createAllComponents();
        Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
        int x=(int)d.getWidth()/2-700/2;
        int y=(int)d.getHeight()/2-150;
        setBounds(x,y,700,300);

        setVisible(true);

    }

    private void createAllComponents()
    {
        JEditorPane editor=new JEditorPane();
        JScrollPane sp=new JScrollPane(editor);
        getContentPane().add(sp,BorderLayout.CENTER);
        EditorKit kit=new FaqKit();
        editor.setEditorKit(kit);
        editor.setContentType("text/plain");
        editor.setEditorKitForContentType("text/plain",kit);
        editor.setText(getFAQ());
        editor.setCaretPosition(0);
       // sp.move(0,0);
    }

    private String getFAQ()
    {
        StringBuffer sb=new StringBuffer("");
        String q="Question: I try to decompile a class file, but jdec does not show me any output";
        String a="Answer:  Please check the following\n";
        a+="\tOutput folder exists in the file system\n";
        a+="\tLog folder exists in the file system\n";
        String qa=q+"\n"+a+"\n";
        sb.append(qa);
        sb.append("\n-----------------------------------------------------------------\n");
        q="Question: All the settings are fine, but still jdec does not show me any output";
        a="Answer:  Please check the following\n";
        a+="\tOpen the log files and check for any exceptions\n";
        a+="\tif there is any exception probably it means jdec could not produce\n";
        a+="\tOutput file\n";
        a+="\tIn this case please report the error to project admin \n\tfor jdec\n";
        qa=q+"\n"+a+"\n\n";
        sb.append(qa);
        sb.append("\n-----------------------------------------------------------------\n");
        q="Question: Sometimes jdec takes considerable time to render the output.Why is this?";
        a="Answer:   Right now jdec is not a very fast decompiler\n";
        a+="\tBut there can be a case where class file is really big\n";
        a+="\tand jdec takes time not only for processing class file \n";
        a+="\tbut also for rendering the output\n";
        qa=q+"\n"+a+"\n";
        sb.append(qa);
        sb.append("\n-----------------------------------------------------------------\n");
        q="Question: How can i make the rendering process faster?";
        a="Answer: Please turn off syntax highlighting for decompiled output\n";
        a+="        This can be done by going to preferences\n";
        qa=q+"\n"+a;

        sb.append(qa);
        sb.append("\n-----------------------------------------------------------------\n");
        return sb.toString();
    }



    class FaqKit extends StyledEditorKit
    {

        public Document createDefaultDocument()
        {

            FAQDoc doc=new FAQDoc();
            return doc;

        }

    }

    class FAQDoc extends DefaultStyledDocument
    {
        String text="";
        int currentOffset=-1;
        String gotString="";
          private int endOffset=-1;
    private int startOffset=-1;
        private Element root=null;

        FAQDoc()
        {
             root=this.getDefaultRootElement();
        }

        public void insertString(int offset, String str, AttributeSet a)
        {

            try
            {
                super.insertString(offset, str, a);
                currentOffset=offset;
                gotString=str;
                text=this.getText(0,this.getLength());
                updateSyntaxandColorForGotText();
            }
            catch (BadLocationException e)
            {




            }
        }


        private void updateSyntaxandColorForGotText()
        {
            int offset=currentOffset;
            int stringLength=gotString.length();
            int end=offset+stringLength;
            // update the syntax/color/font for the total number of children/lines

            int startFrom=root.getElementIndex(offset); // From where to Begin
            int goTo=root.getElementIndex(end); // Go till this child pos/line

            int z=startFrom;
            while(z <= goTo)
            {
                processChild(text,z);
                z++;
            }
        }

          private void processChild(String text, int childIndex)
            {


                startOffset = root.getElement( childIndex ).getStartOffset();
                endOffset = root.getElement( childIndex ).getEndOffset() - 1;



               //((DefaultStyledDocument)thisDoc).setCharacterAttributes(startOffset, childLength, other, true);
                if(endOffset <= startOffset)return;
                scanTextForQA(text, startOffset, endOffset);

            }

         private void scanTextForQA(String text, int startOffset, int endOffset)
         {

          for(int z1=startOffset;z1<endOffset;z1++)
          {

              if(z1 < text.length() && text.charAt(z1)=='Q')
              {
                  char ar[]=new char[8];
                  ar[0]='Q';
                  int counter=1;
                  for(int e=z1+1;e<endOffset && e<text.length();e++)
                  {
                        if(text.charAt(e)==' ' || text.charAt(e)==':')
                        {
                            break;
                        }
                        ar[counter]=text.charAt(e);
                      counter++;

                  }
                  String q=new String(ar);
                  if(q.equals("Question"))
                  {
                    SimpleAttributeSet red=new SimpleAttributeSet();
                    StyleConstants.setForeground(red,Color.RED);
                    this.setCharacterAttributes(z1, 8, red, true);
                     z1+=8;
                  }


              }
              else if(z1 < text.length() && text.charAt(z1)=='A')
              {
                   char ar[]=new char[6];
                  ar[0]='A';
                  int counter=1;
                  for(int e=z1+1;e<endOffset && e<text.length();e++)
                  {
                        if(text.charAt(e)==' ' || text.charAt(e)==':')
                        {
                            break;
                        }
                        ar[counter]=text.charAt(e);
                        counter++;

                  }
                  String q=new String(ar);
                  if(q.equals("Answer"))
                  {
                    SimpleAttributeSet blue=new SimpleAttributeSet();
                    StyleConstants.setForeground(blue,Color.blue);
                    this.setCharacterAttributes(z1, 6, blue, true);
                    z1+=6;
                  }
              }

          }




         }


    }




}
