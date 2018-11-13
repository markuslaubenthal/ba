SRC := $(wildcard src/*.java)
OUT := target

LIST := $(SRC:src/%.java=$(OUT)/%.class)
FILES = $(shell java -cp target/:packages/java-json.jar Main)

all: $(LIST)

$(OUT)/%.class: src/%.java | $(OUT)
	javac -cp packages/java-json.jar:src/ -d $| $<

$(OUT):
	@mkdir $@

echo: $(FILES)
