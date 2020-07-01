package load.base;

import dao.CodeDao;
import entity.Code;
import helper.Helper;
import lombok.SneakyThrows;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public abstract class Load<Entity, Object> {
    protected ArrayList<Entity> entities = getEntities(); //기존의 데이터들
    protected ArrayList<Code> codes = getCodes(); //Code 테이블
    protected ArrayList<String> exceptions = new ArrayList<>(); //예외 처리된 String들
    protected int identity = setFirstIdentity(); //자동증가값

    // 메인 적제 함수
    @SneakyThrows
    public void load(String fileDirectory) {

        //파일 내용(object)을 가져와서
        Object object = getFile(fileDirectory);

        //runnable 갔다가 loading() 과 매칭됨
        etl(object);

        //예외 내용(.toString)을 exceptions 패키지에 TXT 파일로 저장
        makeExceptionsTextFile();
    }

    //runnable 사용시 오버라이딩을 위해 함수를 분리 (etl() Method를 오버라이드 하지 않으면 runnable을 사용하지 않고 적제가 됨)
    protected void etl(Object object){
        loading(object);
    }

    //load Abstract 함수
    protected abstract Object getFile(String fileDirectory) throws IOException, ParseException;

    //예외처리
    protected void makeExceptionsTextFile() {
        try {
            String exception = getString_NowTime() + "\n";

            for (int i = 0; i < exceptions.size(); i++) {
                exception += exceptions.get(i) + "\n";
            }

            exception += "\n";
            Helper.getInstance().makeFile("./src/main/java/load/exceptions", makeExceptionsTextFileName(), "txt", exception);
        } catch (NullPointerException e) {
            System.out.println("makeExceptionsTextFile : 에러가 없습니다 축하드려요!");
        } catch (Exception e) {
            System.out.println("makeExceptionsTextFile Excpetion : " + e);
        }
    }

    // 멤버 변수 설정 Abstract MeThods
    protected abstract int setFirstIdentity();
    protected abstract ArrayList<Entity> getEntities();

    //loading Abstract Methods for 자식클래스
    protected abstract void loading(Object object);
    protected abstract boolean checkCondition(Entity entity);
    protected abstract int insert(Entity entity);

    //Set makeExceptionsTextFile 이름
    protected abstract String makeExceptionsTextFileName();

    //region Helper Methods

    protected ArrayList<Code> getCodes(){
        try {
            return codes = CodeDao.getInstance().getAll();
        }catch(Exception e){
            return codes;
        }
    }

    //시스템 시간을 String으로 반환
    protected String getString_NowTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    //region Refine Methods

    // String 다듬기 (문자열 안에 있는 ",", "~", "\"" 제거) @필요시 재정의
    protected String refine(String line) {
        try {
            return replaceCommaInQuotes(line, "_").replace("~", "").replace("\"", "");
        } catch (Exception e) {
            System.out.println("refindAndSplit Exception : " + e.toString() + "\n" + line);
            exceptions.add("refindAndSplit Exception : " + e.toString() + "\n" + line + "\n");
            return line;
        }
    }
    //","로 스플릿 할때  문자열 내에 있는 "," 제거 (이 함수 구현하는데 2일 걸림ㅠㅠ ;; 노수면 강행군)
    protected String replaceCommaInQuotes(String string, String replaceString) {
        try {
            String str1 = string;
            ArrayList<Integer> arrayList = new ArrayList<>();
            int i = 0;
            while (str1.indexOf("\"") != -1) {
                arrayList.add(str1.indexOf("\""));
                String str11 = str1.substring(0, arrayList.get(i));
                String str12 = str1.substring(arrayList.get(i) + 1);
                str1 = str11 + "-" + str12;
                i++;
            }
            for (int j = 0; j < arrayList.size() / 2; j++) {
                int idx1 = arrayList.get(2 * j);
                int idx2 = arrayList.get(2 * j + 1);
                string = string.substring(0, idx1) + string.substring(idx1, idx2).replace(",", replaceString) + string.substring(idx2);
            }
            return string;
        } catch (Exception e) {
            System.out.println(string + ": replaceCommaInQuotes 변환오류\n" + e.toString());
            exceptions.add(string + ": replaceCommaInQuotes 변환오류\n" + e.toString() + "\n");
            return string;
        }
    }
    //String에서 startString 부터 endString 까지의 문자열 제거 (사용은 하지 않지만 refine 과정에서 필요할 수도 있기 때문에 놔둠)
    protected String partialDelection(String string, String startString, String endString) {
        try {
            int index1 = string.indexOf(startString);
            int index2 = string.indexOf(endString);

            if (index1 < index2) {

                String str1 = string.substring(0, index1);
                String str2 = string.substring(index2 + 1);

                return str1 + str2;
            } else {

                String str1 = string.substring(0, index2);
                String str2 = string.substring(index1 + 1);

                return str1 + str2;
            }
        } catch (Exception e) {
            System.out.println("partialDelection Exception" + e);
            exceptions.add("partialDelection Exception"+e.toString() + "\n");
            return string;
        }
    }

    //endregion

    //region setEntity Helper Methods

    //지번주소, 도로명 주소로부터 LocationCode 와 매칭 될 String return(%군 or %구 까지 뽑음)
    protected String extractLocation(String address1, String address2) {
        String[] splittedObject = ((address1 == null || address1 == "")? address1 : address2).split(" ");
        try {
            return splittedObject[0] + " " + splittedObject[1];
        } catch (NullPointerException e) {
            return splittedObject[0];
        } catch (Exception e){
            return "";
        }
    }

    //String을 sql.Date로
    public static java.sql.Date stringToDate(String str, String format) throws java.text.ParseException {
        return new java.sql.Date(new SimpleDateFormat(format).parse(str).getTime());
    }

    //Integer.parseInt -> input값이 null 일경우 return 0
    protected int stringToInt(String string) {
        if (string == null) {
            return 0;
        } else {
            try {
                return Integer.parseInt(string);
            } catch (Exception e) {
                return -1;
            }
        }
    }

    // String(Code 테이블과 매칭되는 Name속성 컬럼) -> return CodeId
    protected int nameToCode(String name) {
        try {
            for (int i = 0; i < codes.size(); i++) {
                if (codes.get(i).getCodeName().equals(name)) {
                    return codes.get(i).getCodeId();
                }
            }
            return 0;//int 의 null값은 entity.toString 시 0으로 출력되서 따라해봄
        } catch (Exception e) {
            return 0;
        }
    }
    protected int nameToCode(String string, String elseString) {
        String name = string;
        if (name == null) {
            name = elseString;
        }

        int code = 0;
        try {
            return code = nameToCode(name);
        } catch (Exception e) {
            return code;
        }
    }
    protected int nameToCode_IfNull_RegistCode(String name, int MaxCodeNo, int CodeCategoryId){
        //MaxCodeNo -> 코드를 등록할때 그 코드가 가질 수 있는 최댓값 예를들어 50001~59999면 59999 입력
        try {
            for (int i = 0; i < codes.size(); i++) {
                if (codes.get(i).getCodeName().equals(name)) {
                    return codes.get(i).getCodeId();
                }
            }
            ArrayList<Integer> al = new ArrayList<>();
            for (int i = 0; i < codes.size(); i++) {
                int codeId = codes.get(i).getCodeId();
                if(codeId<MaxCodeNo){
                    al.add(codeId);
                }
            }

            Code code = new Code();

            int max = Collections.max(al) + 1;

            code.setCodeId(max);
            code.setCodeName(name);
            code.setCodeCategoryId(CodeCategoryId);

            CodeDao.getInstance().insert(code);
            codes.add(code);
            return max;
        } catch (Exception e) {
            return 0;
        }
    }

    //endregion

    //endregion

}
