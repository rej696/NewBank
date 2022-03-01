BUILDDIR=build

run: build server
	java -classpath ./build newbank/client/ExampleClient
	make stop

server: build build/server.PID

build/server.PID:
	{ java -classpath ./build newbank/server/NewBankServer & echo $$! > $@; } &> logs/server.log 

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
	echo "Clean Complete"
