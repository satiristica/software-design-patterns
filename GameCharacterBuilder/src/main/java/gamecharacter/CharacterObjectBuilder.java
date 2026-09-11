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
        validate();
        return new GameCharacter(race, role, health, weapon);
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