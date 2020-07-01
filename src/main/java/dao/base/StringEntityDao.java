package dao.base;

import dao.ParameterSetter;
import lombok.SneakyThrows;

import java.sql.PreparedStatement;

public abstract class StringEntityDao<E> extends SingleKeyEntityDao<E, String> {
    @SneakyThrows
    public final E getByKey(String key){
        //language=TSQL
        String query = getByKeyQuery();

        return getOne(query, new ParameterSetter() {
            @SneakyThrows
            @Override
            public void setValue(PreparedStatement statement) {
                statement.setString(1, key);
            }
        });
    }

    @SneakyThrows
    public final boolean deleteByKey(String key){
        String query = deleteByKeyQuery();

        return execute(query, new ParameterSetter() {
            @SneakyThrows
            @Override
            public void setValue(PreparedStatement statement) {
                statement.setString(1, key);
            }
        });
    }
}
