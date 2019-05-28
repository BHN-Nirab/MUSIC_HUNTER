package playlistcontroller;

import objectPacking.ObjectPacking;
import ui.WelcomeController;
import user.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class Playlist {
    public Music list;

    public void addMusictoPlaylist(String userID, File path, int rating) throws IOException, ClassNotFoundException {

        Music tmp = new Music();
        tmp.path = path;
        tmp.rating = rating;

        Socket client = new Socket(WelcomeController.serverIP, WelcomeController.serverPort);


        String outputstream = tmp.path.getAbsolutePath() + "?" + tmp.rating + "?" + tmp.priority + "?" + tmp.dateToAddedFavList + "\n";

        OutputStream out = client.getOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(out);

        ObjectPacking object = new ObjectPacking();
        object.command = "addMusictoPlaylist";
        object.comment = outputstream;
        object.from = WelcomeController.currUser;

        objectOut.writeObject(object);


        InputStream input = client.getInputStream();
        ObjectInputStream objectInput = new ObjectInputStream(input);
        ObjectPacking object2 = new ObjectPacking();

        object2 = (ObjectPacking) objectInput.readObject();

        if(!object2.comment.isEmpty() && object2.comment.equals("done"))
        {
            client.close();
        }



    }

    public static void backup() throws IOException, ClassNotFoundException {
        User data = new User();
        File file = new File("src/resource/playlist/" + WelcomeController.currUser + ".txt");
        if(file.exists())
        {
            data.DP = Files.readAllBytes(file.toPath());
        }
        else
            data.DP = new byte[1];


        Socket client = new Socket(WelcomeController.serverIP, WelcomeController.serverPort);

        OutputStream out = client.getOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(out);

        ObjectPacking object = new ObjectPacking();
        object.command = "backup";
        object.comment = WelcomeController.currUser;
        object.from = WelcomeController.currUser;
        object.data = data;

        objectOut.writeObject(object);


        InputStream input = client.getInputStream();
        ObjectInputStream objectInput = new ObjectInputStream(input);
        ObjectPacking object2 = new ObjectPacking();

        object2 = (ObjectPacking) objectInput.readObject();

        if(!object2.comment.isEmpty() && object2.comment.equals("done"))
        {
            client.close();
        }


    }

    public void removeMusicfromPlaylist(String userID, File path) {
        try {
            ArrayList<Music> musiclist = new ArrayList<Music>();
            try {
                Scanner input = new Scanner(new File("src/resource/playlist/" + userID + ".txt"));
                while (input.hasNextLine()) {
                    String musicInfo = input.nextLine();
                    String sub[] = musicInfo.split("\\?");
                    Music tmp = new Music();
                    tmp.path = new File(sub[0]);
                    tmp.rating = Integer.valueOf(sub[1]);
                    tmp.priority = Integer.valueOf(sub[2]);
                    tmp.dateToAddedFavList = sub[3];

                    musiclist.add(tmp);

                }
                input.close();

            } catch (Exception e) {
            }

            for (int i = 0; i < musiclist.size(); i++) {
                Music tmp = musiclist.get(i);

                if (tmp.path.getAbsolutePath().equals(path.getAbsolutePath())) {
                    musiclist.remove(tmp);
                    break;
                }


            }

            /*
            for (Music m : musiclist)
                if (m.path.getAbsolutePath().equals(path.getAbsolutePath())) musiclist.remove(m);
                */

            try {
                FileOutputStream output = new FileOutputStream(new File("src/resource/playlist/" + userID + ".txt"));

                String outputstream = "";
                for (Music m : musiclist)
                    outputstream = outputstream + m.path.getAbsolutePath() + "?" + m.rating + "?" + m.priority + "?" + m.dateToAddedFavList + "\n";

                output.write(outputstream.getBytes());
                output.close();
            } catch (Exception e) {
            }
        } catch (Exception e) {

        }


        try {

            File folder = new File("src/resource/amplitute/" + userID + "/");
            File listOfFile[] = folder.listFiles();

            for (File f : listOfFile) {
                if (f.getName().contains(path.getName())) f.delete();
            }

        } catch (Exception e) {
        }


    }

    public void setRating(String userID, File path, int rating) {
        ArrayList<Music> musiclist = new ArrayList<Music>();
        try {
            Scanner input = new Scanner(new File("src/resource/playlist/" + userID + ".txt"));
            while (input.hasNextLine()) {
                String musicInfo = input.nextLine();
                String sub[] = musicInfo.split("\\?");
                Music tmp = new Music();
                tmp.path = new File(sub[0]);
                tmp.rating = Integer.valueOf(sub[1]);
                tmp.priority = Integer.valueOf(sub[2]);
                tmp.dateToAddedFavList = sub[3];

                musiclist.add(tmp);

            }
            input.close();

        } catch (Exception e) {
        }

        for (Music m : musiclist)
            if (m.path.getAbsolutePath().equals(path.getAbsolutePath())) m.rating = rating;

        try {
            FileOutputStream output = new FileOutputStream(new File("src/resource/playlist/" + userID + ".txt"));

            String outputstream = "";
            for (Music m : musiclist)
                outputstream = outputstream + m.path.getAbsolutePath() + "?" + m.rating + "?" + m.priority + "?" + m.dateToAddedFavList + "\n";

            output.write(outputstream.getBytes());
            output.close();
        } catch (Exception e) {
        }

    }

    public static void updateCurrentMusicInfo(File song, int rating, int priority, String date)
    {
        ArrayList<Music> musiclist = new ArrayList<Music>();
        try {
            Scanner input = new Scanner(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));
            while (input.hasNextLine()) {
                String musicInfo = input.nextLine();
                String sub[] = musicInfo.split("\\?");
                Music tmp = new Music();
                tmp.path = new File(sub[0]);
                tmp.rating = Integer.valueOf(sub[1]);
                tmp.priority = Integer.valueOf(sub[2]);
                tmp.dateToAddedFavList = sub[3];

                musiclist.add(tmp);

            }
            input.close();

        } catch (Exception e) {
        }

        for (Music m : musiclist)
            if (m.path.getAbsolutePath().equals(song.getAbsolutePath())){
                m.rating = rating;
                m.priority = priority;
                m.dateToAddedFavList = date;
            }

        try {
            FileOutputStream output = new FileOutputStream(new File("src/resource/playlist/" + WelcomeController.currUser + ".txt"));

            String outputstream = "";
            for (Music m : musiclist)
                outputstream = outputstream + m.path.getAbsolutePath() + "?" + m.rating + "?" + m.priority + "?" + m.dateToAddedFavList + "\n";

            output.write(outputstream.getBytes());
            output.close();
        } catch (Exception e) {
        }


    }


    public void shareMusic(String userID, File path) {
        ArrayList<Music> musiclist = new ArrayList<Music>();
        try {
            Scanner input = new Scanner(new File("src/resource/playlist/" + userID + ".txt"));
            while (input.hasNextLine()) {
                String musicInfo = input.nextLine();
                String sub[] = musicInfo.split("\\?");
                Music tmp = new Music();
                tmp.path = new File(sub[0]);
                tmp.rating = Integer.valueOf(sub[1]);
                tmp.priority = Integer.valueOf(sub[2]);
                tmp.dateToAddedFavList = sub[3];

                musiclist.add(tmp);

            }
            input.close();

        } catch (Exception e) {
        }

        Music tmp = new Music();
        tmp.path = path;
        tmp.rating = 0;
        musiclist.add(tmp);

        try {
            FileOutputStream output = new FileOutputStream(new File("src/resource/playlist/" + userID + ".txt"));

            String outputstream = "";
            for (Music m : musiclist)
                outputstream = outputstream + m.path.getAbsolutePath() + "?" + m.rating + "?" + m.priority + "?" + m.dateToAddedFavList + "\n";

            output.write(outputstream.getBytes());
            output.close();
        } catch (Exception e) {
        }


    }


}
