package user;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;

public class User implements Serializable {
    public String firstName;
    public String lastName;
    public String userID;
    public String password;
    public byte[] DP;
    public ArrayList<String> friendList;
    public ArrayList<String> friendReq;

    public User() throws IOException {
        File file = new File("src/resource/img/userdp/default.png");
        firstName = null;
        lastName = null;
        userID = null;
        password = null;
        DP = Files.readAllBytes(file.toPath());
        friendList = new ArrayList<String>();
        friendReq = new ArrayList<String>();
    }
}
