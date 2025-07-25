
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.FlightCrewMember;
import acme.realms.FlightCrewMemberRepository;

@Validator
public class FlightCrewMemberValidator extends AbstractValidator<ValidFlightCrewMember, FlightCrewMember> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidFlightCrewMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightCrewMember flightCrewMember, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (flightCrewMember == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String name = flightCrewMember.getIdentity().getName();
			String surname = flightCrewMember.getIdentity().getSurname();
			boolean correctIdentifier;

			correctIdentifier = flightCrewMember.getEmployeeCode().charAt(0) == name.charAt(0) && flightCrewMember.getEmployeeCode().charAt(1) == surname.charAt(0);

			super.state(context, correctIdentifier, "employeeCode", "acme.validation.flightCrewMember.employeeCode.message");
		}

		result = !super.hasErrors(context);

		return result;
	}
}
