package cryptography.crypto_machines;

public interface CryptoMachine {
    String encrypt(String message);
    String decrypt(String message);
}