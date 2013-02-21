/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.update;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.melato.progress.ProgressGenerator;

/** Various utilities, mainly for doing I/O */
public class Streams {
  public static void copy(InputStream in, OutputStream out) throws IOException {
    ProgressGenerator progress = ProgressGenerator.get();
    byte[] buf = new byte[4096];
    int total = 0;
    int n;
    try {
      while ((n = in.read(buf)) > 0) {
        out.write(buf, 0, n);
        total += n;
        progress.setPosition(total);
      }
    } finally {
      in.close();
      out.close();
    }
  }

  public static void copy(URL url, File file) throws IOException {
    File dir = file.getParentFile();
    File tmpFile = new File(dir, file.getName() + ".tmp");
    InputStream in = url.openStream();
    try {
      OutputStream out = new FileOutputStream(tmpFile);
      try {
        copy(in, out);
      } finally {
        out.close();
      }
    } finally {
      in.close();
    }
    file.delete();
    tmpFile.renameTo(file);
  }

  public static String copyToString(URL url)
      throws IOException {
    InputStream in = url.openStream();
    try {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      copy(in, buf);
      return buf.toString();
    } finally {
      in.close();
    }
  }
}
