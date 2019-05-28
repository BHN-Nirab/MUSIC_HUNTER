package ui;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import objectPacking.ObjectPacking;
import user.User;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.Scanner;


public class WelcomeController implements Initializable {

    public static String currUser;

    @FXML
    Button loginButton;
    @FXML
    ImageView loginButtonGraphics;
    @FXML
    Button signupButton;
    @FXML
    ImageView signupButtonGraphics;
    @FXML
    AnchorPane loginDashboard;
    @FXML
    AnchorPane createAccountDashboard;
    @FXML
    Button exitButton;

    @FXML
    TextField firstName;
    @FXML
    TextField lastName;
    @FXML
    TextField userID;
    @FXML
    PasswordField password;
    @FXML
    Button createAccount;
    @FXML
    Button dpChooser;

    @FXML
    TextField loginUser;
    @FXML
    PasswordField loginPassword;
    @FXML
    Button userLogin;

    @FXML
    Circle dpHolder;

    @FXML
    ImageView loginerror;
    @FXML
    ImageView signuperror;

    @FXML
    AnchorPane rootPane;


    private double xOffset = 0;
    private double yOffset = 0;

    public static String serverIP;
    public static int serverPort;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TranslateTransition tt = new TranslateTransition(Duration.millis(1300), rootPane);
        tt.setFromY(700);
        tt.setToY(0);
        tt.play();


        try {
            String config = "";
            String value = "";
            File inputFile = new File("src/resource/client.config");
            Scanner input = new Scanner(inputFile);
            while (input.hasNextLine()) {
                config = input.next();
                value = input.next();

                if (config.equals("serverIP"))
                    serverIP = value;
                else if(config.equals("serverPort")) {
                    serverPort = Integer.valueOf(value);
                    break;
                }
            }


        } catch (Exception e) {
            System.out.println("Error:Config file not found!");
        }

        dpHolder.setFill(new ImagePattern(new Image("/resource/img/iconpack/picture.png")));
        loginButton.setVisible(false);
        loginButtonGraphics.setVisible(false);
        createAccountDashboard.setVisible(false);
        createAccountDashboard.setOpacity(0);
        loginButton.setOpacity(0);
        loginButtonGraphics.setOpacity(0);
        loginerror.setOpacity(0);
        signuperror.setOpacity(0);
        loginerror.setVisible(false);

    }


    public void setLoginButton(ActionEvent event) {


        FadeTransition ftcreateAccountDashboard = new FadeTransition(Duration.millis(1000), createAccountDashboard);
        ftcreateAccountDashboard.setToValue(0);
        ftcreateAccountDashboard.play();

        TranslateTransition ttcreateAccountDashboard = new TranslateTransition(Duration.millis(1000), createAccountDashboard);
        ttcreateAccountDashboard.setToX(-73);
        ttcreateAccountDashboard.play();


        ttcreateAccountDashboard.setOnFinished(e -> {
            FadeTransition ftloginDashboard = new FadeTransition(Duration.millis(1000), loginDashboard);
            ftloginDashboard.setFromValue(0);
            ftloginDashboard.setToValue(1);
            ftloginDashboard.play();

            TranslateTransition ttloginDashboard = new TranslateTransition(Duration.millis(1000), loginDashboard);
            loginDashboard.setLayoutX(368);
            ttloginDashboard.setToX(73);
            ttloginDashboard.play();

            createAccountDashboard.setVisible(false);
        });


        ScaleTransition stloginButton = new ScaleTransition(Duration.millis(500), loginButton);
        stloginButton.setFromY(1);
        stloginButton.setToY(0);
        FadeTransition ftloginButton = new FadeTransition(Duration.millis(500), loginButton);
        ftloginButton.setFromValue(1);
        ftloginButton.setToValue(0);
        ScaleTransition stloginButtonGraphics = new ScaleTransition(Duration.millis(500), loginButtonGraphics);
        stloginButtonGraphics.setFromY(1);
        stloginButtonGraphics.setToY(0);
        FadeTransition ftloginButtonGraphics = new FadeTransition(Duration.millis(500), loginButtonGraphics);
        ftloginButtonGraphics.setFromValue(1);
        ftloginButtonGraphics.setToValue(0);

        stloginButton.play();
        ftloginButton.play();
        stloginButtonGraphics.play();
        ftloginButtonGraphics.play();


        ftloginButtonGraphics.setOnFinished(e -> {
            signupButton.setVisible(true);
            signupButtonGraphics.setVisible(true);
            ScaleTransition stsignupButton = new ScaleTransition(Duration.millis(500), signupButton);
            stsignupButton.setFromY(0);
            stsignupButton.setToY(1);
            FadeTransition ftsignupButton = new FadeTransition(Duration.millis(500), signupButton);
            ftsignupButton.setToValue(1);
            ScaleTransition stsignupButtonGraphics = new ScaleTransition(Duration.millis(500), signupButtonGraphics);
            stsignupButtonGraphics.setFromY(0);
            stsignupButtonGraphics.setToY(1);
            FadeTransition ftsignupButtonGraphics = new FadeTransition(Duration.millis(500), signupButtonGraphics);
            ftsignupButtonGraphics.setToValue(1);

            stsignupButton.play();
            ftsignupButton.play();
            stsignupButtonGraphics.play();
            ftsignupButtonGraphics.play();
        });


    }

    public void setSignupButton(ActionEvent event) throws IOException {
        FadeTransition ftLoginDashboard = new FadeTransition(Duration.millis(1000), loginDashboard);
        ftLoginDashboard.setToValue(0);
        ftLoginDashboard.play();

        TranslateTransition ttLoginDashboard = new TranslateTransition(Duration.millis(1000), loginDashboard);
        ttLoginDashboard.setToX(-73);
        ttLoginDashboard.play();


        ttLoginDashboard.setOnFinished(e -> {
            FadeTransition ftcreateAccountDashboard = new FadeTransition(Duration.millis(1000), createAccountDashboard);
            ftcreateAccountDashboard.setFromValue(0);
            ftcreateAccountDashboard.setToValue(1);
            ftcreateAccountDashboard.play();

            TranslateTransition ttcreateAccountDashboard = new TranslateTransition(Duration.millis(1000), createAccountDashboard);
            ttcreateAccountDashboard.setToX(73);
            ttcreateAccountDashboard.play();

            createAccountDashboard.setVisible(true);
        });


        ScaleTransition stsignupButton = new ScaleTransition(Duration.millis(500), signupButton);
        stsignupButton.setToY(0);
        FadeTransition ftsignupButton = new FadeTransition(Duration.millis(500), signupButton);
        ftsignupButton.setToValue(0);
        ScaleTransition stsignupButtonGraphics = new ScaleTransition(Duration.millis(500), signupButtonGraphics);
        stsignupButtonGraphics.setToY(0);
        FadeTransition ftsignupButtonGrphics = new FadeTransition(Duration.millis(500), signupButtonGraphics);
        ftsignupButtonGrphics.setToValue(0);

        stsignupButton.play();
        ftsignupButton.play();
        stsignupButtonGraphics.play();
        ftsignupButtonGrphics.play();

        ftsignupButtonGrphics.setOnFinished(e -> {
            loginButton.setVisible(true);
            loginButtonGraphics.setVisible(true);
            ScaleTransition stloginButton = new ScaleTransition(Duration.millis(500), loginButton);
            stloginButton.setFromY(0);
            stloginButton.setToY(1);
            FadeTransition ftloginButton = new FadeTransition(Duration.millis(500), loginButton);
            ftloginButton.setToValue(1);
            ScaleTransition stloginButtonGraphics = new ScaleTransition(Duration.millis(500), loginButtonGraphics);
            stloginButtonGraphics.setFromY(0);
            stloginButtonGraphics.setToY(1);
            FadeTransition ftloginButtonGraphics = new FadeTransition(Duration.millis(500), loginButtonGraphics);
            ftloginButtonGraphics.setToValue(1);

            stloginButton.play();
            ftloginButton.play();
            stloginButtonGraphics.play();
            ftloginButtonGraphics.play();
        });


        User user = new User();


        try {
            dpChooser.setOnAction(e -> {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image File", "*.jpg", "*.png", "*.jpeg"));
                File file = fc.showOpenDialog(null);

                if (file != null) {
                    try {
                        user.DP = Files.readAllBytes(file.toPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Image image = new Image(file.toURI().toString());
                    dpHolder.setFill(new ImagePattern(image));
                }


            });
        } catch (Exception e) {
            System.out.println(e);
        }

        createAccount.setOnAction(e -> {
            user.firstName = firstName.getText();
            user.lastName = lastName.getText();
            user.userID = userID.getText();
            user.password = password.getText();

            firstName.setText("");
            lastName.setText("");
            userID.setText("");
            password.setText("");
            boolean is_created = false;

            try {
                Socket client = new Socket(serverIP, serverPort);

                InputStream input = client.getInputStream();
                OutputStream output = client.getOutputStream();

                ObjectOutputStream objectOutput = new ObjectOutputStream(output);

                ObjectPacking object = new ObjectPacking();
                object.command = "createAccount";
                object.from = user.userID;
                object.to = "server";
                object.data = user;

                objectOutput.writeObject(object);

                ObjectInputStream objectInput = new ObjectInputStream(input);

                object = (ObjectPacking) objectInput.readObject();

                if(object.comment.equals("true")) is_created = true;
                else is_created = false;

                client.close();


            }catch(Exception e1){
                System.out.println("Error:Can't connect with server!");
            }


            if (!is_created) {
                signuperror.setLayoutX(298);
                signuperror.setOpacity(0.0);
                FadeTransition ftsignuperror = new FadeTransition(Duration.millis(100), signuperror);
                ftsignuperror.setFromValue(0.0);
                ftsignuperror.setToValue(0.8);
                signuperror.setOpacity(0.8);
                return;
            }
            ///next instruction after creating account....
            currUser = user.userID;

            /*FadeTransition ftRootPane = new FadeTransition(Duration.millis(500), rootPane);
            ftRootPane.setFromValue(1);
            ftRootPane.setToValue(0);
            ftRootPane.play();
*/

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/ui/player.fxml"));
                    Scene scene = new Scene(root);
                    Stage currWindow = (Stage) rootPane.getScene().getWindow();
                    currWindow.setScene(scene);

                    scene.setFill(Color.TRANSPARENT);


                    root.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            xOffset = event.getSceneX();
                            yOffset = event.getSceneY();
                        }
                    });
                    root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            currWindow.setX(event.getScreenX() - xOffset);
                            currWindow.setY(event.getScreenY() - yOffset);
                            currWindow.setOpacity(0.5);
                        }
                    });

                    root.setOnMouseReleased(e12 -> {
                        currWindow.setOpacity(1);
                    });

                } catch (IOException e2) {
                    e2.printStackTrace();
                }



        });

    }


    public void userLoginBtn(ActionEvent event) {

        File file = new File("src/resource/img/userdp");
        String[] myFiles;
        if(file.isDirectory()){
            myFiles = file.list();
            for (int i=0; i<myFiles.length; i++) {
                File myFile = new File(file, myFiles[i]);
                if(!myFile.getName().contains("default.png"))
                    myFile.delete();
            }
        }

        File file2 = new File("src/resource/playlist");
        String[] myFiles2;
        if(file2.isDirectory()){
            myFiles2 = file2.list();
            for (int i=0; i<myFiles2.length; i++) {
                File myFile = new File(file, myFiles2[i]);
                myFile.delete();
            }
        }


        String userNametest = loginUser.getText();
        String passwordtest = loginPassword.getText();
        loginUser.setText("");
        loginPassword.setText("");

        boolean is_login = false;

        try {
            Socket client = new Socket(serverIP, serverPort);

            InputStream input = client.getInputStream();
            OutputStream output = client.getOutputStream();

            ObjectOutputStream objectOutput = new ObjectOutputStream(output);

            ObjectPacking object = new ObjectPacking();
            object.command = "login";
            object.from = currUser;
            object.to = "server";
            object.comment = userNametest + " " + passwordtest;

            objectOutput.writeObject(object);

            ObjectInputStream objectInput = new ObjectInputStream(input);

            object = (ObjectPacking) objectInput.readObject();

            if(object.comment.equals("true")) is_login = true;
            else is_login = false;

            client.close();


        }catch(Exception e1){
            System.out.println("Error:Can't connect with server!");
        }

        if (!is_login) {
            loginerror.setLayoutX(320);
            loginerror.setOpacity(0.0);
            FadeTransition ftloginerror = new FadeTransition(Duration.millis(100), loginerror);
            ftloginerror.setFromValue(0.0);
            ftloginerror.setToValue(0.8);
            loginerror.setOpacity(0.8);
            loginerror.setVisible(true);
            return;
        }

        currUser = userNametest;
        TranslateTransition tt = new TranslateTransition(Duration.millis(1300), rootPane);
        tt.setFromY(0);
        tt.setToY(700);
        tt.play();

        tt.setOnFinished(e->{
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ui/player.fxml"));
                Scene scene = new Scene(root);
                Stage currWindow = (Stage) rootPane.getScene().getWindow();
                currWindow.setScene(scene);
                currWindow.show();

                scene.setFill(Color.TRANSPARENT);


                root.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        xOffset = event.getSceneX();
                        yOffset = event.getSceneY();
                    }
                });
                root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        currWindow.setX(event.getScreenX() - xOffset);
                        currWindow.setY(event.getScreenY() - yOffset);
                        currWindow.setOpacity(0.5);
                    }
                });

                root.setOnMouseReleased(e12 -> {
                    currWindow.setOpacity(1);
                });

            } catch (IOException e2) {
                e2.printStackTrace();
            }

        });

    }

    public void setExitButton(ActionEvent event) {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Timeline timeline = new Timeline();
        KeyFrame key = new KeyFrame(Duration.millis(1000),
                new KeyValue(stage.getScene().getRoot().opacityProperty(), 0));

        timeline.getKeyFrames().add(key);
        timeline.setOnFinished((ae) -> System.exit(1));
        timeline.play();

        stage.opacityProperty().bind(stage.getScene().getRoot().opacityProperty());

    }


    public void setUserLogin(ActionEvent event) {

    }

}
