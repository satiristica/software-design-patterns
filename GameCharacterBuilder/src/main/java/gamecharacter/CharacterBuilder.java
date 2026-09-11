package gamecharacter;

public interface CharacterBuilder {
    CharacterBuilder setRace(String race);
    CharacterBuilder setRole(String role);
    CharacterBuilder setHealth(int health);
    CharacterBuilder setWeapon(String weapon);
}
