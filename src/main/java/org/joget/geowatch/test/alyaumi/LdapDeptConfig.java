package org.joget.geowatch.test.alyaumi;

import java.util.Map;
import java.util.Set;
import org.joget.commons.util.LogUtil;
import org.joget.directory.dao.DepartmentDao;
import org.joget.directory.dao.EmploymentDao;
import org.joget.directory.dao.OrganizationDao;
import org.joget.directory.dao.RoleDao;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.Employment;
import org.joget.directory.model.Role;
import org.joget.directory.model.User;
import org.joget.directory.model.Department;
import org.joget.directory.model.Organization;
import org.joget.directory.model.service.DirectoryManagerProxyImpl;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.ldap.SyncLdapUserDirectoryManagerImpl;
import org.joget.plugin.directory.SecureDirectoryManagerImpl;
import org.joget.plugin.ldap.DepartmentDaoLDAPImpl;
import org.joget.plugin.ldap.EmploymentDaoLDAPImpl;

public class LdapDeptConfig {
    public static void setProperties(SyncLdapUserDirectoryManagerImpl dm) {
        dm.setProperty("departmentBaseDN", "");
        dm.setProperty("departmentImportSearchFilter", "(objectClass=groupOfNames)");
        dm.setProperty("department-attr-id", "cn");
        dm.setProperty("department-attr-name", "description");
        dm.setProperty("department-attr-description", "description");

        dm.setProperty("department-attr-hod", "owner");

        //User to department mapping, Please configure either from department or from users
        dm.setProperty("department-attr-users", "member");
        dm.setProperty("department-attr-mappedToUserAttr", "distinguishedName");

        dm.setProperty("user-attr-departments", "");
        dm.setProperty("user-attr-mappedToDepartmentAttr", "");
    }

    public static int syncDepts(Organization org, Map properties) {
        DepartmentDaoLDAPImpl ldapDeptDao = new DepartmentDaoLDAPImpl(properties);
        DepartmentDao deptDao = (DepartmentDao) AppUtil.getApplicationContext().getBean("departmentDao");

        int count = 0;
        Collection ldapDepts = ldapDeptDao.getDepartmentsByOrganizationId(null, null, null, null, null, null);
        Collection localDepts = deptDao.getDepartmentsByOrganizationId(null, org.getId(), null, null, null, null);

        Map deptMap = new HashMap();
        for (Department d : localDepts) {
            deptMap.put(d.getId(), d);
        }

        for (Department ld : ldapDepts) {
            boolean exist = true;
            Department d = deptMap.get(ld.getId());
            if (d == null) {
                d = new Department();
                d.setId(ld.getId());
                d.setOrganization(org);
                exist = false;
            }

            d.setName(ld.getName());
            d.setDescription(ld.getDescription());
            LogUtil.info("Dept Name",ld.getName());
            LogUtil.info("Hod",ld.getHod().getUser().getFirstName());
            /*if (exist) {
                deptDao.updateDepartment(d);
            } else {
                deptDao.addDepartment(d);
            }*/

            count++;
        }
        return count;
    }

    public static int syncUsers(Organization org, Map properties) {
        EmploymentDaoLDAPImpl ldapEmploymentDao = new EmploymentDaoLDAPImpl(properties);
        DepartmentDao deptDao = (DepartmentDao) AppUtil.getApplicationContext().getBean("departmentDao");
        UserDao userDao = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
        RoleDao roleDao = (RoleDao) AppUtil.getApplicationContext().getBean("roleDao");
        EmploymentDao employmentDao = (EmploymentDao) AppUtil.getApplicationContext().getBean("employmentDao");

        int count = 0;
        Collection ldapUsers = ldapEmploymentDao.getEmployments(null, null, null, null, null, null, null, null);
        Collection localUsers = userDao.findUsers(" where e.password is null", null, null, null, null, null);

        Map userMap = new HashMap();
        for (User u : localUsers) {
            userMap.put(u.getUsername(), u);
        }

        Collection roles = roleDao.getRoles(null, null, null, null, null);
        Map roleMap = new HashMap();
        for (Role r : roles) {
            roleMap.put(r.getId(), r);
        }

        Collection depts = deptDao.getDepartmentsByOrganizationId(null, org.getId(), null, null, null, null);
        Map deptMap = new HashMap();
        for (Department d : depts) {
            deptMap.put(d.getId(), d);
        }

        for (Employment e : ldapUsers) {
            try {
                User lu = e.getUser();

                boolean exist = true;
                User u = userMap.get(lu.getUsername());
                if (u == null) {
                    u = new User();
                    u.setId(lu.getId());
                    u.setUsername(lu.getUsername());
                    exist = false;
                } else {
                    userMap.remove(lu.getUsername());
                }

                u.setFirstName(lu.getFirstName());
                u.setLastName(lu.getLastName());
                u.setEmail(lu.getEmail());
                u.setLocale(lu.getLocale());
                u.setTimeZone(lu.getTimeZone());
                u.setActive(lu.getActive());
                u.setPassword(null);
                u.setReadonly(true);

                //set roles
                if (lu.getRoles() != null && lu.getRoles().size() > 0) {
                    Set userRoles = new HashSet();
                    for (Role role : (Set) lu.getRoles()) {
                        userRoles.add(roleMap.get(role.getId()));
                    }
                    u.setRoles(userRoles);
                }

                if (exist) {
                    userDao.updateUser(u);
                } else {
                    userDao.addUser(u);
                }

                exist = false;
                Employment employment = null;
                if (u.getEmployments() != null && u.getEmployments().size() > 0) {
                    employment = (Employment) u.getEmployments().iterator().next();
                    exist = true;
                } else {
                    employment = new Employment();
                    employment.setUser(u);
                }

                employment.setUserId(lu.getUsername());
                employment.setEmployeeCode(e.getEmployeeCode());
                employment.setRole(e.getRole());

                employment.setOrganizationId(org.getId());

                //update department & hod mapping
                Department d = null;
                System.out.println(e.getDepartment());
                if (e.getDepartment() != null) {
                    System.out.println(e.getDepartment());
                    d = deptMap.get(e.getDepartment().getId());
                    employment.setDepartmentId(d.getId());
                } else {
                    employment.setDepartmentId(null);
                }
                Set hods = new HashSet();
                if (d != null && e.getHods() != null && e.getHods().size() > 0) {
                    hods.add(d);
                }
                employment.setHods(hods);

                if (exist) {
                    employmentDao.updateEmployment(employment);
                } else {
                    employmentDao.addEmployment(employment);
                }

                if (hods.size() > 0) {
                    d.setHod(employment);
                    deptDao.updateDepartment(d);
                }
            } catch (Exception ex) {
                LogUtil.error(SyncLdapUserDirectoryManagerImpl.class.getName(), ex, "");
            }

            count++;
        }

        //remove remaining user with password is null
        for (String username : userMap.keySet()) {
            userDao.deleteUser(username);
        }

        return count;
    }
    public static void run(){
        LogUtil.info("Running method","");
        DirectoryManagerProxyImpl proxy = (DirectoryManagerProxyImpl)AppUtil.getApplicationContext().getBean("directoryManager");
        DirectoryManager dm = proxy.getDirectoryManagerImpl();
        if (dm != null) {
            if (dm instanceof SecureDirectoryManagerImpl) {
                LogUtil.info("In if block","");
                dm = ((SecureDirectoryManagerImpl) dm).getEdm(1);
            }
            LogUtil.info("DM",dm.toString());
            if (dm instanceof SyncLdapUserDirectoryManagerImpl) {
                for(Object dept : dm.getDepartmentList()) {
                    Department d = (Department) dept;
                    LogUtil.info("Dept", d.getName());
                }
                String ldapOrgId = "ORG-001";
                setProperties((SyncLdapUserDirectoryManagerImpl) dm);
                Map properties = ((SyncLdapUserDirectoryManagerImpl) dm).getProperties();
                for(Object obj : properties.entrySet()){
                    Map.Entry entry = (Map.Entry)obj;
                    LogUtil.info(entry.getKey().toString(),entry.getValue().toString());
                }
                OrganizationDao orgDao = (OrganizationDao) AppUtil.getApplicationContext().getBean("organizationDao");
                Organization org = orgDao.getOrganization(ldapOrgId);
                int deptTotal = syncDepts(org, properties);
                LogUtil.info("Total department : ", deptTotal+"");
                /*int total = syncUsers(org, properties);
                System.out.println("Total users : " + total);*/
            }
        }
    }
}
LdapDeptConfig.run();
