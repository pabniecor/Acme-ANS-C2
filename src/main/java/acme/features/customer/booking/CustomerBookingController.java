
package acme.features.customer.booking;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.customer_management.Booking;
import acme.realms.Customer;

@GuiController
public class CustomerBookingController extends AbstractGuiController<Customer, Booking> {

	@Autowired
	private CustomerBookingListService		listService;

	@Autowired
	private CustomerBookingShowService		showService;

	@Autowired
	private CustomerBookingCreateService	createService;

	@Autowired
	private CustomerBookingUpdateService	updateService;

	@Autowired
	private CustomerBookingPublishService	publishService;

	@Autowired
	private CustomerBookingDeleteService	deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
