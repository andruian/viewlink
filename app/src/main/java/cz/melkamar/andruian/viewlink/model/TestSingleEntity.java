package cz.melkamar.andruian.viewlink.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "myent")
public class TestSingleEntity {
    @PrimaryKey(autoGenerate = true)
    Long id;

    @ColumnInfo(name = "mycol")
    int someNumber;

    public TestSingleEntity(int someNumber) {
        this.someNumber = someNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSomeNumber() {
        return someNumber;
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

