package sc.ustc.dao;


public class BaseBean {
    protected String value;
    protected String column;

    public BaseBean() {

    }

    public BaseBean(String value) {
        this.value = value;
    }

    public BaseBean(String value, String column) {
        this.value = value;
        this.column = column;
    }

    public String getColumn() {
        return this.column;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
