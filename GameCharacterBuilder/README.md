# Assignment 1 - Builder Pattern

## Description

This project is about implementation of Builder design pattern in Java.
The same character construction steps can produce two representations:
- GameCharacter object
- JSON String

## Structure
- GameCharacter --> Product
- CharacterBuilder --> Builder interface (Builder)
- CharacterObjectBuilder --> builds a GameCharacter (Concrete Builder)
- CharacterJsonBuilder --> builds JSON String representation (Concrete Builder)
- CharacterDirector --> predefined Knight and Archer configurations (Director)
- Main --> Client

## Run
In terminal
1. git clone https://github.com/satiristica/software-design-patterns.git
2. cd software-design-patterns/GameCharacterBuilder
3. mvn clean package
4. java -cp target/classes gamecharacter.Main
