echo "This script can run on Unix only. Note down the dependencies in the"
echo " classpath and download the libraries accordingly"
LIB=../lib
CP=$LIB/uddi4j.jar:$LIB/jaxr-api.jar
CP=$CP:$LIB/axis.jar:$LIB/saaj.jar:$LIB/jaxrpc.jar:$LIB/commons-logging.jar:$LIB/commons-discovery.jar
java -classpath .:../build/apache-scout.jar:$CP $1
