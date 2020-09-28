package org.joget.geowatch.db.dao.impl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.joget.geowatch.db.dao.Dao;
import org.joget.geowatch.db.dto.Log;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.ASC;
import static org.joget.geowatch.db.dao.impl.AbstractDao.OrderType.DESC;
import static org.joget.geowatch.db.dao.impl.AbstractDao.ParamType.DATE;
import static org.joget.geowatch.db.dao.impl.AbstractDao.ParamType.STRING;

/**
 * Created by k.lebedyantsev
 * Date: 1/4/2018
 * Time: 2:05 PM
 */

public abstract class AbstractDao<T> implements Dao<T> {
    protected SessionFactory sessionFactory;

    private Class<T> clas;

    public AbstractDao(Class<T> clas) {
        this.clas = clas;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public String save(T item) throws Exception {
        return (String) sessionFactory.getCurrentSession().save(item);
    }

    public void update(T item) throws Exception {
        sessionFactory.getCurrentSession().update(item);
    }

    public List<String> save(List<T> itemList) throws Exception{
        List<String> res = new ArrayList<>();
        if(itemList != null) for(T item : itemList) {
            String id = (String)sessionFactory.getCurrentSession().save(item);
            if(id != null) res.add(id);
        }
        return res;
    }

    public void saveOrUpdate(T item) throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(item);
    }

    public void delete(T item) {
        sessionFactory.getCurrentSession().delete(item);
    }

    public void delete(List<T> itemList) {
        if(itemList != null) for(T item : itemList)
        sessionFactory.getCurrentSession().delete(item);
    }

    public T findById(String id) throws Exception {
        if (isEmpty(id)) return null;
        return (T) sessionFactory.getCurrentSession().get(clas, id);
    }

    public List<T> find() throws Exception {
        Query query = sessionFactory.getCurrentSession().createQuery(
                "FROM " + clas.getSimpleName());
        return query.list();
    }


    public List<T> find(String strQuery, String sort, Boolean desc, AParam... params) throws Exception {
        return find(strQuery, sort, desc, null, null, params);
    }

    public List<T> find(String strQuery, String sort, Boolean desc, Integer limit, Integer offset, AParam... params) throws Exception {
        if (isNotBlank(sort)) {
            String filteredSort = filterSpace(sort);
            strQuery = strQuery + " ORDER BY " + filteredSort;
            if (desc) strQuery = strQuery + " DESC";
        }

        Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
        AParam.process(query, params);
        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (offset != null) {
            query.setFirstResult(offset);
        }

        return query.list();
    }

    public List<T> find(String strQuery, Order[] orders, AParam... params) throws Exception {
        return find(strQuery, orders, null, params);
    }

    public List<T> find(String strQuery, Page page, AParam... params) throws Exception {
        return find(strQuery, null, page, params);
    }

    @Override
    public Long count(String strQuery, AParam... params) throws Exception {
        strQuery += Order.process((Order[]) null);
        Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
        AParam.process(query, params);
        return (Long) query.uniqueResult();
    }

    public List<T> find(String strQuery, AParam... params) throws Exception {
        return find(strQuery, (Order[])null, null, params);
    }

    public List<T> find(String strQuery, Order[] orders, Page page, AParam... params) throws Exception {
        strQuery += Order.process(orders);
        Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
        AParam.process(query, params);
        if(page != null) {
            if (page.limit != null) query.setMaxResults(page.limit);
            if (page.offset != null) query.setFirstResult(page.offset);
        }
        System.out.println("------------------------\n"+query+"\n------------------------");
        
        return query.list();
    }

    public T findSingle(String strQuery, AParam... params) throws Exception {
        return findSingle(strQuery, null, params);
    }

    public T findSingle(String strQuery, Order[] orders, AParam... params) throws Exception {
        strQuery += Order.process(orders);
        Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
        AParam.process(query, params);
        query.setMaxResults(1);
        
        
        T res= (T) query.uniqueResult();

        return res;
    }

    public List<T> findNativeSql(String strQuery, T entity, Order[] orders, AParam... params) throws Exception {
        strQuery += Order.process(orders);
        Query query = sessionFactory.getCurrentSession().createSQLQuery(strQuery).addEntity(entity.getClass());
        AParam.process(query, params);

        return query.list();
    }

    public T findNativeSqlSingle(String strQuery, T entity, Order[] orders, AParam... params) throws Exception {
        strQuery += Order.process(orders);
        Query query = sessionFactory.getCurrentSession().createSQLQuery(strQuery);
        AParam.process(query, params);

        return (T) query.uniqueResult();
    }

    private String filterSpace(String str) {
        if (isNotBlank(str)) {
            str = Normalizer.normalize(str, Normalizer.Form.NFKC);
//            if (str.contains(" ")) {
//                str = str.substring(0, str.indexOf(" "));
//            }
        }
        return str;
    }

    public static class Page {
        protected Integer offset;
        protected Integer limit;

        public Page(Integer offset, Integer limit) {
            this.offset = offset;
            this.limit = limit;
        }
    }

    public enum OrderType {ASC, DESC}

    public static class Order {
        private OrderType orderType;
        private String field;

        public Order(String field) {
            this.orderType = ASC;
            this.field = field;
        }

        public Order(String field, OrderType orderType) {
            this.orderType = orderType != null ? orderType : ASC;
            this.field = field;
        }

        protected String get() {
            if (field == null) return " ";
            return field + " " + orderType.name();
        }

        protected static String process(Order... orders) {
            if (orders == null) return "";

            boolean firstFlag = true;
            StringBuilder sb = new StringBuilder();
            for (Order order : orders) {
                if (!firstFlag) sb.append(", ");
                else firstFlag = false;

                sb.append(order.get());
            }
            String res = sb.toString();
            if (isBlank(res)) return "";

            return " ORDER BY " + res;
        }
    }

    enum ParamType {STRING, DATE, CLASS}

    public static abstract class AParam {
        protected String key;

        protected abstract ParamType getTypeParam();

        protected String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        protected static void process(Query query, AParam... params) {
            if (params != null) for (AParam p : params) {
                if (p != null) {
                    switch (p.getTypeParam()) {
                        case STRING:
                            StrParam sp = (StrParam) p;
                            query.setString(sp.key, sp.value);
                            break;
                        case DATE:
                            DateParam dp = (DateParam) p;
                            query.setTimestamp(dp.key, dp.value);
                            break;
                    }
                }
            }
        }
    }

    public static class StrParam extends AParam {
        protected String value;

        @Override
        public ParamType getTypeParam() {
            return STRING;
        }

        public StrParam(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class DateParam extends AParam {
        protected Date value;

        @Override
        public ParamType getTypeParam() {
            return DATE;
        }

        public DateParam(String key, Date value) {
            this.key = key;
            this.value = value;
        }

        public Date getValue() {
            return value;
        }

        public void setValue(Date value) {
            this.value = value;
        }
    }
}
