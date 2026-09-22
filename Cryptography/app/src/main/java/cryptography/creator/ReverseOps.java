package cryptography.creator;

import cryptography.crypto_machines.CryptoMachine;
import cryptography.crypto_machines.ReverseMachine; 

public class ReverseOps extends EncryptOps {
    @Override 
    protected CryptoMachine createMachine() {
        return new ReverseMachine();
    }
}
