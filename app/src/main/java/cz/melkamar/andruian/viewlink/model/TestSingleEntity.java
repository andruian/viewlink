package cz.melkamar.andruian.viewlink.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class TestSingleEntity {
    @Id(autoincrement = true)
    Long id;

    int someNumber;

    public TestSingleEntity(int someNumber) {
        this.someNumber = someNumber;
    }

    @Generated(hash = 787203968)
    public TestSingleEntity(Long id, int someNumber) {
        this.id = id;
        this.someNumber = someNumber;
    }

    @Generated(hash = 1371368161)
    public TestSingleEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSomeNumber() {
        return this.someNumber;
    }

    public void setSomeNumber(int someNumber) {
        this.someNumber = someNumber;
    }

    @Override
    public String toString() {
        return "TestSingleEntity{" +
                "id=" + id +
                ", someNumber=" + someNumber +
                '}';
    }
}

