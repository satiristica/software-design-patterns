package cryptography.creator;

import cryptography.crypto_machines.CryptoMachine;
import cryptography.crypto_machines.ShiftMachine;

public class ShiftOps extends EncryptOps {
    @Override
    protected CryptoMachine createMachine() {
        return new ShiftMachine();
    }
}