
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airline_operations.Airline;
import acme.entities.airline_operations.AirlineRepository;

@Validator
public class AirlineValidator extends AbstractValidator<ValidAirline, Airline> {

	@Autowired
	private AirlineRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAirline annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (airline == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			boolean uniqueAirline;
			Airline existingAirline;

			existingAirline = this.repository.findAirlineByIataCode(airline.getIataCode());
			uniqueAirline = existingAirline == null || airline.getIataCode().isBlank() || existingAirline.equals(airline);

			super.state(context, uniqueAirline, "iataCode", "acme.validation.airline.duplicate-iataCode.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
