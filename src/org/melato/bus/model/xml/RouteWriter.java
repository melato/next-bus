/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.model.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.melato.bus.model.Route;
import org.melato.xml.XMLWriter;

/**
 * Writes routes to XML
 * @author Alex Athanasopoulos
 */
public class RouteWriter {
  public static final String ROUTES = "routes";
  public static final String ROUTE = "route";
  public static final String NAME = "name";
  public static final String LABEL = "label";
  public static final String DIRECTION = "direction";
  public static final String TITLE = "title";
  public static final String DAYS = "days_bitmap";
  public static final String TIME = "time";
  public static final String VALUE = "value";
  public static final String COLOR = "color";
  public static final String BACKGROUND_COLOR = "backgroundColor";
  public static final String PRIMARY = "primary";

  public RouteWriter() {
  }

  public void begin(XMLWriter xml) {
    xml.printHeader();
    xml.tagOpen(RouteWriter.ROUTES);
    xml.println();
  }
  
  public void end(XMLWriter xml) {
    xml.tagEnd(RouteWriter.ROUTES);
    xml.println();
  }

  public static String colorString(int color) {
    return "#" + Integer.toHexString(color);
  }
  
  public static int parseColor(String color) {
    if ( color == null || color.length() == 0 || color.charAt(0) != '#') {
      return 0;
    }
    return Integer.parseInt(color.substring(1), 16);
  }
    
  protected void routeBegin(Route route, XMLWriter xml ) {
    xml.tagOpen( ROUTE, false );
    xml.tagAttribute( NAME, route.getRouteId().getName() );
    xml.tagAttribute( LABEL, route.getLabel() );
    xml.tagAttribute( DIRECTION, route.getRouteId().getDirection() );
    if ( route.isPrimary() ) {
      xml.tagAttribute( PRIMARY, "1" );
    }
    xml.tagAttribute( COLOR, colorString(route.getColor()));
    xml.tagAttribute( BACKGROUND_COLOR, colorString(route.getBackgroundColor()));
    xml.tagClose();
  }
  
  protected void routeBody(Route route, XMLWriter xml) {
    xml.println();
    xml.tagOpen( TITLE);
    xml.text(route.getTitle());
    xml.tagEnd(TITLE);
  }
  
  protected void routeEnd(Route route, XMLWriter xml ) {
    xml.println();
    xml.tagEnd(ROUTE);
    xml.println();
  }

  public void write(Route route, XMLWriter xml) {
    routeBegin(route, xml);
    routeBody(route, xml);
    routeEnd(route, xml);    
  }
  
  public void writeRoutes(List<Route> routes, File file) throws IOException {
    XMLWriter xml = new XMLWriter(file);
    try {
      begin(xml);
      for( Route route: routes ) {
        write(route, xml);
      }
      end(xml);
    } finally {
      xml.close();
    }
  }
  
}
