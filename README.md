# nttdatapay-java-demo
 
## Prerequisites
- Java 1.8 and above
- UAT MID and keys provided by the NDPS team
 
## Project Structure
The project contains the following files:
- `nttdatapay-java-demo` - Payment request initiation demo project 

- `ATOMAESEncryption-1.0-1` - Encryption logic
- `AtomSecurity-0.1` - Encryption logic
 
## Installation
1. Import project as maven project .
2. Modify the JSON request and the keys used for encryption and decryption.
3. Configure `atomcheckout.js` according to UAT and Production environments(URL present in checkout.jsp).
4. Use payNow api to make changes for auth api 
5. Use `response`api to manage responses and update the decryption keys within `response` api. 