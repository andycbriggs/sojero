# sojero

Service Oriented Messaging Platform built on top of JeroMQ.

## Classes

```java
// Provides service discovery
ServiceInterface

// Provides an interface for an individual service
Service

// Services implements some common messaging patterns
ServiceEvent
ServiceEventHandler

ServiceRequest
ServiceRequestHandler

ServiceCommand
ServiceCommandHandler
```

## Usage

```java
// Instantiate a service interface
ServiceInterface inf = new ServiceInterface();

// Create a new service and assign it a unique identifier
Service tempService = inf.getService("TemperatureSensor");

ServiceEvent newTemperature = new ServiceEvent("TemperatureData");
newTemperature.set("temperature", 30);

// Trigger an event on the service
tempService.trigger(newTemperature);

/*
	Service Consumer
*/

// Instantiate a service interface
ServiceInterface inf = new ServiceInterface();

// Retrieve the service pointer from the interface
// Optionally a service could be discovered by regular expression to obtain a List of Services that match a pattern
// For example: All temperature sensors, TemperatureSensor-1, TemperatureSensor-2 with the pattern \TemperatureSensor-\d+\
Service tempSensor = inf.getService("TemperatureSensor");

// Add an event listener for the Service
tempSensor.addEventHandler(new ServiceEventHandler("TemperatureData") {
	@Override
	public onEvent()
	{
		String newTemperature = get("temperature");
		System.out.println( newTemperature );
	}
})

/*
	Additional Functionality
*/

// Commands

// Initiate a command
tempSensor.execute(new ServiceCommand("TakeTemperature"));

// Handle a command
// "service" is a reference to the parent service (tempSensor)
tempSensor.addCommandHandler(new ServiceCommandHandler("TakeTemperature") {
	@Override
	public onCommand()
	{
		// Do something on the command
		ServiceEvent newTemperature = new ServiceEvent("TemperatureData");
		newTemperature.set("temperature", 30);
		// In this example the command causes a new event to notify all listeners of an event
		service.trigger(newTemperature);
	}
})

// Requests
// Asynchronous request
tempSensor.request(new ServiceRequest("TemperatureInCelsius") {
	@Override
	public onResponse(ServiceResponse response)
	{
		String celsiusTemperature = get("temperatureInCelsius");
		System.out.println( celsiusTemperature );
	}
});

tempSensor.addRequestHandler(new ServiceRequestHandler("TemperatureInCelsius") {
	@Override
	public onRequest()
	{
		ServiceResponse response = new ServiceResponse();
		response.set("temperatureInCelsius", 30);
		// All requests must return a respons
		return response;
	}
});
```
