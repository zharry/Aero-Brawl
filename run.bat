@echo off
echo Compiling...
cd src
dir /s /B *.java >> ../sources.txt
cd ..
cd NitroNet
dir /s /B *.java >> ../sources.txt
cd ..
mkdir output
javac -cp dependency/lwjgl.jar;dependency/lwjgl_util.jar -d output @sources.txt
pause
echo Running...
java -Djava.library.path=dependency/native/windows -cp dependency/lwjgl.jar;dependency/lwjgl_util.jar;output AeroBrawlMain
del sources.txt
