package net.sf.jdec.io;

import java.io.IOException;
/*
 *  MemoryDumper.java Copyright (c) 2006,07 Swaroop Belur 
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

/**
 * TODO: Make this class usable from outside for next release
 * All operations like back,fwd should be taken into account.
 * @author swaroop belur
 * @since 1.2.1
 */

public class MemoryDumper extends Writer {

    private static final String EMPTY_DUMP = "<EMPTY_MEMORY_DUMP>";

    private StringBuffer inMemoryBuffer = null;


    private static  final MemoryDumper writer  = newMemoryDumper();

    private  MemoryDumper() {
        super();
        inMemoryBuffer = new StringBuffer();
    }

    public MemoryDumper dumpString(String string){
        inMemoryBuffer.append(string);
        return this;
    }

    public MemoryDumper newLine(){
        inMemoryBuffer.append(System.getProperty("line.separator"));
        return this;
    }

    public MemoryDumper newLine(int times){
        for(int i=1;i<=times;i++) {
            newLine();
        }
        return this;
    }


    public MemoryDumper dumpStringAndSendNewLine(String string){
        dumpString(string);
        newLine();
        return this;
    }


    public MemoryDumper dumpStringAndSendNewLine(String string , int times){
        dumpString(string);
        newLine(times);
        return this;
    }

    public MemoryDumper toConsole(){
        if(inMemoryBuffer!=null && inMemoryBuffer.length() > 0){
            System.out.println(self());
            System.out.println(inMemoryBuffer);
        }
        return this;
    }

    public static MemoryDumper newMemoryDumper(){

            return writer == null?new MemoryDumper():writer;

    }

    public void reset(){
        inMemoryBuffer = new StringBuffer();
    }

    public static  Writer getMemoryWriter(){
        return writer;
    }


    private String self(){
        return "Message from MemoryDumper : "+toString() + newLine(2);
    }

    public String getDump() {
        return inMemoryBuffer != null ?inMemoryBuffer.toString() : EMPTY_DUMP ;
    }


    public void writeOutput(String data) throws IOException {
        dumpStringAndSendNewLine(data);
    }

    public void writeLog(String data) throws IOException {
    }

    public void writeLog(String data, String level) throws IOException {
    }
}