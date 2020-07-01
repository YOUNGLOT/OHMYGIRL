package entity;

import lombok.Data;

@Data
public class Code {
    private int codeId;
    private String codeName;
    private int codeCategoryId;

    public boolean equals(Code entity) {
		return this.codeName.equals(entity.getCodeName()) && this.getCodeCategoryId() == entity.getCodeCategoryId();
	}

	public int hashCode(Code entity){
    	int hashCode = 31;

		hashCode += entity.getCodeName().hashCode();
    	hashCode += entity.getCodeCategoryId();

    	return hashCode;
	}
}

