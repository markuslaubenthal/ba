SRC := $(wildcard src/*.java)
OUT := target

LIST := $(SRC:src/%.java=$(OUT)/%.class)

all: $(LIST)

$(OUT)/%.class: src/%.java | $(OUT)
	javac -cp packages/java-json.jar:packages/JHyphenator-1.0.jar:packages/commons-math3-3.6.1.jar:src/ -d $| $<

$(OUT):
	@mkdir $@
