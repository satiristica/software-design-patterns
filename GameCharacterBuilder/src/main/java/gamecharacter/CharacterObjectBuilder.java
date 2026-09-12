package gamecharacter;

public class CharacterObjectBuilder implements CharacterBuilder {
    private String race;
    private String role;
    private int health;
    private String weapon;


    @Override
    public CharacterObjectBuilder setRace(String race) {
        this.race = race;
        return this;
    }


    @Override
    public CharacterObjectBuilder setRole(String role) {
        this.role = role;
        return this;
    }


    @Override
    public CharacterObjectBuilder setHealth(int health) {
        this.health = health;
        return this;
    }


    @Override
    public CharacterObjectBuilder setWeapon(String weapon) {
        this.weapon = weapon;
        return this;
    }


    public GameCharacter getResult() {
        CharacterValidator.validate(race, role, health, weapon);
        return new GameCharacter(race, role, health, weapon);
    }
}