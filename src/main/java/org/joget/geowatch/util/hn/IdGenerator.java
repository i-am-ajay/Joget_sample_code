package org.joget.geowatch.util.hn;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {

        String prefix = "DEP";
        Connection connection = session.connection();

        try {
            Statement statement = connection.createStatement();

            //ToDo
            ResultSet rs = statement.executeQuery("select count(Department_Id) as Id from demo.Department");

            if (rs.next()) {
                int id = rs.getInt(1) + 101;
                String generatedId = prefix + new Integer(id).toString();
              //  System.out.println("Generated Id: " + generatedId);
                return generatedId;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
