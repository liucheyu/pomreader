module idv.liucheyu.pomreader {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.eclipse.jgit;
    requires com.fasterxml.jackson.databind;
    requires dom4j;

    exports idv.liucheyu.pomreader;
    exports idv.liucheyu.pomreader.controller;
    exports idv.liucheyu.pomreader.service;
    opens idv.liucheyu.pomreader.controller;
    opens idv.liucheyu.pomreader.service;
}