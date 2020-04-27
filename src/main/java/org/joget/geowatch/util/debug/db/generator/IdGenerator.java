package org.joget.geowatch.util.debug.db.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public abstract class IdGenerator implements IdentifierGenerator {

    private static final AtomicLong castId = new AtomicLong(0);

    protected abstract String getPrefix();

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        return getPrefix() + "-" + castId.incrementAndGet();
    }
}
