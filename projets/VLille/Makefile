JAVAC = javac
JAVA = java
JAR = jar
JAVADOC = javadoc

SRC = src/main/java
TEST = src/test/java
CLASS_DIR = target/classes
TEST_CLASS_DIR = target/test-classes
DOC_DIR = doc
LIB_DIR = lib
MAIN_CLASS = fil.l3.coo.main

CLASSPATH = $(CLASS_DIR)
TEST_CLASSPATH = $(CLASS_DIR);$(TEST_CLASS_DIR);$(LIB_DIR)/*

.PHONY: all clean clone compile compile-tests test doc jar run run-test run-jar

all: compile test doc jar run-jar

clean:
	rm -rf $(CLASS_DIR) $(TEST_CLASS_DIR) $(DOC_DIR) v-lille.jar

clone:
	git clone https://gitlab-etu.fil.univ-lille.fr/mohamed-amine.tifoun.etu/projet-coo-g1.git

compile:
	mkdir -p $(CLASS_DIR)
	$(JAVAC) -d $(CLASS_DIR) $(SRC)/fil/l3/coo/*.java \
		$(SRC)/fil/l3/coo/AccesController/*.java \
		$(SRC)/fil/l3/coo/DocaratorPattern/*.java \
		$(SRC)/fil/l3/coo/ExceptionsControlled/*.java \
		$(SRC)/fil/l3/coo/NotificationController/*.java \
		$(SRC)/fil/l3/coo/StateController/DeuxRoueState/*.java \
		$(SRC)/fil/l3/coo/StateController/StationState/*.java \
		$(SRC)/fil/l3/coo/StrategyPattern/RedistributionStargies/*.java \
		$(SRC)/fil/l3/coo/StrategyPattern/VoleStrategies/*.java

compile-tests: compile
	mkdir -p $(TEST_CLASS_DIR)
	$(JAVAC) -cp "$(TEST_CLASSPATH)" -d $(TEST_CLASS_DIR) $(TEST)/fil/l3/coo/*.java

test: compile-tests
	$(JAVA) -cp "$(TEST_CLASSPATH)" org.junit.platform.console.ConsoleLauncher --class-path $(TEST_CLASS_DIR) --scan-class-path

doc:
	mkdir -p $(DOC_DIR)
	$(JAVADOC) -d $(DOC_DIR) -sourcepath $(SRC) -subpackages fil.l3.coo

jar: compile
	$(JAR) cvfe v-lille.jar $(MAIN_CLASS) -C $(CLASS_DIR) .

run: compile
	$(JAVA) -cp $(CLASSPATH) $(MAIN_CLASS)

run-jar: jar
	$(JAVA) -jar v-lille.jar

run-test: compile-tests
	$(JAVA) -cp "$(TEST_CLASSPATH)" org.junit.platform.console.ConsoleLauncher --select-class $(TEST_CLASS)