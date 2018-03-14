package cz.melkamar.andruian.viewlink.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class TestChildEntity {
    @Id(autoincrement = true)
    private Long id;
    private Long parentId;

    String childStr;

    public TestChildEntity(Long parentId, String childStr) {
        this.parentId = parentId;
        this.childStr = childStr;
    }

    @Generated(hash = 577970946)
    public TestChildEntity(Long id, Long parentId, String childStr) {
        this.id = id;
        this.parentId = parentId;
        this.childStr = childStr;
    }

    @Generated(hash = 1494993156)
    public TestChildEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getChildStr() {
        return this.childStr;
    }

    public void setChildStr(String childStr) {
        this.childStr = childStr;
    }

    @Override
    public String toString() {
        return "TestChildEntity{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", childStr='" + childStr + '\'' +
                '}';
    }
}
