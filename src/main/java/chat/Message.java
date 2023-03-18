package chat;

public class Message {
    private String message;
    private boolean online;
    private boolean hasOnlineStatus;
    private String users;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean hasOnlineStatus() {
        return hasOnlineStatus;
    }

    public void setHasOnlineStatus(boolean hasOnlineStatus) {
        this.hasOnlineStatus = hasOnlineStatus;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
