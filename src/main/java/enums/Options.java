package enums;

public enum Options {
    DEFAULT_OPTION("0"),
    IGNORE_DIGITS("2"),
    IGNORE_URLS("4"),
    FIND_REPEAT_WORDS("8"),
    IGNORE_CAPITALIZATION("512");

    public String code;

    Options(String code) {
        this.code = code;
    }
}
