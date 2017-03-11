@echo off

set ANT_HOME=C:/apache-ant-1.7.1
set path=%ANT_HOME%\bin;%PATH%
echo.
echo Ant
echo.
ant make_jar
