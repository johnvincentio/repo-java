rem @echo off
rem
echo Did you first run db2cmd?
rem
rem set PARAM1=%1
set PARAM1=C:\irac7\wrkspc\Pdf\Notes\StoredProcedures\1\99.sql
if "%PARAM1%" == "" goto usage
rem
set JVOUTFILE=%2
if "%JVOUTFILE%" == "" set JVOUTFILE=c:\tmp\jvdb2.txt
rem
set JVOUTFLAG=%3
if "%JVOUTFLAG%" == "" echo "" > %JVOUTFILE%
rem
rem set MYDATABASE="idevdb"
set MYDATABASE="IDEVDB"
set MYUSER="prc4031"
set MYPASSWORD="xxxx"
rem
echo.
echo Connecting to %MYDATABASE%
rem
db2 connect to %MYDATABASE% user %MYUSER% using %MYPASSWORD%
echo.
echo Running script %PARAM1%
db2 -td@ -vf %PARAM1% >> %JVOUTFILE%
echo.
echo Disconnecting
rem
db2 disconnect %MYDATABASE%
echo.
echo Output file is %JVOUTFILE%
goto done
:usage
echo Usage: jvdb2 file.sql
:done
