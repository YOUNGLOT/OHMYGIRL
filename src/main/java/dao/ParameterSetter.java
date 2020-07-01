package dao;

import java.sql.PreparedStatement;

public interface ParameterSetter {
    void setValue(PreparedStatement statement);
}
