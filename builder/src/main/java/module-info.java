/**
 * @author Paul
 *
 */
module builder {
  requires transitive java.desktop;
  requires transitive java.prefs;
  requires transitive java.logging;
  requires transitive com.google.gson;
  requires transitive JRibbonMenu;
  requires com.formdev.flatlaf;
  requires com.formdev.flatlaf.intellijthemes;
  requires org.fife.RSyntaxTextArea;
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
  exports  builder.project;
  opens    builder.project to com.google.gson;
}
