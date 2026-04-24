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


***Answers***

Part 1

1)	In the case of JAX-RS, by default, the resource classes are request scoped, which means that a new instance of the resource class is created for every new HTTP request. Resource classes are not singletons by default unless configured. However, this approach guarantees that there is no sharing of mutable state among requests. On the other hand, this means that the resource class instances themselves are recreated with every request. This implies that the data stored within the resource class would get lost with every request.Therefore, to store common data, we decided to implement our own DataStore class with the static data structures, such as Map and List. The advantage is that the stored data remains consistent through different requests. The disadvantage is that we could encounter a race condition if two or more requests attempt to access the same data concurrently.

2)	Hypermedia, or HATEOAS, is known to be an important part of RESTful architecture because it helps the client find actions that are possible by following links. The client does not need additional documentation but simply finds out which actions it should perform following links provided in the response (for instance, to rooms or sensors). This means that compared to static documentation, there is no strong connection between the client and server, as changing the endpoint would not affect the functionality of the client in any way.

Part 2

1)	Sending back only IDs decreases the amount of data sent back to the server, making the application more efficient and faster, particularly with large sets of data. However, it necessitates making extra requests from the client to get all other data. Sending back the whole object makes all necessary data available in one API call, making the request simpler. However, it may send back redundant data. In this particular application, sending back the whole object is preferred due to simplicity and fewer client-side requests.
2)	The DELETE method is an idempotent action in that performing the same request more than once will still produce the same end result.In our API, the first call to the DELETE method will succeed in deleting the room. The second attempt to delete the room will fail since it does not exist anymore. In return, we get an error message, but it does not affect our system state in any way.

Part 3

1)	Using the @Consumes(MediaType.APPLICATION_JSON) annotation confines the API to accepting only request bodies in the form of JSON. Should a client send the request body in some other media type such as text/plain or application/xml, JAX-RS would not be able to identify an appropriate message body reader for processing the request. Hence, the JAX-RS framework would automatically respond with a status code of 415 Unsupported Media Type.

2)	The use of @QueryParam to filter (/sensors?type=CO2) would make much more sense since it is optional information about a collection that can be used to filter it. On the other hand, an endpoint such as /sensors/type/CO2 implies hierarchy, while a query parameter implies optional information that can be added to the path. Multiple filters can easily be added with query parameters (for example, /sensors?type=CO2&status=ACTIVE). Thus, it would be better to use query parameters.

Part 4

1)	Sub-Resource Locator enhances modularity by outsourcing the processing of the nested resources into a different class. Unlike the previous design that put all logic into one huge class, the parent 	resource now responds with another resource class that processes the nested URLs. For example, in this case, SensorResource uses SensorReadingResource to handle the /readings path. Such a design makes code simpler and more understandable and promotes maintenance and future development.
Part 5

1)	The HTTP status code 422 Unprocessable Entity is more appropriate when the syntax is correct, but the content of the request is incorrect. For instance, creating a sensor with an existing roomId is an inconsistency in the request itself. There is no point in returning 404 as there is a valid endpoint; the request can be processed, but there is some logical inconsistency in it.

2)	The inclusion of Java stack trace from inside Java in the API responses poses a critical security threat. Some information that may be revealed through these includes the following:
Name of classes and packages
File path and line numbers
Framework/library information
This can help malicious users understand how your system works and exploit it. However, to counter this, the API is able to hide any Java stack trace and only include user-friendly error messages through a global exception mapper.

3)	Cross-cutting concerns like logging can be managed centrally using JAX-RS filters. By extending the ContainerRequestFilter and ContainerResponseFilter interfaces, all request and response can be logged easily without any repeated code in each resource handler method. Filters will make the code more maintainable, consistent, and help concentrate the business logic in resource classes.



