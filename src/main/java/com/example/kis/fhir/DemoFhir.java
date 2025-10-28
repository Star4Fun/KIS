package com.example.kis.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Patient;

/** Tiny FHIR (R4) demo using HAPI FHIR. */
public class DemoFhir {
  public static void main(String[] args) {
    FhirContext ctx = FhirContext.forR4();
    IParser json = ctx.newJsonParser().setPrettyPrint(true);

    Patient p = new Patient();
    p.addName().setFamily("Doe").addGiven("John");
    p.setGender(org.hl7.fhir.r4.model.Enumerations.AdministrativeGender.MALE);

    String out = json.encodeResourceToString(p);
    System.out.println(out);
  }
}
