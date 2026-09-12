package gamecharacter;

public class CharacterJsonBuilder implements CharacterBuilder {
    private String race;
    private String role;
    private int health;
    private String weapon;


    @Override
    public CharacterJsonBuilder setRace(String race) {
        this.race = race;
        return this;
    }


    @Override 
    public CharacterJsonBuilder setRole(String role) {
        this.role = role;
        return this;
    }


    @Override 
    public CharacterJsonBuilder setHealth(int health) {
        this.health = health;
        return this;
    }

    
    @Override 
    public CharacterJsonBuilder setWeapon(String weapon) {
        this.weapon = weapon;
        return this;
    }

    
    public String getResult() {
        CharacterValidator.validate(race, role, health, weapon);
        
        return """
        {
            "race": "%s",
            "role": "%s",
            "health": %d, 
            "weapon": "%s"
        }
                """.formatted(race, role, health, weapon);
    }
}