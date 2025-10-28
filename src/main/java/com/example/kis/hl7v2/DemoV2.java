package com.example.kis.hl7v2;

import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.parser.PipeParser;

/** Tiny HL7 v2 demo using HAPI v2. Note: HL7 v2 segments are CR-terminated (\r), not \n. */
public class DemoV2 {
  public static void main(String[] args) throws Exception {
    String msg =
        ""
            + "MSH|^~\\&|HIS|RI|LAB|RI|20250101||ADT^A01|123|P|2.5\r"
            + "PID|||12345^^^HOSP||Doe^John||19800101|M\r";

    PipeParser parser = new PipeParser();
    ADT_A01 adt = (ADT_A01) parser.parse(msg);

    String family = adt.getPID().getPatientName(0).getFamilyName().getSurname().getValue();
    String given = adt.getPID().getPatientName(0).getGivenName().getValue();

    System.out.println("Patient: " + family + ", " + given);
  }
}
