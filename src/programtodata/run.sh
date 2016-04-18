#!/bin/bash
# My first script

java -cp javacc.jar javacc ProgramToData.jj
echo - Generated jj file

javac *.java
echo - Compiled all files

echo - Starting converter
java ProgramToData "/Users/Lucas/Desktop/prog.txt"
echo - Conversion terminated

echo - Script terminated