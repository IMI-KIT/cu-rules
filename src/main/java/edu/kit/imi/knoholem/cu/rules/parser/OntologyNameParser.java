package edu.kit.imi.knoholem.cu.rules.parser;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import edu.kit.imi.knoholem.ontology.KnoHolEMOntology;
import edu.kit.imi.knoholem.ontology.OntologyUtils;

public class OntologyNameParser {
	private OWLDataFactory factory;
	private OWLReasoner reasoner;
	
	OntologyNameParser(){
		this.factory = KnoHolEMOntology.getInstance().getFactory();
		this.reasoner = KnoHolEMOntology.getInstance().getReasoner();
	}
	
	
	public String parseName(String leftLiteral, String zoneId) {
		OWLClass zoneClass = factory.getOWLClass(IRI.create(OntologyUtils.ONTOLOGY_NS + "Zone"));
		OWLDataProperty hasNameProperty = factory.getOWLDataProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + "hasName"));
		Set<OWLNamedIndividual> zones = reasoner.getInstances(zoneClass, false).getFlattened();
		
		for(OWLNamedIndividual zone : zones){
			OWLLiteral zoneNameLiteral = (OWLLiteral) reasoner.getDataPropertyValues(zone, hasNameProperty).toArray()[0];
			if(zoneNameLiteral.getLiteral().equals(zoneId)){
				return getOccupancySensorName(zone, zoneId);
			}
		}
		System.out.println("Zone \"" + zoneId + "\" not found");
		return "-1";
	}

	
	private String getOccupancySensorName(OWLNamedIndividual zone, String zoneId) {
		OWLObjectProperty hasSensorProperty = factory.getOWLObjectProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + "hasSensors"));
		Set<OWLNamedIndividual> sensors = reasoner.getObjectPropertyValues(zone, hasSensorProperty).getFlattened();
		
		for(OWLNamedIndividual sensor : sensors){
			OWLClass sensorClass = (OWLClass) reasoner.getTypes(sensor, true).getFlattened().toArray()[0];
			String className = sensorClass.toStringID().substring(OntologyUtils.ONTOLOGY_NS.length());
			if(className.equals("OccupancySensor")){
				OWLDataProperty hasNameProperty = factory.getOWLDataProperty(IRI.create(OntologyUtils.ONTOLOGY_NS + "hasName"));
				OWLLiteral sensorNameLiteral = (OWLLiteral) reasoner.getDataPropertyValues(sensor, hasNameProperty).toArray()[0];
				return sensorNameLiteral.getLiteral();
			}
		}
		System.out.println("No occupancy sensor found in zone \"" + zoneId + "\"");
		return "-1";
	}

}
