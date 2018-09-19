JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Sim.java \
	Belt.java \
	BeltMover.java \
	Bicycle.java \
	BicycleHandlingThread.java \
	Consumer.java \
	DefException.java \
	DefKnownException.java \
	HandlingException.java \
	Inspector.java \
	OverloadException.java \
	Params.java \
	Producer.java \
	Robot.java \
	Sensor.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
