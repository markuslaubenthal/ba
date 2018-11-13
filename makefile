SRC := $(wildcard src/*.java)
OUT := target

LIST := $(SRC:src/%.java=$(OUT)/%.class)

all: $(LIST)

$(OUT)/%.class: src/%.java | $(OUT)
	javac -cp packages/java-json.jar:src/ -d $| $<

$(OUT):
	@mkdir $@
