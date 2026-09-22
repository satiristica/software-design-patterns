package cryptography.creator;

import cryptography.crypto_machines.CryptoMachine;
import cryptography.crypto_machines.Enigma;

public class EnigmaOps extends EncryptOps {
    @Override
    protected CryptoMachine createMachine() {
        return new Enigma();
    }
}