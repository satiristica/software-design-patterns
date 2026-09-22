package cryptography.crypto_machines;

public class ReverseMachine implements CryptoMachine {

    @Override
    public String encrypt(String message) {
        String result = "";

        for (int i = message.length() - 1; i >= 0; i--) {
            result += message.charAt(i);
        }

        return result;
    }

    @Override
    public String decrypt(String message) {
        return encrypt(message);
    }
}