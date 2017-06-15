public class RootObjectRequest {
    
    //The class uses for request sign up user
    private UserRequest user;

    public UserRequest getUser() { return this.user; }

    public void setUser(UserRequest user) { this.user = user; }

    public RootObjectRequest(UserRequest user) {
        this.user = user;
    }
}