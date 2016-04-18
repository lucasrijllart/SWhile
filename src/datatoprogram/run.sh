#!/bin/bash
# My first script

java -cp javacc.jar javacc DataToProgram.jj
echo - Generated jj file

javac *.java
echo - Compiled all files

cd ../../
echo - Starting converter
java src/datatoprogram/DataToProgram "/Users/Lucas/Desktop/data.txt"
echo - Conversion terminated

echo - Script terminated