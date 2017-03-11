@echo off

set ANT_HOME=C:/apache-ant-1.7.1
set path=%ANT_HOME%\bin;%PATH%

set JAVA_HOME=C:\Program Files\IBM\SDP70\jdk
set path=%JAVA_HOME%\bin;%PATH%;c:\jv\utils
set Classpath=%JAVA_HOME%\lib

echo.
echo Ant
echo.
ant -version
