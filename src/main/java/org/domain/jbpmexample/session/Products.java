package org.domain.jbpmexample.session;

public enum Products {

	BRMS("brms", "rasgabucho"),
	ESB("esb", "poucastrancas"),
	JBPM("jBPM", "poucastrancas"),
	PORTAL("portal", "rasgabucho");

	Products(String label, String specialist) {
		this.label = label;
		this.specialist = specialist;
	}

	private String label;
	private String specialist;

	public String getSpecialist() {
		return specialist;
	}

	public void setSpecialist(String specialist) {
		this.specialist = specialist;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
