# SWhile

## Introduction
SWhile (SimpleWhile) is an IDE for the simple language WHILE created by Neil Jones in his book “Computability and Complexity” (1997) and adapted by Bernhard Reus in his book “Limits of Computation: From a Programming Perspective” (2016).

### Usage
To open SWhile, run the SWhile.jar file

### Grammar
The grammar for WHILE is situated in the help tab of the menu bar.

### Notes
If the program you want to macro call is open in the IDE, just use its name instead of path.
To use simplified macros, files need to be saved to disk!

If a number should be bigger than 2^32, use a tree and not a number.

### Types
Types in SWhile are implicit. Every variable can hold any type of expression, but variables are not bound to any type. To check which operations are valid on types, check the Know operations below.

### Known operations
```
CONS
	nil num   : num + 1
	nil bool  : if bool is false, true
	bool nil  : if bool is false, true
	nil list  : add nil to list
	num list  : add num to list
	bool list : add bool to list
	list list : add list to list
	atom list : add atom to list
```

Every other operation will add the binary tree values together

```
HD
	bool : false
	list : first element of list
```

Every other operation will get the head of the binary tree

```
TL
	num  : num - 1
	bool : false
	list : remove first element of list
```
Every other operation will get the tail of the binary tree

### Preview
See the [poster presentation](https://github.com/lucasrijllart/SWhile/files/5321945/118472_Poster.pdf)

Main IDE screen:

![Main_Screen](https://user-images.githubusercontent.com/11093148/94996175-f0eb7f80-059a-11eb-9c5f-4f10bdf0414e.png)

Example of translator functionality:

![Translator](https://user-images.githubusercontent.com/11093148/94996180-f34dd980-059a-11eb-8476-6d83b3e77e0a.png)

### Contact
Please contact me at: lr255@sussex.ac.uk
or contact the supervisor of this project: bernhard@sussex.ac.uk 
