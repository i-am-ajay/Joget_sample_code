package org.joget.geowatch.util.debug.db.generator;

public class EventIdGenerator extends IdGenerator {
    @Override
    protected String getPrefix() {
        return "E";
    }
}
