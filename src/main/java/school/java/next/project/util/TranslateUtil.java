package school.java.next.project.util;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

/*
 * Check size and type for archive
 * @Author: Aldreks Albuquerque
 * 
 */
public class TranslateUtil {

  public static boolean checkFileSize(File file) {
    double bytes = file.length();
    if (bytes > (3*1024*1000)) {
      return false;
    }

    return true;
  }

  public static boolean checkFileType(File file) {
    try {
      AudioFile f = AudioFileIO.read(file);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
