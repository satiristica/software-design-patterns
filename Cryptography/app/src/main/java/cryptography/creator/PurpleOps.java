package cryptography.creator;

import cryptography.crypto_machines.CryptoMachine;
import cryptography.crypto_machines.Purple; 

public class PurpleOps extends EncryptOps {
    @Override 
    protected CryptoMachine createMachine() {
        return new Purple();
    }
}
