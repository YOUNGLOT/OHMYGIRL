package dao;
import entity.CodeCategory;
import dao.base.IntEntityDao;
import lombok.SneakyThrows;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CodeCategoryDao extends IntEntityDao<CodeCategory> {
	//region singleton
	private CodeCategoryDao() {
	}
	
	private static CodeCategoryDao _instance;
	
	public static CodeCategoryDao getInstance() {
		if (_instance == null)
			_instance = new CodeCategoryDao();
	
		return _instance;
	}
	//endregion

	@Override
	protected String getByKeyQuery() {
		return "select * from CodeCategory where CodeCategoryId = ?";
	}

	@Override
	protected String deleteByKeyQuery() {
		return "Delete CodeCategory Where CodeCategoryId = ?";
	}

	@SneakyThrows
	@Override
	protected CodeCategory readEntity(ResultSet result) {
		CodeCategory entity = new CodeCategory();

		entity.setCodeCategoryId(result.getInt(1));
		entity.setCodeCategoryName(result.getString(2));

	return entity;
}

	@Override
	protected String getCountQuery() {
		return "select count(*) from CodeCategory";
	}

	@Override
	protected String getAllQuery() {
		return "select * from CodeCategory";
	}

	public boolean insert(CodeCategory entity) {
		String query = "insert into CodeCategory values (?, ? )";
		return execute(query, new ParameterSetter() {

			@SneakyThrows
			@Override
			public void setValue(PreparedStatement statement) {

				statement.setInt(1, entity.getCodeCategoryId());
				statement.setString(2, entity.getCodeCategoryName());
			}
		});
	}

	@Override
	public boolean update(CodeCategory entity) {
		String query = "update CodeCategory set CodeCategoryName = ? where CodeCategoryId = ? ";
		return execute(query, new ParameterSetter() {

			@SneakyThrows
			@Override
			public void setValue(PreparedStatement statement) {

				statement.setString(1, entity.getCodeCategoryName());
				statement.setInt(2, entity.getCodeCategoryId());
			}
		});
	}
	@SneakyThrows
	public int getIdByName(String name) {
		String query = "select CodeCategoryId from CodeCategory where CodeCategoryName = ?";
		return getInt(query, new ParameterSetter() {

			@SneakyThrows
			@Override
			public void setValue(PreparedStatement statement) {

				 statement.setString(1, name);
			}
		});
	}


}