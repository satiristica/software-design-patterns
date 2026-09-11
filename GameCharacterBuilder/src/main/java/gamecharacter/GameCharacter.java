package gamecharacter;

public final class GameCharacter {

    private final String race;
    private final String role;
    private final int health;
    private final String weapon;

    GameCharacter(
        String race,
        String role,
        int health,
        String weapon
    ) {
        this.race = race;
        this.role = role;
        this.health = health;
        this.weapon = weapon;
    }
    
}