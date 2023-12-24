@echo off
echo [Step 1] Cleaning logs
cd logs
del /f *.log
cd ..
echo [Step 2] Cleaning function bytecode 
cd functions
del /f *.class
cd ..
echo [Step 3] Cleaning framework bytecode
del /f *.class
cd lib
del /f *.class
cd ..
if %2==run goto run
if %2==clean goto end
goto end
:run
echo [Step 4] Building function list
cd functions
dir /A /B>../flist.txt
echo [Step 5] Compiling functions list
cd ..
for /F "tokens=*" %%A in ('type flist.txt') do (
    echo 	[+] compiling for [%%A]
    javac functions/%%A
)
echo .
echo [Step 6] Deleting function list
del /F flist.txt
echo [Step 7] Recompile framework
javac %1.java
javac lib/ContainerFactory.java
javac lib/Generator.java
java %1
:end