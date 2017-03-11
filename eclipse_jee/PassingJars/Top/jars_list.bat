@echo off

set JAVA_HOME=C:\Program Files\IBM\SDP70\jdk
set path=%JAVA_HOME%\bin;%PATH%
echo.
echo Base.jar
echo.
jar tf C:\jv\development\apps\PassingJars\base.jar

echo.
echo Middle.jar
echo.
jar tf C:\jv\development\apps\PassingJars\middle.jar
echo.
