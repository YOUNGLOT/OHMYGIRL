package classMaker;

import classMaker.base.LoadClassMaker;
import lombok.SneakyThrows;

import java.sql.SQLException;

public class JsonLoadClassMaker extends LoadClassMaker {

    //region singleton
    private JsonLoadClassMaker() {
    }

    private static JsonLoadClassMaker _instance;

    public static JsonLoadClassMaker getInstance() {
        if (_instance == null)
            _instance = new JsonLoadClassMaker();

        return _instance;
    }
    //endregion

    @Override
    protected String getClassTypeName() {
        return "JsonLoad";
    }

    @Override
    protected String getMakePath() {
        return "./src/main/java/load/jsonLoad";
    }

    @Override
    protected String writeImportCode(String code) {
        code += String.format(
                "package load.jsonLoad;\n" +
                        "import entity.%s;\n" +
                        "import dao.%sDao;\n" +
                        "import helper.runnable.ParallelInsert;\n" +
                        "import load.base.JsonLoad;;\n" +
                        "import org.json.simple.JSONArray;" +
                        "import org.json.simple.JSONObject;\n" +
                        "import java.util.ArrayList;\n"+
                        "import lombok.SneakyThrows;\n"
                , tableName, tableName);
        return code +="\n";
    }

    @Override
    protected String writeClassCode(String code) {
        code += String.format(
                "public class %sJsonLoad extends JsonLoad<%s> {\n"
                , tableName, tableName);
        return code += "\n";
    }

    @Override
    protected String writeSingletonCode(String code) {
        code += String.format(
                "\t//region singleton\n" +
                        "\tprivate %sJsonLoad() {\n" +
                        "\t}\n" +
                        "\t\n" +
                        "\tprivate static %sJsonLoad _instance;\n" +
                        "\t\n" +
                        "\tpublic static %sJsonLoad getInstance() {\n" +
                        "\t\tif (_instance == null)\n" +
                        "\t\t\t_instance = new %sJsonLoad();\n" +
                        "\t\n" +
                        "\t\treturn _instance;\n" +
                        "\t}\n" +
                        "\t//endregion\n"
                , tableName, tableName, tableName, tableName);
        return code +="\n";
    }

    @Override
    @SneakyThrows
    protected String writeMethodCode(String code) {

        code = writeSetEntityCode(code);
        code = writeEtlCode(code);
        code = writeSetFirstIdentityCode(code);
        code = writeGetEntitiesCode(code);
        code = writeCheckConditionCode(code);
        code = writeInsertCode(code);
        code = writeMakeExceptionsTextFileNameCode(code);

        return code;
    }

    //region Write Code 구현
    private String writeSetEntityCode(String code) throws SQLException {
        code += String.format(
                "\t@Override\n" +
                        "\tprotected %s setEntity(JSONObject object) {\n" +
                        "\t\t%s entity = new %s();\n" +
                        "\n", tableName, tableName, tableName);
        for (int i = 0; i < columns.length; i++) {
            code += makeSetCode(columns[i], columnTypes[i], i);
        }
        code += String.format(
                "\n\t\treturn entity;" +
                        "\n\t}\n");
        return code += "\n";
    }
    private String makeSetCode(String colmnName, String columnType, int i) throws SQLException {

        if (getIdentityColumn().equals(colmnName)){
            return "";
        }else{
            if(colmnName.equals("CodeId")||colmnName.equals("CodeCategoryId")){
                return String.format("\t\tentity.set%s(stringToInt((String) object.get(\"\")));\n"
                        , colmnName);
            }else if (colmnName.equals("CodeName")||colmnName.equals("CodeCategoryName")) {
                return String.format("\t\tentity.set%s((String) object.get(\"\"));\n"
                        , colmnName);
            }else if(colmnName.contains("Code")){
                return String.format("\t\tentity.set%s(nameToCode((String) object.get(\"\")));\n"
                        , colmnName);
            }else if (columnType.equals("int")){
                return String.format("\t\tentity.set%s(stringToInt((String) object.get(\"\")));\n"
                        , colmnName);
            }else if (columnType.equals("nvarchar")){
                return String.format("\t\tentity.set%s((String) object.get(\"\"));\n"
                        , colmnName);
            }else{
                return String.format("\t\tentity.set%s((String) object.get(\"\"));\n" +
                                "\t\t//todo 맞는 타입을 수동으로 변환\n"
                        , colmnName);
            }
        }
    }
    private String writeEtlCode(String code){
        code += String.format(
                "\t//runnable 미사용시 해당 함수 비활성화\n" +
                "\t@SneakyThrows\n" +
                        "\t@Override\n" +
                        "\tprotected void etl(JSONArray object) {\n" +
                        "\t\tParallelInsert.getInstance().parallelInsert(new ParallelInsert.Run.RunningMethod() {\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void runningMethod() {\n" +
                        "\t\t\t\tsynchronized (ParallelInsert.class) {\n" +
                        "\t\t\t\t\tloading(object);\n" +
                        "\t\t\t\t}\n" +
                        "\t\t\t}\n" +
                        "\t\t});\n" +
                        "\t}\n"
                , tableName);
        return code += "\n";
    }
    private String writeSetFirstIdentityCode(String code){
        code += String.format(
                "\t@Override\n" +
                        "\tprotected int setFirstIdentity() {\n" +
                        "\t\treturn 0;\n" +
                        "\t\t//todo Identity 시작값을 Set하는 Method\n" +
                        "\t}\n");
        return code += "\n";
    }
    private String writeGetEntitiesCode(String code){
        code += String.format(
                "\t@Override\n" +
                        "\tprotected ArrayList getEntities() {\n" +
                        "\t\ttry { return %sDao.getInstance().getAll(); } catch (Exception e){ return entities; }\n" +
                        "\t}\n",tableName);
        return code += "\n";
    }
    private String writeCheckConditionCode(String code){
        code += String.format(
                "\t@Override\n" +
                        "\t protected boolean checkCondition(%s entity) {\n" +
                        "\t\t/*// 해당 메소드를 사용하려면 Entity Class에 Equals && HashCode 메소드를 재구현 해주세요\n" +
                        "\t\tfor (int i = 0; i < entities.size(); i++) {\n" +
                        "\t\t\tif(entities.get(i).equals(entity)){\n" +
                        "\t\t\t\treturn false;\n" +
                        "\t\t\t}\n" +
                        "\t\t}*/\n" +
                        "\t\treturn true;\n" +
                        "\t}\n", tableName);
        return code += "\n";
    }
    private String writeInsertCode(String code){
        code += String.format(
                "\t@Override\n" +
                        "\tprotected int insert(%s entity){\n", tableName);
        if (identityColumn == "none"){
            code += String.format(
                    "\t\t%sDao.getInstance().insert(entity);\n" +
                            "\t\treturn identity;\n" +
                            "\t}\n"
                    , tableName);
        }else{
            code += String.format(
                    "\t\treturn %sDao.getInstance().insert(entity);\n" +
                            "\t}\n"
                    , tableName);
        }
        return code += "\n";
    }
    private String writeMakeExceptionsTextFileNameCode(String code){
        code += String.format(
                "\t@Override\n" +
                        "\tprotected String makeExceptionsTextFileName() {\n" +
                        "\t\treturn \"%sJsonLoadExceptions\";\n" +
                        "\t}\n", tableName);
        return code += "\n";
    }
    //endregion
}
