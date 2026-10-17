JAVAC := javac
JAVA := java
CLASSES_DIR := classes
JSON_JAR := src/zombicide.jar
MAIN_CLASS := Main
MAP_FILE := ville.json
rwildcard = $(foreach directory,$(wildcard $1*/),$(call rwildcard,$(directory),$2)) $(wildcard $1$2)
SOURCES := $(call rwildcard,src/,*.java)

.PHONY: compile run clean

compile:
	mkdir -p $(CLASSES_DIR)
	$(JAVAC) -cp "$(JSON_JAR)" -d $(CLASSES_DIR) $(SOURCES)

run: compile
	$(JAVA) -cp "$(CLASSES_DIR);$(JSON_JAR)" $(MAIN_CLASS) $(MAP_FILE)

clean:
	rm -rf $(CLASSES_DIR)
