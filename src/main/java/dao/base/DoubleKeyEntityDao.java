package dao.base;

public abstract class DoubleKeyEntityDao<E, K1, K2> extends EntityDao<E> {
    protected abstract String getByKeyQuery();

    protected abstract String deleteByKeyQuery();
}