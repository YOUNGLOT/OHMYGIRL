package dao.base;

import dao.ParameterSetter;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;

public abstract class IntEntityDao<E> extends SingleKeyEntityDao<E, Integer>{
    @SneakyThrows
    public final E getByKey(int key){
        //language=TSQL
        String query = getByKeyQuery();

        return getOne(query, new ParameterSetter() {
            @SneakyThrows
            @Override
            public void setValue(PreparedStatement statement) {
                statement.setInt(1, key);
            }
        });
    }

    @SneakyThrows
    public final boolean deleteByKey(int key){
        //language=TSQL
        String query = deleteByKeyQuery();

        return execute(query, new ParameterSetter() {
            @SneakyThrows
            @Override
            public void setValue(PreparedStatement statement) {
                statement.setInt(1, key);
            }
        });
    }

}
