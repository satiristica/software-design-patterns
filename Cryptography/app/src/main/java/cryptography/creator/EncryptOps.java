package cryptography.creator;
import cryptography.crypto_machines.CryptoMachine;

public abstract class EncryptOps {
    protected abstract CryptoMachine createMachine();

    public String encrypt(String message) {
        CryptoMachine machine = createMachine();
        return machine.encrypt(message);
    }
}