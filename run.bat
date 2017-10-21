@echo off
echo Compiling...
dir sources.txt
setlocal enabledelayedexpansion
(for /f "delims=" %%f in ('dir /S /B src\*.java') do @set v=%%f&@echo "!v:\=\\!") >> sources.txt
(for /f "delims=" %%f in ('dir /S /B NitroNet\*.java') do @set v=%%f&@echo "!v:\=\\!") >> sources.txt
mkdir output
javac -cp dependency/lwjgl.jar;dependency/lwjgl_util.jar -d output @sources.txt
del sources.txt
pause
echo Running...
java -Djava.library.path=dependency/native/windows -cp dependency/lwjgl.jar;dependency/lwjgl_util.jar;res;output AeroBrawlMain
