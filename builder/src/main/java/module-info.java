/**
 * @author Paul
 *
 */
module builder {
  requires transitive java.desktop;
  requires transitive java.prefs;
  requires transitive java.logging;
  requires flatlaf;
  requires flatlaf.intellij.themes;
  requires com.google.gson;
  requires org.fife.RSyntaxTextArea;
  requires transitive JRibbonMenu;
  exports  builder.clipboard;
  exports  builder.controller;
  exports  builder.commands;
  exports  builder.common;
  exports  builder.events;
  exports  builder.fonts;
  exports  builder.mementos;
  exports  builder.models;
  exports  builder.parser;
  exports  builder.prefs;
  exports  builder.tables;
  exports  builder.themes;
  exports  builder.views;
  exports  builder.widgets;

}