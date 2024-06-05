package src.system;

import static src.system.utils.MESSAGE.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import src.system.accident.*;
import src.system.compensation.Compensation;
import src.system.compensation.CompensationListImpl;
import src.system.counseling.Counseling;
import src.system.counseling.CounsellingListImpl;
import src.system.user.Customer;
import src.system.user.CustomerListImpl;

public class Main {

  static BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in));

  private static CounsellingListImpl counselingList;
  private static CustomerListImpl customerList;
  private static CustomerListImpl c_customerList;
  private static InsuranceListImpl insuranceList;
  private static AccidentListImpl accidentList;
  private static CompensationListImpl compensationList;

  private static void setData() {
    customerList = new CustomerListImpl();
    c_customerList = new CustomerListImpl();
    insuranceList = new InsuranceListImpl();
    accidentList = new AccidentListImpl();
    counselingList = new CounsellingListImpl();
    compensationList = new CompensationListImpl();
  }

  public static void main(String[] args) {
    setData();
    try {
      while (true) {
        printMenu();
        String choice = input();
        handleUserChoice(choice);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static void handleUserChoice(String choice) throws IOException, InterruptedException {
    switch (choice) {
      case "1":
        registerInsurance();
        break;
      case "2":
        // 상품 설계 로직 추가
        break;
      case "3":
        payInsuranceFee();
        break;
      case "4":
        toAssessDamages();
        break;
      case "5":
        accidentReport();
        break;
      case "6":
        // 대출 로직 추가
        break;
      case "7":
        counselling();
        break;
      case "8":
        compensate();
        break;
      case "x":
        System.exit(0);
        break;
      default:
        System.out.println(MENU_INVALID_CHOICE.getMsg());
    }
  }

  private static void toAssessDamages() {
    accidentList.add(new Accident(1, "교통사고", "2024-06-04", "명지대", 1, "Pending") {
      @Override
      public void receiveAccident() {
        // 사고 접수 로직
      }
    }); // test data

    try {
      System.out.println(MENU_INFO.getMsg());
      showList(accidentList.get());

      System.out.println(MSG_ASSESS_DAMAGE.getMsg());
      long accidentId = Long.parseLong(input());
      Accident accident = accidentList.get(accidentId);

      if (accident != null) {
        handleCompensation(accidentId);
      } else {
        System.out.println(MSG_VALIDATE_ACCIDENT_ID.getMsg());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void handleCompensation(long accidentId) throws IOException {
    System.out.println(MSG_COMPENSATION_ASK.getMsg());
    System.out.println(MSG_YES_OR_NO.getMsg());

    String choice = input();
    switch (choice) {
      case "1":
        System.out.println(MSG_CALCULATE_PAYOUT.getMsg());
        long money = Long.parseLong(input());
        c_customerList.add(customerList.get(accidentList.get(accidentId).getCustomerId()));
        Compensation compensation = new Compensation(money, accidentList.get(accidentId).getCustomerId(), customerList);
        compensationList.add(compensation);
        break;
      case "2":
        accidentList.delete(accidentId);
        break;
      case "x":
        return;
      default:
        System.out.println(MENU_INVALID_CHOICE.getMsg());
    }
  }

  private static void compensate() {
    try {
      System.out.println(MENU_INFO.getMsg());
      showList(compensationList.get());
      System.out.println(MSG_ASSESS_COMPENSATE.getMsg());

      String sCompensationChoice = objReader.readLine().trim();
      long compensationId = Long.parseLong(sCompensationChoice);
      if (compensationList.get(compensationId) != null) {
        long customerId = compensationList.get(compensationId).getCustomerId();
        if (compensationList.get(compensationId).pay()) {
          System.out.println(c_customerList.get(customerId).getName() + "고객님에게 " + compensationList.get(compensationId).getMoney() + "원이 지급되었습니디.");
          customerList.delete(customerId);
        } else {
          System.out.println(c_customerList.get(customerId).getName() + "고객님의 계좌 정보가 없습니다.");
        }

      } else {
        System.out.println(MSG_VALIDATE_COMPENSATE_ID.getMsg());
      }

    } catch (RemoteException e) {
      e.printStackTrace();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void counselling() throws InterruptedException {
    System.out.println(MSG_COUNSELLING_REQUESTED.getMsg());
    Counseling counseling = new Counseling(1, 101, "Test Counseling", "This is a test counseling.");
    counselingList.add(counseling);

    Thread.sleep(2000);

    System.out.println(MSG_COUNSELLING_CONFIRMED.getMsg());
    counseling.confirm();
    counselingList.update(counseling);

    Thread.sleep(2000);

    System.out.println(MSG_COUNSELLING_SCHEDULED.getMsg());
    counseling.complete();
    counselingList.update(counseling);
  }

  private static String input() throws IOException {
    return objReader.readLine().trim();
  }


  private static void registerInsurance() throws IOException {
    Join join = new Join();
    System.out.println("이름, 성별, 전화번호, 생일 입력");
    Customer registerCustomer = join.register(input(), input(), input(), input());
    customerList.add(registerCustomer);

    System.out.println("가입할 보험 종류를 선택하세요:");
    System.out.println("1. 운전자 보험");
    System.out.println("2. 자차 보험");
    String insuranceChoice = input();

    switch (insuranceChoice) {
      case "1":
        insuranceList.add(new Driver((int) registerCustomer.getCustomerID(), new InsuranceFee(20000), 100, new Policy(), 100, 1, new Date()));
        System.out.println(registerCustomer.getName() + "님, 운전자 보험 가입이 완료되었습니다.");
        break;
      case "2":
        insuranceList.add(new OwnCar((int) registerCustomer.getCustomerID(), new InsuranceFee(10000), 100, new Policy(), 100, 1, 1, 1));
        System.out.println(registerCustomer.getName() + "님, 자차 보험 가입이 완료되었습니다.");
        break;
      default:
        System.out.println("유효하지 않은 선택입니다. 보험 가입이 취소되었습니다.");
    }
    System.out.println(registerCustomer.getCustomerID());
  }


  private static void accidentReport() {
    try {
      System.out.println(MSG_ASK_CUSTOMER_ID.getMsg());
      long customerId = Long.parseLong(input());

      Customer customer = customerList.get(customerId);
      if (customer == null) {
        System.out.println(MSG_VALIDATE_ID.getMsg());
        return;
      }

      System.out.println(MSG_ASK_ACCIDENT_DETAILS.getMsg());
      String accidentDetails = input();

      System.out.println(MSG_ASK_ACCIDENT_DATE.getMsg());
      String date = input();

      System.out.println(MSG_ASK_ACCIDENT_LOCATION.getMsg());
      String location = input();

      System.out.println(MSG_ASK_ACCIDENT_TYPE.getMsg());
      String accidentType = input();

      boolean hasDriverInsurance = customer.getInsuranceList().stream().anyMatch(insurance -> insurance instanceof Driver);
      boolean hasAutoInsurance = customer.getInsuranceList().stream().anyMatch(insurance -> insurance instanceof OwnCar);

      if ("PersonalInjury".equals(accidentType) && !hasDriverInsurance) {
        System.out.println("운전자 보험에 가입된 고객만 본인 상해 사고를 접수할 수 있습니다.");
        return;
      }

      if (("Liability".equals(accidentType) || "PropertyDamage".equals(accidentType)) && !hasAutoInsurance) {
        System.out.println("자차 보험에 가입된 고객만 대인배상 및 대물배상 사고를 접수할 수 있습니다.");
        return;
      }

      String[] additionalParams;
      if ("PersonalInjury".equals(accidentType)) {
        System.out.println("부상자의 수를 입력하세요:");
        String numInjuries = input();
        System.out.println("부상의 정도를 입력하세요(1~10):");
        String severity = input();
        additionalParams = new String[]{numInjuries, severity};
      } else if ("Liability".equals(accidentType)) {
        System.out.println("기록을 입력하세요:");
        String record = input();
        System.out.println("손해 비용을 입력하세요:");
        String damageCost = input();
        System.out.println("제3자 이름을 입력하세요:");
        String thirdPartyName = input();
        System.out.println("제3자 연락처를 입력하세요:");
        String thirdPartyContact = input();
        additionalParams = new String[]{record, damageCost, thirdPartyName, thirdPartyContact};
      } else if ("PropertyDamage".equals(accidentType)) {
        System.out.println("재산 피해를 설명하세요:");
        String propertyDamage = input();
        additionalParams = new String[]{propertyDamage};
      } else {
        System.out.println("유효하지 않은 사고 유형입니다.");
        return;
      }

      Accident accident = AccidentFactory.createAccident(accidentType, accidentList.get().size() + 1, accidentDetails, date, location, customer, additionalParams);
      accidentList.add(accident);
      System.out.println(MSG_ACCIDENT_REPORTED.getMsg());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private static void payInsuranceFee() {
    customerList.add(new Customer("hello1", "M", "phone number", "abc")); // 여기서 사용자 생성
    showList(customerList.get());
    try {
      System.out.println(MENU_INFO.getMsg());
      System.out.println(MSG_ASK_CUSTOMER_ID.getMsg());
      long customerId = Long.parseLong(input());
      makeInsurance(customerId);

      System.out.println(MSG_ASK_INSURANCE_ID.getMsg());
      long insuranceId = Long.parseLong(input());

      Customer customer = customerList.get(customerId);
      Insurance insurance = insuranceList.get(insuranceId);
      System.out.println(insurance);
      if (customer != null && insurance != null) {
        customer.pay(insurance);
        System.out.println(MSG_COMPLETE_INSURANCE_FEE.getMsg() + insurance.getInsuranceFee().getDateOfPayment());
      } else {
        System.out.println(MSG_VALIDATE_ID.getMsg());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void makeInsurance(long customerId) {
    insuranceList.add(new OwnCar((int) customerId, new InsuranceFee(10000), 100, new Policy(), 100, 1, 1, 1));
    insuranceList.add(new Driver((int) customerId, new InsuranceFee(20000), 100, new Policy(), 100, 1, new Date()));
    showList(insuranceList.get());
  }

  private static void showList(ArrayList<?> dataList) {
    for (Object o : dataList) {
      System.out.println(o);
    }
  }

  private static void printMenu() {
    System.out.println(WELCOME_MESSAGE.getMsg());
    System.out.println(MENU_INFO.getMsg());
    System.out.println(MENU_JOIN.getMsg());
    System.out.println(MENU_DESIGN.getMsg());
    System.out.println(MENU_PAY.getMsg());
    System.out.println(MENU_DAMAGE_ASSESSMENT.getMsg());
    System.out.println(MENU_ACCIDENT.getMsg());
    System.out.println(MENU_LOAN.getMsg());
    System.out.println(MENU_CONPENSATE.getMsg());
    System.out.println(MENU_COUNSELLING.getMsg());
    System.out.println(MENU_EXIT.getMsg());
  }
}
