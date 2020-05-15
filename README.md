

# Single Threaded/Multi-Threaded Search Engine Back-End (Java)

Writer: Porfirio Mohabir <br /> 
Jan/2019 – May/2019 <br /> 
University of San Francisco <br /> 
CS 212 (Software Development Project)

# Project Overview

Program processes text files by recursively finding text file(s) through directories/sub directories and parsing text file to just stem words. Stores stem words in a nested Inverted Index data structure that maps words to its file(s) and its position(s).Using the Inverted Index data structure, the program can conduct either an exact search or partial search and return its total matches. Program also parses/stem Query File(s) and generate/store sorted search results in a Query Builder Data Structure that maps each query stemmed word to a set of search results. Search results are written to a JSON File.

For more details, see the project guides at:

<https://usf-cs212-spring2019.github.io/guides/>
