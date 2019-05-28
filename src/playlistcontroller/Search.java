package playlistcontroller;

import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import ui.WelcomeController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Search {
    public static MediaView mv;
    boolean exist = false;
    boolean doreturn = false;

    public void saveAmplitudeData(File song) {


        File dir = new File("src/resource/amplitute/" + WelcomeController.currUser + "/");
        File file = new File("src/resource/amplitute/" + WelcomeController.currUser + "/" + song.getName() + ".txt");
        if (file.exists()) exist = true;

        if (!file.exists()) {
            dir.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        MediaPlayer media = new MediaPlayer(new Media(song.toURI().toString()));
        mv.setMediaPlayer(media);

        media.play();
        media.setVolume(0);

        media.setOnReady(new Runnable() {
            @Override
            public void run() {

                try {
                    Scanner input = new Scanner(new File("src/resource/amplitute/" + WelcomeController.currUser + "/" + song.getName() + "timeStamp.txt"));

                    double pasttime = Double.valueOf(input.next());


                    if (exist && Math.abs(pasttime - (media.getTotalDuration().toSeconds())) <= 0.3) {
                        doreturn = true;
                    } else if (exist) file.delete();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });

        media.setAudioSpectrumListener(new AudioSpectrumListener() {
            @Override
            public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
                if (doreturn) return;
                try {
                    FileOutputStream output = new FileOutputStream(file, true);
                    FileOutputStream timeoutput = new FileOutputStream(new File("src/resource/amplitute/" + WelcomeController.currUser + "/" + song.getName() + "timeStamp.txt"));
                    String tmp = "";

                    for (int i = 0; i < magnitudes.length; i++) {

                        tmp = tmp + Float.toString(magnitudes[i]) + " ";

                    }
                    timeoutput.write(String.valueOf(timestamp).getBytes());
                    output.write(tmp.getBytes());
                    output.close();

                } catch (FileNotFoundException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });
    }

    public ArrayList<String> Search(String keyword) {
        ArrayList<String> searchList = new ArrayList<String>();

        Scanner testread = null;
        try {
            testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (testread.hasNextLine()) {
            String info = testread.nextLine();
            String sub[] = info.split("\\?");

            String userSong = sub[0];

            if (userSong.contains(keyword)) searchList.add(userSong);
        }


        ArrayList<String> similarSongPath = null;

        for (String s : searchList) {
            File file = new File("src/resource/amplitute/" + WelcomeController.currUser + "/" + new File(s).getName() + ".txt");
            if (file.exists()) {
                similarSongPath = checkSimilarity(file);
            }
        }

        if (similarSongPath != null)
            for (String s : similarSongPath) searchList.add(s);


        for (int i = 0; i < searchList.size() - 1; i++) {
            String itmp = searchList.get(i);
            for (int j = i + 1; j < searchList.size(); j++) {
                String tmp = searchList.get(j);
                if (itmp.equals(tmp)) searchList.remove(tmp);
            }
        }


        return searchList;
    }

    public ArrayList<String> checkSimilarity(File currFile) {
        ArrayList<String> path = new ArrayList<String>();
        File folder = new File("src/resource/amplitute/" + WelcomeController.currUser + "/");
        File listOfFile[] = folder.listFiles();
        int count = 0;
        int numoffloat1 = 0;
        int numoffloat2 = 0;
        int min = 0;

        for (File f : listOfFile) {
            count = 0;
            numoffloat1 = 0;
            numoffloat2 = 0;
            try {
                Scanner in1 = new Scanner(currFile);
                Scanner in2 = new Scanner(f);

                while (in1.hasNext() && in2.hasNext()) {
                    float f1 = Float.valueOf(in1.next());
                    float f2 = Float.valueOf(in2.next());

                    if ((Math.abs(f1 - f2) < 0.000003)) count++;
                }

                in1.close();
                in2.close();

            } catch (Exception e) {
                System.out.println(e);
            }

            try {
                Scanner in1 = new Scanner(currFile);

                while (in1.hasNext()) {
                    float f1 = Float.valueOf(in1.next());
                    numoffloat1++;
                }

                in1.close();
            } catch (Exception e) {
                System.out.println(e);
            }

            try {
                Scanner in2 = new Scanner(f);

                while (in2.hasNext()) {
                    float f2 = Float.valueOf(in2.next());
                    numoffloat2++;
                }

                in2.close();
            } catch (Exception e) {
                System.out.println(e);
            }


            if (numoffloat1 < numoffloat2) min = numoffloat1;
            else min = numoffloat2;

            if (((count * 100.0) / (double) min) >= 65) {

                Scanner testread = null;
                try {
                    testread = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                while (testread.hasNextLine()) {
                    String info = testread.nextLine();
                    String sub[] = info.split("\\?");

                    String userSong = sub[0];
                    if (f.getName().contains(new File(userSong).getName())) {
                        path.add(userSong);
                    }
                }

            }

        }

        return path;
    }

}


