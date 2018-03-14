package cz.melkamar.andruian.viewlink.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TestEntity {
    @Id(autoincrement = true)
    private Long id;

    int parentInt;

    @ToMany(referencedJoinProperty = "parentId")
    List<TestChildEntity> children;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1461302786)
    private transient TestEntityDao myDao;

    public TestEntity(int parentInt) {
        this.parentInt = parentInt;
        children = new ArrayList<>();
    }

    @Generated(hash = 152822100)
    public TestEntity(Long id, int parentInt) {
        this.id = id;
        this.parentInt = parentInt;
    }

    @Generated(hash = 1020448049)
    public TestEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getParentInt() {
        return this.parentInt;
    }

    public void setParentInt(int parentInt) {
        this.parentInt = parentInt;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 230254600)
    public List<TestChildEntity> getChildren() {
        if (children == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TestChildEntityDao targetDao = daoSession.getTestChildEntityDao();
            List<TestChildEntity> childrenNew = targetDao
                    ._queryTestEntity_Children(id);
            synchronized (this) {
                if (children == null) {
                    children = childrenNew;
                }
            }
        }
        return children;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1590975152)
    public synchronized void resetChildren() {
        children = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (children != null) {
            for (TestChildEntity child : children) {
                builder.append(child.toString()).append("\n");
            }
        }
        return "TestEntity{" +
                "id=" + id +
                ", parentInt=" + parentInt +
                ", children=" + builder.toString() +
                '}';
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 599389557)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTestEntityDao() : null;
    }
}
