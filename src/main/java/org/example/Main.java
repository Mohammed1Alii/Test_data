package org.example;
import org.apache.poi.ss.usermodel.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public void main(String[] args) throws Exception {

        Data_base_conn d = new Data_base_conn();
        List<String> ServiceNu = new ArrayList<>();
        ServiceNu = d.GetAllTeleNu();
        Call_APIs API = new Call_APIs();
        for (String S : ServiceNu){
            String Bill = API.GetVoiceLineBillStatusMain(S);
            String[] parts = Bill.split(";", 3);   // limit 3: the third part (the message) stays in one piece
            String first  = parts.length > 0 ? parts[0].trim() : "";
            String second = parts.length > 1 ? parts[1].trim() : "";
            if (first.equals("Success") && second.equals("Paid")) {
                String Payment = API.GetMainOfferStatus(S,"X");
                if (Payment.startsWith(("Success"))){
                    System.out.println("Service Num : " + S);
                    break;
                }
            }
        }


        List<String> COMOrder = new ArrayList<>();
        COMOrder = d.GetAllComOrderID();
        Call_APIs API2 = new Call_APIs();
        for (String N : COMOrder){
            String Check_Payment = API2.checkOrderPaymentStatus(N);
            if (Check_Payment.equals("Paid")){
                System.out.println("COMOrder : " + N);
                break;
            }
        }

        List<String> DSLAM = new ArrayList<>();
        DSLAM = d.DSLAM();
        Call_APIs API3 = new Call_APIs();
        for(String M : DSLAM){
            long value = ThreadLocalRandom.current().nextLong(9999L, 10000000000000L + 1);
            String s = String.valueOf(value);
            String dslam = API3.GetFTTH_DslamList(M,s);
            if (dslam.equals("Success")){
                System.out.println("DSLAM : " + M );
                break;
            }
        }

    }

}