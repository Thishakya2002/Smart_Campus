***Overview of API***
The Smart Campus REST API is a RESTful web service implemented with Java, JAX-RS (Jersey) and a built-in Grizzly HTTP server. This REST API serves to maintain rooms, sensors and sensor data in a campus context.

This design implements RESTful architecture by having:

Resources exposed through URI (/rooms, /sensors)
Standard HTTP operations (GET, POST, DELETE)
Requests and responses made with JSON payload
Proper HTTP status codes (200, 201, 404, 409, 422, 403)

This system provides functionalities such as:

Managing rooms (CRUD operations)
Managing and filtering sensors
Having nested sub-resources in sensor readings
Automated updates of sensors upon adding their readings
Custom exception handling via custom exception and ExceptionMapper classes
Logging through request/response filters in JAX-RS

***Build and Run the project***
1. Clone repositary
git clone https://github.com/YOUR-USERNAME/YOUR-REPO-NAME.git
cd YOUR-REPO-NAME

2.Build project
mvn clean install

3. Run the server
mvn exec:java -Dexec.mainClass="com.smartcampus.Main"

4. Access the API
http://localhost:8081/api/v1/

***Curl Commands***

1. Get API Root (Discovery Endpoint)

curl -X GET http://localhost:8081/api/v1/


2. Create a Room

curl -X POST http://localhost:8081/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{
  "id": "ROOM-1",
  "name": "Lecture Hall",
  "capacity": 100,
  "sensorIds": []
}'


3. Get All Rooms

curl -X GET http://localhost:8081/api/v1/rooms


4. Create a Sensor

curl -X POST http://localhost:8081/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{
  "id": "SEN-1",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 25.0,
  "roomId": "ROOM-1"
}'


5. Filter Sensors by Type

curl -X GET "http://localhost:8081/api/v1/sensors?type=Temperature"


6. Add a Sensor Reading

curl -X POST http://localhost:8081/api/v1/sensors/SEN-1/readings \
-H "Content-Type: application/json" \
-d '{
  "id": "READ-1",
  "timestamp": 1711111111111,
  "value": 30.5
}'


7. Get Sensor Readings

curl -X GET http://localhost:8081/api/v1/sensors/SEN-1/readings


8. Get Sensors in a Room (Sub-resource)

curl -X GET http://localhost:8081/api/v1/rooms/ROOM-1/sensors

