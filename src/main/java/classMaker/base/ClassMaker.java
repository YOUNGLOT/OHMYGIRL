package classMaker.base;

import lombok.Data;
import lombok.SneakyThrows;
import helper.Helper;
import java.sql.*;
import java.util.ArrayList;

public abstract class ClassMaker {
    //함수별 매개변수 사용시 너무 코드량 증가 -> 멤버변수 사용
    protected String tableName;

    protected static Connection conn;
    protected ResultSetMetaData resultSetMetaData;

    protected int columnCount;
    protected String nameColumn; // getIdByName() 에 매칭 될 컬럼 (이름컬럼)
    protected String identityColumn; // SEQ 지정한 컬럼
    protected String[] columns;
    protected String[] columnTypes;
    protected String classTypeName; // Dao, Entity, Load 3가지 종류가 있뜸()
    protected String makePath;
    protected String[] pkColumns;

    @SneakyThrows
    public boolean makeClass(String tableName) {
        //tableName으로 멤버변수들 셋팅(순서 중요)
        this.tableName = tableName;
        this.conn = Helper.getInstance().getConnection();
        this.resultSetMetaData = getResultSetMetaData();
        this.columnCount = resultSetMetaData.getColumnCount();
        this.identityColumn = getIdentityColumn();
        this.columns = getColumns();
        this.columnTypes = getColumnTypes();
        this.nameColumn = getNameColumn();
        this.classTypeName = getClassTypeName();
        this.makePath = getMakePath();
        this.pkColumns = getPkColumns();

        //변수와 다른 성격으로 판단되어 멤버변수에서 제외
        String code = ""; //class 코드를 담을 String

        //code를 str + str 로 하지않고 매개변수로 가져가서 return하는 이유 :
        //      ->아래 상황에 따라 윗단의 code의 변경이 필요할 때 변경하기 위함;
        code = writeImportCode(code);
        code = writeClassCode(code);
        code = writeSingletonCode(code);
        code = writeMethodCode(code);
        code += "\n}";

        try{
            Helper.getInstance().makeFile(makePath, tableName + classTypeName, "java", code);
            return true;
        }catch (Exception e){
            System.out.println("파일 생성 실패\n"+e);
            return false;
        }
    }
    //클래스 파일이 저장 될 위치
    protected abstract String getMakePath();

    //region Abstract Methods

    protected abstract String writeImportCode(String code);
    protected abstract String writeClassCode(String code);
    protected abstract String writeSingletonCode(String code);
    protected abstract String writeMethodCode(String code) throws SQLException;

    protected abstract String getClassTypeName();
    //endregion

    //region Sub Methods

    @SneakyThrows
    private ResultSetMetaData getResultSetMetaData(){
        try {
            return conn.prepareStatement(String.format("select * from %s", tableName)).executeQuery().getMetaData();
        }catch (Exception e){
            System.out.println("ResultSetMetaData 예외 발생\n"+ e);
            return null;
        }
    }

    @SneakyThrows
    private String getNameColumn() {
        String nameColumn = "";
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].contains("Name")) {
                nameColumn = columns[i];
            }
        }
        return nameColumn;
    }

    private String[] getColumns() throws SQLException {

        String[] columnNames = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = resultSetMetaData.getColumnName(i);
        }

        return columnNames;
    }

    private String[] getColumnTypes() throws SQLException {

        String[] columnTypes = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            columnTypes[i - 1] = resultSetMetaData.getColumnTypeName(i);
        }
        return columnTypes;
    }
    //endregion

    //MSSQL ResultSetMetaData 에서 불러온 types 를 Java와 매칭되는 types로 변경(타임 다듬기!!)
    protected String refineType(String columnType) {
        if (columnType == "NUMBER"||columnType == "int") {
            return "Int";
        } else if (columnType == "CHAR" || columnType == "VARCHAR2"||columnType == "nvarchar") {
            return "String";
        } else if (columnType == "DECIMAL") {
            return "BigDecimal";
        } else {
            System.out.println(columnType + "지정되지 않아 String으로 처리했습니다. 확인해 주세요");
            return "String";
        }
    }

    //camelCase 표기를 위해 첫글자 소문자로 변경
    protected String makeStartLetterSmall(String str) {
        if (str == "String" || str == "Date" || str == "BigDecimal") {
            return str;
        } else
            return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    //region GetIdentityColumn

    protected String getIdentityColumn() throws SQLException {
        String query = "SELECT B.name AS [Table], A.name AS [Colum] FROM syscolumns A JOIN sysobjects B ON B.id = A.id WHERE A.status = 128 AND B.name NOT LIKE 'queue_messages_%'";

        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet result = statement.executeQuery();

        ArrayList<IdentityColumn> entities = new ArrayList<>();
        while (result.next()) {
            IdentityColumn entity = readEntity(result);
            entities.add(entity);
        }

        result.close();
        statement.close();

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getTable().equals(tableName)) {
                return entities.get(i).getColumn();
            }
        }
        return "none";
    }

    @SneakyThrows
    public String[] getPkColumns(){
        String query = String.format("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE TABLE_NAME='%s' AND CONSTRAINT_NAME = 'PK_%s'",tableName,tableName);

        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet result = statement.executeQuery();

        ArrayList<String> pkNames = new ArrayList<>();

        while (result.next()) {
            String pkName = result.getString(1);
            pkNames.add(pkName);
        }

        String[] array = new String[pkNames.size()];

        for (int i = 0; i < pkNames.size(); i++) {
            array[i] = pkNames.get(i);
        }

        return array;
    }

    //아래 class와 method는 getIdentityColumn()의 sub함수여서 private으로 지정
    @Data
    private class IdentityColumn {
        private String table;
        private String column;
    }

    private IdentityColumn readEntity(ResultSet result) throws SQLException {
        IdentityColumn entity = new IdentityColumn();

        entity.setTable(result.getString(1));
        entity.setColumn(result.getString(2));

        return entity;
    }
    //endregion

    }
