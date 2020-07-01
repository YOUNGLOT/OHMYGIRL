package load.base;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public abstract class TxtLoad<Entity> extends Load<Entity, BufferedReader> {

    @SneakyThrows
    public void loading(BufferedReader object) {

        String line = "";

        if ((line = object.readLine()) != null) {
        } //첫줄 Skip

        while ((line = object.readLine()) != null) {
            try {
                String[] splittedLine = refine(line).split(line.contains("\t") ? "\t" : ",");

                Entity entity = setEntity(splittedLine);

                if (checkCondition(entity)) {//entity로 insert 조건 확인

                    insert(entity);
                    identity++;
                    entities.add(entity);
                }
            } catch (Exception e) {
                System.out.println("예외가 발생했습니다" + e + "\n 예외 json : " + e.toString() + "\n" + line );
                exceptions.add(e.toString() + line + e.toString() + "\n");
            }
        }
    }

    protected abstract Entity setEntity(String[] splittedLine);

    protected abstract String setCharacterSet();

    protected BufferedReader getFile(String fileDirectory) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileDirectory)), setCharacterSet()));
            return br;
        } catch (Exception e) {
            System.out.println("FileReader Exception : %s" + e + "\n");
            exceptions.add("FileReader Exception : %s" + e + "\n");
            return null;
        }
    }


}
