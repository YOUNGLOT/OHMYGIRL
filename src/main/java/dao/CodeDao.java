package dao;
import entity.Code;
import dao.base.IntEntityDao;
import lombok.SneakyThrows;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CodeDao extends IntEntityDao<Code> {
	//region singleton
	private CodeDao() {
	}
	
	private static CodeDao _instance;
	
	public static CodeDao getInstance() {
		if (_instance == null)
			_instance = new CodeDao();
	
		return _instance;
	}
	//endregion

	@Override
	protected String getByKeyQuery() {
		return "select * from Code where CodeId = ?";
	}

	@Override
	protected String deleteByKeyQuery() {
		return "Delete Code Where CodeId = ?";
	}

	@SneakyThrows
	@Override
	protected Code readEntity(ResultSet result) {
		Code entity = new Code();

		entity.setCodeId(result.getInt(1));
		entity.setCodeName(result.getString(2));
		entity.setCodeCategoryId(result.getInt(3));

	return entity;
}

	@Override
	protected String getCountQuery() {
		return "select count(*) from Code";
	}

	@Override
	protected String getAllQuery() {
		return "select * from Code";
	}

	public boolean insert(Code entity) {
		String query = "insert into Code values (?, ?, ? )";
		return execute(query, new ParameterSetter() {

			@SneakyThrows
			@Override
			public void setValue(PreparedStatement statement) {

				statement.setInt(1, entity.getCodeId());
				statement.setString(2, entity.getCodeName());
				statement.setInt(3, entity.getCodeCategoryId());
			}
		});
	}

	@Override
	public boolean update(Code entity) {
		String query = "update Code set CodeName = ?, CodeCategoryId = ? where CodeId = ? ";
		return execute(query, new ParameterSetter() {

			@SneakyThrows
			@Override
			public void setValue(PreparedStatement statement) {

				statement.setString(1, entity.getCodeName());
				statement.setInt(2, entity.getCodeCategoryId());
				statement.setInt(3, entity.getCodeId());
			}
		});
	}

	@SneakyThrows
	public int getIdByName(String name) {
		String query = "select CodeId from Code where CodeName = ?";
		return getInt(query, new ParameterSetter() {

			@SneakyThrows
			@Override
			public void setValue(PreparedStatement statement) {

				 statement.setString(1, name);
			}
		});
	}


}