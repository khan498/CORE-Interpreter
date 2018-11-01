JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = Program.java \
	Tokenizer.java \
	Parser.java \
	Printer.java \
	ParseTree.java \
	TreeNode.java \
	IdRedeclaredException \
	IdUndeclaredException \
	InterpreterException \
	InvalidTokenException \
	UnexpectedTokenException

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
