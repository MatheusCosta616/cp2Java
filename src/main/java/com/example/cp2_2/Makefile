.PHONY: clean compile package run run_all

clean:
	mvn clean

compile:
	mvn compile

package:
	mvn package

run:
	java -jar target/nucleotidio-1.0-SNAPSHOT.jar

setup_dirs:
	mkdir -p entradas saidas

copy_inputs:
	cp ../libuncleotidio/entradas/entrada1.txt entradas/

run_all: clean package setup_dirs copy_inputs run 