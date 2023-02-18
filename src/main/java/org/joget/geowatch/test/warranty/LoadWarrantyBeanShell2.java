package org.joget.geowatch.test.warranty;

import org.joget.apps.app.model.AppDefinition;
import org.joget.workflow.model.WorkflowAssignment;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.lang.*;
import java.lang.Object;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.LogUtil;

public class LoadWarrantyBeanShell2 {
    public static Object execute(WorkflowAssignment assignment, AppDefinition appDef, HttpServletRequest request) {
        //#process.recordId#
        String id = "#process.recordId#";
        String strwarr_number = "";
        String strproject_no = "";
        String strrevision = "";
        String strsales_order = "";
        String strdo_no = "";
        String strinvoice_number = "";
        String strequipment_number = "";
        String strproject_end_date = "";
        String strcode = "";
        String stritemType = "";
        String strquantity = "";
        String processType = null;
        try {
            processType = "#variable.processType#";
            if(processType != null && processType.equals("Reload")){
                id = "#variable.rId#";
            }
            LogUtil.info("Warranty Id",id);
            LogUtil.info("--- Second Bean Shell Tool Running ", " ------- \n"+id);
            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
            FormRowSet rows = new FormRowSet();
            FormRow newRow = new FormRow();
            FormRow warranty = formDataDao.load("warranty_review_form", "review_form", id);

            LogUtil.info("--- Before If Else Loop", " ------- \n" + warranty);
            if(warranty != null ){
                LogUtil.info("--- If loop ", " ------- \n"+ id);
                String projectNo=warranty.get("pno")+"";
                strrevision = (warranty.get("rev")!= null)?warranty.get("rev").toString():"";
                newRow.put("revision",strrevision);
                strsales_order = (warranty.get("sales_orders")!= null)?warranty.get("sales_orders").toString():"";
                newRow.put("sales_order",strsales_order);
                strdo_no = (warranty.get("do_no")!= null)?warranty.get("do_no").toString():"";
                newRow.put("do_no",strdo_no);
                strinvoice_number = (warranty.get("invoices")!= null)?warranty.get("invoices").toString():"";
                // newRow.put("invoice_number",strinvoice_number);

                FormRow project=null;
                FormRowSet rowsProjectsData = formDataDao.find("createProject", "projects", "where c_projectNumber =?", new Object[] {projectNo}, null, null, null, null);
                if(rowsProjectsData != null && !rowsProjectsData.isEmpty()){
                    project  = rowsProjectsData.get(0);
                    // strproject_end_date = (prrow.get("projectStartDate")!= null)?prrow.get("projectStartDate").toString():"";
                    //  newRow.put("project_end_date", strproject_end_date);
                }


                String[] DO_Nums = strdo_no.split(",");
                ArrayList DOs = new ArrayList(Arrays.asList(DO_Nums));
                for(Object objDO : DOs){
                    String strDO = objDO.toString();
                    strDO = strDO.replaceFirst("^\\s*", "");
                    LogUtil.info("--- DO number ", " ------- \n"+"@"+strDO);
                    FormRowSet rowsEqpMasterData = formDataDao.find("equipment_master", "equipment_master", "where c_do_Number =?", new Object[] {strDO}, null, null, null, null);
                    if(rowsEqpMasterData != null){

                        for(FormRow	eqrow:rowsEqpMasterData)
                        {
                            strequipment_number = (eqrow.get("equipment_Number")!= null)?eqrow.get("equipment_Number").toString():"";
                            LogUtil.info("--- Equipment master ", " ------- \n"+strequipment_number);
                            eqrow.put("warranty_ref",id);
                            eqrow.put("project_ref",project.getId());
                            eqrow.setId(id+"_"+eqrow.get("equipment_Number"));
                        }

                        formDataDao.saveOrUpdate("warrantyEquipment", "warranty_equipment", rowsEqpMasterData);
                    }
                    LogUtil.info("--- After Equipment master ", " ------- \n");
                    FormRowSet rowDOMasterData = formDataDao.find("do_master_form", "do_master", "where c_do_Number =?", new Object[] {strDO}, null, null, null, null);

                    //FormRow rowDOMasterData = formDataDao.load("do_master_form", "do_master", strDO);
                    String strdoItemCode = "";
                    if(rowDOMasterData != null && !rowDOMasterData.isEmpty()){
                        for(FormRow	rowDO:rowDOMasterData)
                        {
                            strdoItemCode = (rowDO.get("item_code")!= null)?rowDO.get("item_code").toString():"";
                            LogUtil.info("--- Item master ", " ------- \n"+strdoItemCode);
                            FormRowSet rowsItemsMasterData = formDataDao.find("item_master", "items_master", "where c_item_code =?", new Object[] {strdoItemCode}, null, null, null, null);
                            if(rowsItemsMasterData != null && !rowsItemsMasterData.isEmpty()){
                                LogUtil.info("--- Item master ", " ------- \n"+strdoItemCode);
                                for(FormRow	itrow:rowsItemsMasterData)
                                {
                                    // strequipment_number = (row.get("equipment_Number")!= null)?row.get("equipment_Number").toString():"";
                                    itrow.put("warranty_ref",id);
                                    itrow.put("project_ref",project.getId());
                                    itrow.setId(id+"_"+itrow.get("item_code"));
                                }
                                formDataDao.saveOrUpdate("warrantyItem", "warraty_items", rowsItemsMasterData);
                            }
                        }
                    }
                }
                rows.add(newRow);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LogUtil.info("--- Catch Exception =", " ------- \n"+e.getMessage());
        }
        finally {

        }
    return null;
    }
}
return LoadWarrantyBeanShell2.execute(workflowAssignment, appDef, request);
