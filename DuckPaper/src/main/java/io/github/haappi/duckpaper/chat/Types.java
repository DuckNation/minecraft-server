package io.github.haappi.duckpaper.chat;

public enum Types {
    STATUS("status"),
    PLAYER_JOIN("join"),
    PLAYER_LEAVE("leave"),
    PLAYER_CHAT("chat"),
    INFO("info");

    private final String value;

    Types(String value) {
        this.value = value;
    }

    public static Types getByValue(String value) {
        for (Types type : Types.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
