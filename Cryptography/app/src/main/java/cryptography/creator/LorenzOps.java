package cryptography.creator;

import cryptography.crypto_machines.CryptoMachine;
import cryptography.crypto_machines.Lorenz;

public class LorenzOps extends EncryptOps {
    @Override  
    protected CryptoMachine createMachine() {
        return new Lorenz();
    }
}