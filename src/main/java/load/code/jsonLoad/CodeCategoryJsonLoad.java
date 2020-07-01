package load.code.jsonLoad;
import entity.CodeCategory;
import dao.CodeCategoryDao;
import helper.runnable.ParallelInsert;
import load.base.JsonLoad;;
import org.json.simple.JSONArray;import org.json.simple.JSONObject;
import java.util.ArrayList;
import lombok.SneakyThrows;

public class CodeCategoryJsonLoad extends JsonLoad<CodeCategory> {
	//region singleton
	private CodeCategoryJsonLoad() {
	}
	
	private static CodeCategoryJsonLoad _instance;
	
	public static CodeCategoryJsonLoad getInstance() {
		if (_instance == null)
			_instance = new CodeCategoryJsonLoad();
	
		return _instance;
	}
	//endregion

	@Override
	protected CodeCategory setEntity(JSONObject object) {
		CodeCategory entity = new CodeCategory();

		entity.setCodeCategoryId(stringToInt((String) object.get("")));
		entity.setCodeCategoryName((String) object.get(""));

		return entity;
	}
	@SneakyThrows
	@Override
	protected void etl(JSONArray object) {
		ParallelInsert.getInstance().parallelInsert(new ParallelInsert.Run.RunningMethod() {
			@Override
			public void runningMethod() {
				synchronized (ParallelInsert.class) {
					loading(object);
				}
			}
		});
	}
	@Override
	protected int setFirstIdentity() {
		return 0;
		//todo Identity 시작값을 Set하는 Method
	}

	@Override
	protected ArrayList getEntities() {
		try { return CodeCategoryDao.getInstance().getAll(); } catch (Exception e){ return entities; }
	}

	@Override
	 protected boolean checkCondition(CodeCategory entity) {
		/*// 해당 메소드를 사용하려면 Entity Class에 Equals && HashCode 메소드를 재구현 해주세요
		for (int i = 0; i < entities.size(); i++) {
			if(entities.get(i).equals(entity)){
				return false;
			}
		}*/
		return true;
	}

	@Override
	protected int insert(CodeCategory entity){
		CodeCategoryDao.getInstance().insert(entity);
		return identity;
	}

	@Override
	protected String makeExceptionsTextFileName() {
		return "CodeCategoryJsonLoadExceptions";
	}

}