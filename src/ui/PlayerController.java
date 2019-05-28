package ui;


import gesture.Main;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import objectPacking.ObjectPacking;
import playlistcontroller.Music;
import playlistcontroller.Playlist;
import playlistcontroller.Search;
import user.User;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class gestureRec extends Thread{

    @Override
    public void run()
    {
        try {
            gesture.Main.open();
        } catch (Exception e) {
          //  e.printStackTrace();
        }

    }
}

public class PlayerController implements Initializable {

    public static ObservableList<Label> musicList = FXCollections.observableArrayList();
    public static ObservableList<Music> tmpmusic = FXCollections.observableArrayList();
    public static ObservableList<Label> searchList = FXCollections.observableArrayList();
    public static boolean is_play = false;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    ListView<Label> searchResult;
    @FXML
    AnchorPane playlistSidePanel;
    @FXML
    AnchorPane rootPane;
    @FXML
    ListView<Label> playlistHolder;
    @FXML
    ImageView playPauseImage;
    @FXML
    HBox visualizerHolder;
    @FXML
    MediaView mediaview;
    Rectangle rects[];
    File file = null;
    MediaPlayer mediaPlayer = null;
    Music currMusic;
    Media media;
    @FXML
    Label startTime;
    @FXML
    Label addCounter;
    @FXML
    Label endTime;
    @FXML
    MediaView mvHolder;

    @FXML
    AnchorPane infoHolder;
    @FXML
    Slider volumeSlider;

    @FXML
    Label songName;
    @FXML
    Label title;
    @FXML
    Circle albumart;
    @FXML
    Label artist;
    @FXML
    Label year;
    @FXML
    Label album;
    @FXML
    AnchorPane logoutPane;

    @FXML
    Slider timeSlider;
    @FXML
    ImageView volumeButtonImage;
    @FXML
    Slider ratingSlider;
    @FXML
    ImageView rating1;
    @FXML
    ImageView rating2;
    @FXML
    ImageView rating3;
    @FXML
    ImageView rating4;
    @FXML
    ImageView rating5;

    @FXML
    Circle userdpShower;
    @FXML
    Button usernameShower;
    @FXML
    AnchorPane userInfoHolder;
    @FXML
    Button addFrndfromReq;
    @FXML
    Button addFrndfromSug;

    @FXML
    Circle userDP;
    @FXML
    Label userName;
    @FXML
    Label userID;
    @FXML
    Label userPass;
    @FXML
    TextField search;
    @FXML
    Button playBtnfromFrndlist;

    @FXML
    TextField inputpass;
    @FXML
    AnchorPane searchBarHolder;
    @FXML
    HBox hboxinlist;
    @FXML
    ListView<HBox> friendViewer;
    public static User user;

    ObservableList<HBox> frndlist = FXCollections.observableArrayList();

    public static boolean left = false;
    public static boolean right = false;
    public static boolean up = false;
    public static boolean down = false;

    public boolean isSeekingOccurs = false;
    public boolean isListened = false;

    double vol=0;
    int volslider=0;

    boolean is_albumset, is_artistset, is_yearset, is_imageset;
    boolean is_mute;
    boolean is_logoutpaneVisible;
    boolean is_userInfoHolderOpen;
    boolean is_searchBarHolderOpen;
    boolean halfCtrlPressed;
    User tmp = new User();
    boolean isClickRatingSlider;

    public PlayerController() throws IOException {
    }

    Thread t1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        t1 = new gestureRec();
        t1.start();


        playlistSidePanel.setVisible(false);
        TranslateTransition tt = new TranslateTransition(Duration.millis(1300), rootPane);
        tt.setFromY(700);
        tt.setToY(0);
        tt.play();



        try {
            Socket client = new Socket(WelcomeController.serverIP, WelcomeController.serverPort);

            OutputStream output = client.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);

            ObjectPacking object = new ObjectPacking();
            object.command = "initializeUserInfo";
            object.comment = WelcomeController.currUser;

            objectOutput.writeObject(object);

            InputStream input = client.getInputStream();
            ObjectInputStream objectInput = new ObjectInputStream(input);

            object = (ObjectPacking)objectInput.readObject();

            user = (User)object.data;

            FileOutputStream fout = new FileOutputStream("src/resource/img/userdp/" + user.userID + ".jpg");
            fout.write(user.DP);

            client.close();



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        initializePlaylistFile();



        addFrndfromSug.setVisible(false);
        addFrndfromReq.setVisible(false);
        searchResult.setItems(searchList);
        Search.mv = mvHolder;
        is_userInfoHolderOpen = false;
        userInfoHolder.setVisible(false);
        is_searchBarHolderOpen = false;
        searchBarHolder.setVisible(false);

        logoutPane.setVisible(false);
        is_logoutpaneVisible = false;
        halfCtrlPressed = false;
        usernameShower.setText(WelcomeController.currUser);

        try {
            Image dp;

            dp = new Image(new FileInputStream(new File(("src/resource/img/userdp/" + WelcomeController.currUser + ".jpg"))));

            //dp = new Image("/resource/img/userdp/" + WelcomeController.currUser + ".jpg");
            userdpShower.setFill(new ImagePattern(dp));
            currDP = new File("src/resource/img/userdp/" + WelcomeController.currUser + ".jpg");
        }catch (Exception e){}





        ratingSlider.setMin(0);
        ratingSlider.setMax(5);
        is_mute = false;
        timeSlider.setMin(0.0);
        timeSlider.setValue(0);
        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(100);

        infoHolder.setVisible(false);
        infoHolder.setLayoutX(100);
        visualizerHolder.setOpacity(0);
        rects = new Rectangle[100];
        for (int i = 0; i < rects.length; i++) {
            rects[i] = new Rectangle();
            rects[i].setFill(Color.GRAY);
            visualizerHolder.getChildren().addAll(rects[i]);
        }
        for (Rectangle r : rects) {
            r.setWidth(3);
            r.setHeight(100);
        }
        albumart.setFill(new ImagePattern(new Image("/resource/img/iconpack/music.png")));
        title.setPrefWidth(281.6);

        is_play = false;
        playPauseImage.setImage(new Image("/resource/img/iconpack/icons8-circled-play-64.png"));
        ///initializealll...

        initializeviewplaylist();
        playlistHolder.setItems(musicList);


        // playlistHolder.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // playlistHolder.setItems(musicList);
        //initializeList();

        rootPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB)
                opendSearchBar();
        });

        search.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                if (!"".equals(search.getText())) {
                    if (!searchList.isEmpty())
                        searchList.clear();

                    Search s = new Search();
                    ArrayList<String> list = s.Search(search.getText());
                    for (String string : list) {
                        //list hold a path of song as string...
                        /*
                       HBox hbox = new HBox();
                       hbox.setAlignment(Pos.CENTER_RIGHT);
                       hbox.setPrefHeight(50);
                       hbox.setPrefWidth(300);
                        */
                        Label searchlabel = new Label(new File(string).getName());
                        searchlabel.setFont(Font.font("Ebrima", 18));
                        searchlabel.setTextFill(Color.web("#e2e2e2"));
                        searchlabel.setAlignment(Pos.CENTER);
                        searchlabel.setLayoutX(100);
                        //searchlabel.setPrefWidth(300);
                        //searchlabel.setPrefHeight(50);
                       /*
                       ImageView img = new ImageView(new Image("/resource/img/iconpack/icons8-circled-play-64.png"));
                       img.setFitWidth(25);
                       img.setFitHeight(25);
                       Button searchbtn = new Button("",img);
                       searchbtn.setStyle("-fx-background-color: transparent;");
                       searchbtn.setPrefWidth(20);
                       searchbtn.setPrefHeight(20);

                       hbox.getChildren().addAll(searchlabel,searchbtn);
                        */
                        searchList.add(searchlabel);

                    }
                } else searchList.clear();
            }


        });

        friendViewer.setItems(frndlist);
        addCounter.setText(String.valueOf(tmp.friendReq.size()));



// Thread start...

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Runnable updater = new Runnable() {

                    @Override
                    public void run() {
                        //call a function to execute in another thread...
                        test();
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }

                    // UI update is run on the Application thread
                    Platform.runLater(updater);
                }
            }

        });
        // don't let thread prevent JVM shutdown
        thread.setDaemon(true);
        thread.start();

// Thread end...


    }

    public void test()
    {
        if(up && is_play)
        {
            if(vol<=1) {
                mediaPlayer.setVolume(vol);
                volumeSlider.setValue(vol*100);
                vol = vol + 0.2;
                if(vol>=1)
                    vol = 1;
            }
            up = false;
        }

        else if(down && is_play)
        {
            if(vol<=1) {
                mediaPlayer.setVolume(vol);
                volumeSlider.setValue(vol*100);
                vol = vol - 0.2;
                if(vol<=0)
                    vol = 0;
            }
            down = false;
        }

        /*if((up || down ) && !left && !right) {
            setPlayPauseButton();
            System.out.println("up = " + up + " down = " + down);
            up = down = false;
        }*/
        else if(left && !up && !down) {
            setPreviousButton();
            System.out.println("left");
            left = false;
        }
        else if(right && !up && !down)
        {
            setNextButton();
            System.out.println("right");
            right = false;
        }


        /*System.out.println("Left: " + left);
        System.out.println("Right: " + right);
        System.out.println("up: " + up);
        System.out.println("down: " + down);*/
    }



    public void setPlaylistSidePanelButton(MouseEvent event) {
        if (event.getClickCount() == 1) {
            playlistSidePanel.setVisible(true);

            FadeTransition ftPlaylistSidePanel = new FadeTransition(Duration.millis(2000), playlistSidePanel);
            ftPlaylistSidePanel.setFromValue(0);
            ftPlaylistSidePanel.setToValue(1);
            ftPlaylistSidePanel.play();
        }
        if (event.getClickCount() == 2) {
            FadeTransition ftPlaylistSidePanel = new FadeTransition(Duration.millis(2000), playlistSidePanel);
            ftPlaylistSidePanel.setFromValue(1);
            ftPlaylistSidePanel.setToValue(0);
            ftPlaylistSidePanel.play();
            playlistSidePanel.setVisible(false);
        }
    }


    public void setImportPlaylistButton(ActionEvent event) throws IOException, ClassNotFoundException {

        Playlist myPlaylist = new Playlist();

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Import Playlist", "*.mp3", "*.wav"));
        List<File> musicListf = fc.showOpenMultipleDialog(null);
        if (WelcomeController.currUser != null && musicListf != null) {
            for (File f : musicListf) {
                myPlaylist.addMusictoPlaylist(WelcomeController.currUser, f, 0);
                Label btn = new Label("▶ " + f.getName());

                btn.setPrefWidth(340);
                btn.setPrefHeight(30);
                btn.setContentDisplay(ContentDisplay.LEFT);
                btn.setStyle("-fx-background-color:white; -fx-font-size:14; -fx-color: white; -fx-font-family:arial; -fx-opacity:0.5");
                Music tmp = new Music();
                tmp.path = f;
                tmp.rating = 0;
                tmpmusic.add(tmp);

                musicList.add(btn);
            }
        }

        initializePlaylistFile();

    }


    public void setDeleteButton(ActionEvent event) {
        String fileName = null;
        try {
            ObservableList<Label> btn = playlistHolder.getSelectionModel().getSelectedItems();

            Label tmp = null;
            for (Label b : btn) tmp = b;


            fileName = tmp.getText();

            char ch[] = new char[fileName.length()];
            ch = fileName.toCharArray();
            fileName = "";
            for (int i = 2; i < ch.length; i++) fileName += (ch[i] + "");

            Scanner testread = null;
            try {
                testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (testread.hasNextLine()) {
                String info = testread.nextLine();
                String sub[] = info.split("\\?");
                Music tmp1 = new Music();
                tmp1.path = new File(sub[0]);
                tmp1.rating = Integer.valueOf(sub[1]);
                tmp1.priority = Integer.valueOf(sub[2]);
                tmp1.dateToAddedFavList = sub[3];

                if (tmp1.path.getName().equals(fileName)) {
                    Playlist myplaylist = new Playlist();
                    myplaylist.removeMusicfromPlaylist(WelcomeController.currUser, tmp1.path);

                    for (int i = 0; i < tmpmusic.size(); i++) {
                        Music tmp2 = tmpmusic.get(i);
                        if (tmp2.path.getName().equals(fileName)) {
                            tmpmusic.remove(tmp2);
                            break;
                        }
                    }


                    break;
                }
            }


            for (int i = 0; i < musicList.size(); i++) {
                Label lbl = musicList.get(i);
                if (lbl.getText().contains(fileName)) {
                    musicList.remove(lbl);
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public void  setPlayPauseButton(ActionEvent event) {

        isClickRatingSlider = false;
        isListened = false;
        isSeekingOccurs = false;
        if (!is_play) {
            FadeTransition ftVisualizerHolder = new FadeTransition(Duration.millis(2000), visualizerHolder);
            ftVisualizerHolder.setFromValue(0);
            ftVisualizerHolder.setToValue(0.7);
            ftVisualizerHolder.play();
            playPauseImage.setImage(new Image("/resource/img/iconpack/icons8-pause-button-64.png"));
            String fileName = null;
            try {
                ObservableList<Label> btn = playlistHolder.getSelectionModel().getSelectedItems();

                Label tmp = null;
                for (Label b : btn) tmp = b;

                if (btn.isEmpty()) {
                    is_play = false;
                    return;
                }

                fileName = tmp.getText();

                char ch[] = new char[fileName.length()];
                ch = fileName.toCharArray();
                fileName = "";
                for (int i = 2; i < ch.length; i++) fileName += (ch[i] + "");

                Scanner testread = null;
                try {
                    testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (testread.hasNextLine()) {
                    String info = testread.nextLine();
                    String sub[] = info.split("\\?");
                    Music tmp1 = new Music();
                    tmp1.path = new File(sub[0]);
                    tmp1.rating = Integer.valueOf(sub[1]);
                    tmp1.priority = Integer.valueOf(sub[2]);
                    tmp1.dateToAddedFavList = sub[3];

                    if (tmp1.path.getName().equals(fileName)) {


                        currMusic = new Music();
                        currMusic.path = tmp1.path;
                        currMusic.rating = tmp1.rating;
                        currMusic.priority = tmp1.priority;
                        currMusic.dateToAddedFavList = tmp1.dateToAddedFavList;
                        media = new Media(tmp1.path.toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        mediaview.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();
                        Search s = new Search();
                        s.saveAmplitudeData(currMusic.path);


                        break;

                    }
                }


            } catch (Exception e) {
                System.out.println(e);
            }


            is_play = true;
            visualizer();
            openInfo();

        } else {
            FadeTransition ftVisualizerHolder = new FadeTransition(Duration.millis(2000), visualizerHolder);
            ftVisualizerHolder.setFromValue(0.7);
            ftVisualizerHolder.setToValue(0);
            ftVisualizerHolder.play();
            playPauseImage.setImage(new Image("/resource/img/iconpack/icons8-circled-play-64.png"));
            if (mediaPlayer != null)
                mediaPlayer.pause();


            is_play = false;
        }
    }
    public void  setPlayPauseButton() {

        isClickRatingSlider = false;
        isListened = false;
        isSeekingOccurs = false;

        if (!is_play) {
            FadeTransition ftVisualizerHolder = new FadeTransition(Duration.millis(2000), visualizerHolder);
            ftVisualizerHolder.setFromValue(0);
            ftVisualizerHolder.setToValue(0.7);
            ftVisualizerHolder.play();
            playPauseImage.setImage(new Image("/resource/img/iconpack/icons8-pause-button-64.png"));
            String fileName = null;
            try {
                ObservableList<Label> btn = playlistHolder.getSelectionModel().getSelectedItems();

                Label tmp = null;
                for (Label b : btn) tmp = b;

                if (btn.isEmpty()) {
                    is_play = false;
                    return;
                }

                fileName = tmp.getText();

                char ch[] = new char[fileName.length()];
                ch = fileName.toCharArray();
                fileName = "";
                for (int i = 2; i < ch.length; i++) fileName += (ch[i] + "");

                Scanner testread = null;
                try {
                    testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (testread.hasNextLine()) {
                    String info = testread.nextLine();
                    String sub[] = info.split("\\?");
                    Music tmp1 = new Music();
                    tmp1.path = new File(sub[0]);
                    tmp1.rating = Integer.valueOf(sub[1]);
                    tmp1.priority = Integer.valueOf(sub[2]);
                    tmp1.dateToAddedFavList = sub[3];

                    if (tmp1.path.getName().equals(fileName)) {


                        currMusic = new Music();
                        currMusic.path = tmp1.path;
                        currMusic.rating = tmp1.rating;
                        currMusic.priority = tmp1.priority;
                        currMusic.dateToAddedFavList = tmp1.dateToAddedFavList;
                        media = new Media(tmp1.path.toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        mediaview.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();
                        Search s = new Search();
                        s.saveAmplitudeData(currMusic.path);

                        break;

                    }
                }


            } catch (Exception e) {
                System.out.println(e);
            }


            is_play = true;
            visualizer();
            openInfo();

        } else {
            FadeTransition ftVisualizerHolder = new FadeTransition(Duration.millis(2000), visualizerHolder);
            ftVisualizerHolder.setFromValue(0.7);
            ftVisualizerHolder.setToValue(0);
            ftVisualizerHolder.play();
            playPauseImage.setImage(new Image("/resource/img/iconpack/icons8-circled-play-64.png"));
            if (mediaPlayer != null)
                mediaPlayer.pause();


            is_play = false;
        }
    }


    public void setPreviousButton(ActionEvent event) {

        isClickRatingSlider = false;
        isListened = false;
        isSeekingOccurs = false;
        for (int i = 0; i < tmpmusic.size(); i++) {
            if (is_play && tmpmusic.get(i).path.getAbsolutePath().equals(currMusic.path.getAbsolutePath()) && i != 0) {
                mediaPlayer.pause();


                media = new Media(tmpmusic.get(i - 1).path.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                is_play = true;
                mediaview.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();

                currMusic = new Music();
                currMusic.path = tmpmusic.get(i - 1).path;
                currMusic.rating = tmpmusic.get(i - 1).rating;
                currMusic.priority = tmpmusic.get(i-1).priority;
                currMusic.dateToAddedFavList = tmpmusic.get(i-1).dateToAddedFavList;
                visualizer();
                openInfo();
                Search s = new Search();
                s.saveAmplitudeData(currMusic.path);
                return;
            }
        }

    }
    public void setPreviousButton() {
        isClickRatingSlider = false;
        isListened = false;
        isSeekingOccurs = false;

        for (int i = 0; i < tmpmusic.size(); i++) {
            if (is_play && tmpmusic.get(i).path.getAbsolutePath().equals(currMusic.path.getAbsolutePath()) && i != 0) {
                mediaPlayer.pause();


                media = new Media(tmpmusic.get(i - 1).path.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                is_play = true;
                mediaview.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();

                currMusic = new Music();
                currMusic.path = tmpmusic.get(i - 1).path;
                currMusic.rating = tmpmusic.get(i - 1).rating;
                currMusic.priority = tmpmusic.get(i-1).priority;
                currMusic.dateToAddedFavList = tmpmusic.get(i-1).dateToAddedFavList;
                visualizer();
                openInfo();
                Search s = new Search();
                s.saveAmplitudeData(currMusic.path);
                return;
            }
        }

    }


    public void setNextButton(ActionEvent event) {
        isClickRatingSlider = false;
        isListened = false;
        isSeekingOccurs = false;

        for (int i = 0; i < tmpmusic.size(); i++) {
            if (is_play && tmpmusic.get(i).path.getAbsolutePath().equals(currMusic.path.getAbsolutePath()) && i != tmpmusic.size() - 1) {
                mediaPlayer.pause();

                media = new Media(tmpmusic.get(i + 1).path.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                is_play = true;
                mediaview.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
                currMusic = new Music();
                currMusic.path = tmpmusic.get(i + 1).path;
                currMusic.rating = tmpmusic.get(i + 1).rating;
                currMusic.priority = tmpmusic.get(i+1).priority;
                currMusic.dateToAddedFavList = tmpmusic.get(i+1).dateToAddedFavList;
                visualizer();
                openInfo();
                Search s = new Search();
                s.saveAmplitudeData(currMusic.path);

                return;
            }
        }

    }
    public void setNextButton() {
        isClickRatingSlider = false;
        isListened = false;
        isSeekingOccurs = false;

        for (int i = 0; i < tmpmusic.size(); i++) {
            if (is_play && tmpmusic.get(i).path.getAbsolutePath().equals(currMusic.path.getAbsolutePath()) && i != tmpmusic.size() - 1) {
                mediaPlayer.pause();

                media = new Media(tmpmusic.get(i + 1).path.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                is_play = true;
                mediaview.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
                currMusic = new Music();
                currMusic.path = tmpmusic.get(i + 1).path;
                currMusic.rating = tmpmusic.get(i + 1).rating;
                currMusic.priority = tmpmusic.get(i+1).priority;
                currMusic.dateToAddedFavList = tmpmusic.get(i+1).dateToAddedFavList;
                visualizer();
                openInfo();
                Search s = new Search();
                s.saveAmplitudeData(currMusic.path);
                return;
            }
        }

    }



    public void setExitButton(ActionEvent event) throws IOException, ClassNotFoundException {
        Playlist.backup();

        FileOutputStream fout = new FileOutputStream(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
        fout.write("".getBytes());

        File dlt = new File("src/resource/playlist/" + WelcomeController.currUser + ".txt");
        dlt.delete();
        File dlt2 = new File("src/resource/img/userdp/" + WelcomeController.currUser + ".jpg");
        dlt2.delete();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Timeline timeline = new Timeline();
        KeyFrame key = new KeyFrame(Duration.millis(1000),
                new KeyValue(stage.getScene().getRoot().opacityProperty(), 0));

        timeline.getKeyFrames().add(key);
        timeline.setOnFinished((ae) -> System.exit(1));
        timeline.play();

        stage.opacityProperty().bind(stage.getScene().getRoot().opacityProperty());

    }

    public void visualizer() {

        setInfo();
        FadeTransition ftVisualizerHolder = new FadeTransition(Duration.millis(2000), visualizerHolder);
        ftVisualizerHolder.setFromValue(0);
        ftVisualizerHolder.setToValue(0.7);
        ftVisualizerHolder.play();

        mediaPlayer.setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
                for (int i = 0; i < rects.length; i++) {
                    double hight = magnitudes[i];
                    rects[i].setHeight(Math.abs(hight) + 100);
                }

            }
        });

    }

    public void setInfo() {

            setRatingImage(currMusic.rating);

            if (currMusic != null) {

                songName.setText(currMusic.path.getName());
                title.setText(currMusic.path.getName());
                try {
                    media.getMetadata().addListener(new MapChangeListener<String, Object>() {
                        @Override
                        public void onChanged(Change<? extends String, ? extends Object> ch) {
                            if (ch.wasAdded()) {
                                handleMetadata(ch.getKey(), ch.getValueAdded());
                            }
                        }
                    });
                } catch (RuntimeException re) {
                    System.out.println("Caught Exception: " + re.getMessage());
                }

            }

            set_unknown();
    }

    private void handleMetadata(String key, Object value) {
        if (key.equals("album")) {
            album.setText(value.toString());
            is_albumset = true;
        }
        //else

        if (key.equals("artist")) {
            artist.setText(value.toString());
            is_artistset = true;
        }
        //else

        if (key.equals("year")) {
            year.setText(value.toString());
            is_yearset = true;
        }
        //else

        if (key.equals("image")) {
            albumart.setFill(new ImagePattern((Image) value));
            is_imageset = true;
        }
        //else

    }

    public void set_unknown() {
        if (!is_albumset) album.setText("Unknown");
        if (!is_artistset) artist.setText("Unknown");
        ;
        if (!is_yearset) year.setText("Unknown");
        if (!is_imageset) albumart.setFill(new ImagePattern(new Image("/resource/img/iconpack/music.png")));

        is_albumset = false;
        is_artistset = false;
        is_yearset = false;
        is_imageset = false;
    }

    public void updatetmpMusic()
    {
        for(int i=0; i<tmpmusic.size(); i++)
        {
            Music m = tmpmusic.get(i);
            if(m.path.equals(currMusic.path))
            {
                Music tmp = new Music();
                tmp.path = currMusic.path;
                tmp.priority = currMusic.priority;
                tmp.rating = currMusic.rating;
                tmp.dateToAddedFavList = currMusic.dateToAddedFavList;

                tmpmusic.set(i, tmp);
                setRatingImage(tmp.rating);
                return;
            }
        }
    }

    public void openInfo() {
        infoHolder.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), infoHolder);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), infoHolder);
        tt.setFromX(100);
        tt.setToX(340);
        tt.play();

        AtomicInteger percent = new AtomicInteger(0);

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration current) {

                timeSlider.setValue(current.toSeconds());
                int setStartTime = (int) current.toSeconds();
                int mnt = setStartTime / 60;
                int sec = setStartTime % 60;
                String decoratemnt;

                if (mnt >= 0 && mnt <= 9) decoratemnt = "" + 0 + "" + mnt;
                else decoratemnt = mnt + "";

                String decoratesec;

                if (sec >= 0 && sec <= 9) decoratesec = "" + 0 + "" + sec;
                else decoratesec = sec + "";

                startTime.setText(decoratemnt + ":" + decoratesec);



                    if (current.toSeconds() >= percent.get() && percent.get() != 0 && !isSeekingOccurs && !isListened && !isClickRatingSlider && currMusic.priority <= 10) {
                        currMusic.priority++;
                        Playlist.updateCurrentMusicInfo(currMusic.path, currMusic.rating, currMusic.priority, currMusic.dateToAddedFavList);
                        updatetmpMusic();
                        isListened = true;


                        if (currMusic.priority == 0) {
                            String formatedDate = "01-Jan-2000 23:01:01";
                            currMusic.rating = 0;
                            currMusic.dateToAddedFavList = formatedDate;
                            Playlist.updateCurrentMusicInfo(currMusic.path, 0, currMusic.priority, formatedDate);
                            updatetmpMusic();
                        } else if (currMusic.priority == 2) {
                            Date dateAddedtoFavlist = new Date();

                            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            String formatedDate = format.format(dateAddedtoFavlist);

                            currMusic.rating = 1;
                            currMusic.dateToAddedFavList = formatedDate;
                            Playlist.updateCurrentMusicInfo(currMusic.path, 1, currMusic.priority, formatedDate);
                            updatetmpMusic();
                        } else if (currMusic.priority == 4) {
                            Date dateAddedtoFavlist = new Date();

                            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            String formatedDate = format.format(dateAddedtoFavlist);

                            currMusic.rating = 2;
                            currMusic.dateToAddedFavList = formatedDate;
                            Playlist.updateCurrentMusicInfo(currMusic.path, 2, currMusic.priority, formatedDate);
                            updatetmpMusic();
                        } else if (currMusic.priority == 6) {
                            Date dateAddedtoFavlist = new Date();

                            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            String formatedDate = format.format(dateAddedtoFavlist);

                            currMusic.rating = 3;
                            currMusic.dateToAddedFavList = formatedDate;
                            Playlist.updateCurrentMusicInfo(currMusic.path, 3, currMusic.priority, formatedDate);
                            updatetmpMusic();

                        } else if (currMusic.priority == 8) {
                            Date dateAddedtoFavlist = new Date();

                            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            String formatedDate = format.format(dateAddedtoFavlist);

                            currMusic.rating = 4;
                            currMusic.dateToAddedFavList = formatedDate;
                            Playlist.updateCurrentMusicInfo(currMusic.path, 4, currMusic.priority, formatedDate);
                            updatetmpMusic();
                        } else if (currMusic.priority >= 10) {
                            Date dateAddedtoFavlist = new Date();

                            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                            String formatedDate = format.format(dateAddedtoFavlist);

                            currMusic.rating = 5;
                            currMusic.dateToAddedFavList = formatedDate;
                            Playlist.updateCurrentMusicInfo(currMusic.path, 5, currMusic.priority, formatedDate);
                            updatetmpMusic();
                        }
                    }





            }
        });


        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {

                int tmppercent = (int)((mediaPlayer.getTotalDuration().toSeconds()*70)/100);
                percent.set(tmppercent);

                timeSlider.setMin(0.0);
                timeSlider.setValue(0);
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());


                int setStartTime = (int) mediaPlayer.getStopTime().toSeconds();
                int mnt = setStartTime / 60;
                int sec = setStartTime % 60;
                String decoratemnt;

                if (mnt >= 0 && mnt <= 9) decoratemnt = "" + 0 + "" + mnt;
                else decoratemnt = mnt + "";

                String decoratesec;

                if (sec >= 0 && sec <= 9) decoratesec = "" + 0 + "" + sec;
                else decoratesec = sec + "";

                endTime.setText(decoratemnt + ":" + decoratesec);


            }
        });

        timeSlider.setOnMouseDragged(e -> {
            isSeekingOccurs = true;
            mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
        });

        volumeSlider.setOnMouseDragged((e -> {
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        }));


        ratingSlider.setOnMouseDragged(e -> {
            isClickRatingSlider = true;
            int rating = (int) ratingSlider.getValue();

            if(rating==0) {
                currMusic.priority = 0;
                currMusic.rating = 0;
                currMusic.dateToAddedFavList = "01-Jan-2000 23:01:01";
                Playlist.updateCurrentMusicInfo(currMusic.path, rating, currMusic.priority, "01-Jan-2000 23:01:01");
                updatetmpMusic();
            }

            else
            {
                Date dateAddedtoFavlist = new Date();

                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String formatedDate = format.format(dateAddedtoFavlist);
                currMusic.rating = rating;
                currMusic.priority = (rating*2);
                currMusic.dateToAddedFavList = formatedDate;

                Playlist.updateCurrentMusicInfo(currMusic.path, rating, (rating*2), formatedDate);
                updatetmpMusic();
            }

           /* Playlist playlist = new Playlist();
            playlist.setRating(WelcomeController.currUser, currMusic.path, rating);*/


            setRatingImage(rating);

            /*int rating = (int) ratingSlider.getValue();
            Playlist playlist = new Playlist();
            playlist.setRating(WelcomeController.currUser, currMusic.path, rating);

            setRatingImage(rating);*/


        });


    }

    public void volumeButton(ActionEvent event) {
        if (is_play && !is_mute) {
            mediaPlayer.setVolume(0);
            volumeSlider.setValue(0);
            volumeButtonImage.setImage(new Image("/resource/img/iconpack/mute-512.png"));
            is_mute = true;
            return;
        }
        if (is_play && is_mute) {
            mediaPlayer.setVolume(1);
            volumeButtonImage.setImage(new Image("/resource/img/iconpack/volume.png"));
            is_mute = false;
            volumeSlider.setValue(100);
            return;
        }

    }

    public void setRatingImage(int rating) {
        if (rating == 0) {
            rating1.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating2.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating3.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating4.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating5.setImage(new Image("/resource/img/iconpack/heart.png"));
        } else if (rating == 1) {
            rating1.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating2.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating3.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating4.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating5.setImage(new Image("/resource/img/iconpack/heart.png"));
        } else if (rating == 2) {
            rating1.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating2.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating3.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating4.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating5.setImage(new Image("/resource/img/iconpack/heart.png"));
        } else if (rating == 3) {
            rating1.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating2.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating3.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating4.setImage(new Image("/resource/img/iconpack/heart.png"));
            rating5.setImage(new Image("/resource/img/iconpack/heart.png"));
        } else if (rating == 4) {
            rating1.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating2.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating3.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating4.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating5.setImage(new Image("/resource/img/iconpack/heart.png"));
        } else {
            rating1.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating2.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating3.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating4.setImage(new Image("/resource/img/iconpack/heartfav.png"));
            rating5.setImage(new Image("/resource/img/iconpack/heartfav.png"));
        }

    }


    public void initializefavlist() {
        musicList.clear();
        tmpmusic.clear();

        Scanner testread = null;
        try {
            testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
        } catch (FileNotFoundException e) {
            return;
            // e.printStackTrace();
        }
        while (testread.hasNextLine()) {
            String info = testread.nextLine();
            String sub[] = info.split("\\?");
            Music tmp = new Music();
            tmp.path = new File(sub[0]);
            tmp.rating = Integer.valueOf(sub[1]);
            tmp.priority = Integer.valueOf(sub[2]);
            tmp.dateToAddedFavList = sub[3];

            if (tmp.rating == 0) continue;

            tmpmusic.add(tmp);


            Label btn = new Label("▶ " + tmp.path.getName());

            btn.setPrefWidth(340);
            btn.setPrefHeight(30);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setStyle("-fx-background-color:white; -fx-font-size:14; -fx-color: white; -fx-font-family:arial; -fx-opacity:0.5");

            musicList.add(btn);
        }
    }

    public void initializePlaylistFile()
    {
        try{

            Socket client = new Socket(WelcomeController.serverIP, WelcomeController.serverPort);

            OutputStream output = client.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);

            ObjectPacking object = new ObjectPacking();
            object.command = "initializeUserPlaylist";
            object.comment = WelcomeController.currUser;

            objectOutput.writeObject(object);

            InputStream input = client.getInputStream();
            ObjectInputStream objectInput = new ObjectInputStream(input);

            object = (ObjectPacking)objectInput.readObject();

            User tmp = (User)object.data;

            FileOutputStream fout = new FileOutputStream("src/resource/playlist/" + WelcomeController.currUser + ".txt");
            if(tmp.DP.length>=2) {
                fout.write(tmp.DP);

                Scanner testread = null;
                try {
                    testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (testread.hasNextLine()) {
                    String info = testread.nextLine();
                    String sub[] = info.split("\\?");
                    Music tmp1 = new Music();
                    tmp1.path = new File(sub[0]);
                    tmp1.rating = Integer.valueOf(sub[1]);
                    tmp1.priority = Integer.valueOf(sub[2]);
                    tmp1.dateToAddedFavList = sub[3];

                    //check validity of favlist

                    SimpleDateFormat formatedAddedDate = new SimpleDateFormat("dd-MMM-yyy HH:mm:ss");
                    Date addedDate = null;
                    try{
                        addedDate = formatedAddedDate.parse(tmp1.dateToAddedFavList);
                    }catch(Exception e){}

                    Date today = new Date();
                    SimpleDateFormat formatedtoday = new SimpleDateFormat("dd-MMM-yyy HH:mm:ss");
                    String tmptoday = formatedtoday.format(today);

                    Date currDate = null;
                    try{
                        currDate = formatedtoday.parse(tmptoday);
                    }catch(Exception e){}

                    long duration = currDate.getTime() - addedDate.getTime();


                    if(tmp1.rating>0)
                    {
                        if(TimeUnit.MILLISECONDS.toSeconds(duration)>=10)
                        {
                            tmp1.rating--;
                            tmp1.priority = tmp1.priority-2;
                        }
                    }

                    if(tmp1.rating<=0)
                    {
                        tmp1.rating = 0;
                        tmp1.priority = 0;
                    }

                    /*if(TimeUnit.MILLISECONDS.toSeconds(duration)>=10)
                        tmp1.priority = */

                    tmpmusic.add(tmp1);
                }


            }

            client.close();

            String outputArray= "";

            for(int i=0; i<tmpmusic.size(); i++)
            {
                Music m = tmpmusic.get(i);
                outputArray = outputArray + m.path.getAbsolutePath() + "?" + m.rating + "?" + m.priority + "?" + m.dateToAddedFavList + "\n";
            }

            FileWriter writer = new FileWriter(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
            writer.write(outputArray);
            writer.flush();
            writer.close();


        }catch(Exception e)
        {
            System.out.println("Error:" + e);
        }



    }

    public void initializeviewplaylist() {
        musicList.clear();
        tmpmusic.clear();
        initializePlaylistFile();

        Scanner testread = null;
        try {
            testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
        } catch (FileNotFoundException e) {
            return;
            // e.printStackTrace();
        }
        while (testread.hasNextLine()) {
            String info = testread.nextLine();
            String sub[] = info.split("\\?");
            Music tmp = new Music();
            tmp.path = new File(sub[0]);
            tmp.rating = Integer.valueOf(sub[1]);
            tmp.priority = Integer.valueOf(sub[2]);
            tmp.dateToAddedFavList = sub[3];
            tmpmusic.add(tmp);


            Label btn = new Label("▶ " + tmp.path.getName());

            btn.setPrefWidth(340);
            btn.setPrefHeight(30);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setStyle("-fx-background-color:white; -fx-font-size:14; -fx-color: white; -fx-font-family:arial; -fx-opacity:0.5");

            musicList.add(btn);

        }
    }

    public void setviewPlaylist(ActionEvent event) {
        musicList.clear();
        tmpmusic.clear();

        Scanner testread = null;
        try {
            testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
        } catch (FileNotFoundException e) {
            return;
            // e.printStackTrace();
        }
        while (testread.hasNextLine()) {
            String info = testread.nextLine();
            String sub[] = info.split("\\?");
            Music tmp = new Music();
            tmp.path = new File(sub[0]);
            tmp.rating = Integer.valueOf(sub[1]);
            tmp.priority = Integer.valueOf(sub[2]);
            tmp.dateToAddedFavList = sub[3];
            tmpmusic.add(tmp);


            Label btn = new Label("▶ " + tmp.path.getName());

            btn.setPrefWidth(340);
            btn.setPrefHeight(30);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setStyle("-fx-background-color:white; -fx-font-size:14; -fx-color: white; -fx-font-family:arial; -fx-opacity:0.5");

            musicList.add(btn);

        }
        playlistHolder.setItems(musicList);
    }

    public void setviewFavlist(ActionEvent event) {
        initializefavlist();
        playlistHolder.setItems(musicList);
    }

    public void logoutPaneOpener(ActionEvent event) {
        if (!is_logoutpaneVisible) {
            logoutPane.setVisible(true);
            ScaleTransition st = new ScaleTransition(Duration.millis(100), logoutPane);
            st.setFromX(0);
            st.setToX(1);
            st.play();
            FadeTransition ft = new FadeTransition(Duration.millis(100), logoutPane);
            ft.setFromValue(0);
            ft.setToValue(1);
            is_logoutpaneVisible = true;
        } else {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), logoutPane);
            st.setFromX(1);
            st.setToX(0);
            st.play();
            FadeTransition ft = new FadeTransition(Duration.millis(100), logoutPane);
            ft.setFromValue(1);
            ft.setToValue(0);
            is_logoutpaneVisible = false;
        }
    }

    public void logout(ActionEvent event) throws IOException, ClassNotFoundException {
        if (is_play) mediaPlayer.stop();
        FadeTransition ftRootPane = new FadeTransition(Duration.millis(500), rootPane);
        ftRootPane.setFromValue(1);
        ftRootPane.setToValue(0);
        ftRootPane.play();

        ftRootPane.setOnFinished(e1 -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ui/welcome.fxml"));
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

        Playlist.backup();

        FileOutputStream fout = new FileOutputStream(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
        fout.write("".getBytes());

            File dlt = new File("src/resource/playlist/" + WelcomeController.currUser + ".txt");
            File dlt2 = new File("src/resource/img/userdp/" + WelcomeController.currUser + ".jpg");

            dlt.delete();
            dlt2.delete();

            Main.tmp = 0;


    }

    //////userinfo opener code

    public void setsettingButton(ActionEvent event) {

        updateuserInfo();
        if (!is_userInfoHolderOpen) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(1000), userInfoHolder);
            tt.setFromY(0);
            tt.setToY(240);
            tt.play();
            userInfoHolder.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(1000), userInfoHolder);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            is_userInfoHolderOpen = true;

        } else {
            TranslateTransition tt = new TranslateTransition(Duration.millis(2000), userInfoHolder);
            tt.setFromY(240);
            tt.setToY(0);
            tt.play();

            FadeTransition ft = new FadeTransition(Duration.millis(2000), userInfoHolder);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();

            is_userInfoHolderOpen = false;
            userInfoHolder.setVisible(false);
        }
    }

    @FXML
    Button editInfo;
    @FXML
    Button editDP;
    @FXML
    Button editPass;
    @FXML
    Button saveInfo;
    File currDP;

    boolean is_dpChange = false, is_passChange = false;

    File tmpDP;

    public void updateuserInfo() {
        editDP.setVisible(false);
        editPass.setVisible(false);
        saveInfo.setVisible(false);
        inputpass.setVisible(false);


        try {

            userDP.setFill(new ImagePattern(new Image(new FileInputStream(new File("src/resource/img/userdp/" + WelcomeController.currUser + ".jpg")))));
        }catch (Exception e){}
        userName.setText(user.firstName + " " + user.lastName);
        userID.setText(user.userID);
        userPass.setText("");


        editInfo.setOnAction(e -> {
            editDP.setVisible(true);
            editPass.setVisible(true);
            saveInfo.setVisible(true);
            inputpass.setVisible(true);

            FadeTransition ft1 = new FadeTransition(Duration.millis(500), editDP);
            ft1.setFromValue(0);
            ft1.setToValue(1);
            ft1.play();

            FadeTransition ft2 = new FadeTransition(Duration.millis(500), editPass);
            ft2.setFromValue(0);
            ft2.setToValue(1);
            ft2.play();

            FadeTransition ft3 = new FadeTransition(Duration.millis(500), saveInfo);
            ft3.setFromValue(0);
            ft3.setToValue(1);
            ft3.play();

            FadeTransition ft4 = new FadeTransition(Duration.millis(500), inputpass);
            ft4.setFromValue(0);
            ft4.setToValue(1);
            ft4.play();


            editDP.setOnAction(e2 -> {
                try {
                    FileChooser fc = new FileChooser();
                    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image File", "*.jpg", "*.png", "*.jpeg"));
                    File file = fc.showOpenDialog(null);

                    if (file != null) {
                        tmpDP = file;
                        Image image = new Image(file.toURI().toString());
                        userDP.setFill(new ImagePattern(image));
                        is_dpChange = true;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }

            });

            saveInfo.setOnAction(e4 -> {

                try {
                    updateuserLoginInfo();
                    user = tmp;

                } catch (Exception e2) {

                }
                is_dpChange = false;
                is_passChange = false;

                fadeoutTransition();

            });

        });
    }

    private void fadeoutTransition() {

        FadeTransition ft1 = new FadeTransition(Duration.millis(500), editDP);
        ft1.setFromValue(1);
        ft1.setToValue(0);
        ft1.play();

        FadeTransition ft2 = new FadeTransition(Duration.millis(500), editPass);
        ft2.setFromValue(1);
        ft2.setToValue(0);
        ft2.play();

        FadeTransition ft3 = new FadeTransition(Duration.millis(500), saveInfo);
        ft3.setFromValue(1);
        ft3.setToValue(0);
        ft3.play();

        FadeTransition ft4 = new FadeTransition(Duration.millis(500), inputpass);
        ft4.setFromValue(1);
        ft4.setToValue(0);
        ft4.play();

        editDP.setVisible(false);
        editPass.setVisible(false);
        saveInfo.setVisible(false);
        inputpass.setVisible(false);


    }

    private void updateuserLoginInfo() {

        /*String loginData = "";

        if (is_passChange) {

            try {
                File inputFile = new File("src/resource/login/loginData.txt");
                Scanner input = new Scanner(inputFile);
                String inputuserID = "";
                String inputpassword = "";

                while (input.hasNextLine()) {
                    inputuserID = input.next();
                    inputpassword = input.next();

                    if (inputuserID.equals(WelcomeController.currUser)) {
                        inputpassword = tmp.password;

                    }
                    loginData = loginData + inputuserID + " " + inputpassword + "\n";
                }
                input.close();

            } catch (Exception e) {
            }
            try {
                OutputStream out = new FileOutputStream("src/resource/login/loginData.txt");
                byte[] ara = loginData.getBytes();

                for (int i = 0; i < ara.length; i++) out.write(ara[i]);

                out.close();
            } catch (Exception e) {
            }
        }

        if (is_dpChange) {
            currDP.delete();
            Account acc = new Account();
            tmp.DP = acc.copyDPtoResourceFolder(tmpDP, WelcomeController.currUser);
            currDP = tmp.DP;
        }*/
    }

    public void getPass(ActionEvent event) {
        try {
            inputpass.setPromptText("New Password");

            if (!inputpass.getText().isEmpty() && !inputpass.getText().equals(tmp.password)) {
                String tmpPass = inputpass.getText();
                tmp.password = tmpPass;
                userPass.setText(tmp.password);
                is_passChange = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void opendSearchBar() {

        if (!is_searchBarHolderOpen) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(1000), searchBarHolder);
            tt.setFromY(0);
            tt.setToY(240);
            tt.play();
            searchBarHolder.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(1000), searchBarHolder);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            is_searchBarHolderOpen = true;

        } else {
            TranslateTransition tt = new TranslateTransition(Duration.millis(2000), searchBarHolder);
            tt.setFromY(240);
            tt.setToY(0);
            tt.play();

            FadeTransition ft = new FadeTransition(Duration.millis(2000), searchBarHolder);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();

            is_searchBarHolderOpen = false;
            searchBarHolder.setVisible(false);
        }


    }

    public void playfromSearch(ActionEvent event) {

        if (!is_play) {
            FadeTransition ftVisualizerHolder = new FadeTransition(Duration.millis(2000), visualizerHolder);
            ftVisualizerHolder.setFromValue(0);
            ftVisualizerHolder.setToValue(0.7);
            ftVisualizerHolder.play();
            playPauseImage.setImage(new Image("/resource/img/iconpack/icons8-pause-button-64.png"));
            String fileName = null;
            try {
                ObservableList<Label> btn = searchResult.getSelectionModel().getSelectedItems();

                Label tmp = null;
                for (Label b : btn) tmp = b;

                if (btn.isEmpty()) {
                    is_play = false;
                    return;
                }

                fileName = tmp.getText();

                char ch[] = new char[fileName.length()];
                ch = fileName.toCharArray();
                fileName = "";
                for (int i = 0; i < ch.length; i++) fileName += (ch[i] + "");

                Scanner testread = null;
                try {
                    testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (testread.hasNextLine()) {
                    String info = testread.nextLine();
                    String sub[] = info.split("\\?");
                    Music tmp1 = new Music();
                    tmp1.path = new File(sub[0]);
                    tmp1.rating = Integer.valueOf(sub[1]);
                    tmp1.priority = Integer.valueOf(sub[2]);
                    tmp1.dateToAddedFavList = sub[3];

                    if (tmp1.path.getName().equals(fileName)) {


                        currMusic = new Music();
                        currMusic.path = tmp1.path;
                        currMusic.rating = tmp1.rating;
                        media = new Media(tmp1.path.toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        mediaview.setMediaPlayer(mediaPlayer);
                        mediaPlayer.play();
                        Search s = new Search();
                        s.saveAmplitudeData(currMusic.path);

                        break;

                    }
                }


            } catch (Exception e) {
                System.out.println(e);
            }


            is_play = true;
            opendSearchBar();
            visualizer();
            openInfo();

        } else {
            FadeTransition ftVisualizerHolder = new FadeTransition(Duration.millis(2000), visualizerHolder);
            ftVisualizerHolder.setFromValue(0.7);
            ftVisualizerHolder.setToValue(0);
            ftVisualizerHolder.play();
            playPauseImage.setImage(new Image("/resource/img/iconpack/icons8-circled-play-64.png"));
            if (mediaPlayer != null)
                mediaPlayer.pause();


            is_play = false;
        }

    }

    //start add..sug...req bla bla bla....


    public void setSugBtn(ActionEvent event) {
        /*addFrndfromSug.setVisible(true);
        addFrndfromReq.setVisible(false);
        playBtnfromFrndlist.setVisible(false);
        frndlist.remove(0, frndlist.size());

        Account acc = new Account();

        ArrayList<String> listofSuggestedFriend = acc.suggestion(WelcomeController.currUser);

        for (String s : listofSuggestedFriend) {
            HBox hbox = new HBox();
            hbox.setSpacing(30);

            Label label = new Label();
            label.setText(s);
            label.setFont(Font.font("Ebrima", 18));
            label.setTextFill(Color.web("#e2e2e2"));

            Circle circle = new Circle(17);

            User stmp = null;
            try {
                ObjectInputStream ob = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + s + ".txt")));

                Image dp;
                stmp = (User) ob.readObject();
            } catch (Exception e) {
                System.out.println(e);
            }


            Image dp = new Image(stmp.DP.toURI().toString());
            circle.setFill(new ImagePattern(dp));
            hbox.getChildren().addAll(circle, label);


            frndlist.add(hbox);


        }
*/

    }

    public void setAddBtn(ActionEvent event) {
       /* HBox tmp = null;
        try {
            ObservableList<HBox> label = friendViewer.getSelectionModel().getSelectedItems();

            for (HBox b : label) tmp = b;

        } catch (Exception e) {
            System.out.println(e);
        }

        Label l = (Label) tmp.getChildren().get(1);

        for (int i = 0; i < frndlist.size(); i++) {
            HBox h = frndlist.get(i);
            Label tmpl = (Label) h.getChildren().get(1);
            if (tmpl.getText().equals(l.getText())) frndlist.remove(h);
        }

        Account acc = new Account();
        acc.friendReqList(WelcomeController.currUser, l.getText());

*/
    }


    public void setcurrFrndBtn(ActionEvent event) {
        /*playBtnfromFrndlist.setVisible(true);
        addFrndfromSug.setVisible(false);
        addFrndfromReq.setVisible(false);
        frndlist.remove(0, frndlist.size());

        User curruser = null;
        try {
            ObjectInputStream ob = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + WelcomeController.currUser + ".txt")));
            curruser = (User) ob.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }

        for (String s : curruser.friendList) {
            HBox hbox = new HBox();
            hbox.setSpacing(30);

            Label label = new Label();
            label.setText(s);
            label.setFont(Font.font("Ebrima", 18));
            label.setTextFill(Color.web("#e2e2e2"));

            Circle circle = new Circle(17);

            User stmp = null;
            try {
                ObjectInputStream ob = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + s + ".txt")));

                Image dp;
                stmp = (User) ob.readObject();
            } catch (Exception e) {
                System.out.println(e);
            }


            Image dp = new Image(stmp.DP.toURI().toString());
            circle.setFill(new ImagePattern(dp));
            hbox.getChildren().addAll(circle, label);


            frndlist.add(hbox);

        }
*/
    }

    public void setplayBtnfromFrndlist(ActionEvent event) throws IOException, ClassNotFoundException {
        musicList.clear();
        tmpmusic.clear();
        HBox tmp = null;
        try {
            ObservableList<HBox> label = friendViewer.getSelectionModel().getSelectedItems();

            for (HBox b : label) tmp = b;

        } catch (Exception e) {
            System.out.println(e);
        }

        Label l = (Label) tmp.getChildren().get(1);


        Scanner testread = null;
        try {
            testread = new Scanner(new File("src/resource/playlist/" + l.getText() + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (testread.hasNextLine()) {
            String info = testread.nextLine();
            String sub[] = info.split("\\?");
            Music tmp1 = new Music();
            tmp1.path = new File(sub[0]);
            tmp1.rating = 0;
            tmp1.priority = Integer.valueOf(sub[2]);
            tmp1.dateToAddedFavList = sub[3];

            Playlist myplaylist = new Playlist();
            myplaylist.addMusictoPlaylist(WelcomeController.currUser, tmp1.path, 0);
            tmpmusic.add(tmp1);
            Label btn = new Label("▶ " + tmp1.path.getName());

            btn.setPrefWidth(340);
            btn.setPrefHeight(30);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setStyle("-fx-background-color:white; -fx-font-size:14; -fx-color: white; -fx-font-family:arial; -fx-opacity:0.5");

            musicList.add(btn);

        }

    }

    public void frndreq(ActionEvent event) {
        /*addFrndfromReq.setVisible(true);
        addFrndfromSug.setVisible(false);
        frndlist.remove(0, frndlist.size());

        User tmpuser = null;
        try {
            ObjectInputStream ob = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + WelcomeController.currUser + ".txt")));
            tmpuser = (User) ob.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }

        for (String s : tmpuser.friendReq) {
            HBox hbox = new HBox();
            hbox.setSpacing(30);

            Label label = new Label();
            label.setText(s);
            label.setFont(Font.font("Ebrima", 18));
            label.setTextFill(Color.web("#e2e2e2"));

            Circle circle = new Circle(17);

            User stmp = null;
            try {
                ObjectInputStream ob = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + s + ".txt")));

                Image dp;
                stmp = (User) ob.readObject();
            } catch (Exception e) {
                System.out.println(e);
            }


            Image dp = new Image(stmp.DP.toURI().toString());
            circle.setFill(new ImagePattern(dp));
            hbox.getChildren().addAll(circle, label);

            frndlist.add(hbox);

        }
*/
    }

    public void addFrndBtn(ActionEvent event) {
        /*HBox tmp = null;
        try {
            ObservableList<HBox> label = friendViewer.getSelectionModel().getSelectedItems();

            for (HBox b : label) tmp = b;

        } catch (Exception e) {
            System.out.println(e);
        }

        Label l = (Label) tmp.getChildren().get(1);

        for (int i = 0; i < frndlist.size(); i++) {
            HBox h = frndlist.get(i);
            Label tmpl = (Label) h.getChildren().get(1);
            if (tmpl.getText().equals(l.getText())) frndlist.remove(h);
        }

        Account acc = new Account();
        acc.addFriend(WelcomeController.currUser, l.getText());

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File("src/resource/userdata/" + WelcomeController.currUser + ".txt")));
            User tmpuser = (User) input.readObject();

            addCounter.setText(String.valueOf(tmpuser.friendReq.size()));

            input.close();

        } catch (Exception e) {
        }*/
    }


}

