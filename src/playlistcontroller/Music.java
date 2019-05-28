package playlistcontroller;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class Music implements Serializable {
    public File path;
    public int rating;
    public int priority;
    public String dateToAddedFavList;

    public Music() {
        path = null;
        rating = 0;
        priority = 0;
        dateToAddedFavList = "01-Jan-2000 23:01:01";
    }


}
