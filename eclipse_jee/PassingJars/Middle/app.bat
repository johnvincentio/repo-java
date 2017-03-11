@echo off
rem
rem script to run the Java program
rem
set JAVA_HOME=C:\Program Files\IBM\SDP70\jdk
set path=%JAVA_HOME%\bin;%PATH%
rem
set classpath=C:\jv\development\apps\PassingJars\Middle;C:\jv\development\apps\PassingJars\base.jar;%JAVA_HOME%\lib
java com.idc.middle.Middle
