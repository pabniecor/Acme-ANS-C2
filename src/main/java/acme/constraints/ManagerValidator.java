
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.Manager;
import acme.realms.ManagerRepository;

@Validator
public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;
		if (manager == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				boolean uniqueManager;
				Manager existingManager;

				existingManager = this.repository.findManagerByIdentifier(manager.getIdentifier());
				uniqueManager = existingManager == null || existingManager.getIdentifier().isBlank() || existingManager.equals(manager);

				super.state(context, uniqueManager, "identifier", "acme.validation.manager.duplicate-identifier.message");
			}
			{
				boolean isValidIdentifier;

				isValidIdentifier = manager.getIdentifier().matches("^[A-Z]{2,3}\\d{6}$");

				super.state(context, isValidIdentifier, "identifier", "acme.validation.manager.identifier.message");
			}
			{
				String name = manager.getIdentity().getName();
				String surname = manager.getIdentity().getSurname();

				boolean correctIdentifier = manager.getIdentifier().charAt(0) == name.charAt(0) && manager.getIdentifier().charAt(1) == surname.charAt(0);

				super.state(context, correctIdentifier, "identifier", "acme.validation.manager.identifier.message");
			}
		}

		result = !super.hasErrors(context);

		return result;
	}
}
