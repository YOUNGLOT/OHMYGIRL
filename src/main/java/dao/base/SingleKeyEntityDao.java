package dao.base;

public abstract class SingleKeyEntityDao<E, K> extends EntityDao<E> {
    protected abstract String getByKeyQuery();

    protected abstract String deleteByKeyQuery();
}
