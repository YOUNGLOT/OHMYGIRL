package load.base;

import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;

public abstract class JsonLoad<Entity> extends Load<Entity, JSONArray> {

    @SneakyThrows
    public void loading(JSONArray object) {

        for (int i = 0; i < object.size(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) object.get(i);// 한개의 Json을 JSONObject로 변환

                Entity entity = setEntity(jsonObject);//제이슨 내용을 entity에 Set

                if (checkCondition(entity)) {//entity로 insert 조건 확인

                    insert(entity);
                    identity++;
                    entities.add(entity);
                }
            } catch (Exception e) {
                System.out.println("예외가 발생했습니다" + e + "\n 예외 json : " + object.get(i).toString() + "\n");
                exceptions.add(e.toString() + object.get(i).toString()  + "\n");
            }
        }
    }

    protected abstract Entity setEntity(JSONObject object);

    @SneakyThrows
    protected JSONArray getFile(String fileDirectory) {

        JSONParser parser = new JSONParser();

        return (JSONArray) parser.parse(new FileReader(fileDirectory));
    }
}
