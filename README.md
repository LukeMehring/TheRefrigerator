# TheRefrigerator by Luke Mehring

### Task
Create a RESTful API microservice to facilitate one of the following topics:
What's in the fridge?
- Story: I want to add, update, view, delete, or change what's in the fridge
- Constraint: implement a guardrail to prevent more than 12 cans of soda in the fridge at any time
- Constraint: there are multiple refrigerators

This project requires Java 11

## To Compile
``` 
./gradlew jar
```

## To Run Tests
``` 
./gradlew test
```

## To Run in shell
``` 
./gradlew run
```

## To Run in Docker
``` 
./gradlew clean build distTar; docker-compose build; docker-compose up
```


