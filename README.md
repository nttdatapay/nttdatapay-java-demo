# nttdatapay-java-demo
 
## Prerequisites
- Java 1.8 and above
- UAT MID and keys provided by the NDPS team
 
## Option 1: Use Java class file
To initiate a transaction, use the initTransactionForAipay method. Below is an example of how to set up the API endpoint to initiate a transaction.

1. Ensure that you replace the placeholder values with the actual values provided by your integration team.

    ```JAVA
    @GetMapping("/initiateTransaction")
        public void initiateTransaction() {
            String merchId = "317157";// FOR UAT//provided by integration team

            String amount = "1";
            String password = "Test@123";// FOR UAT//provided by integration team

            String userIdString = "";
            String merchTxnId = "test54667678";
            String returnUrl = "http://localhost:9090/transaction-response";// You have to set your own URL here as
                                                                            // response URL
            String userId = "";
            String mobileno = "999999999";
            String emailId = "test@gmail.com";
            String firstName = "Suraj";
            String billingInfo = "";
            String accNo = "999999999";

            String product = "NSE";//provided by integration team 
            String currency = "INR";

            String encReqKey = "A4476C2062FFA58980DC8F79EB6A799E";// For UAT //provided by integration team
            String encRespKey = "75AEF0FA1B94B3C10D4F5B268F757F11";// FOR UAT//provided by integration team

            String udf1 = "udf1";
            String udf2 = "udf2";
            String udf3 = "udf3";
            String udf4 = "udf4";
            String udf5 = "udf5";
            String environment = "UAT"; // use PROD for production

    Aipay.initTransactionForAipay(merchId, amount, merchTxnId, returnUrl, userId, password, mobileno, emailId, firstName, accNo, billingInfo, product, currency, encReqKey, encRespKey, udf1, udf2, udf3, udf4, udf5, environment);
        }
    ```

2. The returnUrl should point to your server's endpoint that will handle the transaction response.

    ``` JAVA
    //To handle Response Use below API
        @PostMapping("/transaction-response")
        public String postResponse(String encData, String merchId) {
            String respKey = "75AEF0FA1B94B3C10D4F5B268F757F11";// FOR UAT//provided by integration team

            Aipay.response(encData, merchId, respKey);
            
            return "";
        }
    ```
    If the project is not in Maven, simply import the JAR.

3. Use the appropriate environment setting (UAT for testing and PROD for production).


## Option 2: Use JAR File
### 🛠️ Install the JAR

1. Install the JAR in your system by using the following command:

    ```bash
    mvn install:install-file -Dfile=aipayclientkit.jar -DgroupId=com.ntt -DartifactId=aipayclientkit -Dversion=1.0 -Dpackaging=jar
    ```

2. After installing, check if the JAR is installed properly by using the following dependency in your pom.xml:

    ```XML
    <dependency>
        <groupId>com.ntt</groupId>
        <artifactId>aipayclientkit</artifactId>
        <version>1.0</version>
    </dependency>
    ```

3. Use the following code to create a GET mapping API for initiating a transaction:

    ```JAVA
    @GetMapping("/initiateTransaction")
    public void initiateTransaction() {
        String merchId = "317157"; // FOR UAT // provided by integration team
        String amount = "1";
        String password = "Test@123"; // FOR UAT // provided by integration team
        String userIdString = "";
        String merchTxnId = "test54667678";
        String returnUrl = "http://localhost:9090/transaction-response"; // You have to set your own URL here as response URL
        String userId = "";
        String mobileno = "999999999";
        String emailId = "test@gmail.com";
        String firstName = "Suraj";
        String billingInfo = "";
        String accNo = "999999999";
        String product = "NSE"; // provided by integration team
        // String product = "NCA"; // For Prod // provided by integration team
        String currency = "INR";
        String encReqKey = "A4476C2062FFA58980DC8F79EB6A799E"; // For UAT // provided by integration team
        String encRespKey = "75AEF0FA1B94B3C10D4F5B268F757F11"; // FOR UAT // provided by integration team
        String udf1 = "udf1";
        String udf2 = "udf2";
        String udf3 = "udf3";
        String udf4 = "udf4";
        String udf5 = "udf5";
        String environment = "UAT"; // use PROD for production

        Aipay.initTransactionForAipay(merchId, amount, merchTxnId, returnUrl, userId, password, mobileno, emailId, firstName, accNo, billingInfo, product, currency, encReqKey, encRespKey, udf1, udf2, udf3, udf4, udf5, environment);
    }
    ```

4. To handle the response, use the following API:
    ```JAVA
    @PostMapping("/transaction-response")
    public String postResponse(String encData, String merchId) {
        String respKey = "75AEF0FA1B94B3C10D4F5B268F757F11"; // FOR UAT // provided by integration team

        Aipay.response(encData, merchId, respKey);
        
        return "";
    }
    ```
