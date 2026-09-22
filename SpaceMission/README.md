# Space Mission - Assignment 2

Java console assignment demonstrating two creational design patterns: Factory Method and Abstract Factory

The project uses a space mission theme with vehicles and equipment for Mars and Moon missions

## Factory Method

The Factory Method part creates different mission vehicles:

- Rover
- Satellite
- Lander

Mission contains the common launch workflow. Each concrete mission decides which vehicle should be created.

FactoryShow runs this part of the project.

## Abstract Factory

The Abstract Factory part creates complete sets of compatible equipment for Mars and Moon missions.

Each set contains:

- environmental sensor
- communication system
- navigation module

MissionEquipmentManager uses the equipment through interfaces and receives an EquipmentFactory through its constructor.

AbstractFactoryShow selects and demonstrates both equipment families.

## Design notes

Factory Method creates one product through inheritance.

Abstract Factory creates a family of related products through composition.

The project follows SRP by separating creation, product behavior, and mission workflows. It follows OCP because new vehicles or equipment families can be added without changing the existing workflow.

Both patterns introduce additional classes. Abstract Factory also requires every concrete factory to be updated when a new product type is added.

## Run

JDK 21+ needed

1. ./gradlew build
2. ./gradlew runFactoryMethod
3. ./gradlew runAbstractFactory
