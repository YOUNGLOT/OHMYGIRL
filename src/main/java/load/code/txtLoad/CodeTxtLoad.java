package load.code.txtLoad;
import entity.Code;
import dao.CodeDao;
import helper.runnable.ParallelInsert;
import java.util.ArrayList;
import lombok.SneakyThrows;
import load.base.TxtLoad;
import java.io.BufferedReader;

public class CodeTxtLoad extends TxtLoad<Code> {
private String characterSet;

	//region singleton
	private CodeTxtLoad(String characterSet) {
		this.characterSet = characterSet;
	}
	
	private static CodeTxtLoad _instance;
	
	public static CodeTxtLoad getInstance(String characterSet) {
		if (_instance == null)
			_instance = new CodeTxtLoad(characterSet);
	
		return _instance;
	}
	//endregion

	@Override
	protected Code setEntity(String[] array) {

		Code code = new Code();
		//todo array와 매칭 시키세요~~
		code.setCodeId(stringToInt(array[0]));
		code.setCodeName(array[1]);
		code.setCodeCategoryId(stringToInt(array[2]));

		return code;
	}
	@Override
	protected String setCharacterSet() {
		return characterSet;
	}

	@SneakyThrows
	@Override
	protected void etl(BufferedReader object) {
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
		return "CodeTxtLoadExceptions";
	}


}