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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.melato.convert.xml.FieldHandler;
import org.melato.convert.xml.FieldWriter;
import org.melato.convert.xml.ReflectionHandler;
import org.melato.convert.xml.ReflectionWriter;
import org.melato.log.Log;
import org.melato.progress.ProgressGenerator;
import org.melato.xml.XMLWriter;


/** Checks for updates to various files and can download them. */
public class PortableUpdateManager {
  public static final String INSTALLED = "installed-files.xml";
  public static final String AVAILABLE = "available-files.xml";
  
  public static final String FILE_TAG = "file";
  public static final String UPDATES_TAG = "updates";
  
  private List<UpdateFile> availableFiles = null;
  private List<UpdateFile> installedFiles = null;
  
  private File  filesDir;
  private URL   indexUrl;
    
  public PortableUpdateManager(URL indexUrl, File filesDir) {
    super();
    setIndexUrl(indexUrl);
    setFilesDir(filesDir);
  }

  public PortableUpdateManager(String indexUrl, File filesDir) {
    super();
    setIndexUrl(indexUrl);
    setFilesDir(filesDir);
  }

  protected PortableUpdateManager() {    
  }
  
  
  protected void setFilesDir(File filesDir) {
    this.filesDir = filesDir;
  }

  protected void setIndexUrl(URL indexUrl) {
    this.indexUrl = indexUrl;
  }

  protected void setIndexUrl(String indexUrl) {
    try {
      this.indexUrl = new URL(indexUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException( e );
    }
  }

  /** Load the index from a local file.
   */
  private boolean readIndex(File file, Collection<UpdateFile> collector) {
    if ( file.exists()) {
      try {
        ReflectionHandler<UpdateFile> reader = new FieldHandler<UpdateFile>(UpdateFile.class, collector);    
        reader.parse(UPDATES_TAG + "/" + FILE_TAG, new FileInputStream(file));
        Log.info( "readIndex size=" + collector.size());
        return true;
      } catch(Exception e) {
        file.delete();
      }
    }
    return false;
  }
  
  /** Load the index from a local file.
   */
  private boolean readIndex(String filename, Collection<UpdateFile> collector) {
    File file = new File(filesDir, filename);
    return readIndex(file, collector);
  }
  
  private void writeIndex(Collection<UpdateFile> index, String filename) {
    File file = new File(filesDir, filename);
    File tmpFile = new File(filesDir, filename + ".tmp");
    XMLWriter xml;
    try {
      xml = new XMLWriter(new FileOutputStream(tmpFile));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException( e );
    } catch (FileNotFoundException e) {
      throw new RuntimeException( e );
    }
    try {
      xml.printHeader();
      xml.tagOpen(UPDATES_TAG);
      ReflectionWriter<UpdateFile> writer = new FieldWriter<UpdateFile>(UpdateFile.class, xml, FILE_TAG);
      for( UpdateFile f: index) {
        writer.write(f);
      }
      xml.tagEnd(UPDATES_TAG);
      xml.close();
      file.delete();
      tmpFile.renameTo( file );
    } finally {
      
    }
  }
  
  private URL getURL(String url) throws MalformedURLException {
    return new URL( indexUrl, url);
  }
  
  private void downloadAvailable() throws IOException {
    File file = new File(filesDir, AVAILABLE);
    Streams.copy(indexUrl, file);
    Log.info( indexUrl );
  }
  
  /** If the list of available updates is empty, check once a week. */
  private int DEFAULT_FREQUENCY_HOURS = 24 * 7;
  
  /**
   * Return true if any of the updates in the list needs to be rechecked relative 
   * If 
   * @param list
   * @param lastUpdateTime
   * @return
   */
  private boolean needsRefresh(Collection<UpdateFile> list, long lastUpdateTime) {
    Log.info( "needsRefresh: " + lastUpdateTime );
    long now = System.currentTimeMillis();
    boolean hasFiles = false;
    boolean needsRefresh = false;
    for(UpdateFile f: list) {
      hasFiles = true;
      long expires = lastUpdateTime + f.getFrequencyHours() * 3600 * 1000L;
      //expires = lastUpdateTime + 10;  // *****  DEBUG *****
      Log.info( "refresh now: " + now + " last: " + lastUpdateTime + " expires: " + expires + " hours: " + f.getFrequencyHours() );
      if ( expires < now ) {
        needsRefresh = true;
        break;
      }
    }
    if ( ! hasFiles ) {
      needsRefresh = lastUpdateTime + DEFAULT_FREQUENCY_HOURS * 3600 * 1000L < now;
    }
    Log.info( "needsRefresh: " + needsRefresh );
    return needsRefresh;
  }

  public boolean isRequired() {
    return isFirstTime();
  }
    
  public boolean isFirstTime() {
    File file = new File(filesDir, AVAILABLE);
    return ! file.exists();    
  }
  
  public void forceUpdates() {
    File file = new File(filesDir, AVAILABLE);
    file.delete();
  }
  /**
   * Check if the list of available updates needs to be refreshed from the server.
   * @return true if we need to download the list, false otherwise.
   */
  public boolean needsRefresh() {
    availableFiles = new ArrayList<UpdateFile>();
    File file = new File(filesDir, AVAILABLE);
    Log.info( file );
    if ( file.exists() ) {
      readIndex(AVAILABLE, availableFiles);
      return needsRefresh(availableFiles, file.lastModified());
    } else {
      availableFiles = null;
      return true;
    }
  }
  /**
   * Update our cache of available updates and load it in memory.
   * The AVAILABLE cache file will be updated if:
   * <ul>
   * <li>It does not exist.
   * <li>Its age is older than the frequencyHours field of any available update. 
   * <ul>
   * In all cases, the available updates will be not-null.
   */
  private void initAvailable() {
    Log.info("initAvailable availableFiles=" + availableFiles );
    if ( availableFiles == null && needsRefresh() ) {
      availableFiles = new ArrayList<UpdateFile>();
      File file = new File(filesDir, AVAILABLE);
      Log.info( file );
      try {
        downloadAvailable();
      } catch (IOException e) {
        Log.info(e);
      }
      if ( file.exists() ) {
        readIndex(AVAILABLE, availableFiles);
      }
    }
  }
  
  private void initInstalled() {    
    if ( installedFiles == null ) {
      installedFiles = new ArrayList<UpdateFile>();
      readIndex(INSTALLED, installedFiles);
    }
  }
  
  private UpdateFile findFile(Collection<UpdateFile> list, String name) {
    for(UpdateFile f: list) {
      if ( name.equals(f.getName())) {
        return f;
      }
    }
    return null;
  }

  public static boolean needsUpdate(UpdateFile installed, UpdateFile available) {
    if ( available == null )
      return false;
    if ( installed == null )
      return true;
    String version = available.getVersion();
    if ( version == null ) {
      return false;
    }
    if ( installed == null || ! version.equals(installed.getVersion())) {
      return true;
    }
    return false;
  }
  
  /**
   * Return a list of files that have available updates.
   * @return
   */
  public List<UpdateFile> getAvailableUpdates() {
    initAvailable();
    initInstalled();
    List<UpdateFile> updates = new ArrayList<UpdateFile>();
    for(UpdateFile available: availableFiles ) {
      Log.info( available );
      String version = available.getVersion();
      if ( version == null ) {
        System.err.println( "Missing version from available update: " + available.getName());
        continue;
      }
      UpdateFile installed = findFile(installedFiles, available.getName());
      if ( installed == null || ! version.equals(installed.getVersion())) {
        updates.add(available);
      }
    }
    Log.info( "available updates: " + updates.size());
    return updates;
  }
  
  public void setInstalled(UpdateFile updateFile) {
    initInstalled();
    String name = updateFile.getName();
    boolean found = false;
    for( int i = 0; i < installedFiles.size(); i++ ) {
      if ( name.equals( installedFiles.get(i).getName())) {
        installedFiles.set(i, updateFile);
        found = true;
        break;        
      }
    }
    if ( ! found )
      installedFiles.add(updateFile);
    writeIndex(installedFiles, INSTALLED);
  }
  
  private void unzip( File zipFile, String entryName, File destFile ) throws ZipException, IOException {
    ZipFile zip = new ZipFile(zipFile);
    ZipEntry entry = new ZipEntry(entryName);
    Streams.copy(zip.getInputStream(entry), new FileOutputStream(destFile));    
  }
  
  private File getTempFile(File file, String suffix) {
    File tmpFile = new File(file.getParentFile(), file.getName() + suffix);
    tmpFile.delete();
    return tmpFile;
  }
  
  public void updateFile(UpdateFile updateFile, File destinationFile) {
    File tmpFile = getTempFile(destinationFile, ".tmp");
    Log.info("update file: " + destinationFile);
    URL url;
    try {
      url = getURL(updateFile.getUrl());
      int size = updateFile.getSize();
      if ( size != 0 ) {
        ProgressGenerator.get().setLimit(size);
      }        
      Streams.copy(url, tmpFile);
      tmpFile.renameTo(destinationFile);
      setInstalled(updateFile);
    } catch (MalformedURLException e) {
      throw new RuntimeException( e );
    } catch (IOException e) {
      throw new RuntimeException( e );
    }    
  }

  public void updateZipedFile(UpdateFile updateFile, String zipEntry, File destinationFile) {
    Log.info("update zip file: " + destinationFile);
    destinationFile.getParentFile().mkdirs();
    File zipFile = getTempFile(destinationFile, ".zip");
    File tmpFile = getTempFile(destinationFile, ".tmp");
    URL url;
    try {
      url = getURL(updateFile.getUrl());
      int size = updateFile.getSize();
      if ( size != 0 ) {
        ProgressGenerator.get().setLimit(size);
      }        
      Streams.copy(url, zipFile);
      unzip( zipFile, zipEntry, tmpFile);
      zipFile.delete();
      tmpFile.renameTo(destinationFile);
      setInstalled(updateFile);
    } catch (MalformedURLException e) {
      throw new RuntimeException( e );
    } catch (IOException e) {
      throw new RuntimeException( e );
    }    
  }

  public void update(List<UpdateFile> updates) {}
 
}
