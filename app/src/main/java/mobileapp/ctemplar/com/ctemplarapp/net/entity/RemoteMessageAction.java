package mobileapp.ctemplar.com.ctemplarapp.net.entity;

import org.jetbrains.annotations.NotNull;

public enum RemoteMessageAction {
    CHANGE_PASSWORD("changePassword");

    private final String text;

    RemoteMessageAction(final String text) {
        this.text = text;
    }

    @NotNull
    @Override
    public String toString() {
        return text;
    }
}
