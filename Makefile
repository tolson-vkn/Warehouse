all: clean java

java:
	javac *.java

clean:
	rm *.class
