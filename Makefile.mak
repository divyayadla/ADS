JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	FiboHeap.java \
	FiboHeapNode.java \
	HashTagCounter.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class