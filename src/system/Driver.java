package src.system;

import java.util.Date;

public class Driver extends Car {

	private Date drivingExperience;

	public Driver(int customerId, InsuranceFee insuranceFee, String paymentStatus, Policy policy, int rate, int mileage, Date date) {
		this.setId();
		this.setInsuranceFee(insuranceFee);
		this.setPaymentStatus(paymentStatus);
		policy.setInsuranceId(getInsuranceID());
		policy.setMemberId(customerId);
		this.setPolicy(policy);
		this.setRate(rate);
		this.setMileage(mileage);
		this.drivingExperience = date;
	}
}