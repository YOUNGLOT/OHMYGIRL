package load.code.jsonLoad;
import entity.Code;
import dao.CodeDao;
import helper.runnable.ParallelInsert;
import load.base.JsonLoad;;
import org.json.simple.JSONArray;import org.json.simple.JSONObject;
import java.util.ArrayList;
import lombok.SneakyThrows;

public class CodeJsonLoad extends JsonLoad<Code> {
	//region singleton
	private CodeJsonLoad() {
	}
	
	private static CodeJsonLoad _instance;
	
	public static CodeJsonLoad getInstance() {
		if (_instance == null)
			_instance = new CodeJsonLoad();
	
		return _instance;
	}
	//endregion

	@Override
	protected Code setEntity(JSONObject object) {
		Code entity = new Code();

		entity.setCodeId(stringToInt((String) object.get("")));
		entity.setCodeName((String) object.get(""));
		entity.setCodeCategoryId(stringToInt((String) object.get("")));

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
		try { return CodeDao.getInstance().getAll(); } catch (Exception e){ return entities; }
	}

	@Override
	 protected boolean checkCondition(Code entity) {
		/*// 해당 메소드를 사용하려면 Entity Class에 Equals && HashCode 메소드를 재구현 해주세요
		for (int i = 0; i < entities.size(); i++) {
			if(entities.get(i).equals(entity)){
				return false;
			}
		}*/
		return true;
	}

	@Override
	protected int insert(Code entity){
		CodeDao.getInstance().insert(entity);
		return identity;
	}

	@Override
	protected String makeExceptionsTextFileName() {
		return "CodeJsonLoadExceptions";
	}

}