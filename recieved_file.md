### Install dependencies
mvn clean package

### Compile project
mvn compile

### Run
mvn exec:java -Dexec.mainClass="client.App"

### Build
mvn package

### Run .jar 
java -cp target/xmpp-client-1.0.jar client.App