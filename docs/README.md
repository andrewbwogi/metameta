# A Meta-Metaprogram in Java

![Diagram](diagram.png?raw=true "Diagram")

This project transforms a subset of metaprograms using Spoon to equivalent metaprograms using ASM and evaluates the transformation. Spoon and ASM are Java libraries used for metaprogramming Java source code and Java bytecode respectively.

The evaluation system is a Python script that runs a set of source programs through a set of Spoon metaprograms and the metaprograms through the transformer to generate ASM metaprograms. It compiles the source programs and the resulting class files are run through each generated ASM metaprogram. Next the outputs from the Spoon metaprograms are compiled and the resulting class files are compared to the output class files of the ASM metaprograms. The comparison is made with the code clone detection program SootDiff. The diagram above represents all the evaluation steps and the names of each element correspond to folders in this repository. Some folders are created during the script execution. The folder Spoon contains a number of metaprograms using Spoon that are successfully evaluated. They insert invocations of new methods in the beginning of a chosen input method or as replacement of all return expressions in the method.

Run `python test.py` to start the evaluation.
