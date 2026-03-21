package io.github.opensri.application.ports;

public interface XmlSerializer <T> {

    String serialize( T document);
}
