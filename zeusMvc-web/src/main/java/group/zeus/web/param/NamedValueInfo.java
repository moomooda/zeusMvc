package group.zeus.web.param;

/**
 * used by @RequestParam resolver
 * @Author: maodazhan
 * @Date: 2020/10/21 18:45
 */
public class NamedValueInfo {

    String name;

    private boolean required;

    private String defaultValue;

    public NamedValueInfo(String name, boolean required, String defaultValue) {
        this.name = name;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
