
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidCustomer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidCustomer
@Table(indexes = {
	@Index(columnList = "identifier")
})
public class Customer extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", message = "{acme.validation.customer.identifier.message}")
	@Column(unique = true)
	private String				identifier;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$", message = "{acme.validation.customer.phone-number.message}")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				physicalAddress;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				city;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				country;

	@Optional
	@ValidNumber(min = 0, max = 500000)
	@Automapped
	private Integer				earnedPoints;

}
