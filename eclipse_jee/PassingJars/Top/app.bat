@echo off
rem
rem script to run the grepdir Java program
rem
set JAVA_HOME=C:\Program Files\IBM\SDP70\jdk
set path=%JAVA_HOME%\bin;%PATH%
rem
set classpath=C:\jv\development\apps\PassingJars\Top;C:\jv\development\apps\PassingJars\middle.jar;%JAVA_HOME%\lib
java com.idc.top.Top
