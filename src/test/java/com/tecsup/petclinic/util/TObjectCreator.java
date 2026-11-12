package com.tecsup.petclinic.util;

import com.tecsup.petclinic.dtos.*;
import com.tecsup.petclinic.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TObjectCreator {

	public static Pet getPet() {
		return Pet.builder().id(1).name("Leo").typeId(1).ownerId(1).build();
	}

	public static PetDTO getPetTO() {
		return new PetDTO(1,"Leo",1,1, "2000-09-07");
	}

	// ==================== VET ====================
	public static Vet getVet() {
		return Vet.builder()
				.id(1)
				.firstName("James")
				.lastName("Carter")
				.email("james.carter@petclinic.com")
				.phone("6085551234")
				.active(true)
				.build();
	}

	public static VetDTO getVetTO() {
		return VetDTO.builder()
				.id(1)
				.firstName("James")
				.lastName("Carter")
				.email("james.carter@petclinic.com")
				.phone("6085551234")
				.active(true)
				.build();
	}

	public static VetDTO newVetTO() {
		return VetDTO.builder()
				.firstName("Alice")
				.lastName("Smith")
				.email("alice.smith@petclinic.com")
				.phone("6085559999")
				.active(true)
				.build();
	}

	public static List<Vet> getActiveVets() {
		List<Vet> vets = new ArrayList<>();
		vets.add(new Vet(1, "James", "Carter", "james.carter@petclinic.com", "6085551234", true, null));
		vets.add(new Vet(2, "Helen", "Leary", "helen.leary@petclinic.com", "6085552345", true, null));
		vets.add(new Vet(3, "Linda", "Douglas", "linda.douglas@petclinic.com", "6085553456", true, null));
		vets.add(new Vet(4, "Rafael", "Ortega", "rafael.ortega@petclinic.com", "6085554567", true, null));
		vets.add(new Vet(5, "Henry", "Stevens", "henry.stevens@petclinic.com", "6085555678", true, null));
		return vets;
	}

	public static List<Vet> getInactiveVets() {
		List<Vet> vets = new ArrayList<>();
		vets.add(new Vet(6, "Sharon", "Jenkins", "sharon.jenkins@petclinic.com", "6085556789", false, null));
		return vets;
	}

	// ==================== OWNER ====================
	public static Owner getOwner() {
		return Owner.builder()
				.id(1)
				.firstName("George")
				.lastName("Franklin")
				.address("110 W. Liberty St.")
				.city("Madison")
				.telephone("6085551023")
				.build();
	}

	public static OwnerDTO getOwnerTO() {
		return OwnerDTO.builder()
				.id(1)
				.firstName("George")
				.lastName("Franklin")
				.address("110 W. Liberty St.")
				.city("Madison")
				.telephone("6085551023")
				.build();
	}

	public static OwnerDTO newOwnerTO() {
		return OwnerDTO.builder()
				.firstName("Bruce")
				.lastName("Wayne")
				.address("Wayne Manor")
				.city("Gotham")
				.telephone("6085550000")
				.build();
	}

	// ==================== SPECIALTY ====================
	public static Specialty getSpecialty() {
		return Specialty.builder()
				.id(1)
				.name("radiology")
				.office("Farewell")
				.hOpen(8)
				.hClose(18)
				.build();
	}

	public static SpecialtyDTO getSpecialtyTO() {
		return SpecialtyDTO.builder()
				.id(1)
				.name("radiology")
				.office("Farewell")
				.hOpen(8)
				.hClose(18)
				.build();
	}

	public static SpecialtyDTO newSpecialtyTO() {
		return SpecialtyDTO.builder()
				.name("dermatology")
				.office("Springfield")
				.hOpen(9)
				.hClose(17)
				.build();
	}

	// ==================== TYPE ====================
	public static PetType getPetType() {
		return PetType.builder()
				.id(1)
				.name("cat")
				.description("Domestic feline")
				.active(true)
				.sizeCategory("small")
				.averageLifespan(15)
				.careLevel("medium")
				.build();
	}

	public static PetTypeDTO getPetTypeTO() {
		return PetTypeDTO.builder()
				.id(1)
				.name("cat")
				.description("Domestic feline")
				.active(true)
				.sizeCategory("small")
				.averageLifespan(15)
				.careLevel("medium")
				.build();
	}

	public static PetTypeDTO newPetTypeTO() {
		return PetTypeDTO.builder()
				.name("ferret")
				.description("Small mammal")
				.active(true)
				.sizeCategory("small")
				.averageLifespan(7)
				.careLevel("medium")
				.build();
	}

	// ==================== VISIT ====================
	public static Visit getVisit() {
		return Visit.builder()
				.id(1)
				.petId(7)
				.vetId(2)
				.visitDate(LocalDate.of(2010, 3, 4))
				.description("rabies shot")
				.cost(new BigDecimal("45.00"))
				.build();
	}

	public static VisitDTO getVisitTO() {
		return VisitDTO.builder()
				.id(1)
				.petId(7)
				.vetId(2)
				.visitDate(LocalDate.of(2010, 3, 4))
				.description("rabies shot")
				.cost(new BigDecimal("45.00"))
				.build();
	}

	public static VisitDTO newVisitTO() {
		return VisitDTO.builder()
				.petId(1)
				.vetId(1)
				.visitDate(LocalDate.of(2024, 5, 10))
				.description("checkup")
				.cost(new BigDecimal("50.00"))
				.build();
	}

	// ==================== VET SPECIALTY ====================
	public static VetSpecialty getVetSpecialty() {
		return VetSpecialty.builder()
				.id(new VetSpecialtyId(2, 1))
				.certificationDate(LocalDate.of(2015, 6, 15))
				.yearsExperience(8)
				.isPrimary(true)
				.notes("Expert in X-ray imaging")
				.build();
	}

	public static VetSpecialtyDTO getVetSpecialtyTO() {
		return VetSpecialtyDTO.builder()
				.vetId(2)
				.specialtyId(1)
				.certificationDate(LocalDate.of(2015, 6, 15))
				.yearsExperience(8)
				.isPrimary(true)
				.notes("Expert in X-ray imaging")
				.build();
	}

	public static VetSpecialtyDTO newVetSpecialtyTO() {
		return VetSpecialtyDTO.builder()
				.vetId(1)
				.specialtyId(2)
				.certificationDate(LocalDate.of(2023, 1, 10))
				.yearsExperience(3)
				.isPrimary(false)
				.notes("Surgery resident")
				.build();
	}
}
