package cryptography.creator;

import cryptography.crypto_machines.CryptoMachine;
import cryptography.crypto_machines.PairSwapMachine;

public class PairSwapOps extends EncryptOps {
    @Override  
    protected CryptoMachine createMachine() {
        return new PairSwapMachine();
    }
}