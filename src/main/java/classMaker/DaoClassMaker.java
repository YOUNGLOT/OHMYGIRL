package classMaker;

import classMaker.base.ClassMaker;

public class DaoClassMaker extends ClassMaker {

    //region singleton

    private DaoClassMaker() {
    }

    private static DaoClassMaker _instance;

    public static DaoClassMaker getInstance() {
        if (_instance == null)
            _instance = new DaoClassMaker();

        return _instance;
    }

    @Override
    protected String getMakePath() {
        return "./src/main/java/dao";
    }

    //endregion

    @Override
    protected String writeImportCode(String code) {
        code += String.format(
                "package dao;\n" +
                        "import entity.%s;\n" +
                        "import dao.base.IntEntityDao;\n" +
                        "import lombok.SneakyThrows;\n" +
                        "import java.sql.PreparedStatement;\n" +
                        "import java.sql.ResultSet;\n"
                , tableName);
        return code += "\n";
    }

    @Override
    protected String writeClassCode(String code) {
        code += String.format(
                "public class %sDao extends IntEntityDao<%s> {\n"
                , tableName,  tableName);
        return code;
    }

    @Override
    protected String writeSingletonCode(String code) {
        code += String.format(
                "\t//region singleton\n" +
                        "\tprivate %sDao() {\n" +
                        "\t}\n" +
                        "\t\n" +
                        "\tprivate static %sDao _instance;\n" +
                        "\t\n" +
                        "\tpublic static %sDao getInstance() {\n" +
                        "\t\tif (_instance == null)\n" +
                        "\t\t\t_instance = new %sDao();\n" +
                        "\t\n" +
                        "\t\treturn _instance;\n" +
                        "\t}\n" +
                        "\t//endregion\n"
                , tableName, tableName, tableName, tableName);
        return code += "\n";
    }

    @Override
    protected String writeMethodCode(String code) {

        code = writeGetByKeyQuery(code);
        code = writeDeleteByKeyQuery(code);
        code = writeReadEntity(code);
        code = writeGetCountQuery(code);
        code = writeGetAllQuery(code);
        code = writeInsert(code);
        code = writeUpdate(code);
        if (!(nameColumn == "")) {
            code = writeGetIdByName(code);
        }
        return code;
    }

    @Override
    protected String getClassTypeName() {
        return "Dao";
    }

    //region codeWrite

    private String writeGetByKeyQuery(String code) {
        code += String.format(
                "\t@Override\n" +
                        "\tprotected String getByKeyQuery() {\n" +
                        "\t\treturn \"select * from %s where %s = ?\";\n" +
                        "\t}\n"
                , tableName, columns[0]);
        return code += "\n";
    }

    private String writeDeleteByKeyQuery(String code) {
        code += String.format(
                "\t@Override\n" +
                        "\tprotected String deleteByKeyQuery() {\n" +
                        "\t\treturn \"Delete %s Where %s = ?\";\n" +
                        "\t}\n"
                , tableName, columns[0]);
        return code += "\n";
    }

    private String writeReadEntity(String code) {
        code += String.format(
                "\t@SneakyThrows\n" +
                        "\t@Override\n" +
                        "\tprotected %s readEntity(ResultSet result) {\n" +
                        "\t\t%s entity = new %s();\n\n"
                , tableName, tableName, tableName);

        for (int i = 0; i < columns.length; i++) {
            code += String.format(
                    "\t\tentity.set%s(result.get%s(%d));\n"
                    , columns[i], refineType(columnTypes[i]), i + 1);
        }

        code += String.format(
                "\n\treturn entity;\n}\n");
        return code += "\n";
    }

    private String writeGetCountQuery(String code) {
        code += String.format(
                "\t@Override\n" +
                        "\tprotected String getCountQuery() {\n" +
                        "\t\treturn \"select count(*) from %s\";\n" +
                        "\t}\n"
                , tableName);
        return code += "\n";
    }

    private String writeGetAllQuery(String code) {
        code += String.format(
                "\t@Override\n" +
                        "\tprotected String getAllQuery() {\n" +
                        "\t\treturn \"select * from %s\";\n" +
                        "\t}\n"
                , tableName);
        return code += "\n";
    }

    private String writeInsert(String code) {
        code += String.format(
                        "\tpublic %s insert(%s entity) {\n" +
                        "\t\tString query = \"insert into %s values (?"
                , getReturnType(), tableName, tableName);
        if (identityColumn.equals("none")) {
            for (int i = 0; i < columns.length - 1; i++) {
                code += ", ?";
            }
            code += " )\";\n";
        } else {
            for (int i = 0; i < columns.length - 2; i++) {
                code += ", ?";
            }
            code += " ) SELECT @@IDENTITY AS SEQ \";\n";
        }
        code += String.format(
                "\t\treturn %s(query, new ParameterSetter() {\n\n" +
                        "\t\t\t@SneakyThrows\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void setValue(PreparedStatement statement) {\n\n", getReturnFun());
        if (identityColumn.equals("none")) {
            for (int i = 0; i < columns.length; i++) {
                code += String.format(
                        "\t\t\t\tstatement.set%s(%d, entity.get%s());\n"
                        , refineType(columnTypes[i]), i + 1, columns[i]);
            }
        } else {
            for (int i = 1; i < columns.length; i++) {
                code += String.format(
                        "\t\t\t\tstatement.set%s(%d, entity.get%s());\n"
                        , refineType(columnTypes[i]), i, columns[i]);
            }
        }
        return code += String.format(
                "\t\t\t}\n" +
                        "\t\t});\n" +
                        "\t}\n\n");
    }

    private String writeUpdate(String code) {
        code += String.format(
                "\t@Override\n" +
                        "\tpublic boolean update(%s entity) {\n" +
                        "\t\tString query = \"update %s set "
                , tableName, tableName);
        for (int i = 1; i < columns.length; i++) {
            if (i == 1) {
                code += (String.format("%s = ?"
                        , columns[i]));
            } else {
                code += (String.format(", %s = ?"
                        , columns[i]));
            }
        }
        code += String.format(" where %s = ? \";\n"
                , columns[0]);
        code += String.format(
                "\t\treturn execute(query, new ParameterSetter() {\n\n" +
                        "\t\t\t@SneakyThrows\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void setValue(PreparedStatement statement) {\n\n");
        for (int i = 0; i < columns.length - 1; i++) {
            code += String.format(
                    "\t\t\t\tstatement.set%s(%d, entity.get%s());\n", refineType(columnTypes[i + 1]), i + 1, columns[i + 1]);
            if (i == columns.length - 2) {
                code += String.format(
                        "\t\t\t\tstatement.set%s(%d, entity.get%s());\n", refineType(columnTypes[0]), i + 2, columns[0]);
            }
        }
        return code += String.format(
                "\t\t\t}\n" +
                        "\t\t});\n" +
                        "\t}\n");
    }

    private String writeGetIdByName(String code) {
        code += String.format(
                "\t@SneakyThrows\n" +
                        "\tpublic int getIdByName(String name) {\n" +
                        "\t\tString query = \"select %s from %s where %s = ?\";\n" +
                        "\t\treturn getInt(query, new ParameterSetter() {\n" +
                        "\n" +
                        "\t\t\t@SneakyThrows\n" +
                        "\t\t\t@Override\n" +
                        "\t\t\tpublic void setValue(PreparedStatement statement) {\n" +
                        "\n" +
                        "\t\t\t\t statement.setString(1, name);\n" +
                        "\t\t\t}\n" +
                        "\t\t});\n" +
                        "\t}\n"
                , columns[0], tableName, nameColumn);
        return code += "\n";
    }
    //endregion

    //HelpMethods
    //insert function에서 identityColumn의 유무에 따라 return 값 설정 (identity가 있으면 insert 된 data의 identity를 return)
    private String getReturnType() {
        return (identityColumn.equals("none")) ? "boolean" : "int";
    }

    private String getReturnFun() {
        return (identityColumn.equals("none")) ? "execute" : "getInt";
    }


}
