JFLAGS = -g
JC = javac
JAR = jar
JAVADOC = javadoc
JV = java 
.SUFFIXES: .java .class

# Rule for compiling Java files
.java.class:
	make doc
	make zombicide.jar
	$(JV) -jar zombicide.jar 15 10 4

# Define the directory structure and Java files
CELLS_DIR = src/Cells
BOARD_DIR = src/Board
PLAYERS_DIR=src/Players
EQUIPMENTS_DIR=src/Equipments
ACTIONS_DIR=src/Actions

PLAYERS_SRC=$(wildcard $(PLAYERS_DIR)/*.java)
EQUIPMENTS_SRC=$(wildcard $(EQUIPMENTS_DIR)/*.java)
CELLS_SRC = $(wildcard $(CELLS_DIR)/*.java)
BOARD_SRC = $(wildcard $(BOARD_DIR)/*.java)
ACTIONS_SRC = $(wildcard $(ACTIONS_DIR)/*.java)
GAME_SRC = src/Game.java
GAMEMAIN_SRC = src/GameMain.java

# Combine all source files
SRC = $(ACTIONS_SRC) $(CELLS_SRC) $(BOARD_SRC) ${PLAYERS_SRC} ${EQUIPMENTS_SRC}  $(GAME_SRC) $(GAMEMAIN_SRC) 

# Define the target for building classes
CLASSES = $(addprefix cls/,$(notdir $(SRC:.java=.class)))

# Default target
default: all

# Target for compiling Java classes
classes: | cls $(CLASSES)

cls:
	$(JC) $(JFLAGS) $(ACTIONS_SRC) $(CELLS_SRC) $(BOARD_SRC) $(PLAYERS_SRC) $(EQUIPMENTS_SRC) $(GAME_SRC) $(GAMEMAIN_SRC) -d cls

# Pattern rule for compiling Java files into the cls directory
cls/%.class: $(CELLS_DIR)/%.java
	$(JC) $(JFLAGS) $< -d cls

cls/%.class: $(BOARD_DIR)/%.java
	$(JC) $(JFLAGS) $< -d cls

cls/Game.class: $(GAME_SRC)
	$(JC) $(JFLAGS) $< -d cls

cls/GameMain.class: $(GAMEMAIN_SRC)
	$(JC) $(JFLAGS) $< -d cls
# Target for generating Jars
zombicide.jar: cls
	$(JAR) cvfe zombicide.jar GameMain -C cls .
# Target for generating Tests
test:
	$(JC) -classpath junit-console.jar:classes test/Board/*.java -d classes
	$(JC) -classpath junit-console.jar:classes test/Cells/*.java -d classes
	$(JC) -classpath junit-console.jar:classes test/Equipments/*.java -d classes
	$(JC) -classpath junit-console.jar:classes test/players/*.java -d classes
	$(JC) -classpath junit-console.jar:classes test/actions/*.java -d classes
	$(JV) -jar junit-console.jar -classpath test:classes -scan-classpath
# Target for generating Javadoc
doc:
	$(JAVADOC) $(SRC) -d javadoc 
all:
	make doc
	make zombicide.jar
	mv zombicide.jar jar/
	$(JV) -jar jar/zombicide.jar 10 14 4
# Target for cleaning compiled files and Javadoc
clean:
	$(RM) -r cls javadoc *.jar
