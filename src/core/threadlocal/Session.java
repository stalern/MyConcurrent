package core.threadlocal;

/**
 * @author stalern
 * @date 2019/12/02~21:10
 */
public class Session {
    private String id;
    private String user;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
