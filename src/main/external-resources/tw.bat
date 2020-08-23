echo off

REM *********************************************
REM * Starts tiddlywiki generator on windows.
REM * History
REM *   20/12/2019  M. Lohn     Created
REM *********************************************

REM *********************************************
REM * set cuurent directory
REM *********************************************
FOR /F "usebackq" %%i IN (`cd`) DO set CURRDIR=%%i


REM *********************************************
REM * check java environment
REM *********************************************
:checkjavahome
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\jdk\jre\bin\java.exe" goto noJavaHome
:setclasspath


REM *********************************************
REM * set classpath
REM *********************************************
:setclasspath
set TIDDLYWIKI_HOME=%CURRDIR%
subst w: /d 
subst w: %TIDDLYWIKI_HOME%\lib
set LOCALCLASSPATH=%TIDDLYWIKI_HOME%\classes

call "%CURRDIR%\lcp.bat" w:\

FOR %%i IN ("w:\*.jar") DO call "%CURRDIR%\lcp.bat" %%i

goto checkjava


REM *********************************************
REM * check java environment
REM *********************************************
:checkjava
set _JAVACMD=%ORACLE_HOME%\jdk\jre\bin\java
goto runConfig


REM *********************************************
REM * run
REM *********************************************
:runConfig
"%_JAVACMD%" -Xmx512m -Dlogging.dir="$CURRDIR" -Dworking.dir="$CURRDIR" -classpath "%LOCALCLASSPATH%" de.bimalo.tiddlywiki.fs.TiddlyWikiGenerator %1 %2 %3 %4
goto end


REM *********************************************
REM * JAVA_HOME error
REM *********************************************
:noJavaHome
echo Warning: JAVA_HOME environment variable is not set.
goto end

:end