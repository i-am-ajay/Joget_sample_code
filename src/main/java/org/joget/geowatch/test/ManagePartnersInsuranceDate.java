package org.joget.geowatch.test;

import org.joget.commons.util.LogUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.app.service.AppUtil;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.joget.apps.form.model.*;
import java.util.HashMap;
import org.joget.commons.util.LogUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormRow;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.joget.apps.form.model.*;
import java.util.HashMap;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.lang.RandomStringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Date;
import java.sql.ResultSet;

public class ManagePartnersInsuranceDate {
    public void runScheduler(){
        String instance_id,insurance1_end_date,insurance2_end_date,insurance3_end_date,insurance4_end_date,partner_company_name,insurance_types;
        String cdate= "#date.dd-MM-yyyy#";
        Date ins1= null;
        Date ins2= null;
        Date ins3= null;
        Date ins4= null;
        long endTime1 = 0,endTime2 = 0,endTime3 = 0,endTime4 = 0;
        long diffTime1 = 0,diffTime2 = 0,diffTime3 = 0,diffTime4 = 0;
        LogUtil.info("Current Date",":" + cdate);

        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

        FormRowSet rows = formDataDao.find("add_partner", "mokxa_partner", null,null,null,null,null,null);

        for(FormRow	row:rows)
        {

//get data from field
            insurance1_end_date = row.getProperty("insurance1_end_date");
            insurance2_end_date = row.getProperty("insurance2_end_date");
            insurance3_end_date = row.getProperty("insurance3_end_date");
            insurance4_end_date = row.getProperty("insurance4_end_date");
            partner_company_name = row.getProperty("partner_company_name");
            insurance_types = row.getProperty("insurance_types");
            instance_id = row.getId();

            LogUtil.info("insurance1_end_date",":" + insurance1_end_date);
            LogUtil.info("insurance2_end_date",":" + insurance2_end_date);
            LogUtil.info("insurance3_end_date",":" + insurance3_end_date);
            LogUtil.info("insurance4_end_date",":" + insurance4_end_date);
            LogUtil.info("partner_company_name",":" + partner_company_name);
            LogUtil.info("insurance_types",":" + insurance_types);

            try{

// Create SimpleDateFormat object
                SimpleDateFormat sdfo = new SimpleDateFormat("dd-MM-yyyy");
                Date currentd = sdfo.parse(cdate);
                LogUtil.info("sdfo",":" + sdfo.parse(cdate));
                long startTime = currentd.getTime();

                if(insurance1_end_date != null && !insurance1_end_date.equals("")){
                    ins1 = sdfo.parse(insurance1_end_date);
                    endTime1 = ins1.getTime();
                    diffTime1 = (endTime1 - startTime)/(1000*60*60*24);
                    LogUtil.info("if condition",":" + insurance1_end_date);
                    LogUtil.info("End Time 1 - "+endTime1,"Difference 1 : " + diffTime1);
                }
                if( insurance2_end_date != null && !insurance2_end_date.equals(""))
                {
                    ins2 = sdfo.parse(insurance2_end_date);
                    endTime2 = ins2.getTime();
                    diffTime2 = (endTime2 - startTime)/(1000*60*60*24);
                    LogUtil.info("End Time 2 - "+endTime2,"Difference 2 :" + diffTime2);
                    LogUtil.info("if condition",":" + insurance1_end_date);
                }
                if(insurance3_end_date != null && !insurance3_end_date.equals(""))
                {
                    ins3 = sdfo.parse(insurance3_end_date);
                    endTime3 = ins3.getTime();
                    diffTime3 = (endTime3 - startTime)/(1000*60*60*24);
                    LogUtil.info("End Time 3 - "+endTime3,"Difference 3" + diffTime3);

                }
                if( insurance4_end_date != null && !insurance4_end_date.equals("")){
                    LogUtil.info("if condition",":" + insurance1_end_date);
                    ins4 = sdfo.parse(insurance4_end_date);
                    endTime4 = ins4.getTime();
                    diffTime4 = (endTime4 - startTime)/(1000*60*60*24);
                    LogUtil.info("End Time 4 - "+endTime4,"Difference 4 " + diffTime4);
                }

                if(diffTime1 == 30)
                {

                    HashMap bmap=new HashMap();

                    bmap.put("insurance1_end_date",insurance1_end_date);
                    bmap.put("insurance2_end_date",insurance2_end_date);
                    bmap.put("insurance3_end_date",insurance3_end_date);
                    bmap.put("insurance4_end_date",insurance4_end_date);
                    bmap.put("insurance_types",insurance_types);
                    bmap.put("partner_company_name",partner_company_name);

//start process
                    org.joget.workflow.model.WorkflowProcessResult processResult=workflowManager.processStart("partner#latest#insurance_end_process", bmap );
                }

                if(diffTime2 == 30)
                {

                    HashMap bmap=new HashMap();

                    bmap.put("insurance1_end_date",insurance1_end_date);
                    bmap.put("insurance2_end_date",insurance2_end_date);
                    bmap.put("insurance3_end_date",insurance3_end_date);
                    bmap.put("insurance4_end_date",insurance4_end_date);
                    bmap.put("insurance_types",insurance_types);
                    bmap.put("partner_company_name",partner_company_name);

//start process
                    org.joget.workflow.model.WorkflowProcessResult processResult=workflowManager.processStart("partner#latest#insurance_end_process", bmap );
                }

                if(diffTime3 == 30)
                {

                    HashMap bmap=new HashMap();

                    bmap.put("insurance1_end_date",insurance1_end_date);
                    bmap.put("insurance2_end_date",insurance2_end_date);
                    bmap.put("insurance3_end_date",insurance3_end_date);
                    bmap.put("insurance4_end_date",insurance4_end_date);
                    bmap.put("insurance_types",insurance_types);
                    bmap.put("partner_company_name",partner_company_name);

//start process
                    org.joget.workflow.model.WorkflowProcessResult processResult=workflowManager.processStart("partner#latest#insurance_end_process", bmap );
                }

                if(diffTime4 == 30)
                {

                    HashMap bmap=new HashMap();

                    bmap.put("insurance1_end_date",insurance1_end_date);
                    bmap.put("insurance2_end_date",insurance2_end_date);
                    bmap.put("insurance3_end_date",insurance3_end_date);
                    bmap.put("insurance4_end_date",insurance4_end_date);
                    bmap.put("insurance_types",insurance_types);
                    bmap.put("partner_company_name",partner_company_name);

//start process
                    org.joget.workflow.model.WorkflowProcessResult processResult=workflowManager.processStart("partner#latest#insurance_end_process", bmap );
                }
            }

            catch(Exception e)
            {
                LogUtil.error("Manage Partners",e,e.getMessage());
            }

        }    }
}
