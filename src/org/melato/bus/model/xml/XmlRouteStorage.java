/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.model.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.melato.bus.model.AbstractRouteStorage;
import org.melato.bus.model.NearbyFilter;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.Schedule;
import org.melato.gps.Point;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Waypoint;
import org.xml.sax.SAXException;

public class XmlRouteStorage extends AbstractRouteStorage {
  public static final String ROUTES_FILE = "routes.xml";
  public static final String PRIMARY_ROUTES_FILE = "primary_routes.xml";
  public static final String STOPS_FILE = "stops.gpx";
  public static final String ROUTES_DIR = "routes";
  public static final String GPX_DIR = "gpx";
  private URL dataUrl;
  
  public XmlRouteStorage(File dataDir) {
    
    super();
     try {
      this.dataUrl = dataDir.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException( e );
    }   
  }


  public XmlRouteStorage(URL dataUrl) {
    super();
    this.dataUrl = dataUrl;
  }

  private URL makeUrl(String file ) throws MalformedURLException {
    URL url = new URL(dataUrl, file );
    return url;    
  }
  
  private URL makeUrl(String dir, String file ) throws MalformedURLException {
    URL url = new URL(dataUrl, dir + "/" + file );      
    return url;    
  }
  
  private List<Route> loadRoutes(String filename) {
    try {
      URL url = makeUrl( filename );
      List<Route> routes = RouteHandler.parseRoutes(url.openStream());
      return routes;
    } catch( IOException e ) {
      throw new RuntimeException(e);
    } catch( SAXException e ) {
      throw new RuntimeException(e);
    }
  }

  public List<Route> loadRoutes() {
    return loadRoutes(ROUTES_FILE);
  }

  public List<Route> loadPrimaryRoutes() {
    return loadRoutes(PRIMARY_ROUTES_FILE);
  }

  public Route loadRoute(RouteId routeId) {
    try {
      URL url = makeUrl( ROUTES_DIR, routeId + ".xml" );
      //System.out.println( "loading " + url );
      List<Route> routes = RouteHandler.parseRoutes(url.openStream());
      if ( routes.isEmpty() ) {
        throw new RuntimeException( "Cannot load " + url );
      }
      // assume there is only one route in the file.  Return the first one.
      return routes.get(0);
    } catch( IOException e ) {
      throw new RuntimeException(e);
    } catch( SAXException e ) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public Schedule loadSchedule(RouteId routeId) {
    try {
      URL url = makeUrl( ROUTES_DIR, routeId + ".xml" );
      //System.out.println( "loading " + url );
      List<ScheduledRoute> routes = ScheduledRouteHandler.parseScheduledRoutes(url.openStream());
      if ( routes.isEmpty() ) {
        throw new RuntimeException( "Cannot load " + url );
      }
      // assume there is only one route in the file.  Return the first one.
      return routes.get(0).getSchedule();
    } catch( IOException e ) {
      throw new RuntimeException(e);
    } catch( SAXException e ) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public List<Waypoint> loadWaypoints(RouteId routeId) {
    try {
      URL url = makeUrl( GPX_DIR, routeId + ".gpx" );
      GPXParser parser = new GPXParser();
      GPX gpx = parser.parse(url.openStream());
      return gpx.getRoutes().get(0).getWaypoints();
    } catch( FileNotFoundException e ) {
      System.err.println( e );
      return Collections.emptyList();
    } catch( IOException e ) {
      throw new RuntimeException(e);
    }
  }
  
  public void iterateAllStops(Collection<Waypoint> collector) {
    try {
      URL url = makeUrl( STOPS_FILE );
      GPXParser parser = new GPXParser();
      parser.parseWaypoints(url.openStream(), collector);
    } catch( IOException e ) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void iterateNearbyStops(Point point, float latDiff, float lonDiff,
      Collection<Waypoint> collector) {
    iterateAllStops(new NearbyFilter(point, latDiff, lonDiff, collector));
  }
}
