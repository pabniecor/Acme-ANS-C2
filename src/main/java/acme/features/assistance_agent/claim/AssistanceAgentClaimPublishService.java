
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.ClaimType;
import acme.entities.flight_management.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		AssistanceAgent currentAgent;
		int id = super.getRequest().getData("id", int.class);
		Claim claim = this.repository.findClaimById(id);

		currentAgent = claim == null ? null : claim.getAssistanceAgent();

		status = claim != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && claim.getDraftMode();

		if (status) {
			String method;
			int legId;
			Leg leg;

			method = super.getRequest().getMethod();

			if (method.equals("GET"))
				status = true;
			else {
				super.getRequest().getData("type", ClaimType.class);
				legId = super.getRequest().getData("leg", int.class);
				leg = super.getRequest().getData("leg", Leg.class);

				Boolean statusDa = legId == 0 ? true : this.repository.findLegsWithDepartureBeforeClaimRegistration().contains(leg);
				status = statusDa;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Claim claim) {
		claim.setDraftMode(false);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		Collection<Leg> legs;
		SelectChoices legChoices;
		SelectChoices claimTypes;

		legs = this.repository.findLegsWithDepartureBeforeClaimRegistration();
		claimTypes = SelectChoices.from(ClaimType.class, claim.getType());
		legChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "accepted", "draftMode", "leg");
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("types", claimTypes);

		super.getResponse().addData(dataset);
	}
}
