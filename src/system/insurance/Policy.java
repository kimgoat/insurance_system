package src.system.insurance;

public class Policy {

	private String policyDetails;

	public Policy(){

	}

	public void finalize() throws Throwable {

	}

//	public void setInsuranceId(Long insuranceID) {
//		this.insuranceID = insuranceID.intValue();
//	}
//	public void setMemberId(int memberID) {
//		this.memberID = memberID;
//	}
@Override
public String toString() {
	return policyDetails;
}
	public void setPolicyDetails(String policyDetails) {
		this.policyDetails = policyDetails;
	}
	public String getPolicyDetails() {
        return this.policyDetails;
    }
}

