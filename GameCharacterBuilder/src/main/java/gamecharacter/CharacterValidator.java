package gamecharacter;

final class CharacterValidator {
    
    private CharacterValidator() {}

    static void validate( 
        String race,
        String role,
        int health,
        String weapon
    ) {

        if (race == null || race.isBlank()) {
            throw new IllegalStateException("Enter the race.");
        }


        if (role == null || role.isBlank()) {
            throw new IllegalStateException("Enter the role");
        }


        if (health <= 0) {
            throw new IllegalStateException("Health must be greater than 0");
        }


        if (weapon == null || weapon.isBlank()) {
            throw new IllegalStateException("Enter the weapon");
        }

    }
}
