
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianInvolvesCreateService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TechnicianInvolvesRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Collection<Task> tasks;
		Collection<MaintenanceRecord> MRs;
		int selectedTaskId;
		Technician loggedTechnician;
		int mrId;
		Task t;
		MaintenanceRecord mr;

		loggedTechnician = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId());

		mrId = super.getRequest().getData("mrId", int.class);
		MRs = this.repository.findMRsByTechnicianId(loggedTechnician.getId());
		mr = this.repository.findMRById(mrId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && MRs.contains(mr) && mr.getDraftMode();

		if (super.getRequest().hasData("id", int.class)) {

			tasks = this.repository.findPublicTasksNotInvolvedWithMR(mrId);
			selectedTaskId = super.getRequest().getData("task", int.class);
			t = this.repository.findTaskById(selectedTaskId);

			if (selectedTaskId != 0)
				status = status && tasks.contains(t);
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Involves involves;
		int mrId;
		MaintenanceRecord mr;

		mrId = super.getRequest().getData("mrId", int.class);
		mr = this.repository.findMRById(mrId);

		involves = new Involves();
		involves.setMaintenanceRecord(mr);

		super.getBuffer().addData(involves);
	}

	@Override
	public void bind(final Involves involves) {
		super.bindObject(involves, "task");
	}

	@Override
	public void validate(final Involves involves) {
		;

	}

	@Override
	public void perform(final Involves involves) {
		this.repository.save(involves);
	}

	@Override
	public void unbind(final Involves involves) {
		Dataset dataset;
		Collection<Task> tasks;
		SelectChoices taskChoices;
		int mrId;

		mrId = super.getRequest().getData("mrId", int.class);
		tasks = this.repository.findPublicTasksNotInvolvedWithMR(mrId);

		taskChoices = SelectChoices.from(tasks, "description", involves.getTask());

		dataset = super.unbindObject(involves, "task");
		dataset.put("task", taskChoices.getSelected().getKey());
		dataset.put("tasks", taskChoices);

		super.getResponse().addData(dataset);
		super.getResponse().addGlobal("mrId", super.getRequest().getData("mrId", int.class));
	}

}
