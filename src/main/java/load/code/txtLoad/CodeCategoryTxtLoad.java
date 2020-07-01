package load.code.txtLoad;
import entity.CodeCategory;
import dao.CodeCategoryDao;
import helper.runnable.ParallelInsert;
import java.util.ArrayList;
import lombok.SneakyThrows;
import load.base.TxtLoad;
import java.io.BufferedReader;

public class CodeCategoryTxtLoad extends TxtLoad<CodeCategory> {
private String characterSet;

	//region singleton
	private CodeCategoryTxtLoad(String characterSet) {
		this.characterSet = characterSet;
	}
	
	private static CodeCategoryTxtLoad _instance;
	
	public static CodeCategoryTxtLoad getInstance(String characterSet) {
		if (_instance == null)
			_instance = new CodeCategoryTxtLoad(characterSet);
	
		return _instance;
	}
	//endregion

	@Override
	protected CodeCategory setEntity(String[] array) {

		CodeCategory codeCategory = new CodeCategory();
		//todo array와 매칭 시키세요~~
		codeCategory.setCodeCategoryId(stringToInt(array[0]));
		codeCategory.setCodeCategoryName(array[1]);

		return codeCategory;
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
		return "CodeCategoryTxtLoadExceptions";
	}


}