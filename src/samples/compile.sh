echo "This script can run on Unix only. Note down the dependencies in the"
echo " classpath and download the libraries accordingly"
# Syntax is ./compile.sh  TestConnection
javac -classpath ../build/apache-scout.jar:../lib/uddi4j.jar:../lib/jaxr-api.jar $1
