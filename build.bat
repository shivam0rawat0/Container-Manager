@echo off
echo [Step 1] Cleaning logs
cd logs
del /f *.log
cd ..
echo [Step 2] Cleaning function bytecode 
cd functions
del /F *.class
echo [Step 3] Building function list
dir /A /B>../flist.txt
echo [Step 4] Compiling functions list
cd ..
for /F "tokens=*" %%A in ('type flist.txt') do (
    echo 	[+] compiling for [%%A]
    javac functions/%%A
)
echo .
echo [Step 5] Deleting function list
del /F flist.txt
echo [Step 6] Cleaning framework bytecode
del /F *.class
echo [Step 7] Recompile framework
javac %1.java
javac lib/ContainerFactory.java
javac lib/Generator.java
java %1