package digital.moveto.botinok.model.entities.enums;

public enum SettingKey {

    START_AUTOMATICALLY_EVERY_24_HOURS("false");

    private final String defaultValue;

    SettingKey(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
