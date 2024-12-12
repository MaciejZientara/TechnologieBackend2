package com.jpacourse.persistence.entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "ADDRESS")
public class AddressEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String city;

	private String addressLine1;

	private String addressLine2;

	private String postalCode;

	//	relacje jednostronne - adres nie wie kto pod nim mieszka, ale osoby wiedzą gdzie mieszkają
	//  jednostronne ponieważ użyto mappedBy dla OneToOne - tylko pomoc dla kompilatora,
	//  tych zmiennych tu "nie powinno być"
	@OneToOne(mappedBy = "address")
	private DoctorEntity doctorResident;

	@OneToOne(mappedBy = "address")
	private PatientEntity patientResident;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}


	public DoctorEntity getDoctorResident() {
		return doctorResident;
	}

	public void setDoctorResident(DoctorEntity doctorResident) {
		this.doctorResident = doctorResident;
	}

	public PatientEntity getPatientResident() {
		return patientResident;
	}

	public void setPatientResident(PatientEntity patientResident) {
		this.patientResident = patientResident;
	}
}
