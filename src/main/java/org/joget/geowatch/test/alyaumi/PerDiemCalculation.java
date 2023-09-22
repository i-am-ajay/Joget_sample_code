package org.joget.geowatch.test.alyaumi;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;

public class PerDiemCalculation {
    public static Object getAmount(){
        String user = "#process.processRequesterId#";
        String designation = "#user."+user+".employee.jobTitle#";
        String tripDays = "#variable.days#";
        String travelCountry = "#variable.travel_country#";
        String organization = "#user."+user+".meta.company#";
        boolean isInternationTravle = isInternationalTrip(organization, travelCountry);
        double perDiemRate = getPerDiemAmount(designation,isInternationTravle);
        double toalAmount = getTotalPerDiemAmount(tripDays, perDiemRate);
        //Set total amount in form field using workflow variable

        return null;
    }

    public static boolean isInternationalTrip(String organization, String country){
        if(organization.equals("Yaumi International Bakeries")){
            if(country.equals("UAE")){
                return false;
            }
        }
        else{
            if(country.equals("KSA")){
                return false;
            }
        }
        return true;
    }
    public static double getPerDiemAmount(String designation, boolean isInternationalTrip){
        String tableName = "";
        String formId = "";
        double rateNumeric = 0.0;
        FormDataDao dao = (FormDataDao)AppUtil.getApplicationContext().getBean("formDataDao");
        FormRowSet rateRowSet = dao.find(formId, tableName,
                "where c_job_title = ?", new Object[]{designation},null,
                null,null,null);

        FormRow rateRow = rateRowSet.get(0);
        String rate = null;
        if(isInternationalTrip) {
           rate =  rateRow.getProperty("p_international_travel");
        }
        else{
            rate = rateRow.getProperty("p_local_travel");
        }
        if(rate != null){
            rateNumeric = Double.parseDouble(rate);
        }
        return rateNumeric;
    }

    public static double getTotalPerDiemAmount(String days, double rate){
        int totalDays = 0;
        int counter = 1;
        double percentage = 0;
        double totalAmount = 0.0;

        if(days != null || !days.equals("")){
            totalDays = Integer.parseInt(days);
            while(true){
                if(rate == 0.0){
                    return totalAmount;
                }
                // calculate percentage
                if(counter==1){
                    percentage = 1;
                }
                else if(counter==2){
                    percentage = .50;
                }
                else if(counter ==3){
                    percentage = .25;
                }
                else if(counter == 4){
                    percentage = .10;
                }
                else{
                    break;
                }

                if(totalDays>7){
                    totalAmount += rate * percentage * 7;
                    totalDays = totalDays -7;
                    counter++;
                }
                else{
                    totalAmount += rate * percentage * totalDays;
                    break;
                }
            }
        }
        return totalAmount;
    }
}
return PerDiemCalculation.getAmount();
