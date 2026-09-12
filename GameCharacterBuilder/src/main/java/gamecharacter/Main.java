package gamecharacter;

public class Main {
    public static void main(String[] args) {
        CharacterDirector director = new CharacterDirector();
        
        CharacterObjectBuilder objBuilder = new CharacterObjectBuilder();

        director.makeKnight(objBuilder);

        GameCharacter knight = objBuilder.getResult();


        CharacterJsonBuilder jsonBuilder = new CharacterJsonBuilder();
        
        director.makeKnight(jsonBuilder);

        String knightJson = jsonBuilder.getResult();

        
        System.out.println(knight);
        System.out.println(knightJson);



        CharacterJsonBuilder jsonTester = new CharacterJsonBuilder();
        director.testingValidation(jsonTester); 

        String testJson = jsonTester.getResult();
        System.out.println(testJson);

    }
}
