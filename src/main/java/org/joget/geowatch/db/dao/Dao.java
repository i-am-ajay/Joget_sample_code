package org.joget.geowatch.db.dao;

import org.joget.geowatch.db.dao.impl.AbstractDao;

import java.util.List;

/**
 * Created by k.lebedyantsev
 * Date: 1/12/2018
 * Time: 4:33 PM
 */
public interface Dao<T> {
    String save(T item) throws Exception;

    List<String> save(List<T> itemList) throws Exception;

    void update(T entity) throws Exception;

    void saveOrUpdate(T entity) throws Exception;

    void delete(T entity) throws Exception;

    void delete(List<T> entityList) throws Exception;

    T findById(String id) throws Exception;

    List<T> find() throws Exception;


    List<T> find(String strQuery, AbstractDao.Order[] orders, AbstractDao.AParam... params) throws Exception;

    List<T> find(String strQuery, AbstractDao.Page page, AbstractDao.AParam... params) throws Exception;

    List<T> find(String strQuery, AbstractDao.AParam... params) throws Exception;

    List<T> find(String strQuery, AbstractDao.Order[] orders, AbstractDao.Page page, AbstractDao.AParam... params) throws Exception;


    T findSingle(String query, AbstractDao.AParam... params) throws Exception;

    T findSingle(String strQuery, AbstractDao.Order[] orders, AbstractDao.AParam... params) throws Exception;


    List<T> find(String query, String sort, Boolean desc, AbstractDao.AParam... params) throws Exception;

    List<T> find(String query, String sort, Boolean desc, Integer limit, Integer offset, AbstractDao.AParam... params) throws Exception;

    Long count(String query, AbstractDao.AParam... params) throws Exception;

    T findNativeSqlSingle(String strQuery, T entity, AbstractDao.Order[] orders, AbstractDao.AParam... params) throws Exception;

}
