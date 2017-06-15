public class UserRequest {

    private String name;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    private String email;

    public String getEmail() { return this.email; }

    public void setEmail(String email) { this.email = email; }

    private String password;

    public String getPassword() { return this.password; }

    public void setPassword(String password) { this.password = password; }

    private String password_confirmation;

    public String getPasswordConfirmation() { return this.password_confirmation; }

    public void setPasswordConfirmation(String password_confirmation) { this.password_confirmation = password_confirmation; }

    private String phone_number;

    public String getPhoneNumber() { return this.phone_number; }

    public void setPhoneNumber(String phone_number) { this.phone_number = phone_number; }

    public UserRequest(String name, String email, String password, String password_confirmation, String phone_number) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.phone_number = phone_number;
    }
}