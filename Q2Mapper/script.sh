#!/usr/bin/env bash 
javac *.java
cat part-r-00000 | java Mapper | sort | java Reducer > result
