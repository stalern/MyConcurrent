package core.threadlocal;


/**
 * 熟悉ThreadLocal原理
 * @author stalern
 * @date 2019/12/01~21:48
 */
public class SessionHandler {

    private static ThreadLocal<Session> session = ThreadLocal.withInitial(Session::new);

    private String getUser() {
        return session.get().getUser();
    }

    private String getStatus() {
        return session.get().getStatus();
    }

    private void setStatus(String status) {
        session.get().setStatus(status);
        if ("close".equals(status)) {
            session.remove();
        }
    }

    public static void main(String[] args) {
        int threads = 3;
        SessionHandler handler = new SessionHandler();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                handler.setStatus("open");
                System.out.println(handler.getStatus());
                handler.setStatus("close");
            }).start();
        }
    }
}
