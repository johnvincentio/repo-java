package net.sf.jdec.settings;
/*
 *  ExceptionSettings.java Copyright (c) 2006,07 Swaroop Belur
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
 * The idea is to control in a single place
 * how and when/where the exceptions should
 * be thrown.
 *
 * An example would be return some status
 * instead of throwing an exception from a method.
 *
 * 
 *
 * @author swaroop belur
 * @since 1.2.1
 */

public class ExceptionSettings {

  private  boolean throwExceptionOnInvalidSourceCodeIndexCheck = true;

  /**
   * Make this configurable ...[todo for UI ] 
   */
  private boolean throwFatalExceptionOnInvalidVariableConfiguration = false;

  ExceptionSettings(){

  }
 
  public  boolean throwExceptionOnInvalidSourceCodeIndexCheck() {
    return throwExceptionOnInvalidSourceCodeIndexCheck;
  }

  public void setThrowExceptionOnInvalidSourceCodeIndexCheck(boolean throwExceptionOnInvalidSourceCodeIndexCheck) {
    this.throwExceptionOnInvalidSourceCodeIndexCheck = throwExceptionOnInvalidSourceCodeIndexCheck;
  }


  public boolean throwFatalExceptionOnInvalidVariableConfiguration() {
    return throwFatalExceptionOnInvalidVariableConfiguration;
  }

  public void setThrowFatalExceptionOnInvalidVariableConfiguration(boolean throwFatalExceptionOnInvalidVariableConfiguration) {
    this.throwFatalExceptionOnInvalidVariableConfiguration = throwFatalExceptionOnInvalidVariableConfiguration;
  }
}