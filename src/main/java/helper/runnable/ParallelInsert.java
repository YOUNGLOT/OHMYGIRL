package helper.runnable;

import java.util.ArrayList;
import java.util.Random;

public class ParallelInsert {
    private final int THREAD_COUNT = 5; // 사용할 쓰레드 개수

    //region singleton
    private ParallelInsert() {
    }

    private static ParallelInsert _instance;

    public static ParallelInsert getInstance() {
        if (_instance == null)
            _instance = new ParallelInsert();

        return _instance;
    }

    //endregion

    public void parallelInsert(Run.RunningMethod runningMethod) throws InterruptedException {

        Runnable r = new Run(runningMethod); // 실제 구현한 Runnable 인터페이스

        ArrayList<Thread> threadList = new ArrayList<>(); // 쓰레드들을 담을 객체

        for (int i = 0; i < THREAD_COUNT; i++) {
            // Runnable 인터페이스를 사용해 새로운 쓰레드를 만듭니다.
            Thread test = new Thread(r);

            test.start(); // 이 메소드를 실행하면 Thread 내의 run()을 수행한다.
            threadList.add(test); // 생성한 쓰레드를 리스트에 삽입
        }

        for (Thread t : threadList) {
            t.join(); // 쓰레드의 처리가 끝날때까지 기다립니다.
        }
    }

    public static class Run implements Runnable {
        private Run.RunningMethod runningMethod;

        public Run(RunningMethod runningMethod) {
            this.runningMethod = runningMethod;
        }

        @Override
        public void run() {
            Random r = new Random(System.currentTimeMillis());

            long s = r.nextInt(3000); // 3초내로 끝내자.
            try {
                Thread.sleep(s); // 쓰레드를 잠시 멈춤
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runningMethod.runningMethod();
        }

        // 상속에 관계 없이 다른 클래스에서 사용하기위해 인터페이스로 구현  ->  *****!변수도 자동전달이 되더라!!!!! 씐기 (인터페이스는 아름다워)
        // synchronized 는 인터페이스로 구현 불가능 -> 인터페이스 구현시 Method 아래에 synchronized (ParallelInsert.class) {}로 재구현하세요
        //      -->그냥 구현하면 중복 된 작업 시행
        public interface RunningMethod {
             void runningMethod();
        }
    }
}
