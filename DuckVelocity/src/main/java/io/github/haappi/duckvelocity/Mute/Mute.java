package io.github.haappi.duckvelocity.Mute;

public class Mute {
    private final Long muteTime;
    private final String reason;

    public Mute(Long muteTime, String reason) {
        this.muteTime = muteTime;
        this.reason = reason;
    }

    public Long getMuteTime() {
        return muteTime;
    }

    public String getReason() {
        return reason;
    }
}
