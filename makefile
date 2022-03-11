BUILDDIR=build

run: build server
	java -classpath ./build newbank/client/ExampleClient
	make stop

server: build build/server.PID

build/server.PID:
	{ java -classpath ./build newbank/server/NewBankServer & echo $$! > $@; } &> logs/server.log 

test: init
	javac -classpath . -d ./test/build newbank/**/*.java
	javac -classpath .:./test/lib/junit-platform-console-standalone-1.8.2.jar -d ./test/build ./test/*.java
	java -jar test/lib/junit-platform-console-standalone-1.8.2.jar --classpath test/build --scan-classpath

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
	rm -rf ./test/build
	rm -rf ./**/*.class
	echo "Clean Complete"
