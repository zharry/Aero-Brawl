@echo off
echo Compiling...
cd src
dir /s /B *.java >> ../sources.txt
cd ..
cd NitroNet
dir /s /B *.java >> ../sources.txt
cd ..
javac -cp dependency/lwjgl.jar -cp dependency/lwjgl_util.jar -d out/production @sources.txt
pause
echo Running...
java -Djava.library.path=dependency/native/windows -cp dependency/lwjgl.jar -cp dependency/lwjgl_util.jar AeroBrawlMain
del sources.txt
