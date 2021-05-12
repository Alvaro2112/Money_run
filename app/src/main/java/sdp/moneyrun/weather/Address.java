package sdp.moneyrun.weather;


import androidx.annotation.NonNull;

import java.util.List;

public final class Address {
    private final List<String> addressLines;

    public Address(List<String> addressLines) {
        this.addressLines = addressLines;
    }

    @NonNull
    public String toString(String separator) {
        StringBuilder buffer = new StringBuilder();
        for (String line : addressLines) buffer.append(line).append(separator);

        return buffer.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return this.toString("\n");
    }
}