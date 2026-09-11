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
        validate();
        
        return """
        {
            "race": "%s",
            "role": "%s",
            "health": %d, 
            "weapon": "%s"
        }
                """.formatted(race, role, health, weapon);
    }


    private void validate() {

        if (race == null || race.isBlank()) {
            throw new IllegalStateException("Enter the race.");
        }   


        if (role == null || role.isBlank()) {
            throw new IllegalStateException("Enter the role.");
        }


        if (health <= 0) {
            throw new IllegalStateException("Health must be greater than 0.");
        }


        if (weapon == null || weapon.isBlank()) {
            throw new IllegalStateException("Enter the weapon.");
        }
    }
}