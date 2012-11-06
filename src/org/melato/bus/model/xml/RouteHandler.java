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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.xml.XMLDelegator;
import org.melato.xml.XMLMappingHandler;
import org.melato.xml.XMLStringHandler;
import org.melato.xml.XMLTag;
import org.xml.sax.SAXException;

/**
 * XML element handler for route
 * @author Alex Athanasopoulos
 */
public class RouteHandler extends XMLMappingHandler {
  List<Route> routes = new ArrayList<Route>();
  XMLStringHandler titleHandler = new XMLStringHandler();
  Route route;


  
  public RouteHandler() {
    setHandler( RouteWriter.TITLE, titleHandler );
  }
  
  @Override
  public void start(XMLTag tag) throws SAXException {
    route = new Route();
    String name = tag.getRequiredAttribute(RouteWriter.NAME);
    String label = tag.getRequiredAttribute(RouteWriter.LABEL);
    String direction = tag.getRequiredAttribute(RouteWriter.DIRECTION);
    if ( "1".equals( tag.getAttribute(RouteWriter.PRIMARY))) {
      route.setPrimary(true);
    }
    int color = RouteWriter.parseColor(tag.getAttribute(RouteWriter.COLOR));
    if ( color != -1 ) {
      route.setColor(color);
    }
    color = RouteWriter.parseColor(tag.getAttribute(RouteWriter.BACKGROUND_COLOR));
    if ( color != -1 ) {
      route.setBackgroundColor(color);
    }
    RouteId id = new RouteId(name, direction);
    route.setRouteId(id);
    route.setLabel(label);
    super.start(tag);
  }
  protected void addRoute(Route route) {
    routes.add(route);
  }
  @Override
  public void end() throws SAXException {
    super.end();
    route.setTitle(titleHandler.getText());
    addRoute(route);
  }
  public void parse(InputStream in) throws IOException, SAXException {
    XMLMappingHandler root = new XMLMappingHandler();
    XMLMappingHandler routesHandler = new XMLMappingHandler();
    root.setHandler( RouteWriter.ROUTES, routesHandler);
    routesHandler.setHandler(RouteWriter.ROUTE, this);    
    XMLDelegator.parse(root, in);
  }
  public static List<Route> parseRoutes(InputStream in) throws IOException, SAXException {
    RouteHandler routeHandler = new RouteHandler();
    routeHandler.parse(in);
    return routeHandler.getRoutes();
  }
  public static List<Route> parseRoutes(File file) throws IOException {
    try {
      return parseRoutes(new FileInputStream(file));
    } catch(SAXException e ) {
      throw new RuntimeException(e);
    }
  }
  public List<Route> getRoutes() {
    return routes;
  }
  
}
