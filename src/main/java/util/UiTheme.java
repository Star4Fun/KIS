package util;

public enum UiTheme {

    CUPERTINO_DARK,
    CUPERTINO_LIGHT,
    DRACULA,
    NORD_DARK,
    NORD_LIGHT,
    PRIMER_DARK,
    PRIMER_LIGHT;

    public String getCssFileName() {
        return switch (this) {
            case CUPERTINO_DARK -> "stylesheets/cupertino-dark.css";
            case CUPERTINO_LIGHT -> "stylesheets/cupertino-light.css";
            case DRACULA -> "stylesheets/dracula.css";
            case NORD_DARK -> "stylesheets/nord-dark.css";
            case NORD_LIGHT -> "stylesheets/nord-light.css";
            case PRIMER_DARK -> "stylesheets/primer-dark.css";
            case PRIMER_LIGHT -> "stylesheets/primer-light.css";
        };
    }

}
