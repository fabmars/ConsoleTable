package org.fabmars.console;

import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fabmars on 19/09/16.
 */
public class ConsoleTableTest {

  public static final String CHARSET_NAME = "UTF-8";


  public static void testOutput(String expectedOutputFile, ConsoleTable consoleTable) {
    try(InputStream expectedOutputStream = ConsoleTableTest.class.getResourceAsStream("/"+expectedOutputFile)) {
      String actualOutput = consoleTable.toString(CHARSET_NAME);
      System.out.println(actualOutput);
      byte[] bytes = readAllBytes(expectedOutputStream);
      Assertions.assertEquals(new String(bytes, CHARSET_NAME), actualOutput);
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] readAllBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
    byte[] buffer = new byte[4096];
    int read;
    while((read = inputStream.read(buffer)) >= 0) {
      baos.write(buffer, 0, read);
    }
    return baos.toByteArray();
  }
}
