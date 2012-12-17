package org.melato.bus.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialization {
  public static Object read(Class<?> clazz, File file) {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
      try {
        Object obj = in.readObject();
        if ( ! clazz.isInstance(obj)) {
          obj = null;
        }
        return obj;
      } finally {
        in.close();
      }
    } catch( Exception e ) {
      return null;
    }
  }
  
  public static void write(Object object, File file) throws IOException {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
    try {
      out.writeObject(object);
    } finally {
      out.close();
    }
  }

}
