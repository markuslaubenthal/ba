SRC := $(wildcard src/*.java)
OUT := target

LIST := $(SRC:src/%.java=$(OUT)/%.class)

all: $(LIST)

$(OUT)/%.class: src/%.java | $(OUT)
	javac -cp src/ -d $| $<

$(OUT):
	@mkdir $@