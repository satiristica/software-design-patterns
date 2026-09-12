package gamecharacter;

public class CharacterDirector {

    private static final String HUMAN = "human";
    private static final String ELF = "elf";

    private static final String KNIGHT = "knight";
    private static final String ARCHER = "archer";

    private static final String SWORD = "sword";
    private static final String BOW = "bow";

    private static final int KNIGHT_HEALTH = 100;
    private static final int ARCHER_HEALTH = 80;
    private static final int HEALTH_TESTER = -500; 


    public void makeKnight(CharacterBuilder builder) {
        builder
            .setRace(HUMAN)
            .setRole(KNIGHT)
            .setHealth(KNIGHT_HEALTH)
            .setWeapon(SWORD);
    }

    
    public void makeArcher(CharacterBuilder builder) {
        builder
            .setRace(ELF)
            .setRole(ARCHER)
            .setHealth(ARCHER_HEALTH)
            .setWeapon(BOW);
    }


    public void testingValidation(CharacterBuilder builder) {
        builder
            .setRace(ELF)
            .setRole(ARCHER)
            .setHealth(HEALTH_TESTER) 
            .setWeapon(BOW); 
    }

}