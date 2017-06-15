public class UserReponse {

    private int id;

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    private String name;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    private String email;

    public String getEmail() { return this.email; }

    public void setEmail(String email) { this.email = email; }

    private String avatar;

    public String getAvatar() { return this.avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    private Date birthday;

    public Date getBirthday() { return this.birthday; }

    public void setBirthday(Date birthday) { this.birthday = birthday; }

    private String phone_number;

    public String getPhoneNumber() { return this.phone_number; }

    public void setPhoneNumber(String phone_number) { this.phone_number = phone_number; }

    private String address;

    public String getAddress() { return this.address; }

    public void setAddress(String address) { this.address = address; }

    private String auth_token;

    public String getAuthToken() { return this.auth_token; }

    public void setAuthToken(String auth_token) { this.auth_token = auth_token; }

    @Override
    public String toString() {
        return "UserReponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", birthday=" + birthday +
                ", phone_number='" + phone_number + '\'' +
                ", address='" + address + '\'' +
                ", auth_token='" + auth_token + '\'' +
                '}';
    }
}
