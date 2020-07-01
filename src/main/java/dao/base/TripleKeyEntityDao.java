package dao.base;

public abstract class TripleKeyEntityDao<E, K1, K2, K3> extends EntityDao<E> {
    protected abstract String getByKeyQuery();

    protected abstract String deleteByKeyQuery();
}
