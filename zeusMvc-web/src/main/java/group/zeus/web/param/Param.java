package group.zeus.web.param;

/**
 * @Author: maodazhan
 * @Date: 2020/10/20 19:37
 */
abstract class Param {

    private final String fieldName;

    private final Object fieldValue;

    public Param(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
