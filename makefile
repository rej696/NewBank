BUILDDIR=build

run: build server
	java -classpath ./build newbank/client/ExampleClient
	make stop

server: build build/server.PID

build/server.PID:
	{ java -classpath ./build newbank/server/NewBankServer & echo $$! > $@; } &> logs/server.log 

test: init build
	javac -classpath .:./lib/junit-platform-console-standalone-1.8.2.jar -d ./build ./test/*.java
	java -jar lib/junit-platform-console-standalone-1.8.2.jar --classpath ./build --scan-classpath

build: init
	javac -classpath . -d ./build newbank/**/*.java

init:
	mkdir -p logs
	mkdir -p build
 
stop: build/server.PID
	kill `cat $<` && rm $<

clean:
	make stop
	rm -rf logs
	rm -rf build
	rm -rf ./**/*.class
	echo "Clean Complete"
