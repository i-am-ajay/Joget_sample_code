package org.joget.geowatch.api.dto.out.resp;

import org.joget.directory.model.User;

public class UserOutResp {
    protected String id;
    protected String firstName;
    protected String lastName;

    public static UserOutResp update(UserOutResp item, User user) {
        if (item == null) return null;

        item.id = user.getId();
        item.firstName = user.getFirstName();
        item.lastName = user.getLastName();
        return item;
    }
}
