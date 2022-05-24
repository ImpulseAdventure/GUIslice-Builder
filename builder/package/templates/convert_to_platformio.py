import os
import sys
import fileinput
import re

def main():
	try:
			args = sys.argv[1:]
	except IndexError:
			raise SystemExit(f"Usage: {sys.argv[0]} <config filename to convert to platformio.ini>")
			
param_1= sys.argv[1] 
strH = ".h"
strT = ".txt"
fileName1 = param_1+strH
fileName2 = param_1+strT
f1 = open(fileName1, "r")
f2 = open(fileName2, "w")

for line in f1:
		if "// " in line:
			line.replace('//', ';')
		if "#define " in line:
			f2.write(line.replace('#define ', '-D'))
			
f1.close()
f2.close()

main()
