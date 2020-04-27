package org.joget.geowatch.db.service;

import org.joget.geowatch.db.dto.Property;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/22/2018
 * Time: 1:11 PM
 */
public interface PropertyService {
    List<Property> listProperties(String classKey);
}
