package tk.cofe.plugin.mybatis.enums;

/**
 * @author : zhengrf
 * @date : 2019-06-27
 */
public enum Annotation {
    PARAM("@Param", "org.apache.ibatis.annotations.Param"),
    SELECT("@Select", "org.apache.ibatis.annotations.Select"),
    UPDATE("@Update", "org.apache.ibatis.annotations.Update"),
    INSERT("@Insert", "org.apache.ibatis.annotations.Insert"),
    DELETE("@Delete", "org.apache.ibatis.annotations.Delete"),
    ;
    private String label;
    private String qualifiedName;

    Annotation(String label, String qualifiedName) {
        this.label = label;
        this.qualifiedName = qualifiedName;
    }

    public String getLabel() {
        return label;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }
}
