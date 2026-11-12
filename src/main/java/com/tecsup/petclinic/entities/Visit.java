package com.tecsup.petclinic.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "visits")
@Data
public class Visit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "pet_id", insertable = false, updatable = false)
	private Integer petId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	@ToString.Exclude
	@JsonIgnore
	private Pet pet;

	@Column(name = "vet_id")
	private Integer vetId;

	@Column(name = "visit_date")
	private LocalDate visitDate;

	@Column(name = "description")
	private String description;

	@Column(name = "cost")
	private BigDecimal cost;
}
